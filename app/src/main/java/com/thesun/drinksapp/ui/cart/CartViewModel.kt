package com.thesun.drinksapp.ui.cart

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.thesun.drinksapp.data.local.database.DrinkDAO
import com.thesun.drinksapp.data.model.Address
import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.data.model.DrinkOrder
import com.thesun.drinksapp.data.model.Order
import com.thesun.drinksapp.data.model.PaymentMethod
import com.thesun.drinksapp.data.model.Voucher
import com.thesun.drinksapp.data.remote.ApiInterface
import com.thesun.drinksapp.data.remote.MoMoApiService
import com.thesun.drinksapp.data.remote.MoMoPaymentRequest
import com.thesun.drinksapp.prefs.DataStoreManager.Companion.user
import com.thesun.drinksapp.prefs.MySharedPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val drinkDAO: DrinkDAO,
    val apiInterface: ApiInterface,
    private val moMoApiService: MoMoApiService
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

    private val _paymentUrl = MutableStateFlow<String?>(null)
    val paymentUrl: StateFlow<String?> = _paymentUrl.asStateFlow()

    // Thông tin MoMo (lưu trữ an toàn, ví dụ: trong backend hoặc config)
    private val partnerCode = "MOMO" // Thay bằng partnerCode thật
    private val accessKey = "F8BBA842ECF85" // Thay bằng accessKey thật
    private val secretKey = "K951B6PE1waDMi640xX08PD3vg6EkVlz" // Thay bằng secretKey thật
    private val redirectUrl = "yourapp://payment" // Thay bằng URL chuyển hướng của bạn
    private val ipnUrl = "https://your-api.com/api/v1/payments/webhook" // Thay bằng URL thông báo

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

    fun checkout(): Order? {
        if (_cartItems.value.isEmpty()) {
            _toastMessage.value = "Giỏ hàng trống"
            return null
        }
        if (_paymentMethod.value == null) {
            _toastMessage.value = "Vui lòng chọn phương thức thanh toán"
            return null
        }
        if (_address.value == null) {
            _toastMessage.value = "Vui lòng chọn địa chỉ"
            return null
        }

        return Order(
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
    }

    fun saveOrderToPrefs(context: android.content.Context, order: Order) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val sharedPreferences = MySharedPreferences(context)
                val gson = Gson()
                val orderJson = gson.toJson(order)
                sharedPreferences.putStringValue("saved_orders", orderJson)
            }
        }
    }

    fun initiateMoMoPayment(order: Order) {
        viewModelScope.launch {
            try {
                val requestId = UUID.randomUUID().toString()
                val orderId = order.id.toString()
                val orderInfo = "Thanh toán đơn hàng DrinksApp #${order.id}"
                val requestType = "captureWallet"
                val lang = "vi"
                val amount = order.total * 1000L // Chuyển sang VND

                // Tạo extraData
                val objExtra = JSONObject()
                objExtra.put("orderId", orderId)
                objExtra.put("appScheme", "momo") // Thay bằng appScheme của bạn
                val extraData = objExtra.toString()

                // Tạo chữ ký
                val rawSignature = "accessKey=$accessKey&amount=$amount&extraData=$extraData&ipnUrl=$ipnUrl&orderId=$orderId&orderInfo=$orderInfo&partnerCode=$partnerCode&redirectUrl=$redirectUrl&requestId=$requestId&requestType=$requestType"
                val signature = hmacSha256(rawSignature, secretKey)

                val request = MoMoPaymentRequest(
                    partnerCode = partnerCode,
                    requestId = requestId,
                    amount = amount,
                    orderId = orderId,
                    orderInfo = orderInfo,
                    redirectUrl = redirectUrl,
                    ipnUrl = ipnUrl,
                    requestType = requestType,
                    signature = signature,
                    lang = lang,
                    extraData = extraData
                )

                // Chuyển hoạt động mạng sang Dispatchers.IO
                val response = withContext(Dispatchers.IO) {
                    moMoApiService.createPayment(request).execute()
                }

                if (response.isSuccessful) {
                    val paymentResponse = response.body()
                    if (paymentResponse != null) {
                        if (paymentResponse.errorCode == 0 && paymentResponse.payUrl != null) {
                            _paymentUrl.value = paymentResponse.payUrl
                            //_toastMessage.value = "Khởi tạo thanh toán MoMo thành công"
                        } else {
                            _toastMessage.value = "Lỗi thanh toán: ${paymentResponse.message ?: "Không có thông tin lỗi"}"
                        }
                    } else {
                        _toastMessage.value = "Lỗi: Phản hồi API rỗng"
                    }
                } else {
                    _toastMessage.value = "Lỗi kết nối: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Lỗi: ${e.message ?: "Lỗi không xác định"}"
            }
        }
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

    private fun hmacSha256(data: String, key: String): String {
        val algorithm = "HmacSHA256"
        val secretKeySpec = SecretKeySpec(key.toByteArray(), algorithm)
        val mac = Mac.getInstance(algorithm)
        mac.init(secretKeySpec)
        val hash = mac.doFinal(data.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
}