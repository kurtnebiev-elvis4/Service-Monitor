package com.mycelium.servicemonitor.worker

import android.content.Context
import android.net.Uri
import com.mycelium.servicemonitor.model.Service
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceDataImporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) {
    /**
     * Reads the JSON content from the provided Uri and returns a list of Service objects.
     */
    fun importServices(uri: Uri): List<Service> =
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.readText()
            json.decodeFromString<List<Service>>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
}
