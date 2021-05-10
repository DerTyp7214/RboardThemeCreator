package de.dertyp7214.rboardthemecreator.core

fun Int.toHex(): String {
    return String.format("#%06X", (0XFFFFFF and this))
}