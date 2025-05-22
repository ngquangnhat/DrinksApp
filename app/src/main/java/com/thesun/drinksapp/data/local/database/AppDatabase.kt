package com.thesun.drinksapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thesun.drinksapp.data.model.CartState
import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.utils.Converters

@Database(entities = [Drink::class, CartState::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun drinkDAO(): DrinkDAO

}