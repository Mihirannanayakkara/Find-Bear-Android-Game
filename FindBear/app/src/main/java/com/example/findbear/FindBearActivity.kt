package com.example.findbear

import android.os.Handler
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import kotlin.random.Random

class FindBearActivity {

    private var score: Int = 0
    private val bearAppearanceGap = 3000L
    private lateinit var bearViews: List<ImageView>
    private var isGameRunning: Boolean = false
    private var numBearsShown = 0
    private val maxNumBears = 4 // Maximum number of moles that can be shown simultaneously
    private val delayBetweenAppearances = 200L
    private val holeDownDuration = 790L
    private var scoreCallback: ((Int) -> Unit)? = null//reset score



    fun startGame(bearViews: List<ImageView>) {
        this.bearViews = bearViews

        score = 0
        isGameRunning = true

        // Start game loop
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isGameRunning) {
                    //random bears appear
                    val numberOfBears = Random.nextInt(1, 4)
                    val bearIndices = mutableListOf<Int>()
                    while (bearIndices.size < numberOfBears) {
                        val bearIndex = Random.nextInt(bearViews.size)
                        if (bearIndex !in bearIndices) {
                            bearIndices.add(bearIndex)
                            showBear(bearIndex)
                        }
                    }

                    handler.postDelayed(this, bearAppearanceGap)
                }
            }
        }, 0)
    }

    fun stopGame() {
        isGameRunning = false
    }


    private fun showBear(bearIndex: Int) {
        if (numBearsShown < maxNumBears) {
            val bearView = bearViews[bearIndex]
            if (bearView.tag != "up") {
                bearView.setImageResource(R.drawable.bear_up)
                bearView.tag = "up"
                numBearsShown++

                // Get the hole's position
                val holeX = bearView.x
                val holeY = bearView.y

                // Animate bear rising from the hole
                val startY = holeY + bearView.height // Adjust this value based on your layout
                val endY = holeY
                val translateAnimation = TranslateAnimation(0f, 0f, startY, endY)
                translateAnimation.duration = 200 // Animation duration in milliseconds
                bearView.startAnimation(translateAnimation)

                bearView.setOnClickListener {
                    if (isGameRunning && bearView.tag == "up") {
                        score++ // Increment score when bear is whacked
                        scoreCallback?.invoke(score)
                        bearView.setImageResource(R.drawable.bear_down)
                        bearView.tag = "down"
                        Handler().postDelayed({
                            bearView.setImageResource(R.drawable.hole)
                            bearView.tag = "down"
                            numBearsShown--
                        }, 1000L)// Delay for mole to hide back into hole after being whacked
                    }
                }

                // Reset mole to "down" state if not whacked within specified duration
                Handler().postDelayed({
                    if (bearView.tag == "up") {
                        bearView.setImageResource(R.drawable.hole)
                        bearView.tag = "down"
                        numBearsShown--
                    }
                }, holeDownDuration)
            }
        }
    }

    fun whackBear(index: Int) {

    }

    //reset score
    fun setScoreCallback(callback: ((Int) -> Unit)) {
        scoreCallback = callback
    }

}