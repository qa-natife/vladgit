package com.natife.voobrazharium.game

import android.os.CountDownTimer
import android.util.Log
import com.natife.voobrazharium.R
import com.natife.voobrazharium.init_game.InitGameContract
import com.natife.voobrazharium.init_game.InitGameRepository
import com.natife.voobrazharium.init_game.Player
import java.util.*
import java.util.concurrent.TimeUnit

class GamePresenter(private val mView: GameContract.View) : GameContract.Presenter {

    private val mRepository: InitGameContract.Repository = InitGameRepository.getInstance()
    private var mCountDownTimer: CountDownTimer? = null
    private val countDownInterval = 500
    private var timeStart: Long = System.currentTimeMillis()


    override fun getPlayerList(): MutableList<Player> {
        return mRepository.getCurrentPlayerList()
    }


    override fun playerWin(playerList: MutableList<Player>, winPlayer: Int, positionGuessingPlayer: Int) {
        val score = playerList[winPlayer].countScore + 1
        val countWords = playerList[winPlayer].countWords + 1
        val scoreGuessingPlayer = playerList[positionGuessingPlayer].countScore + 1
        playerList[positionGuessingPlayer].countScore = scoreGuessingPlayer
        playerList[winPlayer].countScore = score
        playerList[winPlayer].countWords = countWords
        mView.finishCurrentGame()
    }

    override fun notWin() {
        mView.finishCurrentGame()
    }

    override fun initTimer(timerBig: Boolean, timeMove:Int) {

        val newTime: Long
        var difference = 0

        if (mCountDownTimer != null) {
            newTime = System.currentTimeMillis()//время после паузы
            difference = (newTime - timeStart).toInt()// разница между стартом и паузой
        }


        mCountDownTimer = object : CountDownTimer((((timeMove + 1) * 1000) - difference).toLong(), countDownInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                Log.v("Log_tag", "Tick of Progress$millisUntilFinished")
                if (timerBig) {
                    val progress = timeMove - millisUntilFinished.toInt() / 1000
                    mView.setCircularProgressbar(progress)
                }
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished ) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                mView.setTextTimer(String.format(Locale.getDefault(), "%01d:%02d", minutes, seconds))
            }

            override fun onFinish() {
                if (timerBig) {
                    mView.setCircularProgressbar(timeMove)
                }
                mView.setTextTimer(String.format(Locale.getDefault(), "%01d:%02d", 0, 0))
                android.support.v7.app.AlertDialog.Builder(mView.contextActivity(), R.style.ColorDialogTheme)
                        .setTitle(mView.contextActivity().resources.getString(R.string.time_gone))
                        .setMessage(mView.contextActivity().resources.getString(R.string.word_is_guessed))
                        .setPositiveButton(mView.contextActivity().resources.getString(R.string.they_guessed)
                        ) { _, _ -> mView.dialogTimeMoveGone(true) }
                        .setNegativeButton(mView.contextActivity().resources.getString(R.string.they_not_guessed)
                        ) { _, _ -> mView.dialogTimeMoveGone(false) }
                        .setCancelable(false)
                        .show()
            }
        }
        mCountDownTimer!!.start()
    }

    override fun setLocaleRes(name: String?, res: String): String {
        val lang = Locale.getDefault().language
        val nameNew = name?.let { it.substring(0, 1).toUpperCase() + it.substring(1) }
        return if (lang == "en") {
            String.format("%s %s", nameNew, res)
        } else {
            String.format("%s %s", res, nameNew)
        }
    }

    override fun stopCountDownTimer() {
        mCountDownTimer!!.cancel()
    }

    override fun gameActivityDestroyed() {
       mRepository.startRefreshHowPlayScreen()
    }
}
