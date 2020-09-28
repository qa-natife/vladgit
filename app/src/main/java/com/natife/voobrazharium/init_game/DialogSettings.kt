package com.natife.voobrazharium.init_game

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.natife.voobrazharium.R
import com.natife.voobrazharium.utils.*
import com.natife.voobrazharium.utils.audio.AudioUtil
import kotlinx.android.synthetic.main.dialog_settings_game.*

class DialogSettings : DialogFragment() {
    private lateinit var timeMoveTV: TextView
    private lateinit var timeGameTV: TextView
    private lateinit var numberCirclesTV: TextView
    private var timeMove: Int = 0
    private var timeGame: Int = 0
    private var numberCircles: Int = 0
    private lateinit var audio: AudioUtil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_settings_game, null)

        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        timeMoveTV = v.findViewById(R.id.text_time_move)
        timeGameTV = v.findViewById(R.id.text_time_game)
        numberCirclesTV = v.findViewById(R.id.text_number_of_circles)
        audio = AudioUtil.getInstance()

        //get info from preferences
        timeMove = restoreTimeMove(v.context)
        timeGame = restoreTimeGame(v.context)
        numberCircles = restoreNumberCircles(v.context)

        //for first init
        if (timeMove == 0 || timeGame == 0 || numberCircles == 0) {
            initPreference(v.context)
            timeMove = restoreTimeMove(v.context)
            timeGame = restoreTimeGame(v.context)
            numberCircles = restoreNumberCircles(v.context)
        } else {
            timeMoveTV.text = timeMove.toString()
            timeGameTV.text = timeGame.toString()
            numberCirclesTV.text = numberCircles.toString()
        }

        v.findViewById<View>(R.id.number_of_circles_plus).setOnClickListener {
            if (numberCircles != 30) {
                numberCircles += 1
                numberCirclesTV.text = numberCircles.toString()
                audio.soundClickPlayer(dialog.context)
            }
        }
        v.findViewById<View>(R.id.number_of_circles_minus).setOnClickListener {
            if (numberCircles != 1) {
                numberCircles -= 1
                numberCirclesTV.text = numberCircles.toString()
                audio.soundClickPlayer(dialog.context)
            }
        }
        v.findViewById<View>(R.id.time_move_minus).setOnClickListener {
            if (timeMove != 15) {
                timeMove -= 15
                timeMoveTV.text = timeMove.toString()
                audio.soundClickPlayer(dialog.context)
            }
        }
        v.findViewById<View>(R.id.time_move_plus).setOnClickListener {
            if (timeMove != 180) {
                timeMove += 15
                timeMoveTV.text = timeMove.toString()
                audio.soundClickPlayer(dialog.context)
            }
        }
        v.findViewById<View>(R.id.time_game_minus).setOnClickListener {
            if (timeGame != 15) {
                timeGame -= 5
                timeGameTV.text = timeGame.toString()
                audio.soundClickPlayer(dialog.context)
            }
        }
        v.findViewById<View>(R.id.time_game_plus).setOnClickListener {
            if (timeGame != 90) {
                timeGame += 5
                timeGameTV.text = timeGame.toString()
                audio.soundClickPlayer(dialog.context)
            }
        }
        v.findViewById<View>(R.id.buttonSave).setOnClickListener {
            saveTimeMove(v.context, timeMove)
            saveTimeGame(v.context, timeGame)
            saveNumberCircles(v.context, numberCircles)
            audio.soundClickPlayer(dialog.context)
            dismiss()
        }
        v.findViewById<View>(R.id.backSettings).setOnClickListener{
            audio.soundClickPlayer(dialog.context)
            dismiss()
        }
        return v
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}
