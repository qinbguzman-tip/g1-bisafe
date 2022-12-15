package com.example.g1_final_project.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.g1_final_project.models.UserModel
import com.fxn.stash.Stash

object Constants {
    const val USER_MODEL = "USER_MODEL"
    const val IS_LOGGED_IN = "IS_LOGGED_IN"
    const val CURRENT_MILEAGES = "CURRENT_MILEAGES"
    const val HOURS = "HOURS"
    const val MINUTES = "MINUTES"
    const val CURRENT_TIME = "CURRENT_TIME"
    const val HISTORY = "HISTORY"
    fun auth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    fun databaseReference(): DatabaseReference {
        val db = FirebaseDatabase.getInstance().reference.child("BikeJourneyApp")
        db.keepSynced(true)
        return db
    }

    fun userModel(): UserModel {
        return Stash.getObject<Any>(USER_MODEL, UserModel::class.java) as UserModel
    }
}