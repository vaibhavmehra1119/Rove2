package com.example.roveapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.roveapp.databinding.ActivityLoginBinding
import com.example.roveapp.databinding.ActivityLoginSignUpBinding

class LoginSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginSignUpBinding
    private lateinit var signupbtn: Button
    private lateinit var loginbtn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        signupbtn=binding.signup
        loginbtn=binding.logIn
        loginbtn.setOnClickListener {
            val intent = Intent(this@LoginSignUpActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}