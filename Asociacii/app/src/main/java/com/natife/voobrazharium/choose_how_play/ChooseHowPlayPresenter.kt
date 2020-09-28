package com.natife.voobrazharium.choose_how_play

import android.arch.lifecycle.LiveData
import com.natife.voobrazharium.init_game.InitGameContract
import com.natife.voobrazharium.init_game.InitGameRepository
import com.natife.voobrazharium.init_game.Player
import java.util.*
import android.os.CountDownTimer
import com.natife.voobrazharium.utils.restoreNumberCircles

class ChooseHowPlayPresenter(private val mView: ChooseHowPlayContract.View) : ChooseHowPlayContract.Presenter {

    private val mRepository: InitGameContract.Repository = InitGameRepository.getInstance()
    private var playerList = mutableListOf<Player>()
    private var listWords = mutableListOf<String>()
    private var positionWord1 = -1
    private var positionWord2 = -1
    private lateinit var name: String
    private var colorPlayer = 0
    private lateinit var word: String
    private var positionPlayer: Int = 0
    private lateinit var mCountDownTimer: CountDownTimer
    private val COUNT_DOWN_INTERVAL = 1000
    private var playerPosDefault: Int = -1
    private var lapDefault: Int = 0
    private lateinit var word1: String
    private lateinit var word2: String


    init {
        lapDefault = restoreNumberCircles(mView.getContextActivity())
    }


    override fun getPlayerList(): MutableList<Player> {
        return mRepository.getCurrentPlayerList()
    }


    override fun buttonGoPressed() {
        mView.startGameActivity(positionPlayer)
    }


    override fun findDataForFillFields(playerList: MutableList<Player>, timeGame: Int, difficultLevel: Int) {
        this.playerList = playerList
        positionPlayer = getPositionPlayer()
        if (!playerList[positionPlayer].tell && !playerList[positionPlayer].show && !playerList[positionPlayer].draw) {
            playerList[positionPlayer].tell = true
            playerList[positionPlayer].show = true
            playerList[positionPlayer].draw = true
        }

        if (lapDefault != 0) {
            if (this.listWords.size < 2) {
                this.listWords = mRepository.createListWords(difficultLevel, mView.getContextActivity())
            }
            positionWord1 = getRandom(this.listWords.size)
            positionWord2 = getRandom(this.listWords.size)
            word1 = this.listWords[positionWord1]
            word2 = this.listWords[positionWord2]
            positionWord1 = -1


            name = playerList[positionPlayer].name!!
            name = name.substring(0, 1).toUpperCase() + name.substring(1)
            word1 = word1.substring(0, 1).toUpperCase() + word1.substring(1)
            word2 = word2.substring(0, 1).toUpperCase() + word2.substring(1)
            colorPlayer = playerList[positionPlayer].color
            mView.showData(name, colorPlayer, word1, word2, positionPlayer)
            startTimerGame(timeGame)
        } else {
            mView.gameOver()
            mView.showData(name, colorPlayer, word1, word2, positionPlayer)
        }
    }

    override fun replaceWords(){
        positionWord1 = getRandom(this.listWords.size)
        positionWord2 = getRandom(this.listWords.size)
        word1 = this.listWords[positionWord1]
        word2 = this.listWords[positionWord2]
        mView.showData(word1, word2)
    }


    override fun removeSelectedWord(word: String) {
        this.listWords.remove(word)
    }

    private fun getRandom(size: Int): Int {
        val rand = Random()
        var position = rand.nextInt(size)
        if (positionWord1 == -1) {
            return position
        }

        if (this.listWords[positionWord1] == this.listWords[position]) {
            position = getRandom(size)
        }
        return position
    }// getRandom


    private fun startTimerGame(timeGame: Int) {
        mCountDownTimer = object : CountDownTimer(((timeGame + 1) * 1001).toLong(), COUNT_DOWN_INTERVAL.toLong()) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                mView.gameOver()
            }
        }
        mCountDownTimer.start()
    }

    override fun stopTimerGame() {
        mCountDownTimer.cancel()
    }


    private fun getPositionPlayer(): Int {
        if (playerPosDefault < playerList.size - 1) {
            playerPosDefault++
        } else {
            playerPosDefault = 0
            lapDefault--
        }
        return playerPosDefault
    }

    override fun getPosPlayer(): Int {
        return positionPlayer
    }

    override fun getLifeData(): LiveData<Boolean> {
        return InitGameRepository.getInstance().getLifeData()
    }

    override fun restorePlayerListOnRepository(list: List<Player>){
        mRepository.restorePlayerListFromPrefer(list)
    }

}