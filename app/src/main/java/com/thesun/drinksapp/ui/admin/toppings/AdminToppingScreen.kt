package com.thesun.drinksapp.ui.admin.toppings

import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.Topping
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.ui.theme.White
import com.thesun.drinksapp.utils.Constant
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.OutlinedTextField
import kotlin.math.roundToInt

@Composable
fun AdminToppingScreen(
    navController: NavController,
    viewModel: AdminToppingViewModel = hiltViewModel()
) {
    AdminToppingScreenContent(navController, viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminToppingScreenContent(
    navController: NavController,
    viewModel: AdminToppingViewModel
) {
    val context = LocalContext.current
    val toppings by viewModel.toppings.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<Topping?>(null) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.manage_topping),
                        fontSize = 18.sp,
                        color = Color(0xFF212121)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF212121)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("add_topping")
                },
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
                    contentDescription = "Thêm topping"
                )
            }
        },
        containerColor = White
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(paddingValues)
        ){
            Divider(
                color = Color(0xFFE0E0E0),
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                item {
                    SearchBar(
                        searchKeyword = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onSearch = { viewModel.setSearchQuery(searchQuery) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                items(toppings) { topping ->
                    ToppingItem(
                        topping = topping,
                        onClick = {
                            navController.navigate("edit_topping/${topping.id}")
                        },
                        onEdit = {
                            navController.navigate("edit_topping/${topping.id}")
                        },
                        onDelete = { showDeleteDialog = topping }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    showDeleteDialog?.let { topping ->
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
                    viewModel.deleteTopping(topping) { success ->
                        if (success) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.msg_delete_topping_successfully),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
    OutlinedTextField(
        value = searchKeyword,
        onValueChange = onSearchChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(stringResource(R.string.hint_search_topping), fontSize = 14.sp, color = Color.Gray)
        },
        trailingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
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
fun ToppingItem(
    topping: Topping,
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
            defaultElevation = 2.dp
        ),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp)
            ) {
                Text(
                    text = topping.name ?: "",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorPrimaryDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${topping.price ?: 0}${Constant.CURRENCY}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Xóa",
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
fun ToppingItemPreview() {
    ToppingItem(
        topping = Topping(
            id = 1,
            name = "Trân châu",
            price = 5
        ),
        onClick = {},
        onEdit = {},
        onDelete = {}
    )
}
