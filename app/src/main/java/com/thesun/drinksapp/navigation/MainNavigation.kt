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
import com.thesun.drinksapp.data.model.Order
import com.thesun.drinksapp.data.model.RatingReview
import com.thesun.drinksapp.ui.admin.AdminScreen
import com.thesun.drinksapp.ui.cart.CartScreen
import com.thesun.drinksapp.ui.detail_drink.DrinkDetailScreen
import com.thesun.drinksapp.ui.forgot_password.ForgotPasswordScreen
import com.thesun.drinksapp.ui.login.LoginScreen
import com.thesun.drinksapp.ui.payment.PaymentScreen
import com.thesun.drinksapp.ui.rating_reviews.RatingReviewScreen
import com.thesun.drinksapp.ui.receipt_order.ReceiptOrderScreen
import com.thesun.drinksapp.ui.register.RegisterScreen
import com.thesun.drinksapp.ui.select_address.AddressScreen
import com.thesun.drinksapp.ui.select_paymethod.PaymentMethodScreen
import com.thesun.drinksapp.ui.select_voucher.VoucherScreen
import com.thesun.drinksapp.ui.splash.SplashScreen
import com.thesun.drinksapp.ui.tracking_order.TrackingOrderScreen
import com.thesun.drinksapp.ui.user.UserScreen

@Composable
fun MainNavigation(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
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
        composable(
            route = "voucher/{voucherId}/{amount}",
            arguments = listOf(
                navArgument("voucherId") { type = NavType.LongType; defaultValue = 0L },
                navArgument("amount") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { backStackEntry ->
            val voucherId = backStackEntry.arguments?.getLong("voucherId") ?: 0L
            val amount = backStackEntry.arguments?.getInt("amount") ?: 0
            VoucherScreen(
                navController = navController,
                initialSelectedId = voucherId,
                amount = amount
            )
        }
        composable(
            route = "payment/{orderJson}",
            arguments = listOf(navArgument("orderJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("orderJson") ?: ""
            val order = Gson().fromJson(Uri.decode(json), Order::class.java)
            PaymentScreen(
                navController = navController,
                order = order
            )
        }
        composable(
            route = "receipt_order/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L
            ReceiptOrderScreen(
                navController = navController,
                orderId = orderId
            )
        }
        composable(
            route = "tracking_order/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ){
            val orderId = it.arguments?.getLong("orderId") ?: 0L
            TrackingOrderScreen(
                navController = navController,
                orderId = orderId
            )
        }
    }
}