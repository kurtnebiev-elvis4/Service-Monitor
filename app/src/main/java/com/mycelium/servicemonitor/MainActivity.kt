package com.mycelium.servicemonitor

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mycelium.servicemonitor.ui.history.HistoryScreen
import com.mycelium.servicemonitor.ui.main.AddServiceScreen
import com.mycelium.servicemonitor.ui.main.EditServiceScreen
import com.mycelium.servicemonitor.ui.main.ServiceListScreen
import com.mycelium.servicemonitor.ui.notifications.NotificationsScreen
import com.mycelium.servicemonitor.ui.theme.ServiceMonitorTheme
import common.CommonKeys.REQUEST_CODE_POST_NOTIFICATIONS
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.mycelium.servicemonitor.ui.main.ServiceListViewModel
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult

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
    HISTORY("history"),
    NOTIFICATIONS("notifications")
}

@Composable
fun BuildVersionText() {
    val context = LocalContext.current
    val versionName = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Text(
            text = "v$versionName",
            modifier = Modifier.padding(8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    var menuExpanded by remember { mutableStateOf(false) }
    var showRemoveAllConfirmation by remember { mutableStateOf(false) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: Page.SERVICE_LIST.name
    val serviceListViewModel: ServiceListViewModel = hiltViewModel()

    // Add file picker launcher for import
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { serviceListViewModel.importList(it) }
    }

    if (showRemoveAllConfirmation) {
        AlertDialog(
            onDismissRequest = { showRemoveAllConfirmation = false },
            title = { Text("Remove All Services") },
            text = { Text("Are you sure you want to remove all services? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRemoveAllConfirmation = false
                        serviceListViewModel.removeAll()
                    }
                ) {
                    Text("Remove All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveAllConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            when (currentRoute) {
                Page.SERVICE_LIST.name -> {
                    TopAppBar(
                        title = { Text("Сервисы для мониторинга") },
                        actions = {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Menu"
                                )
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Check All Now") },
                                    onClick = {
                                        menuExpanded = false
                                        serviceListViewModel.checkAllNow()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Export List") },
                                    onClick = {
                                        menuExpanded = false
                                        serviceListViewModel.exportList()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Import List") },
                                    onClick = {
                                        menuExpanded = false
                                        importLauncher.launch("application/json")
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("History") },
                                    onClick = {
                                        menuExpanded = false
                                        navController.navigate(Page.HISTORY.name)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Remove All") },
                                    onClick = {
                                        menuExpanded = false
                                        showRemoveAllConfirmation = true
                                    }
                                )
                            }
                        }
                    )
                }

                Page.NOTIFICATIONS.name -> {
                    TopAppBar(
                        title = { Text("Notifications") },
                        actions = {
                            IconButton(onClick = { /* TODO: Implement clear notifications */ }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Clear notifications"
                                )
                            }
                            IconButton(onClick = { /* TODO: Implement refresh notifications */ }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh notifications"
                                )
                            }
                        }
                    )
                }

                Page.HISTORY.name -> {
                    TopAppBar(
                        title = { Text("History") }
                    )
                }

                Page.ADD_SERVICE.name, Page.EDIT_SERVICE.name -> {
                    TopAppBar(
                        title = { Text(if (currentRoute == Page.ADD_SERVICE.name) "Add Service" else "Edit Service") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Services") },
                    label = { Text("Services") },
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        navController.navigate(Page.SERVICE_LIST.name) {
                            popUpTo(Page.SERVICE_LIST.name) { inclusive = true }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Notifications") },
                    label = { Text("Notifications") },
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        navController.navigate(Page.NOTIFICATIONS.name) {
                            popUpTo(Page.NOTIFICATIONS.name) { inclusive = true }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Page.SERVICE_LIST.name,
                modifier = Modifier.padding(paddingValues)
            ) {
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
                composable(Page.NOTIFICATIONS.name) {
                    NotificationsScreen()
                }
            }
            BuildVersionText()
        }
    }
}
