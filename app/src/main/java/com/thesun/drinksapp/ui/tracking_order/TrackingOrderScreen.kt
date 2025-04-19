package com.thesun.drinksapp.ui.tracking_order

import android.graphics.fonts.FontStyle
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.Address
import com.thesun.drinksapp.data.model.DrinkOrder
import com.thesun.drinksapp.data.model.Order
import com.thesun.drinksapp.data.model.RatingReview
import com.thesun.drinksapp.ui.receipt_order.DrinkOrderItem
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimary
import com.thesun.drinksapp.ui.theme.TextColorHeading
import com.thesun.drinksapp.ui.theme.TextColorPrimary
import com.thesun.drinksapp.utils.Constant
import java.net.URLEncoder

@Composable
fun TrackingOrderScreen(
    navController: NavController,
    orderId: Long,
    viewModel: TrackingOrderViewModel = hiltViewModel()
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

    TrackingOrderContent(
        order = order,
        isAdmin = false,
        onBackClick = { navController.popBackStack() },
        onReceiptClick = {
            navController.popBackStack()
            navController.navigate("receipt_order/$orderId") },
        onStatusUpdate = { status ->
            viewModel.updateOrderStatus(orderId, status) { success ->
                if (success && status == Order.STATUS_COMPLETE) {
                    val ratingReview = RatingReview(
                        type = RatingReview.TYPE_RATING_REVIEW_ORDER,
                        id = orderId.toString()
                    )
                    val json = Gson().toJson(ratingReview)
                    val encodedJson = URLEncoder.encode(json, "UTF-8")
                    navController.popBackStack()
                    navController.navigate("rating_reviews/$encodedJson")
                }
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingOrderContent(
    order: Order?,
    isAdmin: Boolean,
    onBackClick: () -> Unit,
    onReceiptClick: () -> Unit,
    onStatusUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theo dõi đơn hàng", color = Color(0xFF212121)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", tint = Color(0xFF212121))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(horizontal = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (order == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ColorPrimary)
                }
            } else {
                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp,
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                ) {
                    items(order.drinks ?: emptyList()) { drink ->
                        DrinkOrderItem(drink)
                    }
                }
                Row(
                    modifier = Modifier
                        .clickable { onReceiptClick() }
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hóa đơn",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorPrimary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = ColorPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                OrderStatusSteps(
                    status = order.status,
                    isAdmin = isAdmin,
                    onStatusUpdate = onStatusUpdate
                )
                if (!isAdmin) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { onStatusUpdate(Order.STATUS_COMPLETE) },
                        enabled = order.status == Order.STATUS_ARRIVED,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (order.status == Order.STATUS_ARRIVED) ColorPrimary else Color.Gray
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Nhận đơn hàng",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                    if (order.status == Order.STATUS_ARRIVED) {
                        Text(
                            text = "Vui lòng kiểm tra kỹ khi nhận được đơn hàng",
                            fontSize = 14.sp,
                            color = TextColorHeading,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusSteps(
    status: Int,
    isAdmin: Boolean,
    onStatusUpdate: (Int) -> Unit
) {
    Column {
        StatusStep(
            iconRes = if (status >= Order.STATUS_NEW) R.drawable.ic_step_enable else R.drawable.ic_step_disable,
            label = "Cửa hàng nhận đơn",
            isClickable = isAdmin,
            onClick = { onStatusUpdate(Order.STATUS_NEW) }
        )
        Box(
            modifier = Modifier
                .width(30.dp)
                .height(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .width(2.dp)
                    .background(if (status >= Order.STATUS_DOING) Color(0xFF43936C) else ColorAccent)
            )
        }
        StatusStep(
            iconRes = if (status >= Order.STATUS_DOING) R.drawable.ic_step_enable else R.drawable.ic_step_disable,
            label = "Chuẩn bị đơn hàng",
            isClickable = isAdmin,
            onClick = { onStatusUpdate(Order.STATUS_DOING) }
        )
        Box(
            modifier = Modifier
                .width(30.dp)
                .height(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .width(2.dp)
                    .background(if (status >= Order.STATUS_DOING) Color(0xFF43936C) else ColorAccent)
            )
        }
        StatusStep(
            iconRes = if (status >= Order.STATUS_ARRIVED) R.drawable.ic_step_enable else R.drawable.ic_step_disable,
            label = "Hoàn thành đơn hàng",
            isClickable = isAdmin,
            onClick = { onStatusUpdate(Order.STATUS_ARRIVED) }
        )
    }
}

@Composable
fun StatusStep(
    iconRes: Int,
    label: String,
    isClickable: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isClickable) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = label,
            modifier = Modifier.size(30.dp),
            tint = Color.Unspecified
        )
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextColorPrimary,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun TrackingOrderContentPreview() {
    MaterialTheme {
        TrackingOrderContent(
            order = Order(
                id = 12345,
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
                status = Order.STATUS_ARRIVED
            ),
            isAdmin = false,
            onBackClick = {},
            onReceiptClick = {},
            onStatusUpdate = {}
        )
    }
}