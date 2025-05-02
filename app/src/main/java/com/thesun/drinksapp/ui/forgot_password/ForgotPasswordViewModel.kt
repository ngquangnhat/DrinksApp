package com.thesun.drinksapp.ui.forgot_password

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.thesun.drinksapp.R
import com.thesun.drinksapp.ui.login.LoginState
import com.thesun.drinksapp.utils.StringUtil.isEmpty
import com.thesun.drinksapp.utils.StringUtil.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val _loginState = MutableStateFlow<LoginState?>(null)
    val loginState: StateFlow<LoginState?> = _loginState

    fun resetPassword(email: String) {
        if (isEmpty(email)) {
            _loginState.value = LoginState.Error(context.getString(R.string.msg_email_require))
            return
        }
        if (!isValidEmail(email)) {
            _loginState.value = LoginState.Error(context.getString(R.string.msg_email_invalid))
            return
        }
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
                firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _loginState.value = LoginState.Success
                    } else {
                        _loginState.value = LoginState.Error(context.getString(R.string.msg_login_error))
                    }
                }
        }
    }

}