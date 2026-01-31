package com.notify2u.app.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object History : Screen("history")
    object Login : Screen("login")
    object Profile : Screen("profile")
}
