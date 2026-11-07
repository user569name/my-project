package com.example.tarot.data

import android.content.Context
import com.example.tarot.data.remote.ApiClient
import com.example.tarot.data.remote.TarotApi

class AppContainer(context: Context) {

    private val tokenStore = TokenStore(context)
    private val api: TarotApi = ApiClient.create { tokenStore.token }

    val repository: TarotRepository = TarotRepository(api, tokenStore)
}
