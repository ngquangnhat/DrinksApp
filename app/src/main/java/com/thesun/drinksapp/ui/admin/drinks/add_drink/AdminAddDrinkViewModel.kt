package com.thesun.drinksapp.ui.admin.drinks.add_drink

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.thesun.drinksapp.data.model.Category
import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.data.repository.CategoryRepository
import com.thesun.drinksapp.data.repository.DrinkRepository
import com.thesun.drinksapp.utils.StringUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminAddDrinkViewModel @Inject constructor(
    private val drinkRepository: DrinkRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _price = MutableStateFlow("")
    val price: StateFlow<String> = _price

    private val _promotion = MutableStateFlow("0")
    val promotion: StateFlow<String> = _promotion

    private val _image = MutableStateFlow("")
    val image: StateFlow<String> = _image

    private val _imageBanner = MutableStateFlow("")
    val imageBanner: StateFlow<String> = _imageBanner

    private val _isFeatured = MutableStateFlow(false)
    val isFeatured: StateFlow<Boolean> = _isFeatured

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory

    private var valueEventListener: ValueEventListener? = null
    private var drinkListener: ValueEventListener? = null
    private var currentDrinkId: String? = null
    private var oldImageUrl: String? = null
    private var oldBannerUrl: String? = null

    init {
        loadCategories()
    }

    fun setName(value: String) {
        _name.value = value
    }

    fun setDescription(value: String) {
        _description.value = value
    }

    fun setPrice(value: String) {
        _price.value = value
    }

    fun setPromotion(value: String) {
        _promotion.value = value
    }

    fun setImage(value: String) {
        _image.value = value
    }

    fun setImageBanner(value: String) {
        _imageBanner.value = value
    }

    fun setIsFeatured(value: Boolean) {
        _isFeatured.value = value
    }

    fun setSelectedCategory(category: Category) {
        _selectedCategory.value = category
    }

    private fun loadCategories() {
        viewModelScope.launch {
            valueEventListener?.let { categoryRepository.getCategoryRef().removeEventListener(it) }
            valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Category>()
                    for (dataSnapshot in snapshot.children) {
                        val category = dataSnapshot.getValue(Category::class.java) ?: continue
                        list.add(category)
                    }
                    _categories.value = list.sortedBy { it.id }
                    if (list.isNotEmpty() && _selectedCategory.value == null) {
                        _selectedCategory.value = list[0]
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            }
            categoryRepository.getCategoryRef()
                .addValueEventListener(valueEventListener!!)
        }
    }

    fun loadDrink(drinkId: String) {
        currentDrinkId = drinkId
        drinkListener?.let { drinkRepository.getDrinkRef().removeEventListener(it) }
        drinkListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val drink = snapshot.getValue(Drink::class.java) ?: return
                _name.value = drink.name ?: ""
                _description.value = drink.description ?: ""
                _price.value = drink.price?.toString() ?: ""
                _promotion.value = drink.sale?.toString() ?: "0"
                _image.value = drink.image ?: ""
                _imageBanner.value = drink.banner ?: ""
                _isFeatured.value = drink.isFeatured ?: false
                oldImageUrl = drink.image
                oldBannerUrl = drink.banner
                val category = _categories.value.find { it.id == drink.categoryId }
                if (category != null) {
                    _selectedCategory.value = category
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        drinkRepository.getDrinkRef()
            .child(drinkId)
            .addListenerForSingleValueEvent(drinkListener!!)
    }

    fun addOrEditDrink(isUpdate: Boolean, onComplete: (Boolean) -> Unit) {
        if (StringUtil.isEmpty(name.value)) {
            onComplete(false)
            return
        }
        if (StringUtil.isEmpty(description.value)) {
            onComplete(false)
            return
        }
        if (StringUtil.isEmpty(price.value)) {
            onComplete(false)
            return
        }
        if (StringUtil.isEmpty(image.value)) {
            onComplete(false)
            return
        }
        if (StringUtil.isEmpty(imageBanner.value)) {
            onComplete(false)
            return
        }
        if (selectedCategory.value == null) {
            onComplete(false)
            return
        }

        if (isUpdate) {
            val map = mutableMapOf<String, Any?>()
            map["name"] = name.value
            map["description"] = description.value
            map["price"] = price.value.toIntOrNull() ?: 0
            map["sale"] = promotion.value.toIntOrNull() ?: 0
            map["image"] = image.value
            map["banner"] = imageBanner.value
            map["isFeatured"] = isFeatured.value
            map["categoryId"] = selectedCategory.value!!.id
            map["categoryName"] = selectedCategory.value!!.name
            drinkRepository.getDrinkRef()
                .child(currentDrinkId!!)
                .updateChildren(map) { error, _ ->
                    onComplete(error == null)
                }
        } else {
            val drinkId = System.currentTimeMillis()
            val drink = Drink(
                id = drinkId,
                name = name.value,
                description = description.value,
                price = price.value.toIntOrNull() ?: 0,
                sale = promotion.value.toIntOrNull() ?: 0,
                image = image.value,
                banner = imageBanner.value,
                isFeatured = isFeatured.value,
                categoryId = selectedCategory.value!!.id,
                categoryName = selectedCategory.value!!.name
            )
            drinkRepository.getDrinkRef()
                .child(drinkId.toString())
                .setValue(drink) { error, _ ->
                    if (error != null) {
                    }
                    onComplete(error == null)
                }
        }
    }

    fun resetFields() {
        _name.value = ""
        _description.value = ""
        _price.value = ""
        _promotion.value = "0"
        _image.value = ""
        _imageBanner.value = ""
        _isFeatured.value = false
        _selectedCategory.value = _categories.value.firstOrNull()
    }

    override fun onCleared() {
        super.onCleared()
        valueEventListener?.let { categoryRepository.getCategoryRef().removeEventListener(it) }
        drinkListener?.let { drinkRepository.getDrinkRef().removeEventListener(it) }
    }
}