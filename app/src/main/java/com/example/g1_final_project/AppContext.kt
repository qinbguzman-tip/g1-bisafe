package com.example.g1_final_project

import android.app.Application
import com.fxn.stash.Stash
import com.google.firebase.FirebaseApp

class AppContext :Application() {

    override fun onCreate() {
        super.onCreate()
        Stash.init(applicationContext)
        FirebaseApp.initializeApp(applicationContext)
    }
}