package com.thesun.drinksapp.ui.change_password

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thesun.drinksapp.R
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.ui.theme.TextColorHeading

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel = hiltViewModel()

) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(state.clearFields) {
        if (state.clearFields) {
            oldPassword = ""
            newPassword = ""
            confirmPassword = ""
        }
    }

    ChangePasswordScreenUI(
        onChangePassword = { oldPass, newPass, confirmPass ->
            viewModel.changePassword(oldPass, newPass, confirmPass){
                navController.popBackStack()
            }
        },
        onBack = { navController.popBackStack() },
        oldPassword = oldPassword,
        newPassword = newPassword,
        confirmPassword = confirmPassword,
        onOldPasswordChange = { oldPassword = it },
        onNewPasswordChange = { newPassword = it },
        onConfirmPasswordChange = { confirmPassword = it },
        isLoading = state.isLoading
    )

    state.messageResId?.let { messageID ->
        val message = stringResource(messageID)
        Log.d("ChangePasswordScreen", "Toast message: $message")
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.clearMessage()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreenUI(
    onChangePassword: (String, String, String) -> Unit,
    onBack: () -> Unit,
    oldPassword: String,
    newPassword: String,
    confirmPassword: String,
    onOldPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    isLoading: Boolean
) {
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.change_password),
                        color = Color(0xFF212121),
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            Divider(
                color = Color(0xFFF0F0F0),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.label_old_password),
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    color = TextColorHeading,
                    modifier = Modifier.align(Alignment.Start)
                )
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = onOldPasswordChange,
                    placeholder = {
                        Text(
                            stringResource(R.string.hint_password_old),
                            fontSize = 14.sp,
                            color = ColorAccent
                        )
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = androidx.compose.ui.text.input.ImeAction.Next
                    ),
                    visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        if (oldPassword.isNotEmpty()) {
                            val image = if (oldPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        }
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = TextColorHeading),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextColorHeading,
                        unfocusedBorderColor = if (oldPassword.isNotEmpty()) TextColorHeading else ColorAccent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.label_new_password),
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    color = TextColorHeading,
                    modifier = Modifier.align(Alignment.Start)
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = onNewPasswordChange,
                    placeholder = {
                        Text(
                            stringResource(R.string.hint_password_new),
                            fontSize = 14.sp,
                            color = ColorAccent
                        )
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = androidx.compose.ui.text.input.ImeAction.Next
                    ),
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        if (newPassword.isNotEmpty()) {
                            val image = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        }
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = TextColorHeading),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextColorHeading,
                        unfocusedBorderColor = if (newPassword.isNotEmpty()) TextColorHeading else ColorAccent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.label_confirm_new_password),
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    color = TextColorHeading,
                    modifier = Modifier.align(Alignment.Start)
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    placeholder = {
                        Text(
                            stringResource(R.string.hint_password_confirm),
                            fontSize = 14.sp,
                            color = ColorAccent
                        )
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done
                    ),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        if (confirmPassword.isNotEmpty()) {
                            val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        }
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = TextColorHeading),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextColorHeading,
                        unfocusedBorderColor = if (confirmPassword.isNotEmpty()) TextColorHeading else ColorAccent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    onClick = {
                        onChangePassword(oldPassword, newPassword, confirmPassword)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = oldPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (oldPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) ColorPrimaryDark else Color(0xFFCACACA),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.change_password),
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ColorPrimaryDark)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChangePasswordScreenPreview() {
    ChangePasswordScreenUI(
        onChangePassword = { _, _, _ -> },
        onBack = {},
        oldPassword = "",
        newPassword = "",
        confirmPassword = "",
        onOldPasswordChange = {},
        onNewPasswordChange = {},
        onConfirmPasswordChange = {},
        isLoading = false
    )
}