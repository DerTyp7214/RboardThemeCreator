package de.dertyp7214.rboardthemecreator.core

import java.util.Locale

fun String.capitalize() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

operator fun String.times(n: Int) = repeat(n)