package com.paul9834.crypto.network

import java.lang.Exception


interface Callback<T> {


    fun onSucess(result: T?)

    fun onFailed (exception: Exception)


}