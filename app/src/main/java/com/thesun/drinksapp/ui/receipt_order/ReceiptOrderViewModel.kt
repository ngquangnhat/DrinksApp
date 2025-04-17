package com.thesun.drinksapp.ui.receipt_order

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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiptOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _order = MutableStateFlow<Order?>(null)
    val order: StateFlow<Order?> = _order.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private var valueEventListener: ValueEventListener? = null

    fun loadOrderDetail(orderId: Long) {
        viewModelScope.launch {
            valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _order.value = snapshot.getValue(Order::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    _toastMessage.value = "Lỗi khi tải đơn hàng: ${error.message}"
                }
            }
            orderRepository.getOrderDetailDatabaseRef(orderId).addValueEventListener(valueEventListener!!)
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    override fun onCleared() {
        valueEventListener?.let {
            _order.value?.let { it1 -> orderRepository.getOrderDetailDatabaseRef(it1.id).removeEventListener(it) }
        }
    }
}