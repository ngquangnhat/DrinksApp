package com.thesun.drinksapp.ui.detail_drink

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.data.model.Topping
import com.thesun.drinksapp.ui.theme.BgFilter
import com.thesun.drinksapp.ui.theme.BgMainColor
import com.thesun.drinksapp.ui.theme.ColorAccent
import com.thesun.drinksapp.ui.theme.ColorPrimary
import com.thesun.drinksapp.ui.theme.ColorPrimaryDark
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
                navController.popBackStack()
//                navController.navigate("cart")
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
                title = { Text(drink?.name ?: "Cafe Manager", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ hàng")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomBar(
                totalPrice = totalPrice,
                onAddToCart = onAddToCart
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(Color.White),
        ) {
            DrinkImage(drink?.banner)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-50).dp)
                    .padding(top = 0.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                DrinkInfo(
                    drink = drink,
                    quantity = quantity,
                    onQuantityChange = onQuantityChange
                )
                Spacer(modifier = Modifier.height(12.dp))
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
                Spacer(modifier = Modifier.height(12.dp))
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
            .height(300.dp)
            .padding(top = 8.dp),
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
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ColorAccent, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = drink?.name ?: "Cafe Manager",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = (drink?.realPrice.toString() + Constant.CURRENCY),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = (drink?.description ?: "Cafe Manager"),
                    fontSize = 14.sp,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .border(
                                1.dp, ColorAccent, RoundedCornerShape(
                                    topStart = 4.dp,
                                    bottomStart = 4.dp
                                )
                            )
                            .clickable {
                                onQuantityChange(false)
                            }
                    ) {
                        Text(
                            text = "-",
                            modifier = Modifier
                                .align(Alignment.Center),
                            fontSize = 18.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .offset(x = (-1).dp)
                            .height(20.dp)
                            .border(1.dp, ColorAccent, RoundedCornerShape(0.dp))
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = quantity.toString(),
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 14.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .offset(x = (-2).dp)
                            .border(
                                1.dp, ColorAccent, RoundedCornerShape(
                                    topEnd = 4.dp,
                                    bottomEnd = 4.dp
                                )
                            )
                            .clickable {
                                onQuantityChange(true)
                            }
                    ) {
                        Text(
                            text = "+",
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 18.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star_yellow),
                        tint = Color(0xFFfbb909),
                        contentDescription = "Đánh giá",
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${drink?.rate ?: 0.0} (${drink?.countReviews ?: 0})",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "-",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Xếp hạng và đánh giá",
                        fontSize = 12.sp,
                        color = ColorPrimary,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clickable { }
                    )
                }
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Giỏ hàng",
                    modifier = Modifier.size(16.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ColorAccent, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp)
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
            Spacer(modifier = Modifier.height(10.dp))
            OptionRow(
                label = "Kích thước",
                options = listOf(Topping.SIZE_REGULAR, Topping.SIZE_MEDIUM, Topping.SIZE_LARGE),
                selectedOption = size,
                onOptionSelected = onSizeChange
            )
            Spacer(modifier = Modifier.height(10.dp))
            OptionRow(
                label = "Đường",
                options = listOf(Topping.SUGAR_NORMAL, Topping.SUGAR_LESS),
                selectedOption = sugar,
                onOptionSelected = onSugarChange
            )
            Spacer(modifier = Modifier.height(10.dp))
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
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        options.forEach { option ->
            Text(
                text = option,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (option == selectedOption) ColorPrimary else Color.White
                    )
                    .border(1.dp, ColorPrimary, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clickable { onOptionSelected(option) },
                color = if (option == selectedOption) Color.White else ColorPrimary,
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
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ColorAccent, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp)
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
                        .clickable { onToppingClick(topping.id) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        topping.name?.let {
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                    Text(
                        text = "+${topping.price}${Constant.CURRENCY}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorPrimaryDark,
                    )
                    Checkbox(
                        checked = topping.isSelected,
                        onCheckedChange = { onToppingClick(topping.id) },
                        modifier = Modifier
                            .size(32.dp)
                            .padding(start = 8.dp),
                        colors = CheckboxDefaults.colors(
                            checkedColor = ColorPrimaryDark,
                            uncheckedColor = ColorAccent
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            maxLines = 4,
            shape = RoundedCornerShape(6.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.LightGray,
                disabledBorderColor = Color.Gray,
                cursorColor = Color.Black,
                containerColor = Color.White
            ),
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Black
            ),
            singleLine = false
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
            .background(BgFilter)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Tổng tiền", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$totalPrice${Constant.CURRENCY}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimary
            )
        }
        Button(
            onClick = onAddToCart,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
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
        banner = "https://hoanghamobile.com/tin-tuc/wp-content/uploads/2024/08/anh-cafe.jpg"
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