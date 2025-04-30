package com.thesun.drinksapp.ui.admin.vouchers.add_voucher

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.Voucher
import com.thesun.drinksapp.data.repository.VoucherRepository
import com.thesun.drinksapp.utils.StringUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AdminAddVoucherViewModel @Inject constructor(
    private val voucherRepository: VoucherRepository,
    private val application: Application
) : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val _voucherDiscount = MutableStateFlow("")
    val voucherDiscount: StateFlow<String> = _voucherDiscount

    private val _voucherMinimum = MutableStateFlow("")
    val voucherMinimum: StateFlow<String> = _voucherMinimum

    private val _isUpdate = MutableStateFlow(false)
    val isUpdate: StateFlow<Boolean> = _isUpdate

    private val _selectedVoucher = MutableStateFlow<Voucher?>(null)
    val selectedVoucher: StateFlow<Voucher?> = _selectedVoucher

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun setVoucherDiscount(discount: String) {
        _voucherDiscount.value = discount
    }

    fun setVoucherMinimum(minimum: String) {
        _voucherMinimum.value = minimum
    }

    fun loadVoucherById(voucherId: Long?) {
        if (voucherId != null) {
            voucherRepository.getVoucherRef().child(voucherId.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val voucher = snapshot.getValue(Voucher::class.java)
                        setVoucherData(voucher)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _toastMessage.value = "Lỗi khi tải voucher: ${error.message}"
                    }
                })
        } else {
            setVoucherData(null)
        }
    }

    private fun setVoucherData(voucher: Voucher?) {
        if (voucher != null) {
            _isUpdate.value = true
            _selectedVoucher.value = voucher
            _voucherDiscount.value = voucher.discount?.toString() ?: ""
            _voucherMinimum.value = voucher.minimum?.toString() ?: ""
        } else {
            _isUpdate.value = false
            _selectedVoucher.value = null
            _voucherDiscount.value = ""
            _voucherMinimum.value = ""
        }
    }

    fun addOrEditVoucher(onSuccess: () -> Unit, onAddSuccess: () -> Unit) {
        val discountStr = _voucherDiscount.value.trim()
        var minimumStr = _voucherMinimum.value.trim()

        if (StringUtil.isEmpty(discountStr)) {
            _toastMessage.value = context.getString(R.string.msg_discount_require)
            return
        }

        val discount: Int
        try {
            discount = discountStr.toInt()
            if (discount <= 0) {
                _toastMessage.value = "Giảm giá phải lớn hơn 0"
                return
            }
        } catch (e: NumberFormatException) {
            _toastMessage.value = "Giảm giá phải là một số hợp lệ"
            return
        }

        val minimum: Int
        if (StringUtil.isEmpty(minimumStr)) {
            minimumStr = "0"
        }
        try {
            minimum = minimumStr.toInt()
            if (minimum < 0) {
                _toastMessage.value = "Đơn hàng tối thiểu không được nhỏ hơn 0"
                return
            }
        } catch (e: NumberFormatException) {
            _toastMessage.value = "Đơn hàng tối thiểu phải là một số hợp lệ"
            return
        }

        _isLoading.value = true
        if (_isUpdate.value) {
            val voucherId = _selectedVoucher.value?.id.toString()
            val map = mapOf(
                "discount" to discount,
                "minimum" to minimum
            )
            voucherRepository.getVoucherRef().child(voucherId)
                .updateChildren(map) { error: DatabaseError?, _: DatabaseReference? ->
                    _isLoading.value = false
                    if (error == null) {
                        _toastMessage.value = application.getString(R.string.msg_edit_voucher_success)
                        onSuccess()
                    } else {
                        _toastMessage.value = "Lỗi khi cập nhật voucher: ${error.message}"
                    }
                }
        } else {
            val newVoucherId = System.currentTimeMillis()
            val newVoucher = Voucher(
                id = newVoucherId,
                discount = discount,
                minimum = minimum
            )
            voucherRepository.getVoucherRef().child(newVoucherId.toString())
                .setValue(newVoucher) { error: DatabaseError?, _: DatabaseReference? ->
                    _isLoading.value = false
                    if (error == null) {
                        _voucherDiscount.value = ""
                        _voucherMinimum.value = ""
                        _toastMessage.value = application.getString(R.string.msg_add_voucher_success)
                        onAddSuccess()
                    } else {
                        _toastMessage.value = "Lỗi khi thêm voucher: ${error.message}"
                    }
                }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}