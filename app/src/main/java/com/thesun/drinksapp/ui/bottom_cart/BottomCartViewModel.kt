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


    private val _cartList = MutableStateFlow<List<Drink>>(emptyList())
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