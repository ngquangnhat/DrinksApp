package com.thesun.drinksapp.ui.user

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.thesun.drinksapp.ui.user.history_tab.HistoryTabScreen
import com.thesun.drinksapp.ui.user.home_tab.HomeScreen
import com.thesun.drinksapp.ui.user.profile_tab.ProfileTabScreen

@Composable
fun UserScreen(
    navController: NavHostController,
) {
    UserScreenUI(
//        onGoToCard = { navController.navigate("cart") }
    )
}

@Composable
fun UserScreenUI(
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
        Column(modifier = Modifier.padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.weight(1f),
                enterTransition = {
                    slideInHorizontally(
                        animationSpec = tween(500),
                        initialOffsetX = { it })
                },
                exitTransition = {
                    slideOutHorizontally(
                        animationSpec = tween(500),
                        targetOffsetX = { -it })
                }
            ) {
                composable(BottomNavItem.Home.route) { HomeScreen(navController) }
                composable(BottomNavItem.Category.route) { HistoryTabScreen() }
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