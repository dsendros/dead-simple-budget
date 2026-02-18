package com.dsb.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dsb.data.repository.BudgetRepository
import com.dsb.ui.chart.ChartScreen
import com.dsb.ui.goals.GoalsScreen
import com.dsb.ui.history.HistoryScreen
import com.dsb.ui.home.HomeScreen
import com.dsb.ui.settings.SettingsScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object History : Screen("history", "History", Icons.Default.List)
    data object Chart : Screen("chart", "Chart", Icons.Default.BarChart)
    data object Goals : Screen("goals", "Goals", Icons.Default.Star)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val bottomNavScreens = listOf(Screen.Home, Screen.History, Screen.Chart, Screen.Goals, Screen.Settings)

@Composable
fun MainNavigation(repository: BudgetRepository, showAddExpenseOnLaunch: Boolean = false) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
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
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(repository, showAddExpenseOnLaunch) }
            composable(Screen.History.route) { HistoryScreen(repository) }
            composable(Screen.Chart.route) { ChartScreen(repository) }
            composable(Screen.Goals.route) { GoalsScreen(repository) }
            composable(Screen.Settings.route) { SettingsScreen(repository) }
        }
    }
}
