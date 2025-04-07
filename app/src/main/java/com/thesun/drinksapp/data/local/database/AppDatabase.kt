package com.thesun.drinksapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thesun.drinksapp.data.model.Drink

@Database(entities = [Drink::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun drinkDAO(): DrinkDAO

}