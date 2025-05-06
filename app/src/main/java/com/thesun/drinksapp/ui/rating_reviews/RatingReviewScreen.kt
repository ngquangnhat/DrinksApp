package com.thesun.drinksapp.ui.rating_reviews

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.RatingReview
import com.thesun.drinksapp.ui.theme.ColorPrimary
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.ui.theme.TextColorHeading
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingReviewScreen(
    navController: NavController,
    ratingReview: RatingReview,
    viewModel: RatingReviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val rating by viewModel.rating
    val review by viewModel.review
    val message by viewModel.message
    val toastMessage by viewModel.toastMessage.collectAsState()
    val drinkRatings by viewModel.drinkRatings.collectAsState()

    LaunchedEffect(ratingReview) {
        viewModel.setRatingReview(ratingReview)
        if (ratingReview.type == RatingReview.TYPE_RATING_REVIEW_DRINK) {
            viewModel.fetchDrinkRatings(ratingReview.id)
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(context.getString(R.string.ratings_and_reviews), fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_rating_and_review),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .padding(top = 8.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = message,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = ColorPrimary,
                modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
            )

            Text(
                text = context.getString(R.string.label_rating),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = TextColorHeading,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )
            RatingBar(
                rating = rating,
                onRatingChanged = { viewModel.updateRating(it) },
                modifier = Modifier.width(200.dp)
            )

            Text(
                text = context.getString(R.string.label_review),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = TextColorHeading,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 16.dp, bottom = 8.dp)
            )
            TextField(
                value = review,
                onValueChange = { viewModel.updateReview(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                placeholder = {
                    Text(
                        context.getString(R.string.hint_rating_review),
                        fontSize = 14.sp,
                        color = Color.Gray.copy(alpha = 0.5f)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                maxLines = 3,
                textStyle = TextStyle(fontSize = 14.sp)
            )
            Text(
                text = context.getString(R.string.label_review_note),
                fontSize = 12.sp,
                color = TextColorHeading.copy(alpha = 0.7f),
                style = TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp)
            )

            Button(
                onClick = {
                    viewModel.sendReview()
                    keyboardController?.hide()
                    navController.popBackStack()
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPrimaryDark
                )
            ) {
                Text(
                    text = context.getString(R.string.label_send_review),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            if (drinkRatings.isNotEmpty()) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    color = Color.Gray.copy(alpha = 0.2f)
                )
                Text(
                    text = "Đánh giá từ người dùng",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = TextColorHeading,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )
                drinkRatings.forEach { rating ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color.Gray.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Khách hàng",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextColorHeading
                            )
                            if (!rating.review.isNullOrEmpty()) {
                                Text(
                                    text = rating.review,
                                    fontSize = 13.sp,
                                    color = TextColorHeading.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(top = 4.dp),
                                    maxLines = 3
                                )
                            }
                        }
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = rating.rate.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorPrimaryDark
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_star_yellow),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp).offset(y = (-2).dp),
                                tint = Color(0xFFfbb909)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun RatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(vertical = 4.dp)
            .then(
                if (onRatingChanged == {}) Modifier
                else Modifier.pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val x = change.position.x
                        val width = size.width.toFloat()
                        val newRating = (x / width) * 5f
                        val roundedRating = (newRating * 2).roundToInt() / 2f
                        onRatingChanged(roundedRating.coerceIn(0f, 5f))
                    }
                }
            ),
        horizontalArrangement = Arrangement.Start
    ) {
        (1..5).forEach { star ->
            val starValue = star.toFloat()
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .then(
                        if (onRatingChanged == {}) Modifier
                        else Modifier.clickable {
                            val newRating = if (rating == starValue - 0.5f) {
                                starValue
                            } else if (rating == starValue) {
                                starValue - 0.5f
                            } else {
                                if (rating >= starValue - 0.25f) starValue else starValue - 0.5f
                            }
                            onRatingChanged(newRating.coerceIn(0f, 5f))
                        }
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star_gray),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.LightGray
                )
                val fraction = when {
                    rating >= starValue -> 1f
                    rating >= starValue - 0.5f -> 0.5f
                    else -> 0f
                }
                if (fraction > 0f) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star_yellow),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(
                                object : Shape {
                                    override fun createOutline(
                                        size: Size,
                                        layoutDirection: LayoutDirection,
                                        density: Density
                                    ): Outline {
                                        return Outline.Rectangle(
                                            Rect(
                                                left = 0f,
                                                top = 0f,
                                                right = size.width * fraction,
                                                bottom = size.height
                                            )
                                        )
                                    }
                                }
                            ),
                        tint = Color(0xFFfbb909)
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}