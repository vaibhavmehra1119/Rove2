package com.example.roveapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.roveapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val username = binding.username
        val password = binding.password
        val button = binding.login
        mAuth = FirebaseAuth.getInstance()

        button.setOnClickListener {
            val email = username.getText().toString()
            val pass = password.getText().toString()
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@LoginActivity,
                        "User logged in successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@LoginActivity, HeatMapActivity::class.java))
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Log in Error: " ,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}