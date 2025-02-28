package com.mycelium.servicemonitor.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mycelium.servicemonitor.model.Service
import com.mycelium.servicemonitor.repository.ServiceRepository
import common.CheckMode
import common.parseHeaders
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory


@HiltWorker
class ServiceCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ServiceRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Read all services from Room. We use first() to take a snapshot of the list.
        val serviceId = inputData.getInt("serviceId", -1)
        if (serviceId == -1) return Result.failure()

        // Retrieve the service from the repository.
        val service = repository.getServiceById(serviceId) ?: return Result.failure()

        // Check the service (HTTP call).
        val mode = CheckMode.fromUrl(service.url)
        val result = when (mode) {
            CheckMode.HTTP, CheckMode.HTTPS -> checkHttp(service)
            CheckMode.TCP_TLS -> checkTcpTls(service)
        }

        // Update the service with new status and last checked timestamp.
        repository.updateServiceStatus(
            service.copy(
                status = result,
                lastChecked = System.currentTimeMillis()
            ).let {
                if (result == "ok") it.copy(lastSuccessfulCheck = System.currentTimeMillis())
                else it
            }
        )

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

    private suspend fun checkHttp(service: Service): String =
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
                requestMethod = service.method.ifEmpty { "GET" }.uppercase()
                if (requestMethod in listOf(
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE"
                    ) && service.body.isNotEmpty()
                ) {
                    doOutput = true
                    outputStream.use { os ->
                        os.write(service.body.toByteArray(Charsets.UTF_8))
                    }
                }
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

    private suspend fun checkTcpTls(service: Service): String =
        try {
            withContext(Dispatchers.IO) {
                val (host, port) = parseTcpTlsUrl(service.url)
                // Create SSLSocket and connect
                val sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
                val sslSocket = sslSocketFactory.createSocket(host, port) as SSLSocket
                sslSocket.use {
                    // Start the TLS handshake
                    it.startHandshake()
                }
            }
            "ok"
        } catch (e: Exception) {
            e.message.orEmpty()
        }

    private fun parseTcpTlsUrl(tcpTlsUrl: String): Pair<String, Int> {
        // Example: tcp-tls://example.com:8443
        // Remove prefix if it starts with tcp-tls://
        val stripped = tcpTlsUrl.removePrefix("tcp-tls://")
        // Split on : to get host and port
        val parts = stripped.split(":")
        val host = parts[0]
        // If no port is specified, default to 443
        val port = if (parts.size > 1) parts[1].toIntOrNull() ?: 443 else 443
        return Pair(host, port)
    }
}


