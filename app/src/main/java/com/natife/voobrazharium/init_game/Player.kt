package com.natife.voobrazharium.init_game

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class Player : Parcelable, Serializable {
    var name: String? = null
    var color: Int = 0
    var countWords: Int = 0
    var countScore: Int = 0
    var show: Boolean = true
    var tell: Boolean = true
    var draw: Boolean = true


    constructor(name: String, color: Int, countWords: Int, countScore: Int, show: Boolean, tell: Boolean, draw: Boolean) {
        this.name = name
        this.color = color
        this.countWords = countWords
        this.countScore = countScore
        this.show = show
        this.tell = tell
        this.draw = draw
    }

    private constructor(`in`: Parcel) {
        name = `in`.readString()
        color = `in`.readInt()
        countWords = `in`.readInt()
        countScore = `in`.readInt()
          tell = `in`.readByte().toInt() != 0
        show = `in`.readByte().toInt() != 0
        draw = `in`.readByte().toInt() != 0
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(name)
        parcel.writeInt(color)
        parcel.writeInt(countWords)
        parcel.writeInt(countScore)
        parcel.writeByte(if (tell) 1 else 0)
        parcel.writeByte(if (show) 1 else 0)
        parcel.writeByte(if (draw) 1 else 0)
    }

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(p0: Parcel): Player {
            return Player(p0)
        }

        override fun newArray(p0: Int): Array<Player?> {
            return arrayOfNulls(p0)
        }
    }
}