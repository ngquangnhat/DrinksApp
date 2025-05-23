package com.thesun.drinksapp.ui.register

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.User
import com.thesun.drinksapp.prefs.DataStoreManager
import com.thesun.drinksapp.ui.login.LoginState
import com.thesun.drinksapp.utils.Constant
import com.thesun.drinksapp.utils.StringUtil.isEmpty
import com.thesun.drinksapp.utils.StringUtil.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val _loginState = MutableStateFlow<LoginState?>(null)
    val loginState: StateFlow<LoginState?> = _loginState

    fun register(email: String, password: String, confirmPassword: String, isAdmin: Boolean) {
        if (isEmpty(email)) {
            _loginState.value = LoginState.Error(context.getString(R.string.msg_email_require))
            return
        }
        if (isEmpty(password)) {
            _loginState.value = LoginState.Error(context.getString(R.string.msg_password_require))
            return
        }
        if (isEmpty(confirmPassword)) {
            _loginState.value = LoginState.Error(context.getString(R.string.msg_confirm_password_require_invalid))
            return
        }
        if (!isValidEmail(email)) {
            _loginState.value = LoginState.Error(context.getString(R.string.msg_email_invalid))
            return
        }
        if (isAdmin && !email.contains(Constant.ADMIN_EMAIL_FORMAT)) {
            _loginState.value = LoginState.Error(context.getString(R.string.msg_email_invalid_user))
            return
        }
        if (password!=confirmPassword) {
            _loginState.value = LoginState.Error(context.getString(R.string.msg_password_invalid))
            return
        }
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        user?.let {
                            val userObject = User(it.email, password).apply {
                                this.isAdmin = isAdmin
                            }
                            DataStoreManager.user = userObject
                        }
                        _loginState.value = LoginState.Success
                    } else {
                        _loginState.value = LoginState.Error(context.getString(R.string.msg_login_error))
                    }
                }
        }

    }

}