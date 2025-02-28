package com.mycelium.servicemonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mycelium.servicemonitor.ui.main.AddServiceScreen
import com.mycelium.servicemonitor.ui.main.EditServiceScreen
import com.mycelium.servicemonitor.ui.main.ServiceListScreen
import com.mycelium.servicemonitor.ui.theme.ServiceMonitorTheme
import com.mycelium.servicemonitor.worker.ServiceCheckWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ServiceMonitorTheme {
                val navController = rememberNavController()
                MyAppNavHost(navController = navController)
            }
        }

        // Периодическая работа: раз в 15 минут (минимально для PeriodicWorkRequest)
        val serverCheckWorkRequest =
            PeriodicWorkRequestBuilder<ServiceCheckWorker>(15, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "ServerCheckWork",
                ExistingPeriodicWorkPolicy.KEEP,
                serverCheckWorkRequest
            )
    }
}

@Composable
fun MyAppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "serviceList") {
        composable("serviceList") {
            ServiceListScreen(
                onAddServiceClick = { navController.navigate("addService") },
                onEditServiceClick = { serviceId -> navController.navigate("editService/$serviceId") }
            )
        }
        composable("addService") {
            AddServiceScreen(
                onServiceSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(
            "editService/{serviceId}",
            arguments = listOf(navArgument("serviceId") { type = NavType.IntType })
        ) {
            EditServiceScreen(
                onServiceUpdated = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}
