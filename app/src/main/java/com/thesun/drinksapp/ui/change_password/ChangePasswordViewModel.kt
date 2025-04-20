package com.thesun.drinksapp.ui.change_password

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.thesun.drinksapp.R
import com.thesun.drinksapp.prefs.DataStoreManager
import com.thesun.drinksapp.utils.StringUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ChangePasswordState())
    val state: StateFlow<ChangePasswordState> = _state.asStateFlow()

    fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String, onSuccess: () -> Unit) {
        if (StringUtil.isEmpty(oldPassword)) {
            _state.value = ChangePasswordState(messageResId = R.string.msg_old_password_require)
            return
        }
        if (StringUtil.isEmpty(newPassword)) {
            _state.value = ChangePasswordState(messageResId = R.string.msg_new_password_require)
            return
        }
        if (StringUtil.isEmpty(confirmPassword)) {
            _state.value = ChangePasswordState(messageResId = R.string.msg_confirm_password_require)
            return
        }
        if (DataStoreManager.user?.password != oldPassword) {
            _state.value = ChangePasswordState(messageResId = R.string.msg_old_password_invalid)
            return
        }
        if (newPassword != confirmPassword) {
            _state.value = ChangePasswordState(messageResId = R.string.msg_confirm_password_invalid)
            return
        }
        if (oldPassword == newPassword) {
            _state.value = ChangePasswordState(messageResId = R.string.msg_new_password_invalid)
            return
        }

        _state.value = ChangePasswordState(isLoading = true)
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            _state.value = ChangePasswordState(
                isLoading = false
            )
            return
        }

        val credential = EmailAuthProvider.getCredential(user.email ?: "", oldPassword)
        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {
                user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        val userLogin = DataStoreManager.user
                        userLogin?.password = newPassword
                        DataStoreManager.user = userLogin
                        _state.value = ChangePasswordState(
                            isLoading = false,
                            messageResId = R.string.msg_change_password_successfully,
                            clearFields = true
                        )
                        viewModelScope.launch {
                            delay(1000)
                            onSuccess()
                        }
                    } else {
                        Log.e("ChangePasswordViewModel", "Update failed: ${updateTask.exception?.message}")
                        _state.value = ChangePasswordState(
                            isLoading = false,
                            messageResId = R.string.msg_change_password_failed
                        )
                    }
                }
            } else {
                Log.e("ChangePasswordViewModel", "Reauth failed: ${reauthTask.exception?.message}")
                _state.value = ChangePasswordState(
                    isLoading = false,
                    messageResId = R.string.msg_old_password_invalid
                )
            }
        }
    }

    fun clearMessage() {
        _state.value = _state.value.copy(messageResId = null)
    }
}

data class ChangePasswordState(
    val isLoading: Boolean = false,
    val messageResId: Int? = null,
    val clearFields: Boolean = false
)