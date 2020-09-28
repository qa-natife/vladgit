package com.natife.voobrazharium.choose_how_play

import android.arch.lifecycle.LiveData
import android.content.Context
import com.natife.voobrazharium.init_game.Player

interface ChooseHowPlayContract {
    interface View {

        fun startGameActivity(posPlayer: Int)

        fun showResultDialog()

        fun showData(name: String, color: Int, word1: String, word2: String, positionPlayer: Int)

        fun showData(word1: String, word2: String)

        fun getContextActivity(): Context

        fun gameOver()
    }

    interface Presenter {

        fun buttonGoPressed()

        fun findDataForFillFields(playerList: MutableList<Player>, timeGame: Int, difficultLevel: Int)

        fun getPlayerList(): MutableList<Player>

        fun stopTimerGame()

        fun getPosPlayer(): Int

        fun getLifeData(): LiveData<Boolean>

        fun removeSelectedWord(word: String)

        fun replaceWords()

        fun restorePlayerListOnRepository(list: List<Player>)
    }
}