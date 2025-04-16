package com.thesun.drinksapp.ui.select_paymethod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesun.drinksapp.data.model.PaymentMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentMethodViewModel @Inject constructor() : ViewModel() {

    private val _paymentMethods = MutableStateFlow<List<PaymentMethod>>(emptyList())
    val paymentMethods: StateFlow<List<PaymentMethod>> = _paymentMethods.asStateFlow()

    private var selectedPaymentMethodId: Int = 0

    init {
        loadPaymentMethods()
    }

    private fun loadPaymentMethods() {
        viewModelScope.launch {
            val methods = listOf(
                PaymentMethod(1, "Thanh toán tiền mặt", "(Thanh toán khi nhận hàng)", false),
                PaymentMethod(2, "Credit or debit card", "(Thẻ Visa hoặc Mastercard)", false),
                PaymentMethod(3, "Chuyển khoản ngân hàng", "(Tự động xác nhận)", false),
                PaymentMethod(4, "ZaloPay", "(Tự động xác nhận)", false)
            )

            _paymentMethods.value = methods.map { method ->
                if (method.id == selectedPaymentMethodId) {
                    method.copy(isSelected = true)
                } else {
                    method
                }
            }
        }
    }

    fun setInitialSelectedPaymentMethod(id: Int) {
        selectedPaymentMethodId = id
        loadPaymentMethods()
    }

    fun selectPaymentMethod(paymentMethod: PaymentMethod) {
        viewModelScope.launch {
            _paymentMethods.value = _paymentMethods.value.map { item ->
                item.copy(isSelected = item.id == paymentMethod.id)
            }
        }
    }
}