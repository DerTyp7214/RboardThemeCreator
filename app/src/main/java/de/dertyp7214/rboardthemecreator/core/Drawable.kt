package de.dertyp7214.rboardthemecreator.core

import android.graphics.Bitmap
import android.graphics.Canvas
import com.devs.vectorchildfinder.VectorDrawableCompat


fun VectorDrawableCompat.getBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}