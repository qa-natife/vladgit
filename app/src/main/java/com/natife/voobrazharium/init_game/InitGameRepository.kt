package com.natife.voobrazharium.init_game

import android.content.Context
import com.natife.voobrazharium.R
import com.natife.voobrazharium.utils.ListGenerator
import java.util.*
import android.arch.lifecycle.MutableLiveData

class InitGameRepository : InitGameContract.Repository {

    private val colorList = java.util.ArrayList(Arrays.asList(R.color.colorPlayer1, R.color.colorPlayer2,
            R.color.colorPlayer3, R.color.colorPlayer4, R.color.colorPlayer5, R.color.colorPlayer6))
    private var playerList = mutableListOf<Player>()
    private val liveData = MutableLiveData<Boolean>().apply {
        value = true
    }


    companion object {

        @Volatile
        private var INSTANCE: InitGameRepository? = null

        fun getInstance(): InitGameContract.Repository {
            if (INSTANCE == null) {
                synchronized(InitGameRepository::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = InitGameRepository()
                    }
                }
            }
            return INSTANCE as InitGameContract.Repository
        }
    }


    override fun addNameInPlayerList(): MutableList<Player> {
        playerList.add(Player("", colorList[playerList.size], 0, 0, true, true, true))
        return playerList
    }


    override fun createListWords(difficultLevel: Int, context: Context): MutableList<String> {
        return ListGenerator.createListSelectedLevel(context, difficultLevel)
    }


    override fun createListPlayer(listWithName: MutableList<Player>?): MutableList<Player> {
        playerList = java.util.ArrayList()
        if (listWithName == null) {
            for (i in 0..2) {
                playerList.add(Player("", colorList[i], 0, 0, true, true, true))
            }
        } else {
            for (i in listWithName.indices) {
                playerList.add(Player(listWithName[i].name!!, colorList[i], 0, 0, true, true, true))
            }
        }
        return playerList
    }

    override fun restorePlayerListFromPrefer(list: List<Player>){
        playerList = list.toMutableList()
    }

    override fun getCurrentPlayerList(): MutableList<Player> {
        return playerList
    }

    override fun getLifeData(): MutableLiveData<Boolean> {
        return liveData
    }


    override fun startRefreshHowPlayScreen(){
        liveData.value = true
    }

}


