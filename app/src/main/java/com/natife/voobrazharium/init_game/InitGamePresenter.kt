package com.natife.voobrazharium.init_game

import com.natife.voobrazharium.R
import com.natife.voobrazharium.base.BasePresenterImpl

class InitGamePresenter : BasePresenterImpl<InitGameContract.View>(), InitGameContract.Presenter {

    private val mRepository: InitGameContract.Repository = InitGameRepository.getInstance()
    private var playerList = mutableListOf<Player>()
    private var flagStartGame = false


    override fun initPlayerList(listWithName: MutableList<Player>?) {
        this.playerList = mRepository.createListPlayer(listWithName)
        baseView?.showListPlayers(this.playerList)
    }

    override fun btnAddPlayerClicked() {
        if (playerList.size <= 5) {
            playerList = mRepository.addNameInPlayerList()
            baseView?.showListPlayers(playerList)
        }
    }

    override fun btnNextClicked(difficultLevel: Int) {
        if (flagStartGame) {
            flagStartGame = false

            //start to play...
            baseView?.startGame(playerList, difficultLevel)
        } else {
            for (i in playerList.indices) {
                if (playerList[i].name.equals("")) {
                    baseView?.showAlert(R.string.set_name)
                    return
                }
                for (j in i + 1 until playerList.size) {
                    if (playerList[i].name.equals(playerList[j].name)) {
                        baseView?.showAlert(R.string.set_different_name)
                        return
                    }
                }
            }
            baseView?.changeScreen(true)
            flagStartGame = true
        }
    }

    override fun btnBackClicked() {
        flagStartGame = false
        baseView?.changeScreen(false)
    }

    override fun btnSettingsClicked() {
        baseView?.showSettingsDialog(flagStartGame)
    }

    override fun getFlagChangeScreen(): Boolean {
        return flagStartGame
    }

}