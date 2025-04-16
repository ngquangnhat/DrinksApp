package com.thesun.drinksapp.ui.select_address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thesun.drinksapp.data.model.Address
import com.thesun.drinksapp.data.repository.UserAddressRepository
import com.thesun.drinksapp.prefs.DataStoreManager
import com.thesun.drinksapp.prefs.DataStoreManager.Companion.user
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val userAddressRepository: UserAddressRepository,
) : ViewModel() {

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private var selectedAddressId: Long = 0
    private var valueEventListener: ValueEventListener? = null

    init {
        loadAddressesFromFirebase()
    }

    fun setInitialSelectedAddress(id: Long) {
        selectedAddressId = id
        loadAddressesFromFirebase()
    }

    fun selectAddress(address: Address) {
        viewModelScope.launch {
            _addresses.value = _addresses.value.map { item ->
                item.copy(isSelected = item.id == address.id)
            }
        }
    }

    private fun loadAddressesFromFirebase() {
        viewModelScope.launch {
            val userEmail = user?.email ?: return@launch
            val database = userAddressRepository.getAddressRef()
            valueEventListener?.let { database.removeEventListener(it) }

            valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newAddresses = mutableListOf<Address>()
                    for (dataSnapshot in snapshot.children) {
                        val address = dataSnapshot.getValue(Address::class.java)
                        address?.let { newAddresses.add(0, it.copy(isSelected = it.id == selectedAddressId)) }
                    }
                    _addresses.value = newAddresses
                }

                override fun onCancelled(error: DatabaseError) {
                    _toastMessage.value = "Lỗi khi tải dữ liệu"
                }
            }
            database.orderByChild("userEmail").equalTo(userEmail)
                .addValueEventListener(valueEventListener!!)
        }
    }

    fun addAddress(name: String, phone: String, address: String) {
        viewModelScope.launch {
            val userEmail = user?.email ?: return@launch
            val id = System.currentTimeMillis()
            val newAddress = Address(id, name, phone, address, userEmail)
            userAddressRepository.getAddressRef()
                .child(id.toString())
                .setValue(newAddress)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        _toastMessage.value = "Thêm địa chỉ thành công"
                    } else {
                        _toastMessage.value = "Lỗi khi thêm địa chỉ"
                    }
                }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        valueEventListener?.let {
            FirebaseDatabase.getInstance().getReference("address").removeEventListener(it)
        }
    }
}