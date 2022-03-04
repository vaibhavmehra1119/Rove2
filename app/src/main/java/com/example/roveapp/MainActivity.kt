package com.example.roveapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.VideoView
//Splash Screen Activity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        try {
            val videoHolder = VideoView(this)
            setContentView(videoHolder)
            val video = Uri.parse("android.resource://" + packageName + "/" + R.raw.splashvideo)
            videoHolder.setVideoURI(video)

            videoHolder.setOnCompletionListener { jump() }
            videoHolder.start()
        } catch (ex: Exception) {
            jump()
        }
    }
    private fun jump() {
        if (isFinishing)
            return
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

        finish()
    }
}