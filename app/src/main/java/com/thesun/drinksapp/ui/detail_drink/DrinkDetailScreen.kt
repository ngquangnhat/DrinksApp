package com.thesun.drinksapp.ui.detail_drink

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.data.model.Topping
import com.thesun.drinksapp.utils.Constant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkDetailScreen(
    drinkId: Long,
    navController: NavController,
    viewModel: DrinkDetailViewModel = hiltViewModel()
) {
    viewModel.init(drinkId)
    val uiState by viewModel.uiState.collectAsState()
    val drink by viewModel.drink.collectAsState()

    DrinkDetailContent(
        drink = drink,
        toppings = uiState.toppings,
        quantity = uiState.quantity,
        variant = uiState.variant,
        size = uiState.size,
        sugar = uiState.sugar,
        ice = uiState.ice,
        notes = uiState.notes,
        totalPrice = uiState.totalPrice,
        onBackClick = { navController.popBackStack() },
        onCartClick = { navController.navigate("cart") },
        onQuantityChange = { increment -> viewModel.updateQuantity(increment) },
        onVariantChange = { viewModel.updateVariant(it) },
        onSizeChange = { viewModel.updateSize(it) },
        onSugarChange = { viewModel.updateSugar(it) },
        onIceChange = { viewModel.updateIce(it) },
        onNotesChange = { viewModel.updateNotes(it) },
        onToppingClick = { viewModel.toggleTopping(it) },
        onAddToCart = {
            viewModel.addToCart {
                navController.navigate("cart")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkDetailContent(
    drink: Drink?,
    toppings: List<Topping>,
    quantity: Int,
    variant: String,
    size: String,
    sugar: String,
    ice: String,
    notes: String,
    totalPrice: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onQuantityChange: (Boolean) -> Unit,
    onVariantChange: (String) -> Unit,
    onSizeChange: (String) -> Unit,
    onSugarChange: (String) -> Unit,
    onIceChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onToppingClick: (Long) -> Unit,
    onAddToCart: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(drink?.name ?: "Cafe Manager") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ hàng")
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(
                totalPrice = totalPrice,
                onAddToCart = onAddToCart
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                DrinkImage(drink?.banner)
                Spacer(modifier = Modifier.height(16.dp))
                DrinkInfo(
                    drink = drink,
                    quantity = quantity,
                    onQuantityChange = onQuantityChange
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomizationSection(
                    variant = variant,
                    size = size,
                    sugar = sugar,
                    ice = ice,
                    onVariantChange = onVariantChange,
                    onSizeChange = onSizeChange,
                    onSugarChange = onSugarChange,
                    onIceChange = onIceChange
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                ToppingSection(
                    toppings = toppings,
                    onToppingClick = onToppingClick
                )
                Spacer(modifier = Modifier.height(16.dp))
                NotesSection(
                    notes = notes,
                    onNotesChange = onNotesChange
                )
            }
        }
    }
}

@Composable
fun DrinkImage(banner: String?) {
    AsyncImage(
        model = banner ?: "https://via.placeholder.com/300",
        contentDescription = "Hình ảnh đồ uống",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun DrinkInfo(
    drink: Drink?,
    quantity: Int,
    onQuantityChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = drink?.name ?: "Cafe Manager",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onQuantityChange(false) }) {
                        Icon(Icons.Default.Remove, contentDescription = "Giảm")
                    }
                    Text(
                        text = quantity.toString(),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 16.sp
                    )
                    IconButton(onClick = { onQuantityChange(true) }) {
                        Icon(Icons.Default.Add, contentDescription = "Tăng")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Star, contentDescription = "Đánh giá", tint = Color.Yellow)
                Text(
                    text = "${drink?.rate ?: 0.0} (${drink?.countReviews ?: 0})",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Xếp hạng và đánh giá",
                    fontSize = 14.sp,
                    color = Color(0xFF6200EE),
                    modifier = Modifier.clickable { /* TODO: Điều hướng đến màn hình đánh giá */ }
                )
            }
        }
    }
}

@Composable
fun CustomizationSection(
    variant: String,
    size: String,
    sugar: String,
    ice: String,
    onVariantChange: (String) -> Unit,
    onSizeChange: (String) -> Unit,
    onSugarChange: (String) -> Unit,
    onIceChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Tùy chỉnh",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OptionRow(
                label = "Đồ uống",
                options = listOf(Topping.VARIANT_ICE, Topping.VARIANT_HOT),
                selectedOption = variant,
                onOptionSelected = onVariantChange
            )
            Spacer(modifier = Modifier.height(8.dp))
            OptionRow(
                label = "Kích thước",
                options = listOf(Topping.SIZE_REGULAR, Topping.SIZE_MEDIUM, Topping.SIZE_LARGE),
                selectedOption = size,
                onOptionSelected = onSizeChange
            )
            Spacer(modifier = Modifier.height(8.dp))
            OptionRow(
                label = "Đường",
                options = listOf(Topping.SUGAR_NORMAL, Topping.SUGAR_LESS),
                selectedOption = sugar,
                onOptionSelected = onSugarChange
            )
            Spacer(modifier = Modifier.height(8.dp))
            OptionRow(
                label = "Đá",
                options = listOf(Topping.ICE_NORMAL, Topping.ICE_LESS),
                selectedOption = ice,
                onOptionSelected = onIceChange
            )
        }
    }
}

