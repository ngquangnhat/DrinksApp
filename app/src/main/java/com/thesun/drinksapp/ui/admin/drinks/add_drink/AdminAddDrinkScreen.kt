package com.thesun.drinksapp.ui.admin.drinks.add_drink

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.Category
import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.ui.theme.White
import com.thesun.drinksapp.utils.GlobalFunction

@Composable
fun AdminAddDrinkScreen(navController: NavController, drinkId: String? = null) {
    val viewModel: AdminAddDrinkViewModel = hiltViewModel()
    LaunchedEffect(drinkId) {
        if (drinkId != null) {
            viewModel.loadDrink(drinkId)
        }
    }
    AdminAddDrinkScreenContent(navController, viewModel, drinkId != null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddDrinkScreenContent(
    navController: NavController,
    viewModel: AdminAddDrinkViewModel,
    isUpdate: Boolean
) {
    val context = LocalContext.current
    val name by viewModel.name.collectAsState()
    val description by viewModel.description.collectAsState()
    val price by viewModel.price.collectAsState()
    val promotion by viewModel.promotion.collectAsState()
    val image by viewModel.image.collectAsState()
    val imageBanner by viewModel.imageBanner.collectAsState()
    val isFeatured by viewModel.isFeatured.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

//    var imageInputMethod by remember { mutableStateOf("url") } // "url" hoặc "upload"
//    var bannerInputMethod by remember { mutableStateOf("url") } // "url" hoặc "upload"
//
//    var imageUri by remember { mutableStateOf<Uri?>(null) }
//    var imageBannerUri by remember { mutableStateOf<Uri?>(null) }
//    var imageUrl by remember { mutableStateOf(image) } // Lưu URL nếu người dùng nhập
//    var imageBannerUrl by remember { mutableStateOf(imageBanner) } // Lưu URL nếu người dùng nhập
//
//    val permissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (!isGranted) {
//            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.GetContent()
//    ) { uri ->
//        uri?.let {
//            imageUri = it
//            viewModel.setImage(it.toString())
//        }
//    }
//
//    val bannerPickerLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.GetContent()
//    ) { uri ->
//        uri?.let {
//            imageBannerUri = it
//            viewModel.setImageBanner(it.toString())
//        }
//    }
//
//    fun checkAndRequestPermission(onGranted: () -> Unit) {
//        val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            Manifest.permission.READ_MEDIA_IMAGES
//        } else {
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        }
//
//        if (ContextCompat.checkSelfPermission(
//                context,
//                permissionToRequest
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            onGranted()
//        } else {
//            permissionLauncher.launch(permissionToRequest)
//        }
//    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            if (isUpdate) R.string.label_update_drink
                            else R.string.label_add_drink
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
        },
        bottomBar = {
            Button(
                onClick = {
                    viewModel.addOrEditDrink(isUpdate) { success ->
                        if (success) {
                            Toast.makeText(
                                context,
                                if (isUpdate) context.getString(R.string.msg_edit_drink_success)
                                else context.getString(R.string.msg_add_drink_success),
                                Toast.LENGTH_SHORT
                            ).show()
                            if (!isUpdate) {
                                viewModel.resetFields()
                            }
                            GlobalFunction.hideSoftKeyboard(context as android.app.Activity)
                            navController.popBackStack()
                        } else {
                            Toast.makeText(
                                context,
                                "Vui lòng điền đầy đủ thông tin",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimaryDark)
            ) {
                Text(
                    text = if (isUpdate) stringResource(R.string.action_edit)
                    else stringResource(R.string.action_add),
                    color = White,
                    fontSize = 14.sp
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Divider(
                color = Color(0xFFE0E0E0),
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = stringResource(R.string.label_name),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.setName(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(6.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = ColorPrimaryDark,
                    unfocusedBorderColor = Color.Gray
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    color = ColorPrimaryDark
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true
            )

            CategoryDropdown(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.setSelectedCategory(it) }
            )

            Text(
                text = stringResource(R.string.label_description),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.setDescription(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(6.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = ColorPrimaryDark,
                    unfocusedBorderColor = Color.Gray
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    color = ColorPrimaryDark
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true
            )

            Text(
                text = stringResource(R.string.label_price_require),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White, RoundedCornerShape(6.dp))
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { viewModel.setPrice(it) },
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(6.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = ColorPrimaryDark,
                        unfocusedBorderColor = Color.Gray,
                        containerColor = Color.Transparent
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        color = ColorPrimaryDark
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
                Text(
                    text = stringResource(R.string.label_currency),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            Text(
                text = stringResource(R.string.label_promotion),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White, RoundedCornerShape(6.dp))
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = promotion,
                    onValueChange = { viewModel.setPromotion(it) },
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(6.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = ColorPrimaryDark,
                        unfocusedBorderColor = Color.Gray,
                        containerColor = Color.Transparent
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        color = ColorPrimaryDark
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
                Text(
                    text = "%",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            Text(
                text = stringResource(R.string.label_image),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            OutlinedTextField(
                value = image,
                onValueChange = { viewModel.setImage(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(6.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = ColorPrimaryDark,
                    unfocusedBorderColor = Color.Gray
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    color = ColorPrimaryDark
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                placeholder = {
                    Text(
                        "Nhập đường dẫn URL...",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            )
            if (image.isNotBlank()) {
                Card(
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .size(width = 100.dp, height = 80.dp)
                        .padding(top = 8.dp)
                ) {
                    SubcomposeAsyncImage(
                        model = image,
                        contentDescription = "Image Preview",
                        modifier = Modifier
                            .height(80.dp)
                            .align(Alignment.Start)
                            .padding(top = 8.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        loading = {
                            Text(
                                text = "Đang tải ảnh...",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        },
                        error = {
                            Text(
                                text = "Lỗi",
                                fontSize = 14.sp,
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    )

                }
            }


            Text(
                text = stringResource(R.string.label_image_large),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            OutlinedTextField(
                value = imageBanner,
                onValueChange = { viewModel.setImageBanner(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(6.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = ColorPrimaryDark,
                    unfocusedBorderColor = Color.Gray
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    color = ColorPrimaryDark
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                placeholder = {
                    Text(
                        "Nhập đường dẫn URL banner...",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            )
            if (imageBanner.isNotBlank()) {
                Card(
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .size(width = 100.dp, height = 80.dp)
                        .padding(top = 8.dp)
                ) {
                    SubcomposeAsyncImage(
                        model = imageBanner,
                        contentDescription = "Banner Preview",
                        modifier = Modifier
                            .height(80.dp)
                            .align(Alignment.Start)
                            .padding(top = 8.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        loading = {
                            Text(
                                text = "Đang tải ảnh...",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        },
                        error = {
                            Text(
                                text = "Lỗi",
                                fontSize = 14.sp,
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    )

                }

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .background(White, RoundedCornerShape(6.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isFeatured,
                    onCheckedChange = { viewModel.setIsFeatured(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = ColorPrimaryDark,
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    text = stringResource(R.string.featured),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Text(
        text = stringResource(R.string.label_category_require),
        fontSize = 14.sp,
        modifier = Modifier.padding(top = 16.dp)
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
    ) {
        OutlinedTextField(
            value = selectedCategory?.name ?: "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = ColorPrimaryDark,
                unfocusedBorderColor = Color.Gray,
                containerColor = Color.White
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                color = ColorPrimaryDark
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name ?: "") },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CategoryDropdownPreview() {
    val categories = listOf(
        Category(id = 1, name = "Cà phê"),
        Category(id = 2, name = "Trà")
    )
    CategoryDropdown(categories, categories.first(), {})
}