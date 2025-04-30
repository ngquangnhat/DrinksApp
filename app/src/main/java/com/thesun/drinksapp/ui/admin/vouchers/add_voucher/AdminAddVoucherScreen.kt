package com.thesun.drinksapp.ui.admin.vouchers.add_voucher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.thesun.drinksapp.R
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.ui.theme.TextColorHeading
import com.thesun.drinksapp.ui.theme.White
import com.thesun.drinksapp.utils.Constant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddVoucherScreen(
    navController: NavController,
    voucherId: Long? = null,
    viewModel: AdminAddVoucherViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(voucherId) {
        viewModel.loadVoucherById(voucherId)
    }

    val voucherDiscount by viewModel.voucherDiscount.collectAsState()
    val voucherMinimum by viewModel.voucherMinimum.collectAsState()
    val isUpdate by viewModel.isUpdate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
            keyboardController?.hide()
        }
    }

    AdminAddVoucherScreenContent(
        navController = navController,
        voucherDiscount = voucherDiscount,
        voucherMinimum = voucherMinimum,
        isUpdate = isUpdate,
        isLoading = isLoading,
        onVoucherDiscountChange = { viewModel.setVoucherDiscount(it) },
        onVoucherMinimumChange = { viewModel.setVoucherMinimum(it) },
        onAddOrEditClick = {
            viewModel.addOrEditVoucher(
                onSuccess = { navController.navigateUp() },
                onAddSuccess = { navController.navigateUp() }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddVoucherScreenContent(
    navController: NavController,
    voucherDiscount: String,
    voucherMinimum: String,
    isUpdate: Boolean,
    isLoading: Boolean,
    onVoucherDiscountChange: (String) -> Unit,
    onVoucherMinimumChange: (String) -> Unit,
    onAddOrEditClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            if (isUpdate) R.string.label_update_voucher
                            else R.string.label_add_voucher
                        ),
                        fontSize = 18.sp,
                        color = Color(0xFF212121)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF212121)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )
        },
        containerColor = White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .background(White)
        ) {
            Divider(
                color = Color(0xFFE0E0E0),
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = stringResource(R.string.label_discount),
                fontSize = 14.sp,
                color = ColorPrimaryDark,
                modifier = Modifier.padding(start = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White, RoundedCornerShape(6.dp))
                    .padding(vertical = 8.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = voucherDiscount,
                    onValueChange = onVoucherDiscountChange,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextColorHeading,
                        unfocusedBorderColor = ColorAccent,
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "%",
                    fontSize = 14.sp,
                    color = ColorPrimaryDark,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            Text(
                text = stringResource(R.string.label_order_minimum),
                fontSize = 14.sp,
                color = ColorPrimaryDark,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .padding(top = 16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White, RoundedCornerShape(6.dp))
                    .padding(vertical = 8.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = voucherMinimum,
                    onValueChange = onVoucherMinimumChange,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextColorHeading,
                        unfocusedBorderColor = ColorAccent,
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = Constant.CURRENCY,
                    fontSize = 14.sp,
                    color = ColorPrimaryDark,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            Button(
                onClick = onAddOrEditClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(top = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPrimaryDark,
                    contentColor = White
                ),
                shape = RoundedCornerShape(30.dp),
                enabled = !isLoading
            ) {
                Text(
                    text = stringResource(
                        if (isUpdate) R.string.action_edit
                        else R.string.action_add
                    ),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminAddVoucherScreenPreview() {
    AdminAddVoucherScreenContent(
        navController = rememberNavController(),
        voucherDiscount = "",
        voucherMinimum = "",
        isUpdate = false,
        isLoading = false,
        onVoucherDiscountChange = {},
        onVoucherMinimumChange = {},
        onAddOrEditClick = {}
    )
}