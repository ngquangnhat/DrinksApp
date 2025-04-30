package com.thesun.drinksapp.ui.admin.feedbacks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.thesun.drinksapp.data.model.Feedback
import com.thesun.drinksapp.data.repository.FeedbackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminFeedbackViewModel @Inject constructor(
    private val feedbackRepository: FeedbackRepository
) : ViewModel() {

    private val _feedbacks = MutableStateFlow<List<Feedback>>(emptyList())
    val feedbacks: StateFlow<List<Feedback>> = _feedbacks.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var valueEventListener: ValueEventListener? = null

    init {
        loadFeedbacks()
    }

    private fun loadFeedbacks() {
        viewModelScope.launch {
            valueEventListener?.let {
                feedbackRepository.getFeedbackRef().removeEventListener(it)
            }
            _feedbacks.value = emptyList()

            valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val feedbackList = mutableListOf<Feedback>()
                    for (dataSnapshot in snapshot.children) {
                        val feedback = dataSnapshot.getValue(Feedback::class.java) ?: continue
                        feedbackList.add( feedback)
                    }
                    _feedbacks.value = feedbackList
                }

                override fun onCancelled(error: DatabaseError) {
                    _errorMessage.value = "Lỗi khi tải dữ liệu phản hồi: ${error.message}"
                }
            }

            feedbackRepository.getFeedbackRef()
                .addValueEventListener(valueEventListener!!)
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        valueEventListener?.let {
            feedbackRepository.getFeedbackRef().removeEventListener(it)
        }
        super.onCleared()
    }
}