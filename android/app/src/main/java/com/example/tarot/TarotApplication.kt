package com.example.tarot

import android.app.Application
import com.example.tarot.data.AppContainer

class TarotApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
