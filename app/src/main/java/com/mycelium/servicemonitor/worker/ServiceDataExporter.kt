package com.mycelium.servicemonitor.worker

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.mycelium.servicemonitor.model.Service
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceDataExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun exportServices(services: List<Service>) {
        val jsonString = Json.encodeToString(services)
        // Write JSON to a file in the app's cache directory.
        val fileName = "services_export.json"
        val file = File(context.cacheDir, fileName)
        file.writeText(jsonString)

        // Get URI using FileProvider.
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        // Create a share intent.
        context.startActivity(
            Intent.createChooser(
                Intent()
                    .setAction(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_STREAM, uri)
                    .setType("application/json")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION),
                "Share exported file"
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}
