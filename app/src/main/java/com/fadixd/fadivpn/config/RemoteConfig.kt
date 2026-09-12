package com.fadixd.fadivpn.config

import java.net.HttpURLConnection
import java.net.URL

object RemoteConfig {

    private const val CONFIG_URL =
        "https://raw.githubusercontent.com/fadixd1/XD-VPN/refs/heads/main/config.txt"

    fun fetch(): String? {
        return try {
            val connection =
                URL(CONFIG_URL).openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("Cache-Control", "no-cache")

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use {
                    it.readText().trim()
                }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
