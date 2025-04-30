package com.thesun.drinksapp.ui.admin.settings


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thesun.drinksapp.R
import com.thesun.drinksapp.ui.theme.ColorPrimary
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.utils.GlobalFunction

@Composable
fun AdminSettingsScreen(
    navController: NavController,
    viewModel: AdminSettingsViewModel = hiltViewModel()
) {
    val email by viewModel.email.collectAsState()

    var showSignOutDialog by remember { mutableStateOf(false) }

    AdminSettingsContent(
        email = email,
        onManageToppingClick = {
            navController.navigate("manage_topping")
        },
        onManageVoucherClick = {
            navController.navigate("manage_voucher")
        },
        onManageFeedbackClick = {
            navController.navigate("manage_feedback")
        },
        onSignOutClick = {
            showSignOutDialog = true
        }
    )

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text(stringResource(R.string.sign_out)) },
            text = { Text("Bạn có chắc muốn đăng xuất không?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.signOut {
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                    showSignOutDialog = false
                }) {
                    Text("Có")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Không")
                }
            }
        )
    }
}

@Composable
fun AdminSettingsContent(
    email: String,
    onManageToppingClick: () -> Unit,
    onManageVoucherClick: () -> Unit,
    onManageFeedbackClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_avatar_default),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.Start)
        )

        Text(
            text = email,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = ColorPrimaryDark,
            modifier = Modifier.padding(top = 5.dp)
        )

        Divider(
            color = ColorPrimary,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        )

        SettingsMenuItem(
            icon = Icons.Default.Star,
            text = stringResource(R.string.manage_topping),
            onClick = onManageToppingClick
        )

        SettingsMenuItem(
            icon = Icons.Default.LocalOffer,
            text = stringResource(R.string.manage_voucher),
            onClick = onManageVoucherClick
        )

        SettingsMenuItem(
            icon = Icons.Default.Feedback,
            text = stringResource(R.string.manage_feedback),
            onClick = onManageFeedbackClick
        )

        SettingsMenuItem(
            icon = Icons.Default.ExitToApp,
            text = stringResource(R.string.sign_out),
            onClick = onSignOutClick
        )
    }
}

@Composable
fun SettingsMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ColorPrimaryDark,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = ColorPrimaryDark,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminSettingsContentPreview() {
    AdminSettingsContent(
        email = "admin@example.com",
        onManageToppingClick = {},
        onManageVoucherClick = {},
        onManageFeedbackClick = {},
        onSignOutClick = {}
    )
}
