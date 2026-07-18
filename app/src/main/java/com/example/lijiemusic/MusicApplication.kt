package com.example.lijiemusic

import android.app.Application
import com.example.net.CookieManager
import com.therouter.TheRouter

class MusicApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TheRouter.init(this)
        CookieManager.init(this)
    }
}