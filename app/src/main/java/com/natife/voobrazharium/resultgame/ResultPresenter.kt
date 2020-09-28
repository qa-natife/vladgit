package com.natife.voobrazharium.resultgame

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import android.view.View
import com.natife.voobrazharium.init_game.InitGameContract
import com.natife.voobrazharium.init_game.InitGameRepository
import com.natife.voobrazharium.init_game.Player
import java.util.ArrayList

class ResultPresenter: ResultContract.Presenter {


    private val mRepository: InitGameContract.Repository = InitGameRepository.getInstance()
    private lateinit var localPayerList: MutableList<Player>


    override fun getPlayerList(): MutableList<Player> {
        return mRepository.getCurrentPlayerList()
    }


    override fun sortPlayerList(): MutableList<Player> {
        localPayerList = ArrayList(getPlayerList())
        (localPayerList as ArrayList<Player>).sortWith(Comparator { player, t1 ->
            when {
                player.countScore == t1.countScore -> 0
                player.countScore < t1.countScore -> 1
                else -> -1
            }
        })
        return localPayerList
    }

    override fun checkWin(): Boolean {
        var flag = true
        if (localPayerList.get(index = 0).countScore == localPayerList.get(index = 1).countScore) {
            flag = false
        }
        return flag
    }

    override fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas)
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }

    override fun restorePlayerListOnRepository(list: List<Player>){
        mRepository.restorePlayerListFromPrefer(list)
    }
}