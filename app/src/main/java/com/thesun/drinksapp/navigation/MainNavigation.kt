package com.thesun.drinksapp.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.thesun.drinksapp.data.model.RatingReview
import com.thesun.drinksapp.ui.admin.AdminScreen
import com.thesun.drinksapp.ui.cart.CartScreen
import com.thesun.drinksapp.ui.detail_drink.DrinkDetailScreen
import com.thesun.drinksapp.ui.forgot_password.ForgotPasswordScreen
import com.thesun.drinksapp.ui.login.LoginScreen
import com.thesun.drinksapp.ui.rating_reviews.RatingReviewScreen
import com.thesun.drinksapp.ui.register.RegisterScreen
import com.thesun.drinksapp.ui.select_address.AddressScreen
import com.thesun.drinksapp.ui.select_paymethod.PaymentMethodScreen
import com.thesun.drinksapp.ui.splash.SplashScreen
import com.thesun.drinksapp.ui.user.UserScreen

@Composable
fun MainNavigation(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash",
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
        composable(
            route = "drinkDetail/{drinkId}?index={index}",
            arguments = listOf(
                navArgument("drinkId") { type = NavType.LongType },
                navArgument("index") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val drinkId = backStackEntry.arguments?.getLong("drinkId") ?: 0L
            val indexString = backStackEntry.arguments?.getString("index")
            val index = indexString?.toIntOrNull() ?: -1
            DrinkDetailScreen(drinkId = drinkId, cartItemIndex = index, navController = navController)
        }
        composable(
            route = "rating_reviews/{ratingReviewJson}",
        ) {backStackEntry ->
            val json = backStackEntry.arguments?.getString("ratingReviewJson") ?: ""
            val ratingReview = Gson().fromJson(Uri.decode(json), RatingReview::class.java)
            RatingReviewScreen(
                navController = navController,
                ratingReview = ratingReview
            )
        }
        composable("cart") {
            CartScreen(navController = navController)
        }
        composable(
            route = "payment_method/{initialSelectedId}",
            arguments = listOf(navArgument("initialSelectedId") { type = NavType.IntType })
        ) { backStackEntry ->
            val initialSelectedId = backStackEntry.arguments?.getInt("initialSelectedId") ?: 0
            PaymentMethodScreen(
                navController = navController,
                initialSelectedId = initialSelectedId
            )
        }
        composable(
            route = "address/{initialSelectedId}",
            arguments = listOf(navArgument("initialSelectedId") { type = NavType.LongType })
        ) { backStackEntry ->
            val initialSelectedId = backStackEntry.arguments?.getLong("initialSelectedId") ?: 0L
            AddressScreen(
                navController = navController,
                initialSelectedId = initialSelectedId
            )
        }
    }
}