package com.example.notepad.controller.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.notepad.R

class SplashScreenActivity : AppCompatActivity() {

    private val splashDisplayLength: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        Handler().postDelayed({
            run {
                startActivity(Intent(this, NoteListActivity::class.java))
                finish()
            }
        }, splashDisplayLength)
    }
}