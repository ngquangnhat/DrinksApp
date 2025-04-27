package com.thesun.drinksapp.ui.admin.categories.add_category

import android.app.Application
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.thesun.drinksapp.R
import com.thesun.drinksapp.data.model.Category
import com.thesun.drinksapp.data.repository.CategoryRepository
import com.thesun.drinksapp.utils.StringUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val application: Application
) : ViewModel() {

    private val context = application.applicationContext

    private val _categoryName = MutableStateFlow("")
    val categoryName: StateFlow<String> = _categoryName

    private val _isUpdate = MutableStateFlow(false)
    val isUpdate: StateFlow<Boolean> = _isUpdate

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun setCategoryName(name: String) {
        _categoryName.value = name
    }

    fun loadCategoryById(categoryId: String?) {
        if (categoryId != null) {
            categoryRepository.getCategoryRef().child(categoryId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val category = snapshot.getValue(Category::class.java)
                        setCategoryData(category)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _toastMessage.value = "Lỗi khi tải danh mục: ${error.message}"
                    }
                })
        } else {
            setCategoryData(null)
        }
    }

    private fun setCategoryData(category: Category?) {
        if (category != null) {
            _isUpdate.value = true
            _selectedCategory.value = category
            _categoryName.value = category.name ?: ""
        } else {
            _isUpdate.value = false
            _selectedCategory.value = null
            _categoryName.value = ""
        }
    }

    fun addOrEditCategory(onSuccess: () -> Unit, onAddSuccess: () -> Unit) {
        val name = _categoryName.value.trim()
        if (StringUtil.isEmpty(name)) {
            _toastMessage.value = context.getString(R.string.msg_name_require)
            return
        }

        _isLoading.value = true
        if (_isUpdate.value) {
            val categoryId = _selectedCategory.value?.id.toString()
            val map = mapOf("name" to name)
            categoryRepository.getCategoryRef().child(categoryId)
                .updateChildren(map) { error: DatabaseError?, _: DatabaseReference? ->
                    _isLoading.value = false
                    if (error == null) {
                        _toastMessage.value = application.getString(R.string.msg_edit_category_success)
                        onSuccess()
                    }
                }
        } else {
            categoryRepository.getCategoryRef()
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val newCategoryId = System.currentTimeMillis()
                        val newCategory = Category(
                            id = newCategoryId,
                            name = name
                        )
                        categoryRepository.getCategoryRef().child(newCategoryId.toString())
                            .setValue(newCategory) { error: DatabaseError?, _: DatabaseReference? ->
                                _isLoading.value = false
                                if (error == null) {
                                    _categoryName.value = ""
                                    _toastMessage.value = application.getString(R.string.msg_add_category_success)
                                    onAddSuccess()
                                }
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _isLoading.value = false
                        _toastMessage.value = "Lỗi khi lấy số lượng danh mục: ${error.message}"
                    }
                })
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}