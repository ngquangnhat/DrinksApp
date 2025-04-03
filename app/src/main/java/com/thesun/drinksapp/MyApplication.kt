package com.thesun.drinksapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.thesun.drinksapp.prefs.DataStoreManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application()  {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        DataStoreManager.init(this)
    }
}