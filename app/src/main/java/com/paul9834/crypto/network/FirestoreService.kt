package com.paul9834.crypto.network

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.paul9834.crypto.firestore.model.Crypto
import com.paul9834.crypto.firestore.model.User


const val CRYPTO_COLLECTION_NAME = "cryptos"
const val USERS_COLLECTION_NAME = "users"


class FirestoreService (val firebaseFirestore: FirebaseFirestore){

    fun setDocument(data: Any, collectionName: String, id:String, callback: Callback<Void>) {
        firebaseFirestore.collection(collectionName).document(id).set(data)
            .addOnSuccessListener {
                callback.onSucess(null)
            }
            .addOnFailureListener {
                callback.onFailed(it)
            }
    }

    fun updateUser(user:User, callback:Callback<User>?) {

        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(user.username)
            .update("cryptoList", user.cryptoList)
            .addOnSuccessListener {
                callback?.onSucess(user)
            }
            .addOnFailureListener {
                callback?.onFailed(it)
            }
    }

    fun updateCrypto (crypto:Crypto) {
        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME).document(crypto.getDocumentId())
            .update("avalaible", crypto.avalaible)
    }

    fun getCryptosList(callback: Callback<List<Crypto>>?) {

        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val cryptoList = result.toObjects(Crypto::class.java)
                    callback!!.onSucess(cryptoList)
                    break
                }
            }
            .addOnFailureListener { exception -> callback!!.onFailed(exception) }
    }

    fun findUserById(id: String, callback: Callback<User>) {
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(id)
            .get()
            .addOnSuccessListener { result ->
                if (result.data != null) {
                    callback.onSucess(result.toObject(User::class.java))
                } else {
                    callback.onSucess(null)
                }
            }
            .addOnFailureListener { exception -> callback.onFailed(exception) }
    }

    fun listenForUpdatesList(cryptos: List<Crypto>, realTimeListener: RealTimeListener<Crypto>) {

        val cryptoReference = firebaseFirestore.collection(CRYPTO_COLLECTION_NAME)

        for (crypto in cryptos) {

            cryptoReference.document(crypto.getDocumentId()).addSnapshotListener {
                snapshot, e->
                if (e != null) {
                    realTimeListener.onError(e)
                }
                if (snapshot != null && snapshot.exists()) {
                    realTimeListener.onDataChanged(snapshot.toObject(Crypto::class.java)!!)
                }


            }
        }
    }

    fun listenForUpdates(user: User, realTimeListener: RealTimeListener<User>) {

        val usersReferences = firebaseFirestore.collection(USERS_COLLECTION_NAME)


        Log.e("USER", user.username)

        usersReferences.document(user.username).addSnapshotListener { snapshot, error ->

            if (error != null) {
                Log.e("USER",    "Fallo", error)
                realTimeListener.onError(error)
            }

            if (snapshot != null && snapshot.exists()) {
                realTimeListener.onDataChanged(snapshot.toObject(User::class.java)!!)
            }


        }



    }


}