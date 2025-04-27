package com.thesun.drinksapp.ui.admin.drinks

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.ui.theme.White
import com.thesun.drinksapp.utils.Constant
import com.thesun.drinksapp.utils.GlobalFunction
import kotlin.math.roundToInt

@Composable
fun AdminDrinkScreen(navController: NavController) {
    val viewModel: AdminDrinkViewModel = hiltViewModel()
    AdminDrinkScreenContent(navController, viewModel)
}

@Composable
fun AdminDrinkScreenContent(navController: NavController, viewModel: AdminDrinkViewModel) {
    val context = LocalContext.current
    val drinks by viewModel.drinks.collectAsState()
    val searchKeyword by viewModel.searchKeyword.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<Drink?>(null) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_drink") },
                containerColor = ColorPrimaryDark,
                contentColor = White,
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_add),
                    contentDescription = "Thêm đồ uống"
                )
            }
        },
        containerColor = Color.White,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(
                    start = paddingValues.calculateStartPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                )
                .padding(top = 20.dp)
                .padding(horizontal = 10.dp)
        ) {
            item {
                SearchBar(
                    searchKeyword = searchKeyword,
                    onSearchChange = { viewModel.setSearchKeyword(it) },
                    onSearch = { viewModel.setSearchKeyword(searchKeyword) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(drinks) { drink ->
                DrinkItem(
                    drink = drink,
                    onClick = { navController.navigate("edit_drink/${drink.id}") },
                    onEdit = { navController.navigate("edit_drink/${drink.id}") },
                    onDelete = { showDeleteDialog = drink }
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    showDeleteDialog?.let { drink ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = {
                Text(
                    stringResource(R.string.msg_delete_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text(stringResource(R.string.msg_confirm_delete)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteDrink(drink) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.msg_delete_drink_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    showDeleteDialog = null
                }) {
                    Text(stringResource(R.string.action_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchKeyword: String,
    onSearchChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    val context = LocalContext.current
    OutlinedTextField(
        value = searchKeyword,
        onValueChange = onSearchChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(stringResource(R.string.hint_search_drink), fontSize = 14.sp, color = Color.Gray)
        },
        trailingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                GlobalFunction.hideSoftKeyboard(context as Activity)
            }
        ),
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = ColorPrimaryDark,
            unfocusedBorderColor = ColorAccent
        )
    )
}


@Composable
fun DrinkItem(
    drink: Drink,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 1.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.size(width = 100.dp, height = 80.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(drink.image),
                    contentDescription = drink.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 5.dp)
            ) {
                Text(
                    text = drink.name ?: "",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorPrimaryDark
                )

                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${drink.realPrice}" + Constant.CURRENCY,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    if (drink.sale > 0) {
                        Text(
                            text = "${drink.price}" + Constant.CURRENCY,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 10.dp),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                if (drink.categoryId > 0) {
                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.label_category_drink),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = drink.categoryName ?: "",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorPrimaryDark,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.label_featured),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = if (drink.isFeatured) "Có" else "Không",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorPrimaryDark,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Sửa",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(5.dp)
                        .clickable { onEdit() },
                    tint = Color(0xFF2E7D32)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Xóa",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(5.dp)
                        .clickable { onDelete() },
                    tint = Color.Red
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    SearchBar(
        searchKeyword = "",
        onSearchChange = {},
        onSearch = {}
    )
}

@Preview(showBackground = true)
@Composable
fun DrinkItemPreview() {
    DrinkItem(
        drink = Drink(
            id = 1,
            name = "Cà phê sữa",
            image = "https://example.com/coffee.jpg",
            price = 30,
            sale = 10,
            categoryId = 1,
            categoryName = "Cà phê",
            isFeatured = true
        ),
        onClick = {},
        onEdit = {},
        onDelete = {}
    )
}