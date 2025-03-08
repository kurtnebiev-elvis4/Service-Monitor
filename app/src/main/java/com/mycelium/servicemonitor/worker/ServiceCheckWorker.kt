package com.mycelium.servicemonitor.worker

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mycelium.servicemonitor.model.CheckHistoryEntity
import com.mycelium.servicemonitor.model.Service
import com.mycelium.servicemonitor.repository.HistoryRepository
import com.mycelium.servicemonitor.repository.ServiceRepository
import common.CheckMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

interface ServiceChecker {
    suspend fun check(): String
}

object CheckMethodFactory {
    fun getChecker(service: Service): ServiceChecker =
        when (CheckMode.fromUrl(service.url)) {
            CheckMode.HTTP, CheckMode.HTTPS -> HttpServiceChecker(service)
            CheckMode.TCP_TLS -> TcpTlsServiceChecker(service)
        }
}

@HiltWorker
class ServiceCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ServiceRepository,
    private val historyRepository: HistoryRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Read all services from Room. We use first() to take a snapshot of the list.
        val serviceId = inputData.getInt("serviceId", -1)
        if (serviceId == -1) return Result.failure()

        // Retrieve the service from the repository.
        val service = repository.getServiceById(serviceId) ?: return Result.failure()

        if (!isNetworkQualitySufficient()) {
            return Result.retry()
        }

        // Use the factory to obtain the appropriate checker and perform the check.
        val checker = CheckMethodFactory.getChecker(service)
        val result = checker.check()

        val updatedService = if (result == "ok") service.copy(
            status = result,
            lastSuccessfulCheck = System.currentTimeMillis(),
            lastChecked = System.currentTimeMillis()
        )
        else service.copy(
            status = result,
            lastChecked = System.currentTimeMillis()
        )

        historyRepository.insert(
            CheckHistoryEntity(
                serviceName = service.name,
                timestamp = System.currentTimeMillis(),
                status = result
            )
        )

        // Update the service with new status and last checked timestamp.
        repository.updateService(updatedService)

        if (result != "ok") {
            notificationHelper.showNotification(
                applicationContext,
                "Server Check",
                "Server is down: ${service.url}",
                serviceId
            )
        } else {
            notificationHelper.cancelNotification(applicationContext, serviceId)
        }

        return Result.success()
    }

    private fun isNetworkQualitySufficient(): Boolean {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        if (networkCapabilities == null || !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            return false
        }
        val downlinkSpeed = networkCapabilities.getLinkDownstreamBandwidthKbps()
        return downlinkSpeed >= 1000
    }
}
