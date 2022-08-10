package de.dertyp7214.rboardthemecreator


@Suppress("MemberVisibilityCanBePrivate", "unused", "SdCardPath")
object Config {
    var newGboard = true
    val PLAY_URL = { packageName: String ->
        "https://play.google.com/store/apps/details?id=$packageName"
    }

    const val GBOARD_PACKAGE_NAME = "com.google.android.inputmethod.latin"
    const val RBOARD_THEME_PACKAGE_NAME = "de.dertyp7214.rboardthememanager"
    const val RBOARD_THEME_DEBUG_PACKAGE_NAME = "de.dertyp7214.rboardthememanager.debug"
    const val RBOARD_THEME_PATCHER_PACKAGE_NAME = "de.dertyp7214.rboardpatcher"
    const val RBOARD_THEME_PATCHER_DEBUG_PACKAGE_NAME = "de.dertyp7214.rboardpatcher.debug"
    const val RBOARD_THEME_CREATOR_PACKAGE_NAME = "de.dertyp7214.rboardthemecreator"
}