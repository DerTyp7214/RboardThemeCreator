package de.dertyp7214.rboardthemecreator

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
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
    @SuppressLint("NewApi", "ResourceType")
    fun generateTheme(
        context: Context,
        @ColorInt color: Int,
        dark: Boolean = false,
        monet: Boolean = true,
        tertiary: Boolean = false,
        hidesecondarylabel: Boolean = false,
        amoled: Boolean = false,
        image: Bitmap? = null
    ): File {
        val workingDir = File(context.filesDir, "theme")
        if (!workingDir.exists()) workingDir.mkdirs()
        workingDir.listFiles()?.forEach { it.delete() }

        val defs = StringBuilder()

        val newOs = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

        val colorSetA1 =
            when {
                amoled && dark -> context.getColor(R.color.theme_amoled_background)
                monet && newOs && dark -> context.getColor(R.color.neutral_900)
                monet && newOs -> context.getColor(R.color.accent2_50)
                dark -> changeHSL(
                    color,
                    -1,
                    -40,
                    -40
                )
                else -> changeHSL(color, -1, 40, 40)
            }
        val colorSetA2 =
            when {
                amoled && dark -> context.getColor(R.color.theme_amoled_key_background)
                monet && newOs && dark -> context.getColor(R.color.neutral_700)
                monet && newOs -> context.getColor(R.color.neutral_0)
                dark -> changeHSL(
                    colorSetA1,
                    -1,
                    0,
                    4
                )
                else -> changeHSL(colorSetA1, -1, 0, -4)
            }
        val colorSetA3 =
            if (dark) changeHSL(colorSetA2, -1, 0, 5) else changeHSL(colorSetA2, -1, 0, -5)
        val colorSetA4 =
            if (ColorUtils.calculateLuminance(colorSetA1) < .5) Color.WHITE else Color.BLACK
        val colorSetA5 =
            when {
                monet && newOs && tertiary -> context.getColor(R.color.accent_300)
                monet && newOs -> context.getColor(R.color.accent_200)
                else -> changeHSL(color, 0, 0, -10)
            }
        val colorSetA6 =
            if (dark) changeHSL(colorSetA5, -1, -5, -10) else changeHSL(colorSetA5, -1, -5, -10)
        val colorSetA7 =
            when {
                amoled && dark -> context.getColor(R.color.theme_amoled_dark_key_background)
                monet && newOs && dark -> context.getColor(R.color.neutral_800)
                monet && newOs -> context.getColor(R.color.neutral_100)
                dark -> changeHSL(
                    colorSetA2,
                    -1,
                    0,
                    4
                )
                else -> changeHSL(colorSetA2, -1, 0, -4)
            }
        val colorSetA8 =
            if (dark) changeHSL(colorSetA7, -1, 0, 5) else changeHSL(colorSetA7, -1, 0, -5)
        val colorSetA9 =
            if (hidesecondarylabel) Color.TRANSPARENT else colorSetA4
        defs.append("@def color_set_a1 ${colorSetA1.toHex()}FF;\n")
        defs.append("@def color_set_a2 ${colorSetA2.toHex()}FF;\n")
        defs.append("@def color_set_a3 ${colorSetA3.toHex()}FF;\n")
        defs.append("@def color_set_a4 ${colorSetA4.toHex()}FF;\n")
        defs.append("@def color_set_a5 ${colorSetA5.toHex()}FF;\n")
        defs.append("@def color_set_a6 ${colorSetA6.toHex()}FF;\n")
        defs.append("@def color_set_a7 ${colorSetA7.toHex()}FF;\n")
        defs.append("@def color_set_a8 ${colorSetA8.toHex()}FF;\n")
        if (hidesecondarylabel) {
            defs.append("@def color_set_a9 ${colorSetA9.toHex()}00;\n")
        } else {
            defs.append("@def color_set_a9 ${colorSetA4.toHex()}FF;\n")
        }

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
            }_${if (dark) "dark" else "light"}${if (tertiary) "_tertiary" else ""}.zip"
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

    @SuppressLint("NewApi", "ResourceType")
    fun parseImage(
        context: Context,
        color: Int,
        dark: Boolean,
        monet: Boolean,
        tertiary: Boolean,
        hidesecondarylabel: Boolean,
        amoled: Boolean,
        imageView: ImageView
    ) {

        val newOs = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

        val colorSetA1 =
            when {
                amoled && dark -> context.getColor(R.color.theme_amoled_background)
                monet && newOs && dark -> context.getColor(R.color.neutral_900)
                monet && newOs -> context.getColor(R.color.accent2_50)
                dark -> changeHSL(
                    color,
                    -1,
                    -40,
                    -40
                )
                else -> changeHSL(color, -1, 40, 40)
            }
        val colorSetA2 =
            when {
                amoled && dark -> context.getColor(R.color.theme_amoled_key_background)
                monet && newOs && dark -> context.getColor(R.color.neutral_700)
                monet && newOs -> context.getColor(R.color.neutral_0)
                dark -> changeHSL(
                    colorSetA1,
                    -1,
                    0,
                    4
                )
                else -> changeHSL(colorSetA1, -1, 0, -4)
            }
        val colorSetA3 =
            if (dark) changeHSL(colorSetA2, -1, 0, 5) else changeHSL(colorSetA2, -1, 0, -5)
        val colorSetA4 =
            if (ColorUtils.calculateLuminance(colorSetA1) < .5) Color.WHITE else Color.BLACK
        val colorSetA5 =
            when {
                monet && newOs && tertiary -> context.getColor(R.color.accent_300)
                monet && newOs -> context.getColor(R.color.accent_200)
                else -> changeHSL(color, 0, 0, -10)
            }
        val colorSetA6 =
            if (dark) changeHSL(colorSetA5, -1, -5, -10) else changeHSL(colorSetA5, -1, -5, -10)
        val colorSetA7 =
            when {
                amoled && dark -> context.getColor(R.color.theme_amoled_dark_key_background)
                monet && newOs && dark -> context.getColor(R.color.neutral_800)
                monet && newOs -> context.getColor(R.color.neutral_100)
                dark -> changeHSL(
                    colorSetA2,
                    -1,
                    0,
                    4
                )
                else -> changeHSL(colorSetA2, -1, 0, -4)
            }
        val colorSetA8 =
            if (dark) changeHSL(colorSetA7, -1, 0, 5) else changeHSL(colorSetA7, -1, 0, -5)
        val colorSetA9 =
            if (hidesecondarylabel) Color.TRANSPARENT else colorSetA4

        val colors = listOf(
            colorSetA1,
            colorSetA2,
            colorSetA3,
            colorSetA4,
            colorSetA5,
            colorSetA6,
            colorSetA7,
            colorSetA8,
            colorSetA9
        )

        val colorMap = listOf(
            Pair(
                colors[1], listOf(
                    "FFD0DB_2", "FFD0DB_3", "FFD0DB_4", "FFD0DB_5",
                    "FFD0DB_6", "FFD0DB_7", "FFD0DB_8", "FFD0DB_9", "FFD0DB_10",
                    "FFD0DB_11", "FFD0DB_12", "FFD0DB_13", "FFD0DB_14", "FFD0DB_15",
                    "FFD0DB_16", "FFD0DB_17", "FFD0DB_18", "FFD0DB_19", "FFD0DB_20",
                    "FFD0DB_21", "FFD0DB_22", "FFD0DB_23", "FFD0DB_24", "FFD0DB_25",
                    "FFD0DB_26", "FFD0DB_27", "FFD0DB_28", "FFD0DB_29", "FFD0DB_30",
                    "FFD0DB_31", "FFD0DB_32", "FFD0DB_33", "FFD0DB_34"
                )
            ),
            Pair(colors[0], listOf("FFE8ED_3", "FFE8ED_1", "FFE8ED_2")),
            Pair(colors[4], listOf("EE5479_1", "EE5479_2", "EE5479_4")),
            Pair(
                colors[6],
                listOf("FFBCCC_24", "FFBCCC_30", "FFBCCC_31", "FFBCCC_32", "FFBCCC_33", "FFBCCC_1")
            ),
            Pair(colors[8], listOf("000000_3")),
            Pair(
                if (ColorUtils.calculateLuminance(colors[4]) < .3) Color.WHITE else Color.BLACK,
                listOf("000000_8")
            ),
            Pair(
                colors[3], listOf(
                    "000000_1", "000000_2", "000000_4",
                    "000000_5", "000000_6_S", "000000_7"
                )
            )
        )

        val vector = VectorChildFinder(context, R.drawable.keyboard, imageView)

        val sets = hashMapOf<Int, HashMap<String, VectorDrawableCompat.VFullPath>>()

        colorMap.forEach { pair ->
            val c = pair.first
            pair.second.forEach { key ->
                val path = vector.findPathByName(key)
                if (path != null) {
                    if (!sets.containsKey(c)) sets[c] = hashMapOf()
                    sets[c]?.set(key, path)

                    if (key.endsWith("_S")) path.strokeColor = c else path.fillColor = c
                }
            }
        }

        imageView.invalidate()
    }
}