package com.thesun.drinksapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.thesun.drinksapp.data.model.Order
import com.thesun.drinksapp.navigation.MainNavigation
import com.thesun.drinksapp.prefs.MySharedPreferences
import com.thesun.drinksapp.ui.cart.CartViewModel
import com.thesun.drinksapp.ui.theme.DrinksAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        setContent {
            navController = rememberNavController()
            DrinksAppTheme {
                MainNavigation(
                    modifier = Modifier.fillMaxSize().background(Color.White),
                    navController = navController
                )
                LaunchedEffect(navController) { handleDeepLink(intent) }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setContent {
            DrinksAppTheme {
                MainNavigation(
                    modifier = Modifier.fillMaxSize().background(Color.White),
                    navController = navController
                )
                LaunchedEffect(navController) { handleDeepLink(intent) }
            }
        }
    }

    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.toString().startsWith("yourapp://payment")) {
                val resultCode = uri.getQueryParameter("resultCode")?.toIntOrNull() ?: -1
                val message = uri.getQueryParameter("message") ?: "Lỗi không xác định"
                val orderId = uri.getQueryParameter("orderId")?.toLongOrNull()
                if (resultCode == 0 && orderId != null) {
                    Toast.makeText(this, "Thanh toán MoMo thành công", Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        val sharedPreferences = MySharedPreferences(this@MainActivity)
                        val gson = Gson()
                        val orderJson = sharedPreferences.getStringValue("saved_orders")
                        val order = if (orderJson.isNullOrEmpty()) null else gson.fromJson(orderJson, Order::class.java)
                        if (order != null && order.id == orderId) {
                            val orderJsonEncoded = Uri.encode(gson.toJson(order))
                            navController.navigate("payment/$orderJsonEncoded")
                        } else {
                            Toast.makeText(this@MainActivity, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Thanh toán MoMo thất bại: $message", Toast.LENGTH_SHORT).show()
                    Log.e("MainActivity", "Thanh toán MoMo thất bại: resultCode=$resultCode, message=$message")
                }
            }
        }
    }
}


