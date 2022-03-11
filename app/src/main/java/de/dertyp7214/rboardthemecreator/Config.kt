package de.dertyp7214.rboardthemecreator

import android.os.Build


@Suppress("MemberVisibilityCanBePrivate", "unused", "SdCardPath")
object Config {
    var useMagisk = false
    var newGboard = true
    val PLAY_URL = { packageName: String ->
        "https://play.google.com/store/apps/details?id=$packageName"
    }

    const val GBOARD_PACKAGE_NAME = "com.google.android.inputmethod.latin"
    const val RBOARD_THEME_PACKAGE_NAME = "de.dertyp7214.rboardthememanager"
    const val RBOARD_THEME_CREATOR_PACKAGE_NAME = "de.dertyp7214.rboardthemecreator"
}