package com.example.tarot.data

import android.content.Context

class AppContainer(context: Context) {

    val repository: TarotRepository = TarotRepository(context.applicationContext)
}
