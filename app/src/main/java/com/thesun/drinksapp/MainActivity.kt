package com.thesun.drinksapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.thesun.drinksapp.ui.theme.DrinksAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrinksAppTheme {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)) {
                    Text(
                        text = "AAAA",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Yellow
                    )
                }
            }
        }
    }
}


