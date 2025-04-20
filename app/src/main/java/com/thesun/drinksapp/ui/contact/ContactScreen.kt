package com.thesun.drinksapp.ui.contact

import android.Manifest
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.thesun.drinksapp.R
import com.thesun.drinksapp.constant.AboutUsConfig
import com.thesun.drinksapp.data.model.Contact
import com.thesun.drinksapp.ui.theme.ColorPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    navController: NavController,
    viewModel: ContactViewModel = hiltViewModel()
) {
    val aboutUsState by viewModel.aboutUsState.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (context is Activity) {
                viewModel.callPhoneNumber(context)
            } else {
                Toast.makeText(context, "Không thể gọi điện từ ngữ cảnh này", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(context, "Quyền gọi điện bị từ chối", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.contact),
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
        ContactContent(
            aboutUsState = aboutUsState,
            contacts = contacts,
            onWebsiteClick = {
                if (context is Activity) {
                    viewModel.openWebsite(context)
                } else {
                    Toast.makeText(
                        context,
                        "Không thể mở website từ ngữ cảnh này",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onContactClick = { contact ->
                if (context is Activity) {
                    if (contact.id == Contact.HOTLINE) {
                        viewModel.requestCallPhone(context, permissionLauncher)
                    } else {
                        viewModel.handleContactClick(contact, context)
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Không thể thực hiện hành động từ ngữ cảnh này",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun ContactContent(
    aboutUsState: AboutUsState,
    contacts: List<Contact>,
    onWebsiteClick: () -> Unit,
    onContactClick: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.img_about_us),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Text(
                text = aboutUsState.title,
                fontSize = 18.sp,
                color = ColorPrimary,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = aboutUsState.content,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )
            Column(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clickable { onWebsiteClick() }
                    .padding(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = aboutUsState.websiteTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0000FF),
                    textDecoration = TextDecoration.Underline
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(contacts) { contact ->
                    ContactItem(
                        contact = contact,
                        onClick = { onContactClick(contact) }
                    )
                }
            }
        }
    }
}

@Composable
fun ContactItem(
    contact: Contact,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(2.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(contact.image),
                contentDescription = contact.getTypeName(),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Text(
                text = when (contact.id) {
                    Contact.FACEBOOK -> stringResource(R.string.label_facebook)
                    Contact.HOTLINE -> stringResource(R.string.label_call)
                    Contact.GMAIL -> stringResource(R.string.label_gmail)
                    Contact.SKYPE -> stringResource(R.string.label_skype)
                    Contact.YOUTUBE -> stringResource(R.string.label_youtube)
                    Contact.ZALO -> stringResource(R.string.label_zalo)
                    else -> ""
                },
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactContentPreview() {
    ContactContent(
        aboutUsState = AboutUsState(
            title = AboutUsConfig.ABOUT_US_TITLE,
            content = AboutUsConfig.ABOUT_US_CONTENT,
            websiteTitle = AboutUsConfig.ABOUT_US_WEBSITE_TITLE
        ),
        contacts = listOf(
            Contact(Contact.FACEBOOK, R.drawable.ic_facebook),
            Contact(Contact.HOTLINE, R.drawable.ic_hotline),
            Contact(Contact.GMAIL, R.drawable.ic_gmail),
            Contact(Contact.SKYPE, R.drawable.ic_skype),
            Contact(Contact.YOUTUBE, R.drawable.ic_youtube),
            Contact(Contact.ZALO, R.drawable.ic_zalo)
        ),
        onWebsiteClick = {},
        onContactClick = {}
    )
}