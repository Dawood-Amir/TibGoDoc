package com.dawoodamir.tibgodoc.api

import com.dawoodamir.tibgodoc.models.DefaultResponse
import com.dawoodamir.tibgodoc.models.LoginUserResponse
import com.dawoodamir.tibgodoc.models.SignUpResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {

    @FormUrlEncoded
    @POST(Constants.USER_URL)
    fun registerUser(
        @Field("request_type") request_type : String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("name") name: String,
        @Field("phoneNumber") phoneNumber: String,
        @Field("userType") userType: String,
        @Field("ADT") ADT: String,
        @Field("address") address: String,
        @Field("workingHours") workingHours: String,
        @Field("chargePerVisit") chargePerVisit: Int,
        @Field("latLng") latLng: String,
        @Field("isSpecialist") isSpecialist: Int,
        @Field("specialistIn")specialistIn:String

    ):Call<SignUpResponse>

    @FormUrlEncoded
    @POST(Constants.USER_URL)
    fun loginUser(
        @Field("request_type") requestType: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("userType") userType: String
    ):Call<LoginUserResponse>

    @FormUrlEncoded
    @POST(Constants.USER_URL)
    suspend fun updateADT(
        @Field("request_type") requestType: String,
        @Field("id") id: Int,
        @Field("ADT") ADT: String
    ): Response<DefaultResponse>

}