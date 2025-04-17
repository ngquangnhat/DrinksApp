package com.thesun.drinksapp.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.thesun.drinksapp.data.local.database.DrinkDAO
import com.thesun.drinksapp.data.model.Order
import com.thesun.drinksapp.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val drinkDAO: DrinkDAO
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Loading)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun processPayment(order: Order?) {
        if (order == null) {
            _toastMessage.value = "Đơn hàng không hợp lệ"
            _uiState.value = PaymentUiState.Error("Không có đơn hàng để xử lý")
            return
        }

        viewModelScope.launch {
            delay(2000)
            orderRepository.getOrderPaymentRef()
                .child(order.id.toString())
                .setValue(order)
                { error: DatabaseError?, _: DatabaseReference? ->
                    if (error != null) {
                        _toastMessage.value = "Lỗi khi lưu đơn hàng: ${error.message}"
                        _uiState.value = PaymentUiState.Error("Lỗi khi lưu đơn hàng")
                    } else {
                        viewModelScope.launch {
                            drinkDAO.deleteAllDrink()
                            _uiState.value = PaymentUiState.Success(order.id)
                        }
                    }
                }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}

sealed class PaymentUiState {
    data object Loading : PaymentUiState()
    data class Success(val orderId: Long) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
}