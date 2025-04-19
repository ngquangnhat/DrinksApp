package com.thesun.drinksapp.ui.user.history_tab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.thesun.drinksapp.data.model.Order
import com.thesun.drinksapp.data.model.TabOrder
import com.thesun.drinksapp.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {
    private val _tabs = MutableStateFlow(listOf(
        TabOrder(TabOrder.TAB_ORDER_PROCESS, "Đang xử lý"),
        TabOrder(TabOrder.TAB_ORDER_DONE, "Hoàn thành")
    ))
    val tabs: StateFlow<List<TabOrder>> = _tabs

    private val _processOrders = MutableStateFlow<List<Order>>(emptyList())
    val processOrders: StateFlow<List<Order>> = _processOrders

    private val _doneOrders = MutableStateFlow<List<Order>>(emptyList())
    val doneOrders: StateFlow<List<Order>> = _doneOrders

    private var allOrdersListener: ValueEventListener? = null
    private var userOrdersListener: ValueEventListener? = null

    fun loadOrders(isAdmin: Boolean, userEmail: String?) {
        viewModelScope.launch {
            if (isAdmin) {
                loadAllOrders()
            } else {
                loadUserOrders(userEmail)
            }
        }
    }

    private fun loadAllOrders() {
        allOrdersListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val processOrders = mutableListOf<Order>()
                val doneOrders = mutableListOf<Order>()
                for (dataSnapshot in snapshot.children) {
                    val order = dataSnapshot.getValue(Order::class.java)
                    order?.let {
                        if (it.status == Order.STATUS_COMPLETE) {
                            doneOrders.add(0, it)
                        } else {
                            processOrders.add(0, it)
                        }
                    }
                }
                _processOrders.value = processOrders
                _doneOrders.value = doneOrders
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error: ${error.message}")
            }
        }
        orderRepository.getAllOrderDatabaseRef()
            .addValueEventListener(allOrdersListener!!)
    }

    private fun loadUserOrders(userEmail: String?) {
        userOrdersListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val processOrders = mutableListOf<Order>()
                val doneOrders = mutableListOf<Order>()
                for (dataSnapshot in snapshot.children) {
                    val order = dataSnapshot.getValue(Order::class.java)
                    order?.let {
                        if (it.status == Order.STATUS_COMPLETE) {
                            doneOrders.add(0, it)
                        } else {
                            processOrders.add(0, it)
                        }
                    }
                }
                _processOrders.value = processOrders
                _doneOrders.value = doneOrders
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase error: ${error.message}")
            }
        }
        orderRepository.getAllOrderDatabaseRef()
            .orderByChild("userEmail")
            .equalTo(userEmail)
            .addValueEventListener(userOrdersListener!!)
    }

    override fun onCleared() {
        allOrdersListener?.let {
            orderRepository.getAllOrderDatabaseRef().removeEventListener(it)
        }
        userOrdersListener?.let {
            orderRepository.getAllOrderDatabaseRef().removeEventListener(it)
        }
        super.onCleared()
    }
}