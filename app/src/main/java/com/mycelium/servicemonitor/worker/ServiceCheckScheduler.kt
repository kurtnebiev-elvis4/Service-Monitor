package com.mycelium.servicemonitor.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mycelium.servicemonitor.model.Service
import com.mycelium.servicemonitor.repository.ServiceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ServiceCheckScheduler @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: ServiceRepository
) {
    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun scheduleAllServiceChecks() = GlobalScope.launch(Dispatchers.Default) {
        val services = repository.getAllServices()

        // For each service, schedule an individual periodic worker.
        services.forEach { service ->
            // Use the service interval; ensure it's at least 15 minutes (the minimum for PeriodicWorkRequest).
            val intervalMinutes = service.interval.toLong().coerceAtLeast(15L)
            val inputData = Data.Builder()
                .putInt("serviceId", service.id)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<ServiceCheckWorker>(
                intervalMinutes, TimeUnit.MINUTES, 2, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

            // Use a unique work name per service (so that if the service changes, it replaces the old request).
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "ServiceCheckWorker_${service.id}",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }
    }

    fun allServiceChecksNow() = GlobalScope.launch(Dispatchers.Default) {
        val services = repository.getAllServices()
        Log.e("ServiceCheckScheduler", "services = $services")
        services.forEach { service ->
            check(service)
        }
    }

    fun checkServiceNow(service: Service) {
        check(service)
    }

    private fun check(service: Service) {
        val inputData = Data.Builder()
            .putInt("serviceId", service.id)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ServiceCheckWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "ImmediateServiceCheckWorker_${service.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }


}
