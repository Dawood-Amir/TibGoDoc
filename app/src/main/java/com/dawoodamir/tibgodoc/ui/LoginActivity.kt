package com.dawoodamir.tibgodoc.ui


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dawoodamir.tibgodoc.R
import com.dawoodamir.tibgodoc.api.Constants
import com.dawoodamir.tibgodoc.api.RetrofitClient
import com.dawoodamir.tibgodoc.models.LoginUserResponse
import com.dawoodamir.tibgodoc.models.User
import com.dawoodamir.tibgodoc.network_conectivity.CheckNetwork
import com.dawoodamir.tibgodoc.network_conectivity.GlobalVars
import com.dawoodamir.tibgodoc.notification.MyFirebaseMessagingService
import com.dawoodamir.tibgodoc.storage.SharedPrefManagerCustom
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private lateinit var rootlay: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        rootlay = findViewById(R.id.rootLay)
        settingADTSharedPref()
        loggingADT()
        clickListeners()

    }

    private fun settingADTSharedPref() {
        if (GlobalVars.isNetworkConnected) {
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
                if (it.isSuccessful) {
                    val token = it.result?.token
                    val sharedPreferences =
                        getSharedPreferences(
                            MyFirebaseMessagingService.SHARED_PREFS,
                            Context.MODE_PRIVATE
                        )
                    sharedPreferences.edit().putString(MyFirebaseMessagingService.KEY, token)
                        .apply()

                } else {
                    val sharedPreferences = getSharedPreferences(
                        MyFirebaseMessagingService.SHARED_PREFS,
                        Context.MODE_PRIVATE
                    )
                    val editor = sharedPreferences.edit()
                    editor.putString(MyFirebaseMessagingService.KEY, "").apply()
                }
            }
        }
    }


    private fun clickListeners() {
        loginB?.setOnClickListener {
            val email = emailE.text.toString().trim()
            val password = passwordEt.text.toString().trim().reversed()
            if (email.isEmpty()) {
                emailEL.error = "Email is required"
                emailEL.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEL.error = "Enter a valid email"
                emailEL.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordEtL.error = "Password is required"
                passwordEtL.requestFocus()
                return@setOnClickListener
            }
            val sharedPreferences =
                getSharedPreferences(MyFirebaseMessagingService.SHARED_PREFS, Context.MODE_PRIVATE)
            val token = sharedPreferences.getString(MyFirebaseMessagingService.KEY, "")
            if (!token.equals("")) {
                setPgVisibility(true)

                loginUser(email, password, token!!)
            } else {
                try {
                    if (GlobalVars.isNetworkConnected) {
                        FirebaseInstanceId.getInstance().instanceId.addOnFailureListener {
                            setPgVisibility(false)
                            snackBarColored("Unable to connect to network ${it.localizedMessage}")

                        }.addOnCompleteListener { task ->
                            setPgVisibility(true)
                            val token1 = task.result?.token
                            loginUser(email, password, token1!!)
                        }
                    } else {
                        snackBarColored("Hoops look like you are out of internet", -1)
                    }
                }catch (e:Exception){
                    snackBarColored("Hoops something went wrong don't tell anyone will fix it shortly", -1)

                }

            }

        }
        goSignUpT.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
        }
    }

    private fun updateADT(ADT: String, id: Int): Boolean {
        val retrofitClient = RetrofitClient.INSTANCE
        CoroutineScope(IO).launch {
            val response = retrofitClient.updateADT(Constants.UPDATE_ADT_REQUEST_TYPE, id, ADT)
            withContext(Main) {
                if (response.isSuccessful) {
                    if (!response.body()!!.error) {
                        return@withContext
                    } else {
                        snackBarColored(response.body()!!.message)
                        setPgVisibility(false)
                        return@withContext
                    }
                } else {
                    toast(response.body()!!.message)
                    setPgVisibility(false)
                    return@withContext
                }
            }
        }
        return true
    }


    private fun loginUser(email: String, password: String, ADT: String) {

        if (GlobalVars.isNetworkConnected) {
            RetrofitClient.INSTANCE.loginUser(
                Constants.LOGIN_USER_REQUEST_TYPE,
                email,
                password,
                "doctor"
            ).enqueue(object : Callback<LoginUserResponse> {
                override fun onFailure(call: Call<LoginUserResponse>, t: Throwable) {
                    println("\\n\\n Error is  : ${t.message}")
                    // toast(t.localizedMessage!!)
                    snackBarColored(t.localizedMessage!!, -1)
                    setPgVisibility(false)
                }

                override fun onResponse(
                    call: Call<LoginUserResponse>,
                    response: Response<LoginUserResponse>
                ) {
                    if (!response.body()?.error!!) {
                        val id = response.body()?.user!!.id
                        val user = User(
                            response.body()?.user?.id!!,
                            response.body()?.user?.d_id!!,
                            response.body()?.user?.name!!,
                            response.body()?.user?.email!!,
                            response.body()?.user?.phoneNumber!!,
                            response.body()?.user?.ADT!!,
                            response.body()?.user?.address!!,
                            response.body()?.user?.chargePerVisit!!,
                            response.body()?.user?.latLng!!,
                            response.body()?.user?.userType!!,
                            response.body()?.user?.isSpecialist!!,
                            response.body()?.user?.workingHours!!,
                            response.body()?.user?.specialistIn!!
                        )
                        SharedPrefManagerCustom.getInstance(applicationContext).saveUser(user)
                        if (updateADT(ADT, id)) {
                            val intent = Intent(applicationContext, HomeActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }


                    } else {
                        snackBarColored("${response.body()?.message}")
                        setPgVisibility(false)
                    }
                }
            })
        } else {
            setPgVisibility(false)
            toast("Check your network connection and try again")
        }
    }

    fun setPgVisibility(flag: Boolean) {
        if (flag) {
            pgBar.visibility = View.VISIBLE
        } else {
            pgBar.visibility = View.INVISIBLE
        }
    }

    private fun toast(s: String) {
        Toast.makeText(
            applicationContext,
            s,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onStart() {
        super.onStart()
        if (SharedPrefManagerCustom.getInstance(this).isLoggedIn) {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val checkNetwork = CheckNetwork(applicationContext)
        checkNetwork.registerDefaultNetworkCallback()
    }

    private fun loggingADT() {

        val sharedPreferences =
            getSharedPreferences(MyFirebaseMessagingService.SHARED_PREFS, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString(MyFirebaseMessagingService.KEY, "")
        Snackbar.make(rootLay, "ADT is : $token", Snackbar.LENGTH_LONG).show()

    }


    private fun snackBarColored(message: String?, length: Int = 0) {
        if (message != null) {
            val snackbar = Snackbar.make(rootLay, message, length)
            val snackBarView = snackbar.view
            val tv =
                snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            tv.setTextColor(Color.parseColor("#FFFFFF"))
            tv.textSize = 12f
            snackBarView.setBackgroundColor(Color.parseColor("#F57F17"))
            snackbar.show()
        }
    }

/*

    lateinit var userList: List<User>

private fun getAllUsers() {

    RetrofitClient.INSTANCE.getAllUsers(Constants.GET_ALL_USERS_REQUEST_TYPE)
        .enqueue(object : Callback<JsonArray> {
            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                try {
                    val userString = response.body().toString()
                    if (userString == "[-1]") {
                        Toast.makeText(
                            applicationContext,
                            "No user found result is $userString",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }
                    userList = Gson().fromJsonExtensionFun(userString)
                    Toast.makeText(
                        applicationContext,
                        "Response massage is $userList }",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        applicationContext,
                        "There's a problem in connection check your network connection and try again",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

        })

}


 inline fun <reified T> Gson.fromJsonExtensionFun(json: String) =
        this.fromJson<T>(json, object : TypeToken<T>() {}.type)!!
*/


}
