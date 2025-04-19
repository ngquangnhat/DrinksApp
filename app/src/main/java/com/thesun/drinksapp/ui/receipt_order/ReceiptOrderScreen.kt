package com.thesun.drinksapp.ui.receipt_order

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.Address
import com.thesun.drinksapp.data.model.DrinkOrder
import com.thesun.drinksapp.data.model.Order
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimary
import com.thesun.drinksapp.ui.theme.TextColorHeading
import com.thesun.drinksapp.ui.theme.TextColorSecondary
import com.thesun.drinksapp.utils.Constant
import com.thesun.drinksapp.utils.DateTimeUtils

@Composable
fun ReceiptOrderScreen(
    navController: NavController,
    orderId: Long,
    viewModel: ReceiptOrderViewModel = hiltViewModel()
) {
    val order by viewModel.order.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    LaunchedEffect(orderId) {
        viewModel.loadOrderDetail(orderId)
    }

    ReceiptOrderContent(
        order = order,
        onBackClick = { navController.popBackStack() },
        onTrackOrderClick = {
            navController.popBackStack()
            navController.navigate("tracking_order/$orderId") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptOrderContent(
    order: Order?,
    onBackClick: () -> Unit,
    onTrackOrderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hóa đơn", color = Color(0xFF212121)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Quay lại",
                            tint = Color(0xFF212121)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (order != null && order.status != Order.STATUS_COMPLETE) {
                Button(
                    onClick = onTrackOrderClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Theo dõi đơn hàng", fontSize = 14.sp, color = Color.White)
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        if (order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {
                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = 30.dp),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, ColorAccent),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Column(
                                Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Cảm ơn bạn đã đặt hàng!",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextColorHeading,
                                    modifier = Modifier.padding(top = 30.dp)
                                )
                                Text(
                                    text = "Giao dịch của bạn đã thành công",
                                    fontSize = 14.sp,
                                    color = TextColorSecondary,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Mã giao dịch",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextColorHeading
                                )
                                Text(
                                    order.id.toString(),
                                    fontSize = 14.sp,
                                    color = TextColorSecondary
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Ngày và giờ",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextColorHeading
                                )
                                Text(
                                    DateTimeUtils.convertTimeStampToDate(
                                        order.dateTime?.toLong() ?: 0
                                    ),
                                    fontSize = 14.sp,
                                    color = TextColorSecondary
                                )
                            }
                            Divider(
                                color = ColorAccent,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            Text(
                                "Danh sách món",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextColorHeading
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                if (order.drinks.isNullOrEmpty()) {
                                    Text(
                                        text = "Không có món",
                                        modifier = Modifier.padding(16.dp),
                                        color = TextColorSecondary
                                    )
                                } else {
                                    order.drinks!!.forEach { drink ->
                                        DrinkOrderItem(drink)
                                    }
                                }
                            }
                            Divider(
                                color = ColorAccent,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            Text(
                                "Tóm tắt thanh toán",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextColorHeading
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Giá món",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextColorHeading
                                )
                                Text(
                                    "${order.price}${Constant.CURRENCY}",
                                    fontSize = 14.sp,
                                    color = TextColorSecondary
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Khuyến mại",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextColorHeading
                                )
                                Text(
                                    "-${order.voucher}${Constant.CURRENCY}",
                                    fontSize = 14.sp,
                                    color = TextColorSecondary
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Tổng cộng",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextColorHeading
                                )
                                Text(
                                    "${order.total}${Constant.CURRENCY}",
                                    fontSize = 14.sp,
                                    color = TextColorSecondary
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Phương thức thanh toán",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextColorHeading
                                )
                                Text(
                                    order.paymentMethod ?: "Không xác định",
                                    fontSize = 14.sp,
                                    color = TextColorSecondary
                                )
                            }
                            Divider(
                                color = ColorAccent,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            Text(
                                "Địa chỉ giao hàng",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextColorHeading
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Họ tên",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextColorHeading
                                )
                                Text(
                                    order.address?.name ?: "",
                                    fontSize = 14.sp,
                                    color = TextColorSecondary
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Số điện thoại",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextColorHeading
                                )
                                Text(
                                    order.address?.phone ?: "",
                                    fontSize = 14.sp,
                                    color = TextColorSecondary
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Địa chỉ",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextColorHeading
                                )
                                Text(
                                    order.address?.address ?: "",
                                    fontSize = 14.sp,
                                    color = TextColorSecondary
                                )
                            }
                        }
                    }
                    Image(
                        painter = painterResource(R.drawable.ic_success),
                        contentDescription = "Thành công",
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

@Composable
fun DrinkOrderItem(drink: DrinkOrder) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = drink.image,
            contentDescription = drink.name,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .border(0.5.dp, ColorAccent, CircleShape),
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = drink.name ?: "",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColorHeading,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${drink.price}${Constant.CURRENCY}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColorHeading
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                drink.option?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        color = TextColorSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text(
                    text = "x${drink.count}",
                    fontSize = 12.sp,
                    color = TextColorSecondary
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun ReceiptOrderContentPreview() {
    MaterialTheme {
        ReceiptOrderContent(
            order = Order(
                id = 12345,
                dateTime = System.currentTimeMillis().toString(),
                drinks = listOf(
                    DrinkOrder(
                        name = "Trà sữa",
                        price = 30,
                        count = 2,
                        option = "Ít đường",
                        image = "https://example.com/tra-sua.jpg"
                    ),
                    DrinkOrder(
                        name = "Cà phê",
                        price = 20,
                        count = 1,
                        option = "Đen",
                        image = "https://example.com/ca-phe.jpg"
                    )
                ),
                price = 80,
                voucher = 10,
                total = 70,
                paymentMethod = "Tiền mặt",
                address = Address(
                    name = "Nguyễn Văn A",
                    phone = "0123456789",
                    address = "123 Đường Láng, Hà Nội"
                ),
                status = 0
            ),
            onBackClick = {},
            onTrackOrderClick = {}
        )
    }
}