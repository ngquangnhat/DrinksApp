package com.thesun.drinksapp.navigation

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.thesun.drinksapp.data.model.Order
import com.thesun.drinksapp.data.model.RatingReview
import com.thesun.drinksapp.ui.admin.AdminScreen
import com.thesun.drinksapp.ui.admin.categories.add_category.AddCategoryScreen
import com.thesun.drinksapp.ui.admin.drinks.add_drink.AdminAddDrinkScreen
import com.thesun.drinksapp.ui.admin.feedbacks.AdminFeedbackScreen
import com.thesun.drinksapp.ui.admin.toppings.AdminToppingScreen
import com.thesun.drinksapp.ui.admin.toppings.add_topping.AddToppingScreen
import com.thesun.drinksapp.ui.admin.vouchers.AdminVoucherScreen
import com.thesun.drinksapp.ui.admin.vouchers.add_voucher.AdminAddVoucherScreen
import com.thesun.drinksapp.ui.cart.CartScreen
import com.thesun.drinksapp.ui.change_password.ChangePasswordScreen
import com.thesun.drinksapp.ui.contact.ContactScreen
import com.thesun.drinksapp.ui.detail_drink.DrinkDetailScreen
import com.thesun.drinksapp.ui.feedback.FeedbackScreen
import com.thesun.drinksapp.ui.forgot_password.ForgotPasswordScreen
import com.thesun.drinksapp.ui.login.LoginScreen
import com.thesun.drinksapp.ui.login.LoginViewModel
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
            DrinkDetailScreen(
                drinkId = drinkId,
                cartItemIndex = index,
                navController = navController
            )
        }
        composable(
            route = "rating_reviews/{ratingReviewJson}",
        ) { backStackEntry ->
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
        ) {
            val orderId = it.arguments?.getLong("orderId") ?: 0L
            TrackingOrderScreen(
                navController = navController,
                orderId = orderId
            )
        }
        composable(route = "feedback") {
            FeedbackScreen(navController = navController)
        }
        composable(route = "contact") {
            ContactScreen(navController = navController)
        }
        composable(route = "change_password") {
            ChangePasswordScreen(navController = navController)
        }
        composable("add_category") {
            AddCategoryScreen(navController)
        }
        composable("edit_category/{id}") { backStackEntry ->
            AddCategoryScreen(
                navController = navController,
                categoryId = backStackEntry.arguments?.getString("id")
            )
        }
        composable("add_drink") {
            AdminAddDrinkScreen(navController)
        }
        composable("edit_drink/{id}") { backStackEntry ->
            AdminAddDrinkScreen(
                navController = navController,
                drinkId = backStackEntry.arguments?.getString("id")
            )
        }
        composable("manage_topping") {
            AdminToppingScreen(navController = navController)
        }
        composable("add_topping") {
            AddToppingScreen(navController)
        }
        composable(
            "edit_topping/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            AddToppingScreen(
                navController = navController,
                toppingId = backStackEntry.arguments?.getLong("id")
            )
        }
        composable("manage_voucher") {
            AdminVoucherScreen(navController = navController)
        }
        composable("add_voucher") {
            AdminAddVoucherScreen(navController)
        }
        composable(
            "edit_voucher/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            AdminAddVoucherScreen(
                navController = navController,
                voucherId = backStackEntry.arguments?.getLong("id")
            )
        }
        composable("manage_feedback") {
            AdminFeedbackScreen(navController = navController)
        }
    }
}