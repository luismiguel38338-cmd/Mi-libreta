package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
    object AddClient : Screen("add_client")
    object EditClient : Screen("edit_client/{clienteId}") {
        fun createRoute(clienteId: Long) = "edit_client/$clienteId"
    }
    object ClientDetail : Screen("client_detail/{clienteId}") {
        fun createRoute(clienteId: Long) = "client_detail/$clienteId"
    }
}
