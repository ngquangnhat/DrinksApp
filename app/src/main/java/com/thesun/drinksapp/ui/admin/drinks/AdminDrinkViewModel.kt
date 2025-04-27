package com.thesun.drinksapp.ui.admin.drinks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.thesun.drinksapp.data.model.Category
import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.data.repository.DrinkRepository
import com.thesun.drinksapp.utils.StringUtil
import com.thesun.drinksapp.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AdminDrinkViewModel @Inject constructor(
    private val drinkRepository: DrinkRepository
) : ViewModel() {

    private val _drinks = MutableStateFlow<List<Drink>>(emptyList())
    val drinks: StateFlow<List<Drink>> = _drinks

    private val _searchKeyword = MutableStateFlow("")
    val searchKeyword: StateFlow<String> = _searchKeyword

    private var childEventListener: ChildEventListener? = null
    private var category: Category? = null

    init {
        loadDrinks("")
    }

    fun setSearchKeyword(keyword: String) {
        _searchKeyword.value = keyword
        loadDrinks(keyword)
    }

    private fun loadDrinks(keyword: String) {
        viewModelScope.launch {
            childEventListener?.let { drinkRepository.getDrinkRef().removeEventListener(it) }
            _drinks.value = emptyList()

            childEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val drink = snapshot.getValue(Drink::class.java) ?: return
                    val currentList = _drinks.value.toMutableList()
                    if (StringUtil.isEmpty(keyword)) {
                        currentList.add(drink)
                    } else {
                        if (Utils.getTextSearch(drink.name)
                                .lowercase(Locale.getDefault())
                                .trim()
                                .contains(
                                    Utils.getTextSearch(keyword)
                                        .lowercase(Locale.getDefault())
                                        .trim()
                                )
                        ) {
                            currentList.add(drink)
                        }
                    }
                    _drinks.value = currentList.sortedBy { it.id }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val updatedDrink = snapshot.getValue(Drink::class.java) ?: return
                    val currentList = _drinks.value.toMutableList()
                    val index = currentList.indexOfFirst { it.id == updatedDrink.id }
                    if (index != -1) {
                        currentList[index] = updatedDrink
                        _drinks.value = currentList.sortedBy { it.id }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val removedDrink = snapshot.getValue(Drink::class.java) ?: return
                    val currentList = _drinks.value.toMutableList()
                    currentList.removeAll { it.id == removedDrink.id }
                    _drinks.value = currentList
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            }
            drinkRepository.getDrinkRef()
                .addChildEventListener(childEventListener!!)
        }
    }

    fun deleteDrink(drink: Drink, onSuccess: () -> Unit) {
        drinkRepository.getDrinkRef()
            .child(drink.id.toString())
            .removeValue { error, _ ->
                if (error == null) {
                    onSuccess()
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        childEventListener?.let { drinkRepository.getDrinkRef().removeEventListener(it) }
    }
}