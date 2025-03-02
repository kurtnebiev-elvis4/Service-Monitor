package com.mycelium.servicemonitor.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "check_history")
data class CheckHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var serviceName: String,
    var timestamp: Long,
    var status: String
)