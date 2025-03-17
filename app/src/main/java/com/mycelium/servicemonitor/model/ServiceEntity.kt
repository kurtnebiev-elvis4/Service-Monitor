package com.mycelium.servicemonitor.model

import androidx.room.ColumnInfo
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
    @ColumnInfo(defaultValue = "")
    val method: String = "",
    @ColumnInfo(defaultValue = "")
    val body: String = "",
    @ColumnInfo(defaultValue = "")
    val responsePattern: String = "",
    @ColumnInfo(defaultValue = "0")
    val useRegexPattern: Boolean = false,
    @ColumnInfo(defaultValue = "")
    val sha1Certificate: String = "",
    val status: String,
    val lastChecked: Long,
    @ColumnInfo(defaultValue = "0")
    val lastSuccessfulCheck: Long = 0L,
    val archived: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val position: Int = 0,
    @ColumnInfo(defaultValue = "")
    val groupName: String = ""
)