package com.gmail.sofiapatiy

import androidx.lifecycle.ViewModel
import com.google.firebase.database.BuildConfig
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger

class MainActivityViewModel : ViewModel() {
    init {
        if (BuildConfig.DEBUG) {
            FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG)
        }
    }
}