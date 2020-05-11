package com.dawoodamir.tibgodoc.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val INSTANCE : Api by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.2.111/loginSignUp/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(Api::class.java)
    }


}