package com.thesun.drinksapp.ui.user

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thesun.drinksapp.R
import com.thesun.drinksapp.components.CartBottomBar
import com.thesun.drinksapp.navigation.BottomNavItem
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.ui.theme.White
import com.thesun.drinksapp.ui.user.history_tab.HistoryScreen
import com.thesun.drinksapp.ui.user.home_tab.HomeScreen
import com.thesun.drinksapp.ui.user.profile_tab.ProfileTabScreen

@Composable
fun UserScreen(
    navController: NavHostController,
) {
    UserScreenUI(
        navMainController = navController,
        onGoToCard = { navController.navigate("cart") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreenUI(
    navMainController: NavHostController,
    onGoToCard: () -> Unit = {},
    ) {

    val context = LocalContext.current
    val backPressedCallback = rememberUpdatedState {
        showConfirmExitApp(context)
    }
    BackHandler(onBack = {
        backPressedCallback.value.invoke()
    })
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination?.route


    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Category,
        BottomNavItem.Profile
    )

    Scaffold(
        topBar = {
            if (currentDestination == BottomNavItem.Category.route ||
                currentDestination == BottomNavItem.Profile.route) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentDestination) {
                                BottomNavItem.Category.route -> "History"
                                BottomNavItem.Profile.route -> "Profile"
                                else -> ""
                            },
                            color = Color(0xFF212121)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = Color(0xFF212121)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        },
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
                                modifier = Modifier.size(26.dp),
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 12.sp,
                            )
                        },
                        alwaysShowLabel = true,
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .background(Color.White)
            .padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.weight(1f),
            ) {
                composable(BottomNavItem.Home.route) { HomeScreen(navMainController) }
                composable(BottomNavItem.Category.route) { HistoryScreen(navMainController) }
                composable(BottomNavItem.Profile.route) { ProfileTabScreen() }
            }
            CartBottomBar(
                onClick = { onGoToCard() }
            )
        }
    }
}

private fun showConfirmExitApp(context: Context) {
    val alertDialog = AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.app_name))
        .setMessage(context.getString(R.string.msg_exit_app))
        .setPositiveButton(context.getString(R.string.action_ok)) { _, _ ->
            (context as? Activity)?.finish()
        }
        .setNegativeButton(context.getString(R.string.action_cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        .setCancelable(false)
        .create()

    alertDialog.show()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun USerScreenPreview() {
    UserScreen(navController = rememberNavController())
}