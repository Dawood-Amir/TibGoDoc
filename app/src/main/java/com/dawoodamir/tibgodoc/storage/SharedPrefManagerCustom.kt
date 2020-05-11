package com.dawoodamir.tibgodoc.storage

import android.content.Context
import com.dawoodamir.tibgodoc.models.User

class SharedPrefManagerCustom private constructor(private val ctx: Context) {


    val isLoggedIn: Boolean
        //Type val where u can add the result through get()
        get() {
            val sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getInt("id", -1) != -1
        }

    val isLoggedInDocID:String
    get(){

            val sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return if(isLoggedIn)sharedPreferences.getInt("d_id",-1).toString() else "-1"

    }
    val user: User
        get() {
            val sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return User(
                sharedPreferences.getInt("id", -1),
                sharedPreferences.getInt("d_id", -1),
                sharedPreferences.getString("name", "")!!,
                sharedPreferences.getString("email", "")!!,
                sharedPreferences.getString("phoneNumber", "")!!,
                sharedPreferences.getString("ADT", "")!!,
                sharedPreferences.getString("address", "")!!,
                sharedPreferences.getInt("chargePerVisit", -1),
                sharedPreferences.getString("latLng", "")!!,
                sharedPreferences.getString("userType", "")!!,
                sharedPreferences.getInt("isSpecialist", -1),
                sharedPreferences.getString("workingHours", "")!!,
                sharedPreferences.getString("specialistIn", "")!!
            )

        }

    fun saveUser(user: User) {
        // Modify it for ADT before saving the user
        val sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("id", user.id)
        editor.putInt("d_id",user.d_id)
        editor.putString("name", user.name)
        editor.putString("email", user.email)
        editor.putString("phoneNumber", user.phoneNumber)
        editor.putString("ADT", user.ADT)
        editor.putString("userType", user.userType)
        editor.putString("address", user.address)
        editor.putInt("chargePerVisit", user.chargePerVisit)
        editor.putString("latLng", user.latLng)
        editor.putString("userType", user.userType)
        editor.putInt("isSpecialist", user.isSpecialist)
        editor.putString("workingHours", user.workingHours)
        editor.putString("specialistIn", user.specialistIn)
        editor.apply()
    }

    fun clear() {
        val sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private const val SHARED_PREF_NAME = "UserSharedPreference"
        private var mInstance: SharedPrefManagerCustom? = null

        @Synchronized
        fun getInstance(ctx: Context): SharedPrefManagerCustom {

            if (mInstance == null) {
                mInstance = SharedPrefManagerCustom(ctx)
            }
            return mInstance as SharedPrefManagerCustom
        }


    }


}