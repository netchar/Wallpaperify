package com.netchar.remote

import com.squareup.moshi.JsonAdapter
import se.ansman.kotshi.KotshiJsonAdapterFactory

@KotshiJsonAdapterFactory
abstract class ApplicationJsonAdapterFactory : JsonAdapter.Factory {
    companion object {
        val instance: ApplicationJsonAdapterFactory = KotshiApplicationJsonAdapterFactory
    }
}