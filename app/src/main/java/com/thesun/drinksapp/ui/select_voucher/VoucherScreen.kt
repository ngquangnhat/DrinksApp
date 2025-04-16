package com.thesun.drinksapp.ui.select_voucher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.Voucher

@Composable
fun VoucherScreen(
    navController: NavController,
    viewModel: VoucherViewModel = hiltViewModel(),
    initialSelectedId: Long = 0,
    amount: Int = 0
) {
    val vouchers by viewModel.vouchers.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setInitialSelectedVoucher(initialSelectedId)
    }

    VoucherContent(
        vouchers = vouchers,
        amount = amount,
        onBackClick = { navController.popBackStack() },
        onVoucherClick = { voucher ->
            viewModel.selectVoucher(voucher)
            val result = Voucher(
                id = voucher.id,
                discount = voucher.discount,
                minimum = voucher.minimum,
                isSelected = true
            )
            navController.previousBackStackEntry?.savedStateHandle?.set("selectedVoucher", result)
            navController.popBackStack()
        },
        initialSelectedId = initialSelectedId
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherContent(
    vouchers: List<Voucher>,
    amount: Int,
    onBackClick: () -> Unit,
    onVoucherClick: (Voucher) -> Unit,
    initialSelectedId: Long
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Khuyến mại",
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
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Divider(
                color = Color(0xFFE0E0E0),
                thickness = 1.dp,
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                items(vouchers) { voucher ->
                    VoucherItem(
                        voucher = voucher,
                        amount = amount,
                        isSelected = voucher.id == initialSelectedId || voucher.isSelected,
                        onClick = { onVoucherClick(voucher) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    OutlinedTextField(
        value = query,
        onValueChange = { query = it; onSearch(it) },
        modifier = modifier,
        placeholder = { Text("Nhập mã khuyến mại", fontSize = 14.sp) },
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
fun VoucherItem(
    voucher: Voucher,
    amount: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = voucher.isVoucherEnable(amount)) { onClick() }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_sale),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.Unspecified
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = voucher.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (voucher.isVoucherEnable(amount)) Color(0xFF212121) else Color(
                        0xFFB0B0B0
                    )
                )
                Text(
                    text = voucher.minimumText,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = if (voucher.isVoucherEnable(amount)) Color(0xFF757575) else Color(
                        0xFFB0B0B0
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )
                if (voucher.getCondition(amount).isNotEmpty()) {
                    Text(
                        text = voucher.getCondition(amount),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            if (voucher.isVoucherEnable(amount)) {
                Icon(
                    painter = painterResource(
                        if (isSelected) R.drawable.ic_item_selected else R.drawable.ic_item_unselect
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            thickness = 1.dp,
            color = Color(0xFFE0E0E0)
        )
    }
}

@Preview
@Composable
fun VoucherContentPreview() {
    MaterialTheme {
        VoucherContent(
            vouchers = listOf(
                Voucher(
                    id = 1,
                    discount = 10,
                    minimum = 50,
                    isSelected = true
                ),
                Voucher(
                    id = 2,
                    discount = 20,
                    minimum = 100,
                    isSelected = false
                )
            ),
            amount = 60,
            onBackClick = {},
            onVoucherClick = {},
            initialSelectedId = 1
        )
    }
}

@Preview
@Composable
fun VoucherItemPreview() {
    MaterialTheme {
        VoucherItem(
            voucher = Voucher(
                id = 1,
                discount = 10,
                minimum = 50,
                isSelected = true
            ),
            amount = 60,
            isSelected = true,
            onClick = {}
        )
    }
}