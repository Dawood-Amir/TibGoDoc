package com.dawoodamir.tibgodoc.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dawoodamir.tibgodoc.R
import com.dawoodamir.tibgodoc.storage.SharedPrefManagerCustom
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        clickListeners()
    }

    private fun clickListeners() {
        logOutB.setOnClickListener {
            SharedPrefManagerCustom.getInstance(this).clear()
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")

    override fun onStart() {
        super.onStart()
        if (!SharedPrefManagerCustom.getInstance(this).isLoggedIn) {
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }else{
            val user = SharedPrefManagerCustom.getInstance(this).user
            userDataT.text = "Name is\t ${user.name} \nEmail is\t ${user.email}\nPhoneNumber is\t ${user.phoneNumber}\nuserTyrpe is\t ${user.userType}\nADT is${user.ADT}\nuserType is ${user.userType}\n user Doc id is ${SharedPrefManagerCustom.getInstance(this).isLoggedInDocID}\nuser address is ${user.address}"
        }
    }

}
