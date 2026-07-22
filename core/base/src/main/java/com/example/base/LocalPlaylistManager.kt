package com.example.base

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

object LocalPlaylistManager {
    private const val PREF_NAME = "MusicAppPrefs"
    private const val KEY_PLAYLIST = "saved_playlist"

    fun savePlaylist(context: Context, playlist: List<SongDetail>) {
        val prefs = context.getSharedPreferences(PREF_NAME, 0)
        val jsonString = Gson().toJson(playlist)
        prefs.edit().putString(KEY_PLAYLIST, jsonString).apply()
    }

    fun getPlaylist(context: Context): List<SongDetail> {
        val prefs = context.getSharedPreferences(PREF_NAME, 0)
        val jsonString = prefs.getString(KEY_PLAYLIST, null)

        //如果本地没存过，直接返回空列表
        if (jsonString.isNullOrEmpty()) {
            return emptyList()
        }

        //String -> List<Track>
        val type = object : TypeToken<List<SongDetail>>() {}.type
        return Gson().fromJson(jsonString, type)
    }
}