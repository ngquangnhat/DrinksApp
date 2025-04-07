package com.thesun.drinksapp.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.thesun.drinksapp.data.model.Drink
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkDAO {
    @Insert
    fun insertDrink(drink: Drink)

    @get:Query("SELECT * FROM drink")
    val listDrinkCart: Flow<List<Drink>>

    @Query("SELECT * FROM drink WHERE id=:id")
    fun checkDrinkInCart(id: Long): List<Drink>?

    @Delete
    fun deleteDrink(drink: Drink)

    @Update
    fun updateDrink(drink: Drink)

    @Query("DELETE from drink")
    fun deleteAllDrink()
}