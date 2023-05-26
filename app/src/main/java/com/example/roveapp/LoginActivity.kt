package com.example.roveapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.example.roveapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    //private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val username = binding.username
        val password = binding.password
        val button = binding.login
        val progressBar=binding.progressBar
        mAuth = FirebaseAuth.getInstance()

        button.setOnClickListener {
            progressBar.visibility=View.VISIBLE
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
                    finish()
                } else {
                    progressBar.visibility=View.INVISIBLE
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