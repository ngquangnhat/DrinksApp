package com.thesun.drinksapp

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
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
                Scaffold { innerPadding ->
                    MainNavigation(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .background(Color.White),
                        navController = navController
                    )
                }
            }

        }
    }
}


