package com.thesun.drinksapp.ui.admin.categories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.thesun.drinksapp.data.model.Category
import com.thesun.drinksapp.data.repository.CategoryRepository
import com.thesun.drinksapp.data.repository.DrinkRepository
import com.thesun.drinksapp.utils.StringUtil
import com.thesun.drinksapp.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AdminCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val drinkRepository: DrinkRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _searchKeyword = MutableStateFlow("")
    val searchKeyword: StateFlow<String> = _searchKeyword

    private var childEventListener: ChildEventListener? = null
    init {
        loadCategories("")
    }

    fun setSearchKeyword(keyword: String) {
        _searchKeyword.value = keyword
        loadCategories(keyword)
    }

    private fun loadCategories(keyword: String) {
        viewModelScope.launch {
            childEventListener?.let { categoryRepository.getCategoryRef().removeEventListener(it) }
            _categories.value = emptyList()

            childEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val category = snapshot.getValue(Category::class.java) ?: return
                    val currentList = _categories.value.toMutableList()

                    if (StringUtil.isEmpty(keyword)) {
                        currentList.add(category)
                    } else {
                        if (Utils.getTextSearch(category.name)
                                .lowercase(Locale.getDefault())
                                .trim()
                                .contains(
                                    Utils.getTextSearch(keyword)
                                        .lowercase(Locale.getDefault())
                                        .trim()
                                )
                        ) {
                            currentList.add(0, category)
                        }
                    }
                    _categories.value = currentList
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val updatedCategory = snapshot.getValue(Category::class.java) ?: return
                    val currentList = _categories.value.toMutableList()
                    val index = currentList.indexOfFirst { it.id == updatedCategory.id }
                    if (index != -1) {
                        currentList[index] = updatedCategory
                        _categories.value = currentList
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val removedCategory = snapshot.getValue(Category::class.java) ?: return
                    val currentList = _categories.value.toMutableList()
                    currentList.removeAll { it.id == removedCategory.id }
                    _categories.value = currentList
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            }
            categoryRepository.getCategoryRef().addChildEventListener(childEventListener!!)
        }
    }

    fun deleteCategory(category: Category, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val drinksSnapshot = drinkRepository.getDrinkRef()
                    .orderByChild("category_id")
                    .equalTo(category.id.toString())
                    .get()
                    .await()

                if (drinksSnapshot.exists() && drinksSnapshot.childrenCount > 0) {
                    onError("Không xóa được danh mục. Có ${drinksSnapshot.childrenCount} đồ uống liên quan.")
                    return@launch
                }

                categoryRepository.getCategoryRef()
                    .child(category.id.toString())
                    .removeValue { error, _ ->
                        if (error == null) onSuccess() else onError(error.message ?: "Lỗi xóa danh mục")
                    }
            } catch (e: Exception) {
                onError(e.message ?: "Lỗi kiểm tra đồ uống")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        childEventListener?.let { categoryRepository.getCategoryRef().removeEventListener(it) }
    }
}