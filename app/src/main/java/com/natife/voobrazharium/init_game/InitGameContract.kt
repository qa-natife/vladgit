package com.natife.voobrazharium.init_game

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.natife.voobrazharium.base.BasePresenter

interface InitGameContract {

    interface View {
        fun showListPlayers(listPlayer: MutableList<Player>)

        fun changeScreen(flagChange: Boolean)

        fun contextActivity(): Context

        fun showSettingsDialog(flagStartGame: Boolean)

        fun showAlert(message: Int)

        fun startGame(playerList: MutableList<Player>, difficultLevel: Int)

        fun createWordList(mRepository: Repository, difficultLevel: Int): MutableList<String>
    }

    interface Presenter: BasePresenter<View> {
        fun initPlayerList(listWithName: MutableList<Player>?)

        fun btnAddPlayerClicked()

        fun btnNextClicked(difficultLevel: Int)

        fun btnBackClicked()

        fun btnSettingsClicked()

        fun getFlagChangeScreen(): Boolean
    }

    interface Repository {
        fun createListPlayer(listWithName: MutableList<Player>?): MutableList<Player>

        fun addNameInPlayerList(): MutableList<Player>

        fun createListWords(difficultLevel: Int, context: Context): MutableList<String>

        fun getCurrentPlayerList(): MutableList<Player>

        fun restorePlayerListFromPrefer(list: List<Player>)

        fun startRefreshHowPlayScreen()
        fun getLifeData(): MutableLiveData<Boolean>
    }
}