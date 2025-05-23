package com.thesun.drinksapp.ui.user.history_tab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thesun.drinksapp.prefs.DataStoreManager.Companion.user
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.ui.theme.Green
import com.thesun.drinksapp.utils.Constant
import androidx.compose.runtime.LaunchedEffect
import com.thesun.drinksapp.ui.admin.statistic.StatisticsViewModel
import java.text.DecimalFormat

@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val statistics by viewModel.statistics.collectAsState()

    LaunchedEffect(user) {
        user?.let {
            viewModel.loadStatistics(it.isAdmin, it.email)
        }
    }

    StatisticsContent(
        statistics = statistics,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun StatisticsContent(
    statistics: StatisticsViewModel.StatisticsData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Thống kê đơn hàng",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ColorPrimaryDark,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard(
                title = "Tổng đơn hàng",
                value = statistics.totalOrders.toString(),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatCard(
                title = "Đơn hoàn thành",
                value = statistics.completedOrders.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val revenueInt = statistics.totalRevenue.toInt()
            val formattedRevenue = if (revenueInt > 1000) {
                DecimalFormat("#,###").format(revenueInt).replace(",", ".")
            } else {
                revenueInt.toString()
            }
            StatCard(
                title = "Tổng doanh thu",
                value = "${formattedRevenue}${Constant.CURRENCY}",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatCard(
                title = "Giá trị TB đơn",
                value = "${statistics.averageOrderValue.toInt()}${Constant.CURRENCY}",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Đồ uống bán chạy nhất",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ColorPrimaryDark,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .border(1.dp, ColorPrimaryDark, RoundedCornerShape(8.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = statistics.topDrink ?: "Chưa có dữ liệu",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorPrimaryDark
                )
                Text(
                    text = "Số lượng: ${statistics.topDrinkCount}",
                    fontSize = 14.sp,
                    color = Green
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Phân bố đơn hàng theo ngày",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ColorPrimaryDark,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ColorPrimaryDark, RoundedCornerShape(8.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                items(statistics.ordersByDate.toList()) { (date, count) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = date,
                            fontSize = 14.sp,
                            color = ColorPrimaryDark
                        )
                        Text(
                            text = "$count đơn",
                            fontSize = 14.sp,
                            color = Green
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(100.dp)
            .border(1.dp, ColorPrimaryDark, RoundedCornerShape(8.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = ColorPrimaryDark,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimaryDark
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun StatisticsScreenPreview() {
    StatisticsContent(
        statistics = StatisticsViewModel.StatisticsData(
            totalOrders = 50,
            completedOrders = 40,
            totalRevenue = 200000.0,
            averageOrderValue = 5000.0,
            topDrink = "Trà sữa",
            topDrinkCount = 100,
            ordersByDate = mapOf(
                "01/05/2025" to 10,
                "02/05/2025" to 15,
                "03/05/2025" to 25
            )
        )
    )
}