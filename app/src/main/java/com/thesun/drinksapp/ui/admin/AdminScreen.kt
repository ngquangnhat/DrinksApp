package com.thesun.drinksapp.ui.admin

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thesun.drinksapp.R
import com.thesun.drinksapp.navigation.AdminNavItem
import com.thesun.drinksapp.ui.admin.categories.AdminCategoryScreen
import com.thesun.drinksapp.ui.admin.drinks.AdminDrinkScreen
import com.thesun.drinksapp.ui.admin.settings.AdminSettingsScreen
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.ui.theme.White
import com.thesun.drinksapp.ui.user.history_tab.HistoryScreen
import com.thesun.drinksapp.ui.user.history_tab.StatisticsScreen

@Composable
fun AdminScreen(
    navController: NavHostController
) {
    AdminScreenUI(navMainController = navController)
}

@Composable
fun AdminScreenUI(
    navMainController: NavHostController
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination?.route

    var showExitDialog by remember { mutableStateOf(false) }
    val backPressedCallback = rememberUpdatedState {
        showExitDialog = true
    }
    BackHandler(onBack = {
        backPressedCallback.value.invoke()
    })

    val items = listOf(
        AdminNavItem.Category,
        AdminNavItem.Drink,
        AdminNavItem.Order,
        AdminNavItem.Statistics,
        AdminNavItem.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = White,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            ) {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination == item.route,
                        onClick = {
                            if (currentDestination != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = White,
                            selectedTextColor = ColorPrimaryDark,
                            indicatorColor = ColorPrimaryDark,
                            unselectedIconColor = ColorAccent,
                            unselectedTextColor = ColorAccent
                        ),
                        icon = {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.label,
                                modifier = Modifier.size(26.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 12.sp
                            )
                        },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = AdminNavItem.Category.route,
                modifier = Modifier.weight(1f).background(Color.White)
            ) {
                composable(AdminNavItem.Category.route) { AdminCategoryScreen(navMainController) }
                composable(AdminNavItem.Drink.route) { AdminDrinkScreen(navMainController) }
                composable(AdminNavItem.Order.route) { HistoryScreen(navMainController) }
                composable(AdminNavItem.Statistics.route) { StatisticsScreen(navMainController) }
                composable(AdminNavItem.Settings.route) { AdminSettingsScreen(navMainController) }
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.app_name),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = stringResource(R.string.msg_exit_app))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        (context as? Activity)?.finishAffinity()
                    }
                ) {
                    Text(text = stringResource(R.string.action_ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false }
                ) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
fun SettingsScreen() {
    Text(text = "Settings Screen", modifier = Modifier.padding(16.dp))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminScreenPreview() {
    AdminScreen(navController = rememberNavController())
}