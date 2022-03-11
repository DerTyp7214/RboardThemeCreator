package de.dertyp7214.rboardthemecreator.core

import android.content.Context
import android.text.format.Formatter

fun Long.toHumanReadableBytes(context: Context): String = Formatter.formatFileSize(context, this)