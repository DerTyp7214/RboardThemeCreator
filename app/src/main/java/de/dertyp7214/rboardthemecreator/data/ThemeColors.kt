package de.dertyp7214.rboardthemecreator.data

import androidx.annotation.ColorInt

data class ThemeColors(
    @ColorInt var mainBackground: Int,
    @ColorInt var keyBackground: Int,
    @ColorInt var keyColor: Int,
    @ColorInt var secondaryKeyBackground: Int,
    @ColorInt var accentBackground: Int,
    @ColorInt var tertiaryBackground: Int,
    var template: String,
)
