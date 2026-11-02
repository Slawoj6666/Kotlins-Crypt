package com.kotlincrypt.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class MainMenuActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startButton = Button(this).apply {
            text = "Start libGDX Game"
            setOnClickListener {
                val intent = Intent(this@MainMenuActivity, GameActivity::class.java)
                startActivity(intent)
            }
        }

        setContentView(startButton)
    }
}
