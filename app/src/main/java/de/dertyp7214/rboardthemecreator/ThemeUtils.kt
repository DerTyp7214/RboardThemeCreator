package de.dertyp7214.rboardthemecreator

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.ColorUtils
import com.devs.vectorchildfinder.VectorChildFinder
import com.devs.vectorchildfinder.VectorDrawableCompat
import de.dertyp7214.rboardthemecreator.core.toHex
import de.dertyp7214.rboardthemecreator.data.ThemeMetadata
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import kotlin.math.min

object ThemeUtils {
    fun generateTheme(
        context: Context,
        @ColorInt color: Int,
        dark: Boolean = false,
        image: Bitmap? = null
    ): File {
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
            if (dark) changeHSL(colorSetA5, -1, -12, 12) else changeHSL(colorSetA5, -1, 27, -41)

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

        val zip = File(
            workingDir,
            "${
                themeName.replace(" ", "_").replace(Regex("[()#]"), "")
            }_${if (dark) "dark" else "light"}.zip"
        )
        val pack = File(context.cacheDir, "theme.pack")

        Zip().apply {
            if (zip.exists()) zip.delete()
            zip(
                workingDir.listFiles()!!.map { it.absolutePath },
                zip.absolutePath
            )
            val img = File(zip.absolutePath.replace(".zip", ""))
            val meta = File(workingDir, "pack.meta").apply {
                writeText("name=$themeName\nauthor=Gboard Theme Creator\n")
            }
            zip(
                arrayListOf(zip.absolutePath, meta.absolutePath).apply {
                    if (image != null) {
                        val stream = FileOutputStream(img)
                        image.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        stream.flush()
                        stream.close()
                        add(img.absolutePath)
                    }
                },
                pack.absolutePath
            )
            meta.delete()
        }
        return pack
    }

    fun shareTheme(activity: Activity, themePack: File, install: Boolean = true) {
        val uri = FileProvider.getUriForFile(
            activity,
            activity.packageName,
            themePack
        )
        ShareCompat.IntentBuilder.from(activity)
            .setStream(uri)
            .setType("application/pack")
            .intent
            .setAction(if (install) Intent.ACTION_VIEW else Intent.ACTION_SEND)
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
        hsl[0] =
            if (hsl[0] + h > 360) hsl[0] + h - 360 else if (hsl[0] + h < 0) hsl[0] + h + 360 else hsl[0] + h
        hsl[1] = min(hsl[1] + (s / 100F), .95F)
        hsl[2] = min(hsl[2] + (l / 100F), .95F)
        return ColorUtils.HSLToColor(hsl)
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

    fun parseImage(context: Context, color: Int, dark: Boolean, imageView: ImageView) {

        val colorSetA1 = if (dark) changeHSL(color, -1, -40, -40) else changeHSL(color, -1, 40, 40)
        val colorSetA2 =
            if (dark) changeHSL(colorSetA1, -1, 0, 4) else changeHSL(colorSetA1, -1, 0, -4)
        val colorSetA3 =
            if (dark) changeHSL(colorSetA2, -1, 16, 14) else changeHSL(colorSetA2, -1, -16, -14)
        val colorSetA4 =
            if (ColorUtils.calculateLuminance(colorSetA1) < .5) Color.WHITE else Color.BLACK
        val colorSetA5 = changeHSL(color, 0, 0, 0)
        val colorSetA6 =
            if (dark) changeHSL(colorSetA5, -1, -27, 15) else changeHSL(colorSetA5, -1, 27, -41)

        val colors = listOf(
            colorSetA1,
            colorSetA2,
            colorSetA3,
            colorSetA4,
            colorSetA5,
            colorSetA6
        )

        val colorMap = mapOf(
            Pair(colors[1], listOf("EAEAEA_1", "EAEAEA_2", "EAEAEA_3")),
            Pair(
                colors[5], listOf(
                    "878787_1", "878787_2", "878787_3",
                    "878787_4", "878787_5", "878787_6"
                )
            ),
            Pair(colors[2], listOf("FAFAFA_1", "FAFAFA_2", "FAFAFA_3")),
            Pair(
                colors[3], listOf(
                    "000000_1", "000000_2", "000000_3", "000000_4", "000000_5",
                    "000000_6", "000000_7", "000000_8", "000000_9", "000000_10",
                    "000000_11", "000000_12", "000000_13", "000000_14", "000000_15",
                    "000000_16", "000000_17", "000000_18", "000000_19", "000000_20",
                    "000000_21", "000000_22", "000000_23", "000000_24", "000000_25",
                    "000000_26", "000000_27", "000000_28", "000000_29", "000000_30",
                    "000000_31", "000000_32", "000000_33", "000000_34", "000000_35",
                    "000000_36", "000000_37", "000000_38", "000000_39", "000000_40",
                    "000000_41", "000000_42", "000000_43", "000000_44", "000000_45",
                    "000000_46", "000000_47", "000000_48", "000000_49", "000000_50",
                    "000000_51", "000000_52", "000000_53", "000000_54", "000000_55",
                    "000000_56", "000000_57", "000000_58_S", "000000_59", "000000_60"
                )
            )
        )

        val vector = VectorChildFinder(context, R.drawable.keyboard, imageView)

        val sets = hashMapOf<Int, HashMap<String, VectorDrawableCompat.VFullPath>>()

        colors.forEach { c ->
            colorMap[c]?.forEach { key ->
                val path = vector.findPathByName(key)
                if (path != null) {
                    if (!sets.containsKey(c)) sets[c] = hashMapOf()
                    sets[c]?.set(key, path)

                    if (key.endsWith("_S")) path.strokeColor = c else path.fillColor = c
                }
            }
        }
    }
}