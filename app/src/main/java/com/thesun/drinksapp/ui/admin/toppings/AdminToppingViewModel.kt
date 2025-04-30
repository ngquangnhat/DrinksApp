package com.thesun.drinksapp.ui.admin.toppings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.thesun.drinksapp.data.model.Topping
import com.thesun.drinksapp.data.repository.DetailDrinkRepository
import com.thesun.drinksapp.utils.GlobalFunction
import com.thesun.drinksapp.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AdminToppingViewModel @Inject constructor(
    private val detailDrinkRepository: DetailDrinkRepository
) : ViewModel() {

    private val _toppings = MutableStateFlow<List<Topping>>(emptyList())
    val toppings: StateFlow<List<Topping>> = _toppings.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var childEventListener: ChildEventListener? = null

    init {
        loadToppings("")
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        loadToppings(query)
    }

    private fun loadToppings(keyword: String) {
        viewModelScope.launch {
            childEventListener?.let {
                detailDrinkRepository.getToppingRef().removeEventListener(it)
            }
            _toppings.value = emptyList()

            childEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    val topping = dataSnapshot.getValue(Topping::class.java) ?: return
                    val currentList = _toppings.value.toMutableList()
                    if (keyword.isBlank()) {
                        currentList.add(topping)
                    } else {
                        if (Utils.getTextSearch(topping.name)
                                .lowercase(Locale.getDefault())
                                .trim()
                                .contains(
                                    Utils.getTextSearch(keyword)
                                        .lowercase(Locale.getDefault())
                                        .trim()
                                )
                        ) {
                            currentList.add(topping)
                        }
                    }
                    _toppings.value = currentList.sortedBy { it.id }
                }

                override fun onChildChanged(
                    dataSnapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    val updatedTopping = dataSnapshot.getValue(Topping::class.java) ?: return
                    val currentList = _toppings.value.toMutableList()
                    val index = currentList.indexOfFirst { it.id == updatedTopping.id }
                    if (index != -1) {
                        currentList[index] = updatedTopping
                        _toppings.value = currentList.sortedBy { it.id }
                    }
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    val removedTopping = dataSnapshot.getValue(Topping::class.java) ?: return
                    val currentList = _toppings.value.toMutableList()
                    currentList.removeAll { it.id == removedTopping.id }
                    _toppings.value = currentList
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            }

            detailDrinkRepository.getToppingRef()
                .addChildEventListener(childEventListener!!)
        }
    }

    fun deleteTopping(topping: Topping, onComplete: (Boolean) -> Unit) {
        detailDrinkRepository.getToppingRef()
            .child(topping.id.toString())
            .removeValue { error, _ ->
                onComplete(error == null)
            }
    }

    override fun onCleared() {
        childEventListener?.let {
            detailDrinkRepository.getToppingRef().removeEventListener(it)
        }
        super.onCleared()
    }
}