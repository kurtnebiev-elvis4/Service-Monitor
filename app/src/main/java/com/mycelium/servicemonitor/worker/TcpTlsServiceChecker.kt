package com.mycelium.servicemonitor.worker

import com.mycelium.servicemonitor.model.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class TcpTlsServiceChecker(
    private val service: Service
) : ServiceChecker {
    override suspend fun check(): String = try {
        withContext(Dispatchers.IO) {
            val (host, port) = parseTcpTlsUrl(service.url)
            val sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
            val sslSocket = sslSocketFactory.createSocket(host, port) as SSLSocket
            sslSocket.use {
                it.startHandshake()
            }
        }
        "ok"
    } catch (e: Exception) {
        e.message.orEmpty()
    }

    private fun parseTcpTlsUrl(tcpTlsUrl: String): Pair<String, Int> {
        val stripped = tcpTlsUrl.removePrefix("tcp-tls://")
        val parts = stripped.split(":")
        val host = parts[0]
        val port = if (parts.size > 1) parts[1].toIntOrNull() ?: 443 else 443
        return Pair(host, port)
    }
}

