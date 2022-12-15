package com.example.g1_final_project.utils

import android.animation.Animator
import android.util.Log
import android.view.View
import com.daimajia.androidanimations.library.YoYo.YoYoString
import android.widget.TextView
import com.daimajia.androidanimations.library.YoYo
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo.AnimatorCallback
import com.fxn.stash.Stash
import timerx.StopwatchBuilder
import timerx.TimeTickListener

object Controller {
    private const val TAG = "Controller"
    var isAnimStarted = false
    var animation1: YoYoString? = null
    var animation2: YoYoString? = null
    private var target1: View? = null
    private var timeTextView: TextView? = null
    private fun slideOut() {
        animation1 = YoYo.with(Techniques.SlideOutLeft)
            .duration(2000)
            .onEnd { animator: Animator? -> if (isAnimStarted) slideIn() }
            .playOn(target1)
    }

    private fun slideIn() {
        animation2 = YoYo.with(Techniques.SlideInRight)
            .duration(2000)
            .onEnd { animator: Animator? -> if (isAnimStarted) slideOut() }
            .playOn(target1)
    }

    fun stopAnimation() {
        isAnimStarted = false
        if (animation1 != null && animation2 != null) {
            animation1!!.stop(true)
            animation2!!.stop(true)
        }
    }

    fun startAnimation(target: View?) {
        target1 = target
        slideOut()
        isAnimStarted = true
    }

    var lastHourInt = Stash.getInt(Constants.HOURS, 0)
    var lastMinutesInt = Stash.getInt(Constants.MINUTES, 0)
    var stopwatch = StopwatchBuilder()
        .startFormat("HH:MM")
        .onTick { time: CharSequence ->
            if (Stash.getInt(Constants.MINUTES, 0) == 60) Stash.put(Constants.MINUTES, 0)
            Log.d(TAG, "time: $time")
            val values = time.toString().split(":").toTypedArray()
            val hoursStr = values[0]
            val minutesStr = values[1]
            Log.d(TAG, "hoursStr: $hoursStr")
            Log.d(TAG, "minutesStr: $minutesStr")
            var newMinutesInt = minutesStr.toInt()
            var finalMinutesInt = lastMinutesInt
            if (newMinutesInt != lastMinutesInt) {
                // DIFFERENT MINUTES FROM BEFORE
                newMinutesInt = newMinutesInt - lastMinutesInt
                lastMinutesInt = minutesStr.toInt()
                finalMinutesInt = newMinutesInt + Stash.getInt(Constants.MINUTES, 0)
                Stash.put(Constants.MINUTES, finalMinutesInt)
            }
            var newHourInt = hoursStr.toInt()
            var finalHourInt = lastHourInt
            if (newHourInt != lastHourInt) {
                // DIFFERENT MINUTES FROM BEFORE
                newHourInt = newHourInt - lastHourInt
                lastHourInt = hoursStr.toInt()
                finalHourInt = newHourInt + Stash.getInt(Constants.HOURS, 0)
                Stash.put(Constants.HOURS, finalHourInt)
            }
            Log.d(TAG, "newHourInt: $newHourInt")
            Log.d(TAG, "newMinutesInt: $newMinutesInt")
            Stash.put(Constants.CURRENT_TIME, "$finalHourInt hrs $finalMinutesInt mins")
            Log.d(TAG, "finalHourInt: $finalHourInt")
            Log.d(TAG, "finalMinutesInt: $finalMinutesInt")
            timeTextView!!.text = "$finalHourInt hrs $finalMinutesInt mins"
        }
        .build()

    fun startStopWatch(textView: TextView?) {
        timeTextView = textView
        stopwatch.start()
    }

    fun stopStopWatch() {
        stopwatch.stop()
        stopwatch.reset()
    }
}