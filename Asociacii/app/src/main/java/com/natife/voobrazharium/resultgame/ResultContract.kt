package com.natife.voobrazharium.resultgame

import android.graphics.Bitmap
import android.widget.RelativeLayout
import com.natife.voobrazharium.init_game.Player

interface ResultContract {

    interface Presenter {
        fun sortPlayerList(): MutableList<Player>
        fun getPlayerList(): MutableList<Player>
        fun checkWin(): Boolean
        fun getBitmapFromView(view: android.view.View): Bitmap
        fun restorePlayerListOnRepository(list: List<Player>)
    }

}