package com.mycelium.servicemonitor

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mycelium.servicemonitor.ui.history.HistoryScreen
import com.mycelium.servicemonitor.ui.main.AddServiceScreen
import com.mycelium.servicemonitor.ui.main.EditServiceScreen
import com.mycelium.servicemonitor.ui.main.ServiceListScreen
import com.mycelium.servicemonitor.ui.theme.ServiceMonitorTheme
import common.CommonKeys.REQUEST_CODE_POST_NOTIFICATIONS
import dagger.hilt.android.AndroidEntryPoint

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

        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        // In your Activity's onCreate (or wherever appropriate)
        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(POST_NOTIFICATIONS),
                REQUEST_CODE_POST_NOTIFICATIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, you can now show notifications
            } else {
                // Permission denied; handle accordingly (show rationale or disable notification features)
            }
        }
    }
}

enum class Page(name: String) {
    SERVICE_LIST("serviceList"),
    ADD_SERVICE("addService"),
    EDIT_SERVICE("editService"),
    HISTORY("history")
}

@Composable
fun MyAppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Page.SERVICE_LIST.name) {
        composable(Page.SERVICE_LIST.name) {
            ServiceListScreen(
                onAddServiceClick = { navController.navigate(Page.ADD_SERVICE.name) },
                onEditServiceClick = { serviceId -> navController.navigate("editService/$serviceId") },
                openPage = { page -> navController.navigate(page) }
            )
        }
        composable(Page.ADD_SERVICE.name) {
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
        composable(Page.HISTORY.name) {
            HistoryScreen()
        }
    }
}
