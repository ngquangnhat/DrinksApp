package com.thesun.drinksapp.ui.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController

@Composable
fun UserScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    UserScreen(modifier = modifier)
}

@Composable
fun UserScreen(modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Text("user", modifier = modifier.align(Alignment.Center))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun USerScreenPreview(){
    UserScreen(modifier = Modifier)
}