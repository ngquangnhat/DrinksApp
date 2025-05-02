package com.thesun.drinksapp.ui.feedback

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.ui.theme.TextColorHeading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    navController: NavController,
    viewModel: FeedbackViewModel = hiltViewModel()
) {
    val userEmail by viewModel.userEmail.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val feedbackState by viewModel.feedbackState.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Phản hồi",
                        color = Color(0xFF212121)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color(0xFF212121)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        FeedbackContent(
            userEmail = userEmail,
            userName = userName,
            feedbackState = feedbackState,
            onNameChange = viewModel::updateName,
            onPhoneChange = viewModel::updatePhone,
            onCommentChange = viewModel::updateComment,
            onSendFeedback = {
                viewModel.sendFeedback {
                    navController.popBackStack()
                }
            },
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun FeedbackContent(
    userEmail: String?,
    userName: String?,
    feedbackState: FeedbackState,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onCommentChange: (String) -> Unit,
    onSendFeedback: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp,
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Họ và tên (*)",
                    fontSize = 14.sp,
                    color = TextColorHeading
                )
                OutlinedTextField(
                    value = userName ?: feedbackState.name,
                    onValueChange = onNameChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    singleLine = true,
                    enabled = userName == null,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextColorHeading,
                        unfocusedBorderColor = ColorAccent,
                    )
                )
                Text(
                    text = "Số điện thoại",
                    fontSize = 14.sp,
                    color = TextColorHeading,
                    modifier = Modifier.padding(top = 16.dp)
                )
                OutlinedTextField(
                    value = feedbackState.phone,
                    onValueChange = onPhoneChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextColorHeading,
                        unfocusedBorderColor = ColorAccent,
                    )
                )
                Text(
                    text = "Email",
                    fontSize = 14.sp,
                    color = TextColorHeading,
                    modifier = Modifier.padding(top = 16.dp)
                )
                OutlinedTextField(
                    value = userEmail ?: "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    singleLine = true,
                    enabled = false,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextColorHeading,
                        unfocusedBorderColor = ColorAccent,
                    )
                )
                Text(
                    text = "Nội dung (*)",
                    fontSize = 14.sp,
                    color = TextColorHeading,
                    modifier = Modifier.padding(top = 16.dp)
                )
                OutlinedTextField(
                    value = feedbackState.comment,
                    onValueChange = onCommentChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(top = 5.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextColorHeading,
                        unfocusedBorderColor = ColorAccent,
                    )
                )
            }

        }
        Box(
            modifier = Modifier
                .width(200.dp)
                .align(Alignment.CenterHorizontally)
                .padding(top = 30.dp)
                .background(ColorPrimaryDark, RoundedCornerShape(10.dp))
                .clickable {
                    onSendFeedback()
                    keyboardController?.hide()
                }
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Gửi phản hồi",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedbackContentPreview() {
    FeedbackContent(
        userEmail = "user@example.com",
        userName = "John Doe",
        feedbackState = FeedbackState(
            name = "John Doe",
            phone = "0123456789",
            comment = "Great app!"
        ),
        onNameChange = {},
        onPhoneChange = {},
        onCommentChange = {},
        onSendFeedback = {}
    )
}