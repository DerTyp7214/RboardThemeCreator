package de.dertyp7214.rboardthemecreator

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.ColorUtils
import de.dertyp7214.rboardthemecreator.core.toHex
import de.dertyp7214.rboardthemecreator.data.ThemeMetadata
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.math.min


object ThemeUtils {
    fun generateTheme(context: Context, @ColorInt color: Int, dark: Boolean = false): File {
        val workingDir = File(context.filesDir, "theme")
        if (!workingDir.exists()) workingDir.mkdirs()
        workingDir.listFiles()?.forEach { it.delete() }

        val defs = StringBuilder()

        val colorSetA1 = if (dark) changeHSL(color, -1, -40, -40) else changeHSL(color, -1, 40, 40)
        val colorSetA2 =
            if (dark) changeHSL(colorSetA1, -1, 0, 4) else changeHSL(colorSetA1, -1, 0, -4)
        val colorSetA3 =
            if (dark) changeHSL(colorSetA2, -1, 16, 14) else changeHSL(colorSetA2, -1, -16, -14)
        val colorSetA4 =
            if (ColorUtils.calculateLuminance(colorSetA1) < .5) Color.WHITE else Color.BLACK
        val colorSetA5 = changeHSL(color, 0, 0, 0)
        val colorSetA6 =
            if (dark) changeHSL(colorSetA5, -1, -27, 41) else changeHSL(colorSetA5, -1, 27, -41)

        defs.append("@def color_set_a1 ${colorSetA1.toHex()}FF;\n")
        defs.append("@def color_set_a2 ${colorSetA2.toHex()}FF;\n")
        defs.append("@def color_set_a3 ${colorSetA3.toHex()}FF;\n")
        defs.append("@def color_set_a4 ${colorSetA4.toHex()}FF;\n")
        defs.append("@def color_set_a5 ${colorSetA5.toHex()}FF;\n")
        defs.append("@def color_set_a6 ${colorSetA6.toHex()}FF;\n")

        val themeName = "Color Theme (${color.toHex()})"

        File(workingDir, "style_sheet_variables.css").writeText(defs.toString())
        File(workingDir, "metadata.json").writeText(
            ThemeMetadata(
                name = themeName,
                is_light_theme = ColorUtils.calculateLuminance(colorSetA1) > .5
            ).toString()
        )
        File(workingDir, "style_sheet_md2.css").writeText(getRaw(context, R.raw.style_sheet_md2))
        File(workingDir, "style_sheet_md2_border.css").writeText(
            getRaw(
                context,
                R.raw.style_sheet_md2_border
            )
        )

        val zip = File(workingDir, "${themeName.replace(" ", "_").replace(Regex("[()#]"), "")}.zip")
        val pack = File(context.cacheDir, "theme.pack")

        Zip().apply {
            if (zip.exists()) zip.delete()
            zip(
                workingDir.listFiles()!!.map { it.absolutePath },
                zip.absolutePath
            )
            val meta = File(workingDir, "pack.meta").apply {
                writeText("name=$themeName\nauthor=Gboard Theme Creator\n")
            }
            zip(
                listOf(zip.absolutePath, meta.absolutePath),
                pack.absolutePath
            )
            meta.delete()
        }
        return pack
    }

    fun shareTheme(activity: Activity, themePack: File) {
        val uri = FileProvider.getUriForFile(
            activity,
            activity.packageName,
            themePack
        )
        ShareCompat.IntentBuilder.from(activity)
            .setStream(uri)
            .setType("application/pack")
            .intent
            .setAction(Intent.ACTION_SEND)
            .setDataAndType(uri, "application/pack")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION).apply {
                activity.startActivity(
                    Intent.createChooser(
                        this,
                        activity.getString(R.string.share_theme)
                    )
                )
            }
    }

    private fun getRaw(context: Context, id: Int): String {
        return BufferedReader(InputStreamReader(context.resources.openRawResource(id))).use(
            BufferedReader::readText
        )
    }

    private fun changeHSL(color: Int, h: Int, s: Int, l: Int): Int {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(color, hsl)
        Log.d("AA", hsl.joinToString(","))
        Log.d("AA", "$h $s $l ${color.toHex()}")
        hsl[0] =
            if (hsl[0] + h > 360) hsl[0] + h - 360 else if (hsl[0] + h < 0) hsl[0] + h + 360 else hsl[0] + h
        hsl[1] = min(hsl[1] + (s / 100F), .95F)
        hsl[2] = min(hsl[2] + (l / 100F), .95F)
        Log.d("BB", hsl.joinToString(","))
        return ColorUtils.HSLToColor(hsl).also { Log.d("BB", it.toHex()) }
    }

    fun getSystemAccent(context: Context): Int {
        val typedValue = TypedValue()
        val contextThemeWrapper = ContextThemeWrapper(
            context,
            android.R.style.Theme_DeviceDefault
        )
        contextThemeWrapper.theme.resolveAttribute(
            android.R.attr.colorAccent,
            typedValue, true
        )
        return typedValue.data
    }
}