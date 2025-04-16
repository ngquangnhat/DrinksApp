package com.thesun.drinksapp.ui.select_address

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thesun.drinksapp.R
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimary
import com.thesun.drinksapp.ui.theme.TextColorHeading
import com.thesun.drinksapp.ui.theme.White
import com.thesun.drinksapp.data.model.Address as DrinksAddress

@Composable
fun AddressScreen(
    navController: NavController,
    viewModel: AddressViewModel = hiltViewModel(),
    initialSelectedId: Long = 0
) {
    val addresses by viewModel.addresses.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    AddressContent(
        addresses = addresses,
        onBackClick = { navController.popBackStack() },
        onAddressClick = { address ->
            viewModel.selectAddress(address)
            val result = DrinksAddress(
                id = address.id,
                address = address.address
            )
            navController.previousBackStackEntry?.savedStateHandle?.set("selectedAddress", result)
            navController.popBackStack()
        },
        onAddAddressClick = { name, phone, address ->
            viewModel.addAddress(name, phone, address)
        },
        initialSelectedId = initialSelectedId
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressContent(
    addresses: List<DrinksAddress>,
    onBackClick: () -> Unit,
    onAddressClick: (DrinksAddress) -> Unit,
    onAddAddressClick: (String, String, String) -> Unit,
    initialSelectedId: Long
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Địa chỉ giao hàng",
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ColorPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Thêm địa chỉ",
                    color = White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            item {
                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp,
                )
            }
            items(addresses) { address ->
                AddressItem(
                    address = address,
                    isSelected = address.id == initialSelectedId || address.isSelected,
                    onClick = { onAddressClick(address) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddAddressBottomSheet(
            onDismiss = { showAddDialog = false },
            onAdd = { name, phone, address ->
                onAddAddressClick(name, phone, address)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddressItem(
    address: DrinksAddress,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.White)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        ) {
            address.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            address.phone?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            address.address?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        Image(
            painter = painterResource(
                if (isSelected) R.drawable.ic_item_selected
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AddAddressBottomSheet(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color.White,
        modifier = Modifier.wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Thêm địa chỉ giao hàng",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Họ và tên (*)",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 20.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextColorHeading,
                    unfocusedBorderColor = ColorAccent,
                ),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Next)
            )

            Text(
                text = "Số điện thoại (*)",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 16.dp)
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextColorHeading,
                    unfocusedBorderColor = ColorAccent,
                ),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                ),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = androidx.compose.ui.text.input.ImeAction.Next
                )
            )

            Text(
                text = "Địa chỉ",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 16.dp)
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextColorHeading,
                    unfocusedBorderColor = ColorAccent,
                ),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                ),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 5.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
                ) {
                    Text(
                        text = "Hủy",
                        fontSize = 14.sp,
                        color = Color(0xFF212121),
                    )
                }
                Button(
                    onClick = {
                        if (name.isBlank() || phone.isBlank() || address.isBlank()) {
                            Toast.makeText(
                                context,
                                "Vui lòng nhập đầy đủ thông tin",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            onAdd(name, phone, address)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 5.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
                ) {
                    Text(
                        text = "Thêm",
                        fontSize = 14.sp,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AddressContentPreview() {
    MaterialTheme {
        AddressContent(
            addresses = listOf(
                DrinksAddress(
                    1,
                    "Nguyễn Văn A",
                    "0123456789",
                    "123 Đường Láng, Hà Nội",
                    "user@example.com",
                    true
                ),
                DrinksAddress(
                    2,
                    "Trần Thị B",
                    "0987654321",
                    "456 Cầu Giấy, Hà Nội",
                    "user@example.com",
                    false
                )
            ),
            onBackClick = {},
            onAddressClick = {},
            onAddAddressClick = { _, _, _ -> },
            initialSelectedId = 1
        )
    }
}

@Preview
@Composable
fun AddressItemPreview() {
    MaterialTheme {
        AddressItem(
            address = DrinksAddress(
                1,
                "Nguyễn Văn A",
                "0123456789",
                "123 Đường Láng, Hà Nội",
                "user@example.com",
                true
            ),
            isSelected = true,
            onClick = {}
        )
    }
}