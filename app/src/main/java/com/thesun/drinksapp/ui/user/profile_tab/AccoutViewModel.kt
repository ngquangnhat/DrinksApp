package com.thesun.drinksapp.ui.user.profile_tab


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.thesun.drinksapp.prefs.DataStoreManager.Companion.user
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor() : ViewModel() {

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _userImg = MutableStateFlow<String?>(null)
    val userImg: StateFlow<String?> = _userImg.asStateFlow()


    init {
        loadUserEmail()
    }

    private fun loadUserEmail() {
        viewModelScope.launch {
            _userEmail.value = user?.email
            _userName.value = user?.userName
            _userImg.value = user?.profilePictureUrl
        }
    }

    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            FirebaseAuth.getInstance().signOut()
            user = null
            _userEmail.value = null
            onSuccess()
        }
    }

    fun setUserEmail(email: String) {
        _userEmail.value = email
    }
}