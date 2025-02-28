package com.mycelium.servicemonitor.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val url: String,
    val interval: Int,
    val headers: String,
    val status: String,
    val lastChecked: Long,
    val archived: Boolean = false
)

