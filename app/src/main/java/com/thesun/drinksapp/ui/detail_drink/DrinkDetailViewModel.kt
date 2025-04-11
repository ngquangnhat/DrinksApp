package com.thesun.drinksapp.ui.detail_drink

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.thesun.drinksapp.data.local.database.DrinkDAO
import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.data.model.Topping
import com.thesun.drinksapp.data.repository.DetailDrinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrinkDetailViewModel @Inject constructor(
    private val drinkDao: DrinkDAO,
    private val drinkDetailRepository: DetailDrinkRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DrinkDetailUiState())
    val uiState: StateFlow<DrinkDetailUiState> = _uiState.asStateFlow()

    private val _drink = MutableStateFlow<Drink?>(null)
    val drink: StateFlow<Drink?> = _drink.asStateFlow()

    private var drinkValueEventListener: ValueEventListener? = null
    private var toppingValueEventListener: ValueEventListener? = null

    private var drinkOld: Drink? = null

    fun init(drinkId: Long, drinkOld: Drink? = null) {
        this.drinkOld = drinkOld
        _uiState.update { it.copy(drinkId = drinkId) }
        loadDrinkDetails(drinkId)
        loadToppings()
        drinkOld?.let {
            val currentToppings = _uiState.value.toppings
            if (currentToppings.isNotEmpty()) {
                restorePreviousToppingSelections(it, currentToppings)
            }
        }
    }

    private fun loadDrinkDetails(drinkId: Long) {
        if (drinkId == 0L) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Không tìm thấy ID đồ uống"
                )
            }
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        drinkValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val drinkData = snapshot.getValue(Drink::class.java)
                if (drinkData == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Không tìm thấy đồ uống với ID: $drinkId"
                        )
                    }
                    return
                }
                _drink.value = drinkData
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalPrice = calculateTotalPrice(drinkData, it.quantity, it.toppings)
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Lỗi khi tải dữ liệu đồ uống: ${error.message}"
                    )
                }
            }
        }
        drinkDetailRepository.getDetailDrinkRef(drinkId)
            .addValueEventListener(drinkValueEventListener!!)
    }

    private fun loadToppings() {
        toppingValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newToppings = snapshot.children.mapNotNull { it.getValue(Topping::class.java) }
                val currentToppings = _uiState.value.toppings.associateBy { it.id }
                val updatedToppings = newToppings.map { newTopping ->
                    val existingTopping = currentToppings[newTopping.id]
                    newTopping.copy(isSelected = existingTopping?.isSelected ?: false)
                }
                _uiState.update { it.copy(toppings = updatedToppings) }
                drinkOld?.let { restorePreviousToppingSelections(it, updatedToppings) }
            }

            override fun onCancelled(error: DatabaseError) {
                _uiState.update {
                    it.copy(error = "Lỗi khi tải topping: ${error.message}")
                }
            }
        }
        drinkDetailRepository.getToppingRef().addValueEventListener(toppingValueEventListener!!)
    }

    private fun restorePreviousSelections(drink: Drink) {
        _uiState.update {
            it.copy(
                quantity = drink.count,
                variant = drink.variant ?: Topping.VARIANT_ICE,
                size = drink.size ?: Topping.SIZE_REGULAR,
                sugar = drink.sugar ?: Topping.SUGAR_NORMAL,
                ice = drink.ice ?: Topping.ICE_NORMAL,
                notes = drink.note ?: ""
            )
        }
        updateTotalPrice()
    }

    private fun restorePreviousToppingSelections(drink: Drink, toppings: List<Topping>) {
        val selectedToppingIds = drink.toppingIds?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList()
        val updatedToppings = toppings.map { topping ->
            topping.copy(isSelected = selectedToppingIds.contains(topping.id))
        }
        _uiState.update { it.copy(toppings = updatedToppings) }
        updateTotalPrice()
    }

    fun updateQuantity(increment: Boolean) {
        _uiState.update {
            val newQuantity = if (increment) it.quantity + 1 else (it.quantity - 1).coerceAtLeast(1)
            it.copy(quantity = newQuantity)
        }
        updateTotalPrice()
    }

    fun updateVariant(variant: String) {
        _uiState.update { it.copy(variant = variant) }
    }

    fun updateSize(size: String) {
        _uiState.update { it.copy(size = size) }
    }

    fun updateSugar(sugar: String) {
        _uiState.update { it.copy(sugar = sugar) }
    }

    fun updateIce(ice: String) {
        _uiState.update { it.copy(ice = ice) }
    }

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun toggleTopping(toppingId: Long) {
        _uiState.update {
            val updatedToppings = it.toppings.map { topping ->
                if (topping.id == toppingId) topping.copy(isSelected = !topping.isSelected) else topping
            }
            it.copy(toppings = updatedToppings)
        }
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        _uiState.update {
            it.copy(totalPrice = calculateTotalPrice(_drink.value, it.quantity, it.toppings))
        }
    }

    private fun calculateTotalPrice(drink: Drink?, quantity: Int, toppings: List<Topping>): Int {
        if (drink == null) return 0
        val toppingPrice = toppings.filter { it.isSelected }.sumOf { it.price }
        val priceOneDrink = drink.realPrice + toppingPrice
        return priceOneDrink * quantity
    }

    fun addToCart(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val drink = _drink.value?.copy(
                count = _uiState.value.quantity,
                variant = _uiState.value.variant,
                size = _uiState.value.size,
                sugar = _uiState.value.sugar,
                ice = _uiState.value.ice,
                note = _uiState.value.notes.takeIf { it.isNotBlank() },
                toppingIds = _uiState.value.toppings.filter { it.isSelected }
                    .joinToString(",") { it.id.toString() },
                option = getAllOptions(),
                priceOneDrink = (_drink.value?.realPrice ?: 0) +
                        _uiState.value.toppings.filter { it.isSelected }.sumOf { it.price },
                totalPrice = _uiState.value.totalPrice
            ) ?: return@launch

            if (isDrinkInCart(drink.id)) {
                drinkDao.updateDrink(drink)
            } else {
                drinkDao.insertDrink(drink)
            }
            onSuccess()
        }
    }

    private suspend fun isDrinkInCart(drinkId: Long): Boolean {
        return drinkDao.checkDrinkInCart(drinkId)?.isNotEmpty() == true
    }

    private fun getAllOptions(): String {
        val options = mutableListOf<String>()
        with(_uiState.value) {
            options.add("Loại: $variant")
            options.add("Kích cỡ: $size")
            options.add("Đường: $sugar")
            options.add("Đá: $ice")
            val selectedToppings = toppings.filter { it.isSelected }.joinToString(", ") { it.name.toString() }
            if (selectedToppings.isNotBlank()) options.add("Topping: $selectedToppings")
            if (notes.isNotBlank()) options.add("Ghi chú: $notes")
        }
        return options.joinToString(", ")
    }

    override fun onCleared() {
        super.onCleared()
        drinkValueEventListener?.let {
            _uiState.value.drinkId?.let { it1 -> drinkDetailRepository.getDetailDrinkRef(it1).removeEventListener(it) }
        }
        toppingValueEventListener?.let {
            drinkDetailRepository.getToppingRef().removeEventListener(it)
        }
    }
}