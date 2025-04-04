package com.thesun.drinksapp.ui.splash

import SplashViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.thesun.drinksapp.R
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark

@Composable
fun SplashScreen(
    navController: NavHostController,
    splashViewModel: SplashViewModel = hiltViewModel()
) {
    val navigationState by splashViewModel.navigationState.collectAsState()
    SplashScreenUI()
    LaunchedEffect(navigationState) {
        when (navigationState) {
            SplashNavigationState.GoToLogin -> {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }

            SplashNavigationState.GoToUser -> {
                navController.navigate("role_user") {
                    popUpTo("splash") { inclusive = true }
                }
            }

            SplashNavigationState.GoToAdmin -> {
                navController.navigate("role_admin") {
                    popUpTo("splash") { inclusive = true }
                }
            }

            else -> Unit
        }
    }
}

@Composable
fun SplashScreenUI() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.thumb_splash),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.3f
        )

        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(top = 150.dp)
                .padding(horizontal = 50.dp),
            contentScale = ContentScale.Crop
        )
        LinearProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.7f)
                .padding(bottom = 24.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(16.dp)),
            color = ColorPrimaryDark,
            trackColor = ColorAccent,
        )
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SplashScreenUI()
}
