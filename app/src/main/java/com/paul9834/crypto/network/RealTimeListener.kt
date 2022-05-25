package com.paul9834.crypto.network

import com.google.firebase.firestore.FirebaseFirestoreException
import java.lang.Exception

interface RealTimeListener <T> {

    fun onDataChanged(updateData: T)

    fun onError(exception: Exception)

    fun onErrorFirebase (firebaseFirestoreException: FirebaseFirestoreException)

}