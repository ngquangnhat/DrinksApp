package com.thesun.drinksapp.ui.login

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.User
import com.thesun.drinksapp.prefs.DataStoreManager
import com.thesun.drinksapp.utils.Constant
import com.thesun.drinksapp.utils.StringUtil.isEmpty
import com.thesun.drinksapp.utils.StringUtil.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@SuppressLint("StaticFieldLeak")
@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val _loginState = MutableStateFlow<LoginState?>(null)
    val loginState: StateFlow<LoginState?> = _loginState

    fun login(email: String, password: String, isAdmin: Boolean) {
        if (isEmpty(email)) {
            _loginState.value = LoginState.Error(context.getString(R.string.msg_email_require))
            return
        }
        if (isEmpty(password)) {
            _loginState.value = LoginState.Error(context.getString(R.string.msg_password_require))
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
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            firebaseAuth.signInWithEmailAndPassword(email, password)
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
