package com.dawoodamir.tibgodoc.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.dawoodamir.tibgodoc.R
import com.dawoodamir.tibgodoc.api.Constants
import com.dawoodamir.tibgodoc.api.RetrofitClient
import com.dawoodamir.tibgodoc.models.SignUpResponse
import com.dawoodamir.tibgodoc.models.User
import com.dawoodamir.tibgodoc.network_conectivity.CheckNetwork
import com.dawoodamir.tibgodoc.network_conectivity.GlobalVars
import com.dawoodamir.tibgodoc.notification.MyFirebaseMessagingService
import com.dawoodamir.tibgodoc.storage.SharedPrefManagerCustom
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_signup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUpActivity : AppCompatActivity() {
    private lateinit var specialistInTIL: TextInputLayout
    private lateinit var specialistInE: TextInputEditText
    private lateinit var isSpecialistCB: CheckBox
    private var isChecked = false
    private lateinit var rootlay: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        init()
        clickListeners()

    }

    private fun clickListeners() {
        isSpecialistCB.setOnClickListener {
            if (isChecked) {
                isChecked = false
                isSpecialistCB.isChecked = false
                specialistInTIL.visibility = View.GONE
            } else {
                isChecked = true
                isSpecialistCB.isChecked = true
                specialistInTIL.visibility = View.VISIBLE
            }
        }
        signUpB.setOnClickListener {
            val email = emailE.text.toString().trim()
            val phone = phoneNumE.text.toString().trim()
            val name = nameE.text.toString().trim()
            val password = passwordEt.text.toString().trim().reversed()
            val address = addressE.text.toString().trim()
            val workingH = workingHoursE.text.toString().trim()
            val chargePerV = chargePerVE.text.toString().trim()
            val specialistIn = specialistInE.text.toString().trim()
            if (name.isEmpty()) {
                nameEL.error = "Name is required"
                nameEL.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                emailEL.error = "Email is required"
                emailEL.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailEL.error = "Enter a valid email"
                emailEL.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordEtL.error = "Password is required"
                passwordEtL.requestFocus()
                return@setOnClickListener
            }
            if (phone.isEmpty()) {
                phoneNumEL.error = "Phone# is required"
                phoneNumEL.requestFocus()
                return@setOnClickListener
            }
            if (address.isEmpty()) {
                phoneNumEL.error = "Address is required"
                phoneNumEL.requestFocus()
                return@setOnClickListener
            }
            if (workingH.isEmpty()) {
                workingHoursEL.error = "Working Hours are required"
                workingHoursEL.requestFocus()
                return@setOnClickListener
            }
            if (chargePerV.isEmpty()) {
                chargePerVEL.error = "Charges per visit is required"
                chargePerVEL.requestFocus()
                return@setOnClickListener
            }
            if (isChecked) {
                if (specialistIn.isEmpty()) {
                    specialistInTIL.error = "Specialization is required"
                    specialistInTIL.requestFocus()
                    return@setOnClickListener
                }
            }
            val sharedPreferences =
                getSharedPreferences(MyFirebaseMessagingService.SHARED_PREFS, Context.MODE_PRIVATE)
              val aDT = sharedPreferences.getString(MyFirebaseMessagingService.KEY, "")!!

            val randomLng: Double = 51.00000 + Math.random() * (41.000000000000 - 51.00000)
            val randomLAT: Double = 81.00000 + Math.random() * (71.000000000000 - 81.00000)

            val isSpecInt = if (isSpecialistCB.isChecked) 1 else 0

            signUpUser(
                name,
                email,
                password,
                phone,
                aDT,
                address,
                workingH,
                chargePerV.toInt(),
                "$randomLAT , $randomLng",
                isSpecInt,
                specialistIn
            )


        }

        goLoginT.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }

    }

    private fun signUpUser(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
        ADT: String,
        address: String,
        workingHours: String,
        chargePerV: Int,
        latlng: String,
        isSpecialist: Int,
        specialistIn: String
    ) {
        if (GlobalVars.isNetworkConnected) {
            setPgVisibility(true)

            RetrofitClient.INSTANCE.registerUser(
                Constants.SIGN_UP_REQUEST_TYPE,
                email,
                password,
                name,
                phoneNumber,
                "doctor",
                ADT, address, workingHours, chargePerV, latlng, isSpecialist, specialistIn
            ).enqueue(object : Callback<SignUpResponse> {
                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    snackBarColored(t.message, 0)
                    setPgVisibility(false)
                }

                override fun onResponse(
                    call: Call<SignUpResponse>,
                    response: Response<SignUpResponse>
                ) {
                    if (!response.body()?.error!!) {
                        val user =
                            User(
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
                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        setPgVisibility(false)
                        startActivity(intent)
                    } else {
                        snackBarColored(response.body()?.message, 0)
                    }
                    setPgVisibility(false)
                }
            })
        } else {
            snackBarColored("Hoops look like you are out of internet", -1)
        }
    }

    private fun settingADTSharedPref() {
        try {
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
        }catch (e:Exception){
            snackBarColored(e.localizedMessage)
        }
    }

    private fun init() {
        specialistInTIL = findViewById(R.id.specialistInTextInputLayout)
        specialistInE = findViewById(R.id.specialistInE)
        isSpecialistCB = findViewById(R.id.isSpecialistCheckBox)
        rootlay = findViewById(R.id.rootLay)
    }


    private fun snackBarColored(message: String?, length: Int=0) {
      if(message!=null){
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

    fun setPgVisibility(flag: Boolean) {
        if (flag) {
            pgBar.visibility = View.VISIBLE
        } else {
            pgBar.visibility = View.INVISIBLE
        }
    }

    override fun onStart() {
        super.onStart()

        if (SharedPrefManagerCustom.getInstance(this).isLoggedIn) {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        settingADTSharedPref()
        val checkNetwork = CheckNetwork(applicationContext)
        checkNetwork.registerDefaultNetworkCallback()

    }


}
