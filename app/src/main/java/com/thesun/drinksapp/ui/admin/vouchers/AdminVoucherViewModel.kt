package com.thesun.drinksapp.ui.admin.vouchers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.thesun.drinksapp.data.model.Voucher
import com.thesun.drinksapp.data.repository.VoucherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminVoucherViewModel @Inject constructor(
    private val voucherRepository: VoucherRepository
) : ViewModel() {

    private val _vouchers = MutableStateFlow<List<Voucher>>(emptyList())
    val vouchers: StateFlow<List<Voucher>> = _vouchers.asStateFlow()

    private var childEventListener: ChildEventListener? = null

    init {
        loadVouchers()
    }

    private fun loadVouchers() {
        viewModelScope.launch {
            childEventListener?.let {
                voucherRepository.getVoucherRef().removeEventListener(it)
            }
            _vouchers.value = emptyList()

            childEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    val voucher = dataSnapshot.getValue(Voucher::class.java) ?: return
                    val currentList = _vouchers.value.toMutableList()
                    currentList.add(voucher)
                    _vouchers.value = currentList.sortedBy { it.discount ?: 0 }
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    val updatedVoucher = dataSnapshot.getValue(Voucher::class.java) ?: return
                    val currentList = _vouchers.value.toMutableList()
                    val index = currentList.indexOfFirst { it.id == updatedVoucher.id }
                    if (index != -1) {
                        currentList[index] = updatedVoucher
                        _vouchers.value = currentList.sortedBy { it.discount ?: 0 }
                    }
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    val removedVoucher = dataSnapshot.getValue(Voucher::class.java) ?: return
                    val currentList = _vouchers.value.toMutableList()
                    currentList.removeAll { it.id == removedVoucher.id }
                    _vouchers.value = currentList.sortedBy { it.discount ?: 0 }
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            }

            voucherRepository.getVoucherRef()
                .addChildEventListener(childEventListener!!)
        }
    }

    fun deleteVoucher(voucher: Voucher, onComplete: (Boolean) -> Unit) {
        voucherRepository.getVoucherRef()
            .child(voucher.id.toString())
            .removeValue { error, _ ->
                onComplete(error == null)
            }
    }

    override fun onCleared() {
        childEventListener?.let {
            voucherRepository.getVoucherRef().removeEventListener(it)
        }
        super.onCleared()
    }
}