package com.thesun.drinksapp.ui.user.home_tab

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.thesun.drinksapp.data.model.Category
import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.data.model.Filter
import com.thesun.drinksapp.data.repository.CategoryRepository
import com.thesun.drinksapp.data.repository.DrinkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val drinkRepository: DrinkRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _allDrinks = MutableStateFlow<List<Drink>>(emptyList())
    val allDrinks: StateFlow<List<Drink>> = _allDrinks
    private val _searchKeyword = MutableStateFlow("")
    val searchKeyword: StateFlow<String> = _searchKeyword

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _selectedFilters = MutableStateFlow<Map<Long, Filter>>(emptyMap())
    val selectedFilters: StateFlow<Map<Long, Filter>> = _selectedFilters.asStateFlow()

    fun updateFilter(categoryId: Long, newFilter: Filter) {
        _selectedFilters.update { currentFilters ->
            currentFilters.toMutableMap().apply {
                this[categoryId] = newFilter
            }
        }
    }

    private var drinkListener: ValueEventListener? = null
    private var categoryListener: ValueEventListener? = null
    private var originalDrinks: List<Drink> = emptyList()


    init {
        fetchDrinks()
        fetchCategories()
    }

    fun onSearchKeywordChange(keyword: String) {
        _searchKeyword.value = keyword
        applySearchFilter()
    }

    private fun fetchDrinks() {
        _loading.value = true
        drinkListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val drinks = snapshot.children.mapNotNull {
                    it.getValue(Drink::class.java)
                }
                originalDrinks = drinks
                applySearchFilter()
                _loading.value = false
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        drinkRepository.getDrinkRef().addValueEventListener(drinkListener!!)
    }


    private fun fetchCategories() {
        _loading.value = true
        categoryListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cats = snapshot.children.mapNotNull {
                    it.getValue(Category::class.java)
                }
                _categories.value = cats
                _loading.value = false
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        categoryRepository.getCategoryRef().addValueEventListener(categoryListener!!)
    }
    private fun applySearchFilter() {
        val keyword = _searchKeyword.value.trim()
        _allDrinks.value = if (keyword.isEmpty()) {
            originalDrinks
        } else {
            originalDrinks.filter { it.name?.contains(keyword, ignoreCase = true) == true }
        }
    }


    override fun onCleared() {
        super.onCleared()
        drinkListener?.let {
            drinkRepository.getDrinkRef().removeEventListener(it)
        }
        categoryListener?.let {
            categoryRepository.getCategoryRef()?.removeEventListener(it)
        }
    }
}
