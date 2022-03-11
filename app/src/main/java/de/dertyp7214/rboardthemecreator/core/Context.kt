package de.dertyp7214.rboardthemecreator.core

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.os.postDelayed

fun delayed(delay: Long, callback: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(delay) { callback() }
}

fun Context.getAttr(@AttrRes attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

@SuppressLint("DiscouragedApi")
fun Context.getNavigationBarHeight(): Int {
    val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        return 0
    }
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
}