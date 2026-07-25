package com.example.lijiemusic

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.net.CookieManager
import com.therouter.TheRouter

class MusicApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        TheRouter.init(this)
        CookieManager.init(this)
    }
}