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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.thesun.drinksapp.data.model.Order
import com.thesun.drinksapp.navigation.MainNavigation
import com.thesun.drinksapp.ui.theme.DrinksAppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val win: Window = window
        val winParams: WindowManager.LayoutParams = win.attributes
        winParams.flags =
            winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        win.attributes = winParams
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            DrinksAppTheme {
                    MainNavigation(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        navController = navController
                    )
            }

        }
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.toString().startsWith("yourapp://payment")) {
                val resultCode = uri.getQueryParameter("resultCode")?.toIntOrNull() ?: -1
                val message = uri.getQueryParameter("message") ?: "Lỗi không xác định"
                val orderId = uri.getQueryParameter("orderId")

                if (resultCode == 0) {
                    Toast.makeText(this, "Thanh toán MoMo thành công", Toast.LENGTH_SHORT).show()
                    val orderJson = Uri.encode(Gson().toJson(Order(id = orderId?.toLong() ?: 0)))
                    val navIntent = Intent(this, MainActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse("yourapp://payment/$orderJson")
                    }
                    startActivity(navIntent)
                } else {
                    Toast.makeText(this, "Thanh toán MoMo thất bại: $message", Toast.LENGTH_SHORT).show()
                    Log.e("MainActivity", "Thanh toán MoMo thất bại: $message")
                }
            }
        }
    }
}


