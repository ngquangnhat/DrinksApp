package com.thesun.drinksapp.ui.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import com.thesun.drinksapp.data.model.Feedback
import com.thesun.drinksapp.data.repository.FeedbackRepository
import com.thesun.drinksapp.prefs.DataStoreManager.Companion.user
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val feedbackRepository: FeedbackRepository
) : ViewModel() {

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    private val _feedbackState = MutableStateFlow(FeedbackState())
    val feedbackState: StateFlow<FeedbackState> = _feedbackState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _userProfileImage = MutableStateFlow<String?>(null)
    val userProfileImage: StateFlow<String?> = _userProfileImage.asStateFlow()

    var feedback: Feedback? = null

    init {
        loadUserEmail()
    }

    private fun loadUserEmail() {
        viewModelScope.launch {
            _userEmail.value = user?.email
            _userProfileImage.value = user?.profilePictureUrl
            _userName.value = user?.userName
        }
    }

    fun updateName(name: String) {
        _feedbackState.value = _feedbackState.value.copy(name = name)
    }

    fun updatePhone(phone: String) {
        _feedbackState.value = _feedbackState.value.copy(phone = phone)
    }

    fun updateComment(comment: String) {
        _feedbackState.value = _feedbackState.value.copy(comment = comment)
    }
    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun sendFeedback(onSuccess: () -> Unit) {
        val name = _userName.value ?: ""
        val phone = _feedbackState.value.phone
        val comment = _feedbackState.value.comment
        val email = _userEmail.value ?: ""

        if (name.isBlank()) {
            _toastMessage.value = "Vui lòng nhập tên của bạn !"
            return
        }
        if (comment.isBlank()) {
            _toastMessage.value = "Vui lòng nhập phản hồi của bạn !"
            return
        }

        _isLoading.value = true
        feedback = if (user?.profilePictureUrl == null || user?.userName == null){
            Feedback(name, phone, email, comment)
        } else {
            Feedback(name, phone, email, _userName.value, _userProfileImage.value, comment)
        }
        viewModelScope.launch {
            feedbackRepository.getFeedbackRef()
                .child(System.currentTimeMillis().toString())
                .setValue(feedback)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        _feedbackState.value = FeedbackState()
                        _toastMessage.value = "Gửi phản hồi thành công !"
                        viewModelScope.launch {
                            delay(1000)
                            onSuccess()
                        }
                    } else {
                        _toastMessage.value = "Gửi phản hồi thất bại !"
                    }
                }
        }
    }
    }
data class FeedbackState(
    val name: String = "",
    val phone: String = "",
    val comment: String = ""
)