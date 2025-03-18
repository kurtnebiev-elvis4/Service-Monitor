package com.mycelium.servicemonitor

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.mycelium.servicemonitor.worker.ServiceCheckScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidApp
class TheApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var scheduler: ServiceCheckScheduler

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduler.scheduleAllServiceChecks()

        arrayOf(
            "all_users",
            "new_orders",
            "card_issued",
            "provider_balances",
            "daily_summary",
            "admin_alerts"
        ).forEach {
            Firebase.messaging.subscribeToTopic(it)
                .addOnCompleteListener { task ->
                    var msg = "Subscribed"
                    if (!task.isSuccessful) {
                        msg = "Subscribe failed"
                    }
                    Log.d("!!!!", msg)
                }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = false
            // Configure other options as desired
        }
}