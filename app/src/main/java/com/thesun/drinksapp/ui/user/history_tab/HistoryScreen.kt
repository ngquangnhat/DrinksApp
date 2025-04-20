package com.thesun.drinksapp.ui.user.history_tab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.collectAsState
import com.thesun.drinksapp.data.model.Order
import com.thesun.drinksapp.data.model.TabOrder
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import com.thesun.drinksapp.data.model.Address
import com.thesun.drinksapp.data.model.DrinkOrder
import com.thesun.drinksapp.prefs.DataStoreManager.Companion.user
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.thesun.drinksapp.R
import com.thesun.drinksapp.ui.theme.BgFilter
import com.thesun.drinksapp.ui.theme.Green
import com.thesun.drinksapp.ui.theme.TextColorHeading
import com.thesun.drinksapp.ui.theme.TextColorSecondary
import com.thesun.drinksapp.utils.Constant
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, viewModel: HistoryViewModel = hiltViewModel()) {
    val tabs by viewModel.tabs.collectAsState()

    LaunchedEffect(user) {
        user?.let {
            viewModel.loadOrders(it.isAdmin, it.email)
        }
    }
    Column {
        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp,
        )
        HistoryContent(
            tabs = tabs,
            viewModel = viewModel,
            isAdmin = user?.isAdmin ?: false,
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )

    }
}

@Composable
fun HistoryContent(
    tabs: List<TabOrder>,
    viewModel: HistoryViewModel,
    isAdmin: Boolean,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            contentColor = ColorPrimaryDark,
            modifier = Modifier.fillMaxWidth(),
            indicator = { tabPositions ->
                if (tabPositions.isNotEmpty() && selectedTabIndex < tabPositions.size) {
                    TabRowDefaults.Indicator(
                        color = ColorPrimaryDark,
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = index == selectedTabIndex,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    onClick = {
                        selectedTabIndex = index
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    selectedContentColor = ColorPrimaryDark,
                    unselectedContentColor = ColorAccent,
                    text = {
                        Text(
                            text = tab.name.uppercase(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                )
            }
        }

        androidx.compose.foundation.pager.HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) { page ->
            when (tabs[page].type) {
                TabOrder.TAB_ORDER_PROCESS -> ProcessOrderScreen(
                    ordersFlow = viewModel.processOrders,
                    isAdmin = isAdmin,
                    navController = navController
                )

                TabOrder.TAB_ORDER_DONE -> DoneOrderScreen(
                    ordersFlow = viewModel.doneOrders,
                    isAdmin = isAdmin,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun ProcessOrderScreen(
    ordersFlow: kotlinx.coroutines.flow.StateFlow<List<Order>>,
    isAdmin: Boolean,
    navController: NavController
) {
    val orders by ordersFlow.collectAsState(emptyList())
    OrderList(
        orders = orders,
        isAdmin = isAdmin,
        navController = navController
    )
}

@Composable
fun DoneOrderScreen(
    ordersFlow: kotlinx.coroutines.flow.StateFlow<List<Order>>,
    isAdmin: Boolean,
    navController: NavController
) {
    val orders by ordersFlow.collectAsState(emptyList())
    OrderList(
        orders = orders,
        isAdmin = isAdmin,
        navController = navController
    )
}

@Composable
fun OrderList(orders: List<Order>, isAdmin: Boolean, navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        items(orders) { order ->
            OrderItem(
                order = order,
                isAdmin = isAdmin,
                onTrackClick = { navController.navigate("tracking_order/${order.id}") },
                onReceiptClick = { navController.navigate("receipt_order/${order.id}") }
            )
        }
    }
}

@Composable
fun OrderItem(
    order: Order,
    isAdmin: Boolean,
    onTrackClick: () -> Unit,
    onReceiptClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = order.drinks?.firstOrNull()?.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(0.5.dp, ColorAccent, CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(Modifier.weight(1f)) {
                if (isAdmin) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.image_drink_example),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Unspecified
                        )
                        Text(
                            text = order.userEmail ?: "",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                }

                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = "#" + order.id.toString(),
                        color = TextColorHeading,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${order.total}${Constant.CURRENCY}",
                        color = TextColorHeading,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = order.listDrinksName ?: "",
                        color = TextColorSecondary,
                        lineHeight = 14.sp,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "(${order.drinks?.size ?: 0} món)",
                        color = TextColorSecondary,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (order.status == Order.STATUS_COMPLETE) {
                        Text(
                            text = "Thành công",
                            color = Green,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(6.dp))
                                .border(1.dp, Green, RoundedCornerShape(6.dp))
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            if (order.status == Order.STATUS_COMPLETE) onReceiptClick()
                            else onTrackClick()
                        }
                    ) {
                        Text(
                            text = if (order.status == Order.STATUS_COMPLETE) "Hoá đơn" else "Theo dõi",
                            color = ColorPrimaryDark,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(16.dp),
                            tint = ColorPrimaryDark
                        )
                    }
                }
            }
        }

        if (order.status == Order.STATUS_COMPLETE) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .padding(horizontal = 10.dp)
                    .background(Color.White, RoundedCornerShape(6.dp))
                    .border(1.dp, ColorAccent, RoundedCornerShape(6.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(5.dp)
                ) {
                    Text(
                        text = order.rate.toString(),
                        color = TextColorHeading,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_star_yellow),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(16.dp),
                        tint = Color.Unspecified
                    )
                }
                Column(
                    Modifier
                        .weight(1f)
                        .background(BgFilter)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Đánh giá",
                        color = TextColorHeading,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = order.review ?: "Chưa có đánh giá",
                        color = TextColorSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderItemPreview() {
    OrderItem(
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
            status = Order.STATUS_COMPLETE,
            rate = 5.0,
            review = "Rất ngon!",
            userEmail = "test@example.com"
        ),
        isAdmin = true,
        onTrackClick = {},
        onReceiptClick = {}
    )
}
