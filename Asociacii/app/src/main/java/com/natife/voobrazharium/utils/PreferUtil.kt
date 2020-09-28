package com.natife.voobrazharium.utils

import android.content.Context
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import com.google.gson.Gson
import com.natife.voobrazharium.init_game.Player


var TIME_MOVE_KEY = "timeMove"
var TIME_GAME_KEY = "timeGame"
var NUMBER_CIRCLE_KEY = "numberCircles"
var COLOR_DRAW_KEY = "colorDraw"
var LIST_USERS = "listUsers"

fun initPreference (context: Context) {
    saveTimeMove(context, TIME_MOVE_DEFAULT)
    saveTimeGame(context, TIME_GAME_DEFAULT)
    saveNumberCircles(context, NUMBER_LAP_DEFAULT)
    saveColorDraw(context, ContextCompat.getColor(context, com.natife.voobrazharium.R.color.colorDefault))
}

fun saveTimeMove(context: Context, value: Int) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = prefs.edit()
    editor.putInt(TIME_MOVE_KEY, value)
    editor.apply()
}

fun restoreTimeMove(context: Context): Int {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getInt(TIME_MOVE_KEY, 0)
}

fun saveTimeGame(context: Context, value: Int) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = prefs.edit()
    editor.putInt(TIME_GAME_KEY, value)
    editor.apply()
}

fun restoreTimeGame(context: Context): Int {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getInt(TIME_GAME_KEY, 0)
}

fun saveNumberCircles(context: Context, value: Int) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = prefs.edit()
    editor.putInt(NUMBER_CIRCLE_KEY, value)
    editor.apply()
}

fun restoreNumberCircles(context: Context): Int {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getInt(NUMBER_CIRCLE_KEY, 0)
}

fun saveColorDraw(context: Context, value: Int) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = prefs.edit()
    editor.putInt(COLOR_DRAW_KEY, value)
    editor.apply()
}

fun restoreColorDraw(context: Context): Int {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getInt(COLOR_DRAW_KEY, 0)
}

fun savePlayerList(playerList: List<Player>, context: Context){
    val gson = Gson()
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = prefs.edit()
    editor.putString(LIST_USERS, gson.toJson(playerList))
    editor.apply()
}

fun restorePlayerList(context: Context): List<Player>{
    val gson = Gson()
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return gson.fromJson(prefs.getString(LIST_USERS, ""), Array<Player>::class.java).toList()
}