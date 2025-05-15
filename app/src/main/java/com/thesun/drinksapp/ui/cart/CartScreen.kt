package com.thesun.drinksapp.ui.cart

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.Address
import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.data.model.Order
import com.thesun.drinksapp.data.model.PaymentMethod
import com.thesun.drinksapp.data.model.Voucher
import com.thesun.drinksapp.data.remote.ApiInterface
import com.thesun.drinksapp.ui.theme.BgFilter
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimary
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.ui.theme.TextColorHeading
import com.thesun.drinksapp.utils.Constant
import com.thesun.drinksapp.utils.Constant.TYPE_CREDIT
import com.thesun.drinksapp.utils.Constant.TYPE_MOMO
import com.thesun.drinksapp.utils.Utils.PUBLIC_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems
    val paymentMethod by viewModel.paymentMethod
    val address by viewModel.address
    val voucher by viewModel.voucher
    val totalPrice by viewModel.totalPrice
    val itemCount by viewModel.itemCount
    val toastMessage by viewModel.toastMessage.collectAsState()
    val paymentUrl by viewModel.paymentUrl.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val apiInterface = viewModel.apiInterface

    LaunchedEffect(Unit) {
        try {
            PaymentConfiguration.init(context, PUBLIC_KEY)
            Log.d("CartScreen", "Stripe initialized successfully with pk_test")
        } catch (e: Exception) {
            Log.e("CartScreen", "Stripe initialization failed: ${e.message}")
            Toast.makeText(context, "Lỗi khởi tạo Stripe: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    val paymentSheet = rememberPaymentSheet { result ->
        when (result) {
            is PaymentSheetResult.Completed -> {
                Toast.makeText(context, "Thanh toán Visa/Mastercard thành công", Toast.LENGTH_SHORT).show()
                val order = viewModel.checkout()
                if (order != null) {
                    navController.popBackStack()
                    navController.navigate("payment/${Uri.encode(Gson().toJson(order))}")
                }
            }
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(context, "Thanh toán bị hủy", Toast.LENGTH_SHORT).show()
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(context, "Thanh toán thất bại: ${result.error.message}", Toast.LENGTH_SHORT).show()
                Log.e("CartScreen", "Payment failed: ${result.error.message}")
            }
        }
    }

    LaunchedEffect(paymentUrl) {
        paymentUrl?.let { url ->
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    LaunchedEffect(Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<PaymentMethod?>("selectedPaymentMethod", null)
            ?.collect { selected ->
                selected?.let { viewModel.updatePaymentMethod(it) }
            }
    }
    LaunchedEffect(Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Address?>("selectedAddress", null)
            ?.collect { selected ->
                selected?.let { viewModel.updateAddress(it) }
            }
    }
    LaunchedEffect(Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Voucher?>("selectedVoucher", null)
            ?.collect { selected ->
                viewModel.updateVoucher(selected)
            }
    }

    CartContent(
        cartItems = cartItems,
        paymentMethod = paymentMethod,
        address = address,
        voucher = voucher,
        totalPrice = totalPrice,
        itemCount = itemCount,
        onAddOrderClick = { navController.navigate("role_user") },
        onPaymentMethodClick = {
            val selectedId = paymentMethod?.id ?: 0
            navController.navigate("payment_method/$selectedId")
        },
        onAddressClick = {
            val selectedId = address?.id ?: 0L
            navController.navigate("address/$selectedId")
        },
        onVoucherClick = {
            val selectedId = voucher?.id ?: 0L
            val amount = totalPrice
            navController.navigate("voucher/$selectedId/$amount")
        },
        onCheckoutClick = {
            val order = viewModel.checkout()
            if (order != null) {
                when (paymentMethod?.id) {
                    TYPE_MOMO -> viewModel.initiateMoMoPayment(order)
                    TYPE_CREDIT -> coroutineScope.launch {
                        initiateStripePayment(order, paymentSheet, apiInterface, context)
                    }
                    else -> {
                        val orderJson = Uri.encode(Gson().toJson(order))
                        navController.popBackStack()
                        navController.navigate("payment/$orderJson")
                    }
                }
            } else {
                Log.e("CartScreen", "Checkout failed: Order is null")
            }
        },
        onDeleteItem = { drink, position -> viewModel.deleteCartItem(drink, position) },
        onUpdateItem = { drink, position -> viewModel.updateCartItem(drink, position) },
        onEditItem = { drink, index ->
            navController.navigate("drinkDetail/${drink.id}?index=$index")
        },
        onBackClick = { navController.popBackStack() },
    )
}

private suspend fun initiateStripePayment(
    order: Order,
    paymentSheet: PaymentSheet,
    apiInterface: ApiInterface,
    context: android.content.Context
) {
    Log.d("CartScreen", "Initiating Stripe payment for amount: ${order.total * 1000} VND")
    val paymentData = createPaymentIntent(order.total, apiInterface)
    if (paymentData != null) {
        val (clientSecret, ephemeralKey, customerId) = paymentData
        Log.d("CartScreen", "PaymentIntent created with client_secret: $clientSecret, ephemeralKey: $ephemeralKey, customerId: $customerId")
        paymentSheet.presentWithPaymentIntent(
            clientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "DrinksApp",
                customer = PaymentSheet.CustomerConfiguration(
                    id = customerId,
                    ephemeralKeySecret = ephemeralKey
                ),
                allowsDelayedPaymentMethods = true
            )
        )
    } else {
        Toast.makeText(context, "Không thể khởi tạo thanh toán Stripe", Toast.LENGTH_SHORT).show()
        Log.e("CartScreen", "Failed to create PaymentIntent")
    }
}

private suspend fun createPaymentIntent(amount: Int, apiInterface: ApiInterface): Triple<String, String, String>? = withContext(Dispatchers.IO) {
    try {
        val customerResponse = apiInterface.getCustomer()
        if (!customerResponse.isSuccessful || customerResponse.body()?.id == null) {
            Log.e("CartScreen", "Failed to get customer: ${customerResponse.code()}, ${customerResponse.errorBody()?.string()}")
            return@withContext null
        }
        val customerId = customerResponse.body()!!.id
        Log.d("CartScreen", "Customer ID: $customerId")

        val ephemeralKeyResponse = apiInterface.getEphemeralKey(customerId)
        if (!ephemeralKeyResponse.isSuccessful || ephemeralKeyResponse.body()?.id == null) {
            Log.e("CartScreen", "Failed to get ephemeral key: ${ephemeralKeyResponse.code()}, ${ephemeralKeyResponse.errorBody()?.string()}")
            return@withContext null
        }
        val ephemeralKey = ephemeralKeyResponse.body()!!.secret
        Log.d("CartScreen", "Ephemeral Key: $ephemeralKey")

        val paymentIntentResponse = apiInterface.getPaymentIntent(
            customer = customerId,
            amount = (amount * 1000).toString(),
            currency = "vnd",
            automaticPay = true
        )
        if (!paymentIntentResponse.isSuccessful || paymentIntentResponse.body()?.client_secret == null) {
            Log.e("CartScreen", "Failed to create PaymentIntent: ${paymentIntentResponse.code()}, ${paymentIntentResponse.errorBody()?.string()}")
            return@withContext null
        }
        val clientSecret = paymentIntentResponse.body()!!.client_secret
        Log.d("CartScreen", "PaymentIntent client_secret: $clientSecret")

        Triple(clientSecret, ephemeralKey, customerId)
    } catch (e: Exception) {
        Log.e("CartScreen", "Exception in createPaymentIntent: ${e.message}")
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartContent(
    cartItems: List<Drink>,
    paymentMethod: PaymentMethod?,
    address: Address?,
    voucher: Voucher?,
    totalPrice: Int,
    itemCount: Int,
    onBackClick: () -> Unit,
    onAddOrderClick: () -> Unit,
    onPaymentMethodClick: () -> Unit,
    onAddressClick: () -> Unit,
    onVoucherClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    onDeleteItem: (Drink, Int) -> Unit,
    onUpdateItem: (Drink, Int) -> Unit,
    onEditItem: (Drink, Int) -> Unit,
    modifier: Modifier = Modifier
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
        bottomBar = {
            BottomBar(
                totalPrice = totalPrice,
                onCheckoutClick = onCheckoutClick
            )
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            CartItemsSection(
                cartItems = cartItems,
                onDeleteItem = onDeleteItem,
                onUpdateItem = onUpdateItem,
                onEditItem = onEditItem
            )
            AddOrderSection(onAddOrderClick = onAddOrderClick)
            Divider(
                color = BgFilter,
                thickness = 6.dp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                InfoSection(
                    title = "Phương thức thanh toán",
                    value = paymentMethod?.name ?: "Chưa chọn phương thức thanh toán",
                    onClick = onPaymentMethodClick
                )
                InfoSection(
                    title = "Địa chỉ",
                    value = address?.address ?: "Chưa chọn địa chỉ giao hàng",
                    onClick = onAddressClick
                )
                InfoSection(
                    title = "Voucher",
                    value = voucher?.title ?: "Chưa áp dụng mã khuyến mại",
                    onClick = onVoucherClick
                )
            }
            Divider(
                color = BgFilter,
                thickness = 6.dp,
                modifier = Modifier.padding(top = 20.dp, bottom = 16.dp)
            )
            SummarySection(
                itemCount = itemCount,
                drinkPrice = cartItems.sumOf { it.totalPrice },
                voucher = voucher
            )
        }
    }
}

@Composable
private fun CartItemsSection(
    cartItems: List<Drink>,
    onDeleteItem: (Drink, Int) -> Unit,
    onUpdateItem: (Drink, Int) -> Unit,
    onEditItem: (Drink, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp,
        )
        Spacer(modifier = Modifier.height(12.dp))
        cartItems.forEachIndexed { index, drink ->
            CartItem(
                drink = drink,
                onDelete = { onDeleteItem(drink, index) },
                onUpdate = { updatedDrink -> onUpdateItem(updatedDrink, index) },
                onEdit = { onEditItem(drink, index) }
            )
        }
    }
}

@Composable
private fun CartItem(
    drink: Drink,
    onDelete: () -> Unit,
    onUpdate: (Drink) -> Unit,
    onEdit: () -> Unit
) {
    var count by remember { mutableIntStateOf(drink.count) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = drink.image,
                contentDescription = drink.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.image_drink_example),
                error = painterResource(R.drawable.image_drink_example)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    drink.name?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextColorHeading,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        text = (drink.priceOneDrink.toString() + Constant.CURRENCY),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColorHeading
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    drink.option?.let {
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            color = Color(0xFF757575),
                            maxLines = 2,
                            lineHeight = 14.sp,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(260.dp)
                        )
                    }
                    Text(
                        text = "x${drink.count}",
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = "Chỉnh sửa món",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete),
                                contentDescription = "Xóa món",
                                tint = Color.Red,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(
                        modifier = Modifier
                            .height(24.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(3.dp)
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .border(
                                        1.dp, ColorAccent, RoundedCornerShape(
                                            topStart = 4.dp,
                                            bottomStart = 4.dp
                                        )
                                    )
                                    .clickable {
                                        if (count > 1) {
                                            count--
                                            val updatedDrink = drink.copy(
                                                count = count,
                                                totalPrice = drink.priceOneDrink * count
                                            )
                                            onUpdate(updatedDrink)
                                        }
                                    }
                            ) {
                                Text(
                                    text = "-",
                                    modifier = Modifier.align(Alignment.Center),
                                    fontSize = 18.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .offset(x = (-1).dp)
                                    .height(24.dp)
                                    .border(1.dp, ColorAccent, RoundedCornerShape(0.dp))
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = "$count",
                                    modifier = Modifier.align(Alignment.Center),
                                    fontSize = 14.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .offset(x = (-2).dp)
                                    .border(
                                        1.dp, ColorAccent, RoundedCornerShape(
                                            topEnd = 4.dp,
                                            bottomEnd = 4.dp
                                        )
                                    )
                                    .clickable {
                                        count++
                                        val updatedDrink = drink.copy(
                                            count = count,
                                            totalPrice = drink.priceOneDrink * count
                                        )
                                        onUpdate(updatedDrink)
                                    }
                            ) {
                                Text(
                                    text = "+",
                                    modifier = Modifier.align(Alignment.Center),
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun AddOrderSection(
    onAddOrderClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAddOrderClick() }
            .padding(top = 2.dp, start = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color(0xFFB0B0B0)
        )
        Text(
            text = "Tiếp tục thêm vào giỏ hàng",
            fontSize = 14.sp,
            color = ColorPrimaryDark,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun InfoSection(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColorHeading,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = value,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    color = Color(0xFF757575),
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color(0xFFB0B0B0)
            )
        }
        Divider(
            color = ColorAccent,
            thickness = 1.dp,
        )
    }
}

@Composable
private fun SummarySection(
    itemCount: Int,
    drinkPrice: Int,
    voucher: Voucher? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = "Thanh toán",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 16.sp,
            color = TextColorHeading
        )
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Giá món",
                    fontSize = 14.sp,
                    color = TextColorHeading,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "($itemCount món)",
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = Color(0xFF757575),
                )
            }
            Text(
                text = drinkPrice.toString() + Constant.CURRENCY,
                fontSize = 14.sp,
                color = Color(0xFF757575)
            )
        }
        Divider(
            color = ColorAccent,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Khuyến mại",
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    color = TextColorHeading,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = voucher?.title ?: "Chưa áp dụng mã khuyến mại",
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    color = Color(0xFF757575),
                )
            }
            if (voucher != null) {
                Text(
                    text = "-" + voucher.getPriceDiscount(drinkPrice).toString() + Constant.CURRENCY,
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}

@Composable
private fun BottomBar(
    totalPrice: Int,
    onCheckoutClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgFilter)
            .padding(16.dp)
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Tổng cộng",
                fontSize = 14.sp,
                color = TextColorHeading
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = totalPrice.toString() + Constant.CURRENCY,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimaryDark
            )
        }
        Button(
            onClick = onCheckoutClick,
            colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                text = "Thanh toán",
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun CartContentPreview() {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        CartContent(
            cartItems = listOf(
                Drink(
                    id = 1,
                    name = "Trà sữa",
                    priceOneDrink = 30000,
                    count = 2,
                    totalPrice = 60000,
                    option = "Ít đường",
                    image = "https://example.com/tra-sua.jpg"
                ),
                Drink(
                    id = 2,
                    name = "Cà phê",
                    priceOneDrink = 20000,
                    count = 1,
                    totalPrice = 20000,
                    option = "Đen",
                    image = "https://example.com/ca-phe.jpg"
                )
            ),
            paymentMethod = PaymentMethod(id = 1, name = "Tiền mặt"),
            address = Address(id = 1, address = "123 Đường Láng, Hà Nội"),
            voucher = Voucher(id = 1, discount = 10),
            totalPrice = 70000,
            itemCount = 2,
            onBackClick = {},
            onAddOrderClick = {},
            onPaymentMethodClick = {},
            onAddressClick = {},
            onVoucherClick = {},
            onCheckoutClick = {},
            onDeleteItem = { _, _ -> },
            onUpdateItem = { _, _ -> },
            onEditItem = { _, _ -> }
        )
    }
}