package com.thesun.drinksapp.data.local.di

import android.content.Context
import androidx.room.Room
import com.thesun.drinksapp.data.local.database.AppDatabase
import com.thesun.drinksapp.data.local.database.DrinkDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDrinkDAO(appDatabase: AppDatabase): DrinkDAO {
        return appDatabase.drinkDAO()
    }

    @Provides
    @Singleton
    fun provideAppDatabase (@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }
}