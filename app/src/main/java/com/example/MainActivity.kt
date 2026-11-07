package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.database.AppDatabase
import com.example.data.repository.LibretaRepository
import com.example.ui.navigation.Screen
import com.example.ui.screens.AddEditClientScreen
import com.example.ui.screens.ClientDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MiLibretaTheme
import com.example.ui.viewmodel.AddEditClientViewModel
import com.example.ui.viewmodel.ClientDetailViewModel
import com.example.ui.viewmodel.HomeViewModel
import com.example.ui.viewmodel.ReportsViewModel
import com.example.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiLibretaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MiLibretaApp()
                }
            }
        }
    }
}

@Composable
fun MiLibretaApp() {
    val context = LocalContext.current
    val repository = remember {
        val database = AppDatabase.getDatabase(context)
        LibretaRepository(
            clienteDao = database.clienteDao(),
            movimientoDao = database.movimientoDao(),
            configDao = database.configDao()
        )
    }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topLevelRoutes = listOf(Screen.Home.route, Screen.Reports.route, Screen.Settings.route)
    val showBottomBar = currentRoute in topLevelRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                        label = { Text("Libreta") },
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            if (currentRoute != Screen.Home.route) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                        label = { Text("Reportes") },
                        selected = currentRoute == Screen.Reports.route,
                        onClick = {
                            if (currentRoute != Screen.Reports.route) {
                                navController.navigate(Screen.Reports.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Storefront, contentDescription = null) },
                        label = { Text("Mi Negocio") },
                        selected = currentRoute == Screen.Settings.route,
                        onClick = {
                            if (currentRoute != Screen.Settings.route) {
                                navController.navigate(Screen.Settings.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Pestaña 1: Inicio / Libreta
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.Factory(repository)
                )
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToAddClient = {
                        navController.navigate(Screen.AddClient.route)
                    },
                    onNavigateToClientDetail = { clienteId ->
                        navController.navigate(Screen.ClientDetail.createRoute(clienteId))
                    }
                )
            }

            // Pestaña 2: Reportes y Estadísticas
            composable(Screen.Reports.route) {
                val reportsViewModel: ReportsViewModel = viewModel(
                    factory = ReportsViewModel.Factory(repository)
                )
                ReportsScreen(viewModel = reportsViewModel)
            }

            // Pestaña 3: Configuración y Respaldo de Negocio
            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModel.Factory(repository)
                )
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.Factory(repository)
                )
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onExportCsv = { onReady ->
                        homeViewModel.exportDebtsCsv(onReady)
                    }
                )
            }

            // Pantalla: Agregar cliente
            composable(Screen.AddClient.route) {
                val addClientViewModel: AddEditClientViewModel = viewModel(
                    factory = AddEditClientViewModel.Factory(repository, 0L)
                )
                AddEditClientScreen(
                    viewModel = addClientViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Pantalla: Editar cliente existente
            composable(
                route = Screen.EditClient.route,
                arguments = listOf(
                    navArgument("clienteId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val clienteId = backStackEntry.arguments?.getLong("clienteId") ?: 0L
                val editClientViewModel: AddEditClientViewModel = viewModel(
                    key = "edit_client_$clienteId",
                    factory = AddEditClientViewModel.Factory(repository, clienteId)
                )
                AddEditClientScreen(
                    viewModel = editClientViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Pantalla: Detalle del cliente
            composable(
                route = Screen.ClientDetail.route,
                arguments = listOf(
                    navArgument("clienteId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val clienteId = backStackEntry.arguments?.getLong("clienteId") ?: 0L
                val detailViewModel: ClientDetailViewModel = viewModel(
                    key = "client_detail_$clienteId",
                    factory = ClientDetailViewModel.Factory(repository, clienteId)
                )
                ClientDetailScreen(
                    viewModel = detailViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToEditClient = { id ->
                        navController.navigate(Screen.EditClient.createRoute(id))
                    }
                )
            }
        }
    }
}
