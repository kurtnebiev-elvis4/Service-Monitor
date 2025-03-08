package com.mycelium.servicemonitor.worker

import com.mycelium.servicemonitor.model.Service
import common.parseHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class HttpServiceChecker(
    private val service: Service
) : ServiceChecker {
    override suspend fun check(): String = try {
        val url = URL(service.url)
        val connection = withContext(Dispatchers.IO) { url.openConnection() } as HttpURLConnection

        if (connection is HttpsURLConnection && service.sha1Certificate.isNotEmpty()) {
            configureHttps(connection)
        }

        parseHeaders(service.headers).forEach { (key, value) ->
            connection.setRequestProperty(key, value)
        }
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.requestMethod = service.method.ifEmpty { "GET" }.uppercase()

        if (connection.requestMethod in listOf("POST", "PUT", "PATCH", "DELETE") && service.body.isNotEmpty()) {
            connection.doOutput = true
            connection.outputStream.use { os ->
                os.write(service.body.toByteArray(Charsets.UTF_8))
            }
        }

        withContext(Dispatchers.IO) {
            connection.connect()
        }
        if (connection.responseCode in 200..299) "ok" else "${connection.responseCode} ${connection.responseMessage}"
    } catch (e: Exception) {
        e.message.orEmpty()
    }

    private fun configureHttps(connection: HttpsURLConnection) {
        val trustAllCerts = arrayOf<javax.net.ssl.TrustManager>(
            object : javax.net.ssl.X509TrustManager {
                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
            }
        )
        val sslContext = javax.net.ssl.SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        connection.sslSocketFactory = sslContext.socketFactory
        connection.hostnameVerifier = javax.net.ssl.HostnameVerifier { hostname, session ->
            session.peerHost.equals(hostname, ignoreCase = true)
        }
    }
}
