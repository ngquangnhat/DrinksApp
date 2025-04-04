package com.thesun.drinksapp.ui.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController

@Composable
fun AdminScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    AdminScreen(modifier = modifier)
}

@Composable
fun AdminScreen(modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Text("admin", modifier = modifier.align(Alignment.Center))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminScreenPreview(){
    AdminScreen(modifier = Modifier)
}
