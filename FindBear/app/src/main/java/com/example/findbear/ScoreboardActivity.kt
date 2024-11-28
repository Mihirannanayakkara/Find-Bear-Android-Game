package com.example.findbear

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ScoreboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var scoreListAdapter: ScoreListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        scoreListAdapter = ScoreListAdapter(getScoresFromDatabase())
        recyclerView.adapter = scoreListAdapter
    }

    //get data from database
    private fun getScoresFromDatabase(): List<GameActivity.Score> {
        val dbHelper = GameActivity.ScoreDbHelper(this)
        return dbHelper.getTopScores()
    }
}
