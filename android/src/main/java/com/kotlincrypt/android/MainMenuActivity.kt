package com.kotlincrypt.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
class MainMenuActivity : Activity() {

    private val Layoutarr = intArrayOf(R.layout.activity_main_menu, R.layout.settings)
    private var currentLayoutIndex = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView(Layoutarr[currentLayoutIndex])
    }
    private fun setView(LayoutRedId: Int) {
        setContentView(LayoutRedId)
        currentLayoutIndex = Layoutarr.indexOf(LayoutRedId)
        setupvievents(LayoutRedId)
    }

    private fun setupvievents(LayoutRedId: Int) {
        when (LayoutRedId) {
            R.layout.activity_main_menu -> {
                val startButton = findViewById<Button>(R.id.startButton)
                val settingsButton = findViewById<Button>(R.id.settings)

                startButton.setOnClickListener {
                    startActivity(
                        Intent(this, GameActivity::class.java)
                    )
                }

                settingsButton.setOnClickListener {
                    setView(Layoutarr[1])
                }
            }

            R.layout.settings -> {
                val backButton = findViewById<Button>(R.id.back_button)

                backButton.setOnClickListener {
                    setView(Layoutarr[0])
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (currentLayoutIndex == 1) {
            setView(Layoutarr[0])
        } else {
            super.onBackPressed()
        }
    }

    }
