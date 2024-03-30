package com.ferhatozcelik.firebase

import android.app.Activity
import com.google.firebase.FirebaseApp

/**
 * Created by ferhatozcelik on 27.03.2024.
 */
fun Activity.initializeFirebase() {
  val firebaseApp = FirebaseApp.initializeApp(this.applicationContext) ?: throw IllegalStateException("FirebaseApp initialization failed.")

}