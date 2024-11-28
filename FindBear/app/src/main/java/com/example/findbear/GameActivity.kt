package com.example.findbear

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*



class GameActivity : AppCompatActivity() {

    private lateinit var game: FindBearActivity
    private lateinit var holeImageViews: List<ImageView>
    private lateinit var scoreTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var timer: CountDownTimer
    private lateinit var dbHelper: ScoreDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Initialize FindBearActivity instance
        game = FindBearActivity()

        // Get hole ImageViews from the XML layout
        holeImageViews = listOf(
            findViewById(R.id.hole1),
            findViewById(R.id.hole2),
            findViewById(R.id.hole3),
            findViewById(R.id.hole4),
            findViewById(R.id.hole5),
            findViewById(R.id.hole6),
            findViewById(R.id.hole7),
            findViewById(R.id.hole8),
            findViewById(R.id.hole9),
            findViewById(R.id.hole10),
            findViewById(R.id.hole11),
            findViewById(R.id.hole12)
        )

        // Get the score and timer TextViews
        scoreTextView = findViewById(R.id.scoreTextView)
        timerTextView = findViewById(R.id.timerTextView)

        // Initialize SQLite database helper
        dbHelper = ScoreDbHelper(this)

        // Set up click listeners for hole ImageViews
        for ((index, holeImageView) in holeImageViews.withIndex()) {
            holeImageView.setOnClickListener {
                game.whackBear(index) // Pass the index of the hole
            }
        }

        // Update the score when the game sends changes
        game.setScoreCallback { newScore -> updateScore(newScore) }

        // Start the game and timer
        startGame()

    }

    private fun startGame() {
        // Start the game
        game.startGame(holeImageViews)

        // Start the timer
        timer = object : CountDownTimer(30000, 1000) { // 1 minute countdown
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                timerTextView.text = "Time: $secondsRemaining"
            }

            override fun onFinish() {
                timerTextView.text = "Time up"
                game.stopGame() // Stop the game when the timer finishes
                showNameInputDialog(scoreTextView.text.toString().removePrefix("Score: ").toInt())
            }
        }.start()
    }

    //popup for player name input
    private fun showNameInputDialog(score: Int) {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Enter Your Name")
            .setView(input)
            .setPositiveButton("Save") { dialog, _ ->
                val name = input.text.toString()
                saveScoreToDatabase(name, score)
                dialog.dismiss()
                // Navigate to ScoreboardActivity
                val intent = Intent(this@GameActivity, ScoreboardActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                // Navigate to ScoreboardActivity
                val intent = Intent(this@GameActivity, ScoreboardActivity::class.java)
                startActivity(intent)
            }
            .show()
    }

    private fun saveScoreToDatabase(name: String, score: Int) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ScoreEntry.COLUMN_NAME_DATE, currentDate)
            put(ScoreEntry.COLUMN_NAME_NAME, name)
            put(ScoreEntry.COLUMN_NAME_SCORE, score)
        }
        db.insert(ScoreEntry.TABLE_NAME, null, values)
    }

    override fun onStop() {
        super.onStop()
        // Stop the timer
        timer.cancel()
    }

    // Update score TextView when the score changes
    private fun updateScore(score: Int) {
        scoreTextView.text = "Score: $score"
    }

    class ScoreDbHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(SQL_CREATE_ENTRIES)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // Handle database upgrades, if necessary
        }

        companion object {
            const val DATABASE_VERSION = 1
            const val DATABASE_NAME = "Score.db"

            private const val SQL_CREATE_ENTRIES = """
                CREATE TABLE ${ScoreEntry.TABLE_NAME} (
                ${ScoreEntry._ID} INTEGER PRIMARY KEY,
                ${ScoreEntry.COLUMN_NAME_DATE} TEXT,
                ${ScoreEntry.COLUMN_NAME_NAME} TEXT,
                ${ScoreEntry.COLUMN_NAME_SCORE} INTEGER)
            """
        }

        fun getTopScores(): List<Score> {
            val scores = mutableListOf<Score>()
            val db = readableDatabase
            val query = "SELECT * FROM ${ScoreEntry.TABLE_NAME} ORDER BY ${ScoreEntry.COLUMN_NAME_SCORE} DESC LIMIT 10"
            val cursor = db.rawQuery(query, null)
            with(cursor) {
                while (moveToNext()) {
                    val name = getString(getColumnIndexOrThrow(ScoreEntry.COLUMN_NAME_NAME))
                    val score = getInt(getColumnIndexOrThrow(ScoreEntry.COLUMN_NAME_SCORE))
                    val date = getString(getColumnIndexOrThrow(ScoreEntry.COLUMN_NAME_DATE))
                    scores.add(Score(name, score, date))
                }
            }
            cursor.close()
            return scores
        }
    }

    data class Score(val name: String, val score: Int, val date: String)

    object ScoreEntry {
        const val TABLE_NAME = "score"
        const val _ID = "_id"
        const val COLUMN_NAME_DATE = "date"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_SCORE = "score"
    }

}
