package com.example.findbear

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<ImageButton>(R.id.playButton).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
        findViewById<TextView>(R.id.howtoplaybutton).setOnClickListener {
            startActivity(Intent(this, HowToPlayActivity::class.java))
        }

        findViewById<Button>(R.id.highScoresButton).setOnClickListener {
            startActivity(Intent(this, ScoreboardFullActivity::class.java))
        }
    }
}
