package com.thesun.drinksapp.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.thesun.drinksapp.ui.admin.AdminScreen
import com.thesun.drinksapp.ui.forgot_password.ForgotPasswordScreen
import com.thesun.drinksapp.ui.login.LoginScreen
import com.thesun.drinksapp.ui.register.RegisterScreen
import com.thesun.drinksapp.ui.splash.SplashScreen
import com.thesun.drinksapp.ui.user.UserScreen

@Composable
fun MainNavigation(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        enterTransition = { slideInHorizontally(animationSpec = tween(500), initialOffsetX = { it }) },
        exitTransition = { slideOutHorizontally(animationSpec = tween(500), targetOffsetX = { -it }) }
    ) {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("forgot_password") {
            ForgotPasswordScreen(navController = navController)
        }
        composable("role_admin") {
            AdminScreen(navController = navController)
        }
        composable("role_user") {
            UserScreen(navController = navController)
        }
    }
}