package com.paul9834.crypto.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.paul9834.crypto.R
import com.paul9834.crypto.adapter.CryptosAdapter
import com.paul9834.crypto.adapter.CryptosAdapterLister
import com.paul9834.crypto.firestore.model.Crypto
import com.paul9834.crypto.firestore.model.User
import com.paul9834.crypto.network.Callback
import com.paul9834.crypto.network.FirestoreService
import com.paul9834.crypto.network.RealTimeListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_trader.*
import java.lang.Exception


/**
 * @author Santiago Carrillo
 * 2/14/19.
 */
class TraderActivity : AppCompatActivity(), CryptosAdapterLister {


    lateinit var firestoreService: FirestoreService

    private val cryptosAdapter: CryptosAdapter = CryptosAdapter(this)

    private var username:String? = null

    private var user:User?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trader)

        firestoreService = FirestoreService(FirebaseFirestore.getInstance())

        username = intent.extras!![USERNAME_KEY]!!.toString()

        usernameTextView.text = username



        configureRecyclerView()


        loadCryptos()

        fab.setOnClickListener { view ->

            Snackbar.make(view, getString(R.string.generating_new_cryptos), Snackbar.LENGTH_SHORT)
                .setAction("Info", null).show()

            generateCryptoCurrenciasRandom()

        }

    }

    private fun generateCryptoCurrenciasRandom() {
        for (crypto in cryptosAdapter.cryptoList) {

            val amount = (1..10).random()
            crypto.avalaible += amount
            firestoreService.updateCrypto(crypto)

        }
    }

    private fun loadCryptos() {
        firestoreService.getCryptosList(object : Callback<List<Crypto>> {

            override fun onSucess(cryptoList: List<Crypto>?) {

                firestoreService.findUserById(username!!, object : Callback<User> {
                    override fun onSucess(result: User?) {
                        user = result
                        if (user!!.cryptoList == null) {
                            val userCryptoList = mutableListOf<Crypto>()

                            for (crypto in cryptoList!!) {
                                val cryptoUser = Crypto()
                                cryptoUser.name = crypto.name

                                //cryptoUser.avalaible = crypto.avalaible

                                cryptoUser.avalaible = 0

                                var disponible = crypto.avalaible

                                Log.e("AVALAIBLE", "$disponible cantidad"  );


                                cryptoUser.image_url = crypto.image_url
                                userCryptoList.add(cryptoUser)
                            }
                            user!!.cryptoList = userCryptoList
                            firestoreService.updateUser(user!!, null)
                        }
                        loadUserCryptos()
                        addRealTimeDatabaseListeners(user!!, cryptoList!!)

                    }

                    override fun onFailed(exception: Exception) {
                        showGeneralServerErrorMessage()
                    }

                })


                this@TraderActivity.runOnUiThread {
                    cryptosAdapter.cryptoList = cryptoList!!
                    cryptosAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailed(exception: Exception) {
                Log.e("TraderActivity", "error loading criptos", exception)
                showGeneralServerErrorMessage()
            }


        })
    }

    private fun addRealTimeDatabaseListeners(user: User, cryptoList: List<Crypto>) {

        firestoreService.listenForUpdates(user, object: RealTimeListener<User> {

            override fun onDataChanged(updateData: User) {

                this@TraderActivity.user = updateData
                loadUserCryptos()

            }

            override fun onError(exception: Exception) {
                showGeneralServerErrorMessage()
            }

            override fun onErrorFirebase(firebaseFirestoreException: FirebaseFirestoreException) {
                showGeneralServerErrorMessage()
            }


        })
        firestoreService.listenForUpdatesList(cryptoList, object : RealTimeListener<Crypto> {
            override fun onDataChanged(updateData: Crypto) {
                var pos = 0
                for (crypto in cryptosAdapter.cryptoList) {
                    if(crypto.name.equals(updateData.name)) {
                        crypto.avalaible = updateData.avalaible
                        cryptosAdapter.notifyItemChanged(pos)
                    }
                    pos++
                }
            }

            override fun onError(exception: Exception) {
               showGeneralServerErrorMessage()
            }


            override fun onErrorFirebase(firebaseFirestoreException: FirebaseFirestoreException) {
              showGeneralServerErrorMessage()
            }


        }
        )

    }

    private fun loadUserCryptos() {

        runOnUiThread {

            if (user != null && user!!.cryptoList != null) {

                infoPanel.removeAllViews()

                for (cryp in user!!.cryptoList!!) {
                    addUserCryptoInfoRow(cryp)
                }

            }

        }

    }

    private fun addUserCryptoInfoRow(cryp: Crypto) {

        val view = LayoutInflater.from(this).inflate(R.layout.coin_info, infoPanel, false)

        view.findViewById<TextView>(R.id.coinLabel).text =
            getString(R.string.coin_info, cryp.name, cryp.avalaible.toString())

        Picasso.get().load(cryp.image_url).into(view.findViewById<ImageView>(R.id.coinIcon))

        infoPanel.addView(view)


    }

    private fun configureRecyclerView() {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager  = layoutManager
        recyclerView.adapter = cryptosAdapter

    }

    fun showGeneralServerErrorMessage() {
        Snackbar.make(fab, getString(R.string.error_while_connecting_to_the_server), Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }

    override fun onBuyCryptoClicked(crypto: Crypto) {
        var flag = false
        if (crypto.avalaible > 0) {
            for (cryptoUser in user!!.cryptoList!!) {
                if (cryptoUser.name == crypto.name) {
                    cryptoUser.avalaible += 1
                    flag = true
                    break
                }
            }
            if (!flag) {
                Log.e("COIN", "NO EXITE CRYPTO")
                val cryptoUser = Crypto()
                cryptoUser.name = crypto.name
                cryptoUser.avalaible = 1
                cryptoUser.image_url = crypto.image_url
                user!!.cryptoList = user!!.cryptoList!!.plusElement(cryptoUser)
            }
            crypto.avalaible--
            firestoreService.updateUser(user!!, null)
            firestoreService.updateCrypto(crypto)
        } else {
            Toast.makeText(this, "No hay suficientes criptomonedas disponibles", Toast.LENGTH_SHORT).show()
        }
    }
}