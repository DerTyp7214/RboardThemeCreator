package de.dertyp7214.rboardthemecreator.data

import androidx.annotation.ColorInt

data class ThemeColors(
    @param:ColorInt var mainBackground: Int,
    @param:ColorInt var keyBackground: Int,
    @param:ColorInt var keyColor: Int,
    @param:ColorInt var secondaryKeyBackground: Int,
    @param:ColorInt var accentBackground: Int,
    @param:ColorInt var tertiaryBackground: Int,
    var template: String,
)
