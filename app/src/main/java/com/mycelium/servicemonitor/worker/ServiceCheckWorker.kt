package com.mycelium.servicemonitor.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mycelium.servicemonitor.NotificationHelper
import com.mycelium.servicemonitor.model.Service
import com.mycelium.servicemonitor.repository.ServiceRepository
import common.parseHeaders
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@HiltWorker
class ServiceCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ServiceRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Read all services from Room. We use first() to take a snapshot of the list.
        val serviceId = inputData.getInt("serviceId", -1)
        if (serviceId == -1) return Result.failure()

        // Retrieve the service from the repository.
        val service = repository.getServiceById(serviceId) ?: return Result.failure()

        // Check the service (HTTP call).
        val result = checkServer(service)

        // Update the service with new status and last checked timestamp.
        repository.updateServiceStatus(
            service.copy(
                status = result,
                lastChecked = System.currentTimeMillis()
            )
        )

        if (result != "ok") {
            sendNotification(applicationContext, "Server is down: ${service.url}")
        }

        return Result.success()
    }

    private suspend fun checkServer(service: Service): String =
        try {
            val url = URL(service.url)
            with(withContext(Dispatchers.IO) {
                url.openConnection()
            } as HttpURLConnection) {
                parseHeaders(service.headers).forEach { (key, value) ->
                    setRequestProperty(key, value)
                }
                connectTimeout = 5000
                readTimeout = 5000
                requestMethod = "GET"
                connect()
                if (responseCode in 200..299) {
                    "ok"
                } else {
                    "$responseCode $responseMessage"
                }
            }
        } catch (e: Exception) {
            e.message.orEmpty()
        }


    private fun sendNotification(context: Context, message: String) {
        NotificationHelper.showNotification(context, "Server Check", message)
    }
}
