package com.paul9834.crypto.firestore.model

class Crypto (var name:String = "", var image_url:String = "", var avalaible:Int =0) {

    fun getDocumentId() :String {
        return name.toLowerCase()
    }


}