@Composable
fun OptionRow(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        options.forEach { option ->
            Text(
                text = option,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (option == selectedOption) Color(0xFF6200EE) else Color.White
                    )
                    .border(1.dp, Color(0xFF6200EE), RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clickable { onOptionSelected(option) },
                color = if (option == selectedOption) Color.White else Color(0xFF6200EE),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ToppingSection(
    toppings: List<Topping>,
    onToppingClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Thêm lựa chọn khác",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            toppings.forEach { topping ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToppingClick(topping.id) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = topping.isSelected,
                        onCheckedChange = { onToppingClick(topping.id) }
                    )
                    Text(
                        text = "${topping.name} (+${topping.price}${Constant.CURRENCY})",
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NotesSection(
    notes: String,
    onNotesChange: (String) -> Unit
) {
    Column {
        Text(
            text = "Ghi chú",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            placeholder = { Text("Tùy chọn") },
            maxLines = 4
        )
    }
}

@Composable
fun BottomBar(
    totalPrice: Int,
    onAddToCart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Tổng cộng", fontSize = 14.sp)
            Text(
                text = "$totalPrice${Constant.CURRENCY}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EE)
            )
        }
        Button(
            onClick = onAddToCart,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D6E63))
        ) {
            Text("Thêm vào giỏ hàng", fontSize = 16.sp, color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DrinkDetailContentPreview() {
    val sampleDrink = Drink(
        id = 1,
        name = "Cafe Manager",
        banner = "https://via.placeholder.com/300"
    )
    val sampleToppings = listOf(
        Topping(id = 0, name = "Item 0", price = 5000),
        Topping(id = 1, name = "Item 1", price = 5000),
        Topping(id = 2, name = "Item 2", price = 5000),
        Topping(id = 4, name = "Item 4", price = 5000),
        Topping(id = 5, name = "Item 5", price = 5000),
        Topping(id = 6, name = "Item 6", price = 5000)
    )
    DrinkDetailContent(
        drink = sampleDrink,
        toppings = sampleToppings,
        quantity = 1,
        variant = Topping.VARIANT_ICE,
        size = Topping.SIZE_MEDIUM,
        sugar = Topping.SUGAR_NORMAL,
        ice = Topping.ICE_NORMAL,
        notes = "",
        totalPrice = 0,
        onBackClick = {},
        onCartClick = {},
        onQuantityChange = {},
        onVariantChange = {},
        onSizeChange = {},
        onSugarChange = {},
        onIceChange = {},
        onNotesChange = {},
        onToppingClick = {},
        onAddToCart = {}
    )
}