package com.paul9834.crypto.adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.paul9834.crypto.R
import com.paul9834.crypto.firestore.model.Crypto
import com.squareup.picasso.Picasso


class CryptosAdapter (val cryptosAdapterLister: CryptosAdapterLister) : RecyclerView.Adapter<CryptosAdapter.ViewHolder>(){


    var cryptoList: List<Crypto> = ArrayList()


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var image = view.findViewById<ImageView>(R.id.image)
        var name = view.findViewById<TextView>(R.id.nameTextView)
        var avalaibletxt = view.findViewById<TextView>(R.id.availableTextView)
        var buyButton = view.findViewById<Button>(R.id.buyButton)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.crypto_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val crypto = cryptoList[position]

        Picasso.get().load(crypto.image_url).into(holder.image)
        holder.name.text = crypto.name
        holder.avalaibletxt.text = holder.itemView.context.getString(R.string.available_message, crypto.avalaible.toString())

        holder.buyButton.setOnClickListener {
            cryptosAdapterLister.onBuyCryptoClicked(crypto)
        }




    }

    override fun getItemCount(): Int {
       return cryptoList.size
    }

}