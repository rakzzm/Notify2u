package com.notify2u.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.notify2u.app.ui.screens.HistoryScreen
import com.notify2u.app.ui.screens.HomeScreen
import com.notify2u.app.ui.screens.NotificationSettingsScreen
import com.notify2u.app.ui.screens.TodoListScreen
import com.notify2u.app.ui.screens.CalendarScreen
import com.notify2u.app.ui.screens.SupportScreen
import com.notify2u.app.ui.screens.LoginScreen
import com.notify2u.app.ui.screens.ProfileScreen
import com.notify2u.app.ui.viewmodel.HomeViewModel
import com.notify2u.app.ui.viewmodel.AuthViewModel
import com.notify2u.app.ui.viewmodel.AuthViewModelFactory
import com.notify2u.app.ui.viewmodel.HomeViewModel
import com.notify2u.app.ui.viewmodel.TodoViewModel
import com.notify2u.app.ui.viewmodel.TodoViewModelFactory
import com.notify2u.app.data.local.Notify2uDatabase
import com.notify2u.app.data.repository.FirestoreRepository
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    viewModel: HomeViewModel,
    authViewModel: AuthViewModel,
    firestoreRepository: FirestoreRepository,
    navController: NavHostController,
    initialTaskTitle: String = "",
    openAddSheet: Boolean = false
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val navItems = listOf(
        Pair("Home", Icons.Default.Home),
        Pair("To-Do", Icons.Default.List),
        Pair("Calendar", Icons.Default.DateRange),
        Pair("History", Icons.Default.Refresh),
        Pair("Profile", Icons.Default.Person),
        Pair("Support", Icons.Default.Info)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                navItems.forEach { item ->
                    val label = item.first
                    val icon = item.second
                    val route = when (label) {
                        "Home" -> "home/ALL"
                        "To-Do" -> "todo_list"
                        "Calendar" -> "calendar"
                        "History" -> "history"
                        "Profile" -> "profile"
                        "Support" -> "support"
                        else -> "home/ALL"
                    }
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                        selected = currentRoute?.startsWith(route.split("/")[0]) ?: false,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = if (authViewModel.isLoggedIn.value) "home/ALL" else "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate("home/ALL") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("home/{filterType}") { backStackEntry ->
                val filterType = backStackEntry.arguments?.getString("filterType") ?: "ALL"
                HomeScreen(
                    viewModel = viewModel,
                    navController = navController,
                    filterType = filterType,
                    openAddSheet = openAddSheet,
                    initialName = initialTaskTitle
                )
            }

            composable(Screen.History.route) {
                com.notify2u.app.ui.screens.HistoryScreen(
                    viewModel = viewModel,
                    navController = navController
                )
            }

            composable("notification_settings") {
                com.notify2u.app.ui.screens.NotificationSettingsScreen(
                    navController = navController
                )
            }

            composable("todo_list") {
                val todoDao = Notify2uDatabase.getInstance(LocalContext.current).todoDao()
                val todoViewModel: TodoViewModel = viewModel(
                    factory = TodoViewModelFactory(todoDao, firestoreRepository)
                )
                TodoListScreen(
                    viewModel = todoViewModel
                )
            }

            composable("calendar") {
                val todoDao = com.notify2u.app.data.local.Notify2uDatabase.getInstance(androidx.compose.ui.platform.LocalContext.current).todoDao()
                val todoViewModel: com.notify2u.app.ui.viewmodel.TodoViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = com.notify2u.app.ui.viewmodel.TodoViewModelFactory(todoDao, firestoreRepository)
                )
                com.notify2u.app.ui.screens.CalendarScreen(
                    homeViewModel = viewModel,
                    todoViewModel = todoViewModel,
                    navController = navController
                )
            }

            composable("support") {
                com.notify2u.app.ui.screens.SupportScreen(
                    navController = navController
                )
            }

            composable("profile") {
                ProfileScreen(
                    viewModel = authViewModel,
                    navController = navController
                )
            }
        }
    }
}
