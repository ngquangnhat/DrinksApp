package com.thesun.drinksapp.ui.cart

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesun.drinksapp.data.local.database.DrinkDAO
import com.thesun.drinksapp.data.model.Address
import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.data.model.DrinkOrder
import com.thesun.drinksapp.data.model.Order
import com.thesun.drinksapp.data.model.PaymentMethod
import com.thesun.drinksapp.data.model.Voucher
import com.thesun.drinksapp.data.repository.DrinkRepository
import com.thesun.drinksapp.prefs.DataStoreManager.Companion.user
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val drinkDAO: DrinkDAO
) : ViewModel() {

    private val _cartItems = mutableStateOf<List<Drink>>(emptyList())
    val cartItems: State<List<Drink>> = _cartItems

    private val _paymentMethod = mutableStateOf<PaymentMethod?>(null)
    val paymentMethod: State<PaymentMethod?> = _paymentMethod

    private val _address = mutableStateOf<Address?>(null)
    val address: State<Address?> = _address

    private val _voucher = mutableStateOf<Voucher?>(null)
    val voucher: State<Voucher?> = _voucher

    private val _totalPrice = mutableIntStateOf(0)
    val totalPrice: State<Int> = _totalPrice

    private val _itemCount = mutableIntStateOf(0)
    val itemCount: State<Int> = _itemCount

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            drinkDAO.listDrinkCart.collect {
                _cartItems.value = it
                calculateTotalPrice()
                updateItemCount()
            }
        }
    }

    fun deleteCartItem(drink: Drink, position: Int) {
        viewModelScope.launch {
            drinkDAO.deleteDrink(drink)
            val newList = _cartItems.value.toMutableList().apply { removeAt(position) }
            _cartItems.value = newList
            calculateTotalPrice()
            updateItemCount()
        }
    }

    fun updateCartItem(drink: Drink, position: Int) {
        viewModelScope.launch {
            drinkDAO.updateDrink(drink)
            val newList = _cartItems.value.toMutableList().apply { set(position, drink) }
            _cartItems.value = newList
            calculateTotalPrice()
        }
    }
    fun updatePaymentMethod(paymentMethod: PaymentMethod) {
        viewModelScope.launch {
            _paymentMethod.value = paymentMethod
        }
    }

    fun updateAddress(address: Address) {
        viewModelScope.launch {
            _address.value = address
        }
    }

    fun updateVoucher(voucher: Voucher?) {
        viewModelScope.launch {
            _voucher.value = voucher
            calculateTotalPrice()
        }
    }

    fun checkout(navigateToPayment: (Order) -> Unit) {
        if (_cartItems.value.isEmpty()) {
            _toastMessage.value = "Giỏ hàng trống"
            return
        }
        if (_paymentMethod.value == null) {
            _toastMessage.value = "Vui lòng chọn phương thức thanh toán"
            return
        }
        if (_address.value == null) {
            _toastMessage.value = "Vui lòng chọn địa chỉ"
            return
        }

        val order = Order(
            id = System.currentTimeMillis(),
            userEmail = user?.email,
            profilePictureUrl = user?.profilePictureUrl,
            dateTime = System.currentTimeMillis().toString(),
            drinks = _cartItems.value.map {
                DrinkOrder(it.name, it.option, it.count, it.priceOneDrink, it.image)
            },
            price = _totalPrice.intValue,
            voucher = _voucher.value?.getPriceDiscount(_totalPrice.intValue) ?: 0,
            total = calculateFinalAmount(),
            paymentMethod = _paymentMethod.value!!.name,
            address = _address.value,
            status = Order.STATUS_NEW
        )
        navigateToPayment(order)
    }

    private fun calculateTotalPrice() {
        val total = _cartItems.value.sumOf { it.totalPrice }
        _totalPrice.intValue = total
        _totalPrice.intValue = calculateFinalAmount()
    }

    private fun calculateFinalAmount(): Int {
        var amount = _totalPrice.intValue
        _voucher.value?.let {
            amount -= it.getPriceDiscount(_totalPrice.intValue)
        }
        return amount
    }

    private fun updateItemCount() {
        _itemCount.intValue = _cartItems.value.size
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}