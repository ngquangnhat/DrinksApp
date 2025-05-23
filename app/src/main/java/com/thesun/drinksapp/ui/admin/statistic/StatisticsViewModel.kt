package com.thesun.drinksapp.ui.admin.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.thesun.drinksapp.data.model.Order
import com.thesun.drinksapp.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {
    private val _statistics = MutableStateFlow(StatisticsData())
    val statistics: StateFlow<StatisticsData> = _statistics

    private var ordersListener: ValueEventListener? = null

    data class StatisticsData(
        val totalOrders: Int = 0,
        val completedOrders: Int = 0,
        val totalRevenue: Double = 0.0,
        val averageOrderValue: Double = 0.0,
        val topDrink: String? = null,
        val topDrinkCount: Int = 0,
        val ordersByDate: Map<String, Int> = emptyMap()
    )

    fun loadStatistics(isAdmin: Boolean, userEmail: String?) {
        viewModelScope.launch {
            if (isAdmin) {
                loadAllOrders()
            } else {
                loadUserOrders(userEmail)
            }
        }
    }

    private fun loadAllOrders() {
        ordersListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                processOrders(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error: ${error.message}")
            }
        }
        orderRepository.getAllOrderDatabaseRef()
            .addValueEventListener(ordersListener!!)
    }

    private fun loadUserOrders(userEmail: String?) {
        ordersListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                processOrders(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error: ${error.message}")
            }
        }
        orderRepository.getAllOrderDatabaseRef()
            .orderByChild("userEmail")
            .equalTo(userEmail)
            .addValueEventListener(ordersListener!!)
    }

    private fun processOrders(snapshot: DataSnapshot) {
        val orders = mutableListOf<Order>()
        for (dataSnapshot in snapshot.children) {
            val order = dataSnapshot.getValue(Order::class.java)
            order?.let { orders.add(it) }
        }

        val completedOrders = orders.filter { it.status == Order.STATUS_COMPLETE }
        val totalOrders = orders.size
        val completedCount = completedOrders.size
        val totalRevenue = completedOrders.sumOf { it.total.toDouble() ?: 0.0 }
        val averageOrderValue = if (completedCount > 0) totalRevenue / completedCount else 0.0

        val drinkCountMap = mutableMapOf<String, Int>()
        orders.forEach { order ->
            order.drinks?.forEach { drink ->
                drink.name?.let {
                    drinkCountMap[it] = drinkCountMap.getOrDefault(it, 0) + (drink.count ?: 1)
                }
            }
        }
        val topDrinkEntry = drinkCountMap.maxByOrNull { it.value }
        val topDrink = topDrinkEntry?.key
        val topDrinkCount = topDrinkEntry?.value ?: 0

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val ordersByDate = orders.groupBy {
            it.dateTime?.toLongOrNull()?.let { timestamp ->
                dateFormat.format(Date(timestamp))
            } ?: "Unknown"
        }.mapValues { it.value.size }
            .toList()
            .sortedByDescending { (dateStr, _) ->
                if (dateStr == "Unknown") Long.MIN_VALUE
                else runCatching { dateFormat.parse(dateStr)?.time }.getOrDefault(Long.MIN_VALUE)
            }.toMap()

        _statistics.value = StatisticsData(
            totalOrders = totalOrders,
            completedOrders = completedCount,
            totalRevenue = totalRevenue,
            averageOrderValue = averageOrderValue,
            topDrink = topDrink,
            topDrinkCount = topDrinkCount,
            ordersByDate = ordersByDate
        )
    }

    override fun onCleared() {
        ordersListener?.let {
            orderRepository.getAllOrderDatabaseRef().removeEventListener(it)
        }
        super.onCleared()
    }
}