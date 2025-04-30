package com.thesun.drinksapp.ui.admin.toppings.add_topping

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.Topping
import com.thesun.drinksapp.data.repository.DetailDrinkRepository
import com.thesun.drinksapp.utils.StringUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AddToppingViewModel @Inject constructor(
    private val toppingRepository: DetailDrinkRepository,
    private val application: Application
) : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val _toppingName = MutableStateFlow("")
    val toppingName: StateFlow<String> = _toppingName

    private val _toppingPrice = MutableStateFlow("")
    val toppingPrice: StateFlow<String> = _toppingPrice

    private val _isUpdate = MutableStateFlow(false)
    val isUpdate: StateFlow<Boolean> = _isUpdate

    private val _selectedTopping = MutableStateFlow<Topping?>(null)
    val selectedTopping: StateFlow<Topping?> = _selectedTopping

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun setToppingName(name: String) {
        _toppingName.value = name
    }

    fun setToppingPrice(price: String) {
        _toppingPrice.value = price
    }

    fun loadToppingById(toppingId: Long?) {
        if (toppingId != null) {
            toppingRepository.getToppingRef().child(toppingId.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val topping = snapshot.getValue(Topping::class.java)
                        setToppingData(topping)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _toastMessage.value = "Lỗi khi tải topping: ${error.message}"
                    }
                })
        } else {
            setToppingData(null)
        }
    }

    private fun setToppingData(topping: Topping?) {
        if (topping != null) {
            _isUpdate.value = true
            _selectedTopping.value = topping
            _toppingName.value = topping.name ?: ""
            _toppingPrice.value = topping.price.toString() ?: ""
        } else {
            _isUpdate.value = false
            _selectedTopping.value = null
            _toppingName.value = ""
            _toppingPrice.value = ""
        }
    }

    fun addOrEditTopping(onSuccess: () -> Unit, onAddSuccess: () -> Unit) {
        val name = _toppingName.value.trim()
        val priceStr = _toppingPrice.value.trim()

        if (StringUtil.isEmpty(name)) {
            _toastMessage.value = context.getString(R.string.msg_name_require)
            return
        }
        if (StringUtil.isEmpty(priceStr)) {
            _toastMessage.value = context.getString(R.string.msg_price_require)
            return
        }

        val price: Int
        try {
            price = priceStr.toInt()
            if (price <= 0) {
                _toastMessage.value = "Giá phải lớn hơn 0"
                return
            }
        } catch (e: NumberFormatException) {
            _toastMessage.value = "Giá phải là một số hợp lệ"
            return
        }

        _isLoading.value = true
        if (_isUpdate.value) {
            val toppingId = _selectedTopping.value?.id.toString()
            val map = mapOf(
                "name" to name,
                "price" to price
            )
            toppingRepository.getToppingRef().child(toppingId)
                .updateChildren(map) { error: DatabaseError?, _: DatabaseReference? ->
                    _isLoading.value = false
                    if (error == null) {
                        _toastMessage.value = application.getString(R.string.msg_edit_topping_success)
                        onSuccess()
                    } else {
                        _toastMessage.value = "Lỗi khi cập nhật topping: ${error.message}"
                    }
                }
        } else {
            val newToppingId = System.currentTimeMillis()
            val newTopping = Topping(
                id = newToppingId,
                name = name,
                price = price
            )
            toppingRepository.getToppingRef().child(newToppingId.toString())
                .setValue(newTopping) { error: DatabaseError?, _: DatabaseReference? ->
                    _isLoading.value = false
                    if (error == null) {
                        _toppingName.value = ""
                        _toppingPrice.value = ""
                        _toastMessage.value = application.getString(R.string.msg_add_topping_success)
                        onAddSuccess()
                    } else {
                        _toastMessage.value = "Lỗi khi thêm topping: ${error.message}"
                    }
                }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}