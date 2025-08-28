package com.thesun.drinksapp.ui.admin.categories

import android.app.Activity
import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.Category
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
import com.thesun.drinksapp.ui.theme.White
import com.thesun.drinksapp.utils.GlobalFunction
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun AdminCategoryScreen(navController: NavController) {
    val viewModel: AdminCategoryViewModel = hiltViewModel()
    CategoryScreenContent(navController, viewModel)
}

@Composable
fun CategoryScreenContent(navController: NavController, viewModel: AdminCategoryViewModel) {
    val context = LocalContext.current
    val categories by viewModel.categories.collectAsState()
    val searchKeyword by viewModel.searchKeyword.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<Category?>(null) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_category") },
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
                    contentDescription = "Thêm danh mục"
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
                    bottom = paddingValues.calculateBottomPadding()
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
            items(categories) { category ->
                CategoryItem(
                    category = category,
                    onClick = { navController.navigate("edit_category/${category.id}") },
                    onEdit = { navController.navigate("edit_category/${category.id}") },
                    onDelete = { showDeleteDialog = category }
                )
            }
        }
    }

    showDeleteDialog?.let { category ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text(stringResource(R.string.msg_delete_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.msg_confirm_delete)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCategory(category,
                        onSuccess = {
                            Toast.makeText(context, context.getString(R.string.msg_delete_category_successfully), Toast.LENGTH_SHORT).show()
                            showDeleteDialog = null
                        },
                        onError = {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            showDeleteDialog = null
                        })
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
            Text(stringResource(R.string.hint_search_category), fontSize = 14.sp, color = Color.Gray)
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
fun CategoryItem(
    category: Category,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, end = 1.dp, start = 1.dp, bottom = 1.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.name ?: "",
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimaryDark
            )
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

@Preview(showBackground = true)
@Composable
fun CategoryItemPreview() {
    CategoryItem(
        category = Category(id = 1, name = "Cà phê"),
        onClick = {},
        onEdit = {},
        onDelete = {}
    )
}