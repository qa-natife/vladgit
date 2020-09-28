package com.natife.voobrazharium.utils.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import com.natife.voobrazharium.R

class AudioUtil : OnAudio{
    private lateinit var sp: SoundPool
    private var soundIdClick: Int = 1
    private var soundIdApplause: Int = 1

    init {
//        initSP()
    }

    companion object {

        @Volatile
        private var INSTANCE: AudioUtil? = null

        fun getInstance(): AudioUtil {
            if (INSTANCE == null) {
                synchronized(AudioUtil::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = AudioUtil()

                    }
                }
            }
            return INSTANCE as AudioUtil
        }
    }


//    private fun initSP():SoundPool {
//        val audioAttrib = AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_GAME)
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .build()
//
//        sp = SoundPool.Builder()
//                .setAudioAttributes(audioAttrib)
//                .build()
//        return sp
//    }


    override fun soundClick(context: Context){
        soundIdClick = sp.load(context, R.raw.click, 1);

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val leftVolume = curVolume / maxVolume
        val rightVolume = curVolume / maxVolume
        val priority = 1
        val no_loop = 0
        val normal_playback_rate = 1f
        sp.play(soundIdClick, leftVolume, rightVolume, priority, no_loop, normal_playback_rate)
    }


    override fun soundApplause(context: Context) {
        soundIdApplause = sp.load(context, R.raw.applause, 1);

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val leftVolume = curVolume / maxVolume
        val rightVolume = curVolume / maxVolume
        val priority = 1
        val no_loop = 0
        val normal_playback_rate = 1f
        sp.play(soundIdApplause, leftVolume, rightVolume, priority, no_loop, normal_playback_rate)
    }

    override fun soundClickPlayer(context: Context) {
        Thread(Runnable {
            MediaPlayer.create(context,  R.raw.click).start()
        }).start()
    }

    override fun soundApplausePlayer(context: Context) {
        Thread(Runnable {
            MediaPlayer.create(context,  R.raw.applause).start()
        }).start()
    }

}