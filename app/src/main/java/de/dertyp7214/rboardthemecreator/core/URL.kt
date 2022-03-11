package de.dertyp7214.rboardthemecreator.core

import java.net.HttpURLConnection
import java.net.URL

fun URL.getTextFromUrl(): String {
    return try {
        readText()
    } catch (e: Exception) {
        ""
    }
}

fun URL.isReachable(): Boolean {
    return try {
        (openConnection() as HttpURLConnection).responseCode == 200
    } catch (e: Exception) {
        false
    }
}