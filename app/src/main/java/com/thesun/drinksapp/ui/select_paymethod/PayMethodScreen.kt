package com.thesun.drinksapp.ui.select_paymethod

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.PaymentMethod
import com.thesun.drinksapp.utils.Constant

@Composable
fun PaymentMethodScreen(
    navController: NavController,
    viewModel: PaymentMethodViewModel = hiltViewModel(),
    initialSelectedId: Int = 0
) {
    viewModel.setInitialSelectedPaymentMethod(initialSelectedId)
    val paymentMethods by viewModel.paymentMethods.collectAsState()

    PaymentMethodContent(
        paymentMethods = paymentMethods,
        onBackClick = {navController.popBackStack()},
        onPaymentMethodClick = { paymentMethod ->
            viewModel.selectPaymentMethod(paymentMethod)
            val result = PaymentMethod(
                id = paymentMethod.id,
                name = paymentMethod.name
            )
            navController.previousBackStackEntry?.savedStateHandle?.set("selectedPaymentMethod", result)
            navController.popBackStack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodContent(
    paymentMethods: List<PaymentMethod>,
    onBackClick: () -> Unit,
    onPaymentMethodClick: (PaymentMethod) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Giỏ hàng",
                        fontSize = 18.sp,
                        color = Color(0xFF212121)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color(0xFF212121)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            item {
                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp,
                )
            }
            items(paymentMethods) { paymentMethod ->
                PaymentMethodItem(
                    paymentMethod = paymentMethod,
                    onClick = { onPaymentMethodClick(paymentMethod) }
                )
            }
        }
    }
}

@Composable
fun PaymentMethodItem(
    paymentMethod: PaymentMethod,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    when (paymentMethod.id) {
                        Constant.TYPE_GOPAY -> R.drawable.ic_gopay
                        Constant.TYPE_CREDIT -> R.drawable.ic_credit
                        Constant.TYPE_BANK -> R.drawable.ic_bank
                        Constant.TYPE_ZALO_PAY -> R.drawable.ic_zalopay
                        else -> R.drawable.ic_gopay
                    }
                ),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                paymentMethod.name?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                paymentMethod.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Status icon
            Image(
                painter = painterResource(
                    if (paymentMethod.isSelected) R.drawable.ic_item_selected
                    else R.drawable.ic_item_unselect
                ),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Preview
@Composable
fun PaymentMethodContentPreview() {
    MaterialTheme {
        PaymentMethodContent(
            paymentMethods = listOf(
                PaymentMethod(1, "Thanh toán tiền mặt", "(Thanh toán khi nhận hàng)", true),
                PaymentMethod(2, "Credit or debit card", "(Thẻ Visa hoặc Mastercard)", false),
                PaymentMethod(3, "Chuyển khoản ngân hàng", "(Tự động xác nhận)", false),
                PaymentMethod(4, "ZaloPay", "(Tự động xác nhận)", false)
            ),
            onBackClick = {},
            onPaymentMethodClick = {}
        )
    }
}

@Preview
@Composable
fun PaymentMethodItemPreview() {
    MaterialTheme {
        PaymentMethodItem(
            paymentMethod = PaymentMethod(
                id = 1,
                name = "Thanh toán tiền mặt",
                description = "(Thanh toán khi nhận hàng)",
                isSelected = true
            ),
            onClick = {}
        )
    }
}