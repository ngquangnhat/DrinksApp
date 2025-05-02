package com.thesun.drinksapp.ui.user.profile_tab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.thesun.drinksapp.R
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.ui.theme.TextColorHeading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navController: NavController,
    viewModel: AccountViewModel = viewModel()
) {
    val userEmail = viewModel.userEmail.collectAsState().value
    val userName = viewModel.userName.collectAsState().value
    val profilePictureUrl = viewModel.userImg.collectAsState().value

    Column {
        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp,
        )
        AccountContent(
            userEmail = userEmail,
            userName = userName,
            profilePictureUrl = profilePictureUrl,
            onFeedbackClick = { navController.navigate("feedback") },
            onContactClick = { navController.navigate("contact") },
            onChangePasswordClick = { navController.navigate("change_password") },
            onSignOutClick = {
                viewModel.signOut {
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        )

    }
}

@Composable
fun AccountContent(
    userEmail: String?,
    userName: String? = null,
    profilePictureUrl: String? = null,
    onFeedbackClick: () -> Unit,
    onContactClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        AsyncImage(
            model = profilePictureUrl ?: R.drawable.ic_avatar_default,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_avatar_default),
            error = painterResource(R.drawable.ic_avatar_default)
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (userName != null) {
            Text(
                text = "Tên người dùng",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimaryDark
            )
            Text(
                text = userName,
                fontSize = 14.sp,
                color = TextColorHeading,
                modifier = Modifier.padding(top = 5.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        Text(
            text = "Email",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ColorPrimaryDark
        )
        Text(
            text = userEmail ?: "Chưa đăng nhập",
            fontSize = 14.sp,
            color = TextColorHeading,
            modifier = Modifier.padding(top = 5.dp)
        )
        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        AccountItem(
            iconRes = R.drawable.ic_feedback,
            text = "Phản hồi",
            onClick = onFeedbackClick
        )
        AccountItem(
            iconRes = R.drawable.ic_contact,
            text = "Liên hệ",
            onClick = onContactClick,
        )
        AccountItem(
            iconRes = R.drawable.ic_manage_account,
            text = "Đổi mật khẩu",
            onClick = onChangePasswordClick,
        )
        AccountItem(
            iconRes = R.drawable.ic_logout,
            text = "Đăng xuất",
            onClick = onSignOutClick,
        )
    }
}

@Composable
private fun AccountItem(
    iconRes: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
       Row (
           modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
           verticalAlignment = Alignment.CenterVertically
       ){
           Icon(
               painter = painterResource(iconRes),
               contentDescription = null,
               modifier = Modifier.size(24.dp),
               tint = Color(0xFF808080)
           )
           Text(
               text = text,
               fontSize = 14.sp,
               color = TextColorHeading,
               modifier = Modifier.padding(start = 10.dp)
           )
       }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountContentPreview() {
    AccountContent(
        userEmail = "user@example.com",
        userName = "John Doe",
        onFeedbackClick = {},
        onContactClick = {},
        onChangePasswordClick = {},
        onSignOutClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    AccountScreen(
        navController = rememberNavController(),
        viewModel = AccountViewModel().apply { setUserEmail("user@example.com") }
    )
}