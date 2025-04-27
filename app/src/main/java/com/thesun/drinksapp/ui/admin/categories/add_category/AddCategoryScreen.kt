package com.thesun.drinksapp.ui.admin.categories.add_category

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.Category
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.ui.theme.TextColorHeading
import com.thesun.drinksapp.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(navController: NavController, categoryId: String? = null) {
    val viewModel: AddCategoryViewModel = hiltViewModel()
    val context = LocalContext.current

    LaunchedEffect(categoryId) {
        viewModel.loadCategoryById(categoryId)
    }

    val categoryName by viewModel.categoryName.collectAsState()
    val isUpdate by viewModel.isUpdate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()


    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    AddCategoryScreenContent(
        navController = navController,
        categoryName = categoryName,
        isUpdate = isUpdate,
        isLoading = isLoading,
        onCategoryNameChange = { viewModel.setCategoryName(it) },
        onAddOrEditClick = {
            viewModel.addOrEditCategory (
                onSuccess = {
                    if (isUpdate) {
                        navController.navigateUp()
                    }
                },
                onAddSuccess = {
                        navController.navigateUp()
            })
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreenContent(
    navController: NavController,
    categoryName: String,
    isUpdate: Boolean,
    isLoading: Boolean,
    onCategoryNameChange: (String) -> Unit,
    onAddOrEditClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            if (isUpdate) R.string.label_update_category
                            else R.string.label_add_category
                        ),
                        fontSize = 18.sp,
                        color = Color(0xFF212121)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color(0xFF212121)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .background(White)
        ) {
            Divider(
                color = Color(0xFFE0E0E0),
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = stringResource(R.string.label_name),
                fontSize = 14.sp,
                color = ColorPrimaryDark,
                modifier = Modifier.padding(bottom = 4.dp, start = 10.dp)
            )

            OutlinedTextField(
                value = categoryName,
                onValueChange = onCategoryNameChange,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                ),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextColorHeading,
                    unfocusedBorderColor = ColorAccent,
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(top = 10.dp),
            )

            Button(
                onClick = onAddOrEditClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .padding(horizontal = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorPrimaryDark,
                    contentColor = White
                ),
                shape = RoundedCornerShape(30.dp),
                enabled = !isLoading
            ) {
                Text(
                    text = stringResource(
                        if (isUpdate) R.string.action_edit
                        else R.string.action_add
                    ),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddCategoryScreenContentPreview() {
    AddCategoryScreenContent(
        navController = rememberNavController(),
        categoryName = "",
        isUpdate = false,
        isLoading = false,
        onCategoryNameChange = {},
        onAddOrEditClick = {}
    )
}