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

    LaunchedEffect(ratingReview) {
        viewModel.setRatingReview(ratingReview)
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
                title = { Text(context.getString(R.string.ratings_and_reviews), fontSize = 18.sp) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_rating_and_review),
                contentDescription = null,
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = message,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = ColorPrimary,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = context.getString(R.string.label_rating),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextColorHeading,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.Start)
            )

            RatingBar(
                rating = rating,
                onRatingChanged = { viewModel.updateRating(it) }
            )

            Text(
                text = context.getString(R.string.label_review),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextColorHeading,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.Start)
            )

            TextField(
                value = review,
                onValueChange = { viewModel.updateReview(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(top = 8.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(6.dp)),
                placeholder = { Text(context.getString(R.string.hint_rating_review), fontSize = 14.sp, color = Color.LightGray) },
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
                maxLines = 4
            )

            Text(
                text = context.getString(R.string.label_review_note),
                fontSize = 12.sp,
                color = TextColorHeading,
                style = TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Button(
                onClick = {
                    viewModel.sendReview()
                    keyboardController?.hide()
                    navController.popBackStack()
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(top = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPrimaryDark
                ),
            ) {
                Text(
                    text = context.getString(R.string.label_send_review),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@Composable
fun RatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(top = 8.dp)
            .width(200.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val x = change.position.x
                    val width = size.width.toFloat()
                    val newRating = (x / width) * 5f
                    val roundedRating = (newRating * 2).roundToInt() / 2f
                    onRatingChanged(roundedRating.coerceIn(0f, 5f))
                }
            },
        horizontalArrangement = Arrangement.Center
    ) {
        (1..5).forEach { star ->
            val starValue = star.toFloat()
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        val newRating = if (rating == starValue - 0.5f) {
                            starValue
                        } else if (rating == starValue) {
                            starValue - 0.5f
                        } else {
                            if (rating >= starValue - 0.25f) starValue else starValue - 0.5f
                        }
                        onRatingChanged(newRating.coerceIn(0f, 5f))
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star_gray),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
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
                            .size(28.dp)
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
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}