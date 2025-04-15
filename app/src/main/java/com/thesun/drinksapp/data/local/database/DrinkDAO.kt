package com.thesun.drinksapp.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thesun.drinksapp.data.model.Drink
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrink(drink: Drink)

    @get:Query("SELECT * FROM drink")
    val listDrinkCart: Flow<List<Drink>>

    @Query("SELECT * FROM drink WHERE id=:id")
    suspend fun checkDrinkInCart(id: Long): List<Drink>?

    @Delete
    suspend fun deleteDrink(drink: Drink)

    @Update
    suspend fun updateDrink(drink: Drink)

    @Query("DELETE from drink")
    suspend fun deleteAllDrink()
}