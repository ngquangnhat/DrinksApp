package com.thesun.drinksapp.ui.select_voucher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thesun.drinksapp.data.model.Voucher
import com.thesun.drinksapp.data.repository.VoucherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoucherViewModel @Inject constructor(
    private val voucherRepository: VoucherRepository
) : ViewModel() {

    private val _vouchers = MutableStateFlow<List<Voucher>>(emptyList())
    val vouchers: StateFlow<List<Voucher>> = _vouchers.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private var selectedVoucherId: Long = 0
    private var valueEventListener: ValueEventListener? = null

    init {
        loadVouchersFromFirebase()
    }

    fun setInitialSelectedVoucher(id: Long) {
        selectedVoucherId = id
        loadVouchersFromFirebase()
    }

    fun selectVoucher(voucher: Voucher) {
        viewModelScope.launch {
            _vouchers.value = _vouchers.value.map { item ->
                item.copy(isSelected = item.id == voucher.id)
            }
        }
    }

    private fun loadVouchersFromFirebase() {
        viewModelScope.launch {
            val database = voucherRepository.getVoucherRef()
            valueEventListener?.let { database.removeEventListener(it) }

            valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newVouchers = mutableListOf<Voucher>()
                    for (dataSnapshot in snapshot.children) {
                        val voucher = dataSnapshot.getValue(Voucher::class.java)
                        voucher?.let { newVouchers.add(0, it.copy(isSelected = it.id == selectedVoucherId)) }
                    }
                    _vouchers.value = newVouchers
                }

                override fun onCancelled(error: DatabaseError) {
                    _toastMessage.value = "Lỗi khi tải dữ liệu"
                }
            }
            database.addValueEventListener(valueEventListener!!)
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        valueEventListener?.let {
            voucherRepository.getVoucherRef().removeEventListener(it)
        }
    }
}