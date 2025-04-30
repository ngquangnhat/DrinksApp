package com.thesun.drinksapp.ui.admin.settings


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
class AdminSettingsViewModel @Inject constructor(
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    init {
        loadUserEmail()
    }

    private fun loadUserEmail() {
        viewModelScope.launch {
            _email.value = user?.email ?: "Cafe Manager"
        }
    }

    fun signOut(onSignOutComplete: () -> Unit) {
        FirebaseAuth.getInstance().signOut()
        viewModelScope.launch {
            user = null
            onSignOutComplete()
        }
    }
}