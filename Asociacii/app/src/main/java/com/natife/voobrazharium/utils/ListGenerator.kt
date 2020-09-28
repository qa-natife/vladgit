package com.natife.voobrazharium.utils

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

class ListGenerator {

    companion object {

        private val EASE_LEVEL = "lowLevel"
        private val NORMAL_LEVEL = "mediumLevel"
        private val HARD_LEVEL = "hardLevel"

        fun createListSelectedLevel(context: Context, difficultLevel: Int): MutableList<String> {
            val am = context.assets
            var `is`: InputStream? = null
            var stringInGson = ""
            try {
                val lang = Locale.getDefault().language
                `is` = when (lang) {
                    "ru" -> am.open("words.txt")
                    "en" -> am.open("words_en.txt")
                    "uk" -> am.open("words_ukr.txt")
                    else -> am.open("words_en.txt")
                }
                stringInGson = convertStreamToString(`is`!!)
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return createListSelectedLevel(stringInGson, difficultLevel)
        }

        @Throws(IOException::class)
        private fun convertStreamToString(inputStream: InputStream): String {
            val result = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var length =  inputStream.read(buffer)
            while (length != -1) {
                result.write(buffer, 0, length)
                length =  inputStream.read(buffer)
            }
            return result.toString("UTF-8")
        }

        private fun createListSelectedLevel(stringInGson: String, difficultLevel: Int): MutableList<String> {
            val list =  mutableListOf<String>()
            var level = ""
            try {
                when (difficultLevel) {
                    1 -> level = EASE_LEVEL
                    2 -> level = NORMAL_LEVEL
                    3 -> level = HARD_LEVEL
                }

                val obj = JSONObject(stringInGson)
                val messages = obj.get(level) as JSONArray

                for (i in 0 until messages.length()) {
                    var word = messages.getString(i)
                    word = word.substring(0, 1).toUpperCase() + word.substring(1)
                    list.add(word)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return list
        }
    }
}