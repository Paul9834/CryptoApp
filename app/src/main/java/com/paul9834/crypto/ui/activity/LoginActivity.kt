package com.paul9834.crypto.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paul9834.crypto.R
import com.paul9834.crypto.firestore.model.User
import com.paul9834.crypto.network.Callback
import com.paul9834.crypto.network.FirestoreService
import com.paul9834.crypto.network.USERS_COLLECTION_NAME
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_trader.*
import java.lang.Exception

/**
 * @author Santiago Carrillo
 * github sancarbar
 * 1/29/19.
 */


const val USERNAME_KEY = "username_key"

class LoginActivity : AppCompatActivity() {


    private val TAG = "LoginActivity"

    private var autn:FirebaseAuth = FirebaseAuth.getInstance()

    lateinit var firestoreService: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firestoreService = FirestoreService(FirebaseFirestore.getInstance())



    }

    fun onStartClicked(view: View) {

        view.isEnabled = false

        autn.signInAnonymously()
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    val username = username.text.toString()

                    firestoreService.findUserById(username, object : Callback<User> {
                        override fun onSucess(result: User?) {

                            if (result == null) {
                                val user = User()
                                user.username = username
                                saverUserAndStartMainActivity(user, view)

                            } else {
                                startMainActivity(username)
                            }
                        }
                        override fun onFailed(exception: Exception) {
                            showErrorMessage(view)
                        }
                    })

                } else {
                    showErrorMessage(view)
                    view.isEnabled = true
                }
            }
    }

    private fun saverUserAndStartMainActivity(user: User, view: View) {

        firestoreService.setDocument(user, USERS_COLLECTION_NAME, user.username, object : Callback<Void> {

            override fun onSucess(result: Void?) {
               startMainActivity(user.username)
            }
            override fun onFailed(exception: Exception) {
                showErrorMessage(view)
                Log.e(TAG,"error", exception)
                view.isEnabled = true
            }
        })
    }

    private fun showErrorMessage(view: View) {
        Snackbar.make(view, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }

    private fun startMainActivity(username: String) {
        val intent = Intent(this@LoginActivity, TraderActivity::class.java)
        intent.putExtra(USERNAME_KEY, username)
        startActivity(intent)
        finish()
    }

}
