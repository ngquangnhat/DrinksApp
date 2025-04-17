package com.thesun.drinksapp.ui.payment

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.Order

@Composable
fun PaymentScreen(
    navController: NavController,
    order: Order,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    LaunchedEffect(order) {
        viewModel.processPayment(order)
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is PaymentUiState.Success -> {
                val orderId = (uiState as PaymentUiState.Success).orderId
                navController.navigate("receipt_order/$orderId") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
            is PaymentUiState.Error -> {
            }
            is PaymentUiState.Loading -> {
            }
        }
    }

    PaymentContent(uiState = uiState)
}

@Composable
fun PaymentContent(
    uiState: PaymentUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is PaymentUiState.Loading -> {
                Image(
                    painter = painterResource(R.drawable.ic_loading),
                    contentDescription = "Đang xử lý",
                    modifier = Modifier
                )
            }
            is PaymentUiState.Success -> {
                Text(
                    text = "Thanh toán thành công!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            is PaymentUiState.Error -> {
                Text(
                    text = uiState.message,
                    fontSize = 16.sp,
                    color = Color.Red,
                    modifier = Modifier
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentContentLoadingPreview() {
    MaterialTheme {
        PaymentContent(uiState = PaymentUiState.Loading)
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentContentSuccessPreview() {
    MaterialTheme {
        PaymentContent(uiState = PaymentUiState.Success(orderId = 1))
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentContentErrorPreview() {
    MaterialTheme {
        PaymentContent(uiState = PaymentUiState.Error("Lỗi khi lưu đơn hàng"))
    }
}