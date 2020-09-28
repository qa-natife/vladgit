package com.natife.voobrazharium.utils.audio

import android.content.Context


interface OnAudio {

    fun soundClick(context: Context)
    fun soundClickPlayer(context: Context)

    fun soundApplause(context: Context)
    fun soundApplausePlayer(context: Context)
}