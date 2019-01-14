package com.nora.sendingmylocation.ui.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nora.sendingmylocation.R
import com.nora.sendingmylocation.ui.main.MainActivity
import com.nora.sendingmylocation.ui.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnSignIn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        tvSignUpBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
