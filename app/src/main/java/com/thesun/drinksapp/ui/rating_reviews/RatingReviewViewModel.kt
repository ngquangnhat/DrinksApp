package com.thesun.drinksapp.ui.rating_reviews

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesun.drinksapp.data.model.Rating
import com.thesun.drinksapp.data.model.RatingReview
import com.thesun.drinksapp.data.repository.RatingReviewRepository
import com.thesun.drinksapp.utils.GlobalFunction.encodeEmailUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class RatingReviewViewModel @Inject constructor(
    private val ratingReviewRepository: RatingReviewRepository
) : ViewModel() {

    private val _rating = mutableFloatStateOf(5f)
    val rating: State<Float> = _rating

    private val _review = mutableStateOf("")
    val review: State<String> = _review

    private val _message = mutableStateOf("")
    val message: State<String> = _message

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private var ratingReview: RatingReview? = null

    fun setRatingReview(data: RatingReview?) {
        ratingReview = data
        _message.value = when (data?.type) {
            RatingReview.TYPE_RATING_REVIEW_DRINK -> "Đánh giá và nhận xét món uống"
            RatingReview.TYPE_RATING_REVIEW_ORDER -> "Đánh giá và nhận xét đơn hàng"
            else -> ""
        }

    }

    fun updateRating(newRating: Float) {
        _rating.floatValue = newRating.coerceIn(0f, 5f)
    }

    fun updateReview(newReview: String) {
        _review.value = newReview
    }

    fun sendReview() {
        val ratingObj = Rating(review.value.trim(), rating.value.toDouble())
        viewModelScope.launch {
            if (ratingReview == null) {
                _toastMessage.value = "Dữ liệu không hợp lệ"
                return@launch
            }
            try {
                when (ratingReview?.type) {
                    RatingReview.TYPE_RATING_REVIEW_DRINK -> {
                        ratingReviewRepository.getRatingDrinkRef(ratingReview!!.id)
                            .child(encodeEmailUser().toString())
                            .setValue(ratingObj)
                            .await()
                    }

                    RatingReview.TYPE_RATING_REVIEW_ORDER -> {
                        val updates = mapOf(
                            "rate" to ratingObj.rate,
                            "review" to ratingObj.review
                        )
                        ratingReviewRepository.getRatingOrderRef()
                            .child(ratingReview!!.id)
                            .updateChildren(updates)
                            .await()
                    }

                    else -> {
                        _toastMessage.value = "Loại đánh giá không hợp lệ"
                        return@launch
                    }
                }
                _toastMessage.value = "Gửi đánh giá thành công"
                resetForm()
            } catch (e: Exception) {
                _toastMessage.value = "Lỗi: ${e.message}"
            }
        }
    }

    private fun resetForm() {
        _rating.floatValue = 5f
        _review.value = ""
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

}