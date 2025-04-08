package com.thesun.drinksapp.ui.bottom_cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesun.drinksapp.data.local.database.DrinkDAO
import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.data.model.Rating
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomCartViewModel @Inject constructor(
    private val drinkDao: DrinkDAO
) : ViewModel() {

//    private val mockDrink = Drink(
//        id = 1,
//        name = "Cà phê sữa đá",
//        description = "Cà phê sữa đá thơm ngon",
//        price = 40,
//        image = "https://example.com/image.jpg",
//        banner = "https://example.com/banner.jpg",
//        categoryId = 1,
//        categoryName = "Cà phê",
//        sale = 10,  // Giảm giá 10%
//        isFeatured = true,
//        count = 100,
//        totalPrice = 40,
//        priceOneDrink = 40,
//        option = "Không đường",
//        variant = "Vị ngọt",
//        size = "L",
//        sugar = "Ít đường",
//        ice = "Đá đầy",
//        toppingIds = "1,2",
//        note = "Không đá",
//        rating = hashMapOf(
//            "user123" to Rating(rate = 5.0, review = "Rất ngon!"),
//            "user456" to Rating(rate = 4.0, review = "Tạm ổn")
//        )
//    )

    private val _cartList = MutableStateFlow<List<Drink>>(emptyList())
    //    private val _cartList = MutableStateFlow<List<Drink>>(
//        listOf(
//            mockDrink,
//            mockDrink.copy(2, price = 50)
//        )
//    )
    val cartList: StateFlow<List<Drink>> = _cartList.asStateFlow()

    val totalAmount: StateFlow<Int> = cartList.map { list ->
        list.sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    init {
        viewModelScope.launch {
            drinkDao.listDrinkCart.collect { list ->
                _cartList.value = list
            }
        }
    }
}