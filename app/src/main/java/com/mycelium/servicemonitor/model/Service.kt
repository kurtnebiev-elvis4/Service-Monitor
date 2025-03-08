package com.mycelium.servicemonitor.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient as KTransient

@Serializable
data class Service(
    @KTransient val id: Int = 0,

    val name: String,
    val url: String,
    val interval: Int,
    val headers: String = "",
    val method: String = "",
    val body: String = "",

    val sha1Certificate: String = "",
    val position: Int = 0,

    @KTransient val status: String = "",
    @KTransient val lastSuccessfulCheck: Long = 0L,
    @KTransient val lastChecked: Long = 0L,
    @KTransient val archived: Boolean = false
)