package com.paul9834.crypto.network

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
            .update("cryptosList", user.cryptoList)
            .addOnSuccessListener {
                callback?.onSucess(user)
            }
            .addOnFailureListener {
                callback?.onFailed(it)
            }
    }

    fun updateCrypto (crypto:Crypto) {
        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME).document(crypto.getDocumentId())
            .update("available", crypto.avalaible)
    }

    fun getCryptosList(callback: Callback<List<Crypto>>?) {

        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME)
            .get()
            .addOnSuccessListener {
                for(document in it) {
                    val cryptoList = it.toObjects(Crypto::class.java)
                    callback!!.onSucess(cryptoList)
                    break
                }
            }
            .addOnFailureListener {

            }
            .addOnFailureListener {
                callback!!.onFailed(it)
            }
    }

    fun findUserById(id:String, callback:Callback<User>) {
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(id)
            .get()
            .addOnSuccessListener {
                if(it.data != null) {
                    callback.onSucess(it.toObject(User::class.java))
                } else {
                    callback.onSucess(null)
                }
            }
            .addOnFailureListener {
                callback.onFailed(it)
            }
    }

}