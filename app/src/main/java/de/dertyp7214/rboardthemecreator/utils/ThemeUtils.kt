@file:Suppress("SameParameterValue")

package de.dertyp7214.rboardthemecreator.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.TypedValue
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.webkit.WebViewAssetLoader
import de.dertyp7214.colorutilsc.ColorUtilsC
import de.dertyp7214.rboardthemecreator.R
import de.dertyp7214.rboardthemecreator.Zip
import de.dertyp7214.rboardthemecreator.core.capitalize
import de.dertyp7214.rboardthemecreator.core.toHex
import de.dertyp7214.rboardthemecreator.data.ThemeColors
import de.dertyp7214.rboardthemecreator.data.ThemeMetadata
import de.dertyp7214.rboardthemecreator.screens.MainActivity
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min
import kotlin.math.roundToInt

object ThemeUtils {

    private val previewListeners = mutableListOf<(String) -> Unit>()
    var getPreviewImage: ((LifecycleOwner, ThemeColors, (Bitmap?) -> Unit) -> Unit) =
        { _, _, c -> c(null) }
        private set
    var updateColors: ((ThemeColors) -> Unit) = {}
        private set

    private var currentTemplate = ""
    private val currentPreview = HashMap<String, MutableLiveData<String>>()

    @SuppressLint("NewApi", "ResourceType")
    fun generateTheme(
        context: Context,
        colors: ThemeColors,
        themeName: String,
        author: String? = null,
        image: Bitmap? = null
    ): File {
        val workingDir = File(context.filesDir, "theme")
        if (!workingDir.exists()) workingDir.mkdirs()
        workingDir.listFiles()?.forEach { it.delete() }

        val repoHelper = RepoHelper.instance ?: RepoHelper.init(context)

        val template = repoHelper.getTemplate(colors.template) ?: repoHelper.getTemplate("default")
        ?: throw Exception("Template not found")

        val defs = StringBuilder()

        defs.appendLine("@def web_color_bg ${colors.mainBackground.toHex()};")
        defs.appendLine("@def web_color_label ${colors.keyColor.toHex()};")
        defs.appendLine("@def web_color_accent ${colors.accentBackground.toHex()};")
        defs.appendLine(
            "@def web_color_accent_pressed ${
                shadeColor(
                    colors.accentBackground,
                    .05f
                ).toHex()
            };"
        )
        defs.appendLine("@def web_color_key_bg ${colors.keyBackground.toHex()};")
        defs.appendLine(
            "@def web_color_key_bg_pressed ${colors.secondaryKeyBackground.toHex()};"
        )
        defs.appendLine("@def web_color_secondary_key_bg ${colors.secondaryKeyBackground.toHex()};")
        defs.appendLine(
            "@def web_color_secondary_key_bg_pressed ${
                shadeColor(
                    colors.secondaryKeyBackground,
                    .05f
                ).toHex()
            };"
        )

        val parsedThemeName =
            themeName.ifEmpty {
                "Color Theme (${colors.accentBackground.toHex()}) ${
                    if (ColorUtils.calculateLuminance(
                            colors.mainBackground
                        ) > .5
                    ) "light" else "dark"
                }"
            }

        File(workingDir, "style_sheet_variables.css").writeText(defs.toString())
        File(workingDir, "metadata.json").writeText(
            template.metadata.merge(
                ThemeMetadata(
                    name = parsedThemeName,
                    isLightTheme = ColorUtils.calculateLuminance(colors.mainBackground) > .5
                )
            ).toString()
        )
        File(workingDir, "style_sheet_md2.css").writeText(template.styleSheetMd)
        File(workingDir, "style_sheet_md2_border.css").writeText(template.styleSheetMdBorder)

        template.imageBase64.forEach { (file, base64) ->
            File(workingDir, file).writeBytes(
                Base64.decode(
                    base64,
                    Base64.DEFAULT
                )
            )
        }

        val zip = File(
            workingDir,
            "${
                parsedThemeName.replace(" ", "_").replace(Regex("[()#]"), "")
            }.zip"
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
                writeText("name=$parsedThemeName\nauthor=${author ?: "Gboard Theme Creator"}\n")
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
        val action = if (install) Intent.ACTION_VIEW else Intent.ACTION_SEND
        ShareCompat.IntentBuilder(activity)
            .setStream(uri)
            .setType("application/pack")
            .intent
            .setAction(action)
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

    private fun darkenColor(color: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = hsv[2] - hsv[2] * factor
        return Color.HSVToColor(hsv)
    }

    private fun lightenColor(color: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = hsv[2] + hsv[2] * factor
        return Color.HSVToColor(hsv)
    }

    private fun saturateColor(color: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[1] = hsv[1] * factor
        return Color.HSVToColor(hsv)
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

    fun buildColorSets(
        context: Context,
        color: Int,
        dark: Boolean,
        monet: Boolean,
        tertiary: Boolean,
        amoled: Boolean,
        template: String
    ): ThemeColors {
        val mainBackground =
            when {
                amoled && dark -> context.getColor(R.color.theme_amoled_background)
                monet && dark -> context.getColor(R.color.neutral_900)
                monet -> context.getColor(R.color.accent_50)
                else -> color
            }
        val keyBackground =
            when {
                amoled && dark -> context.getColor(R.color.theme_amoled_key_background)
                monet && dark -> context.getColor(R.color.neutral2_800)
                monet -> context.getColor(R.color.neutral_100)
                else -> {
                    if (dark) lightenColor(mainBackground, .2f) else darkenColor(
                        mainBackground,
                        .2f
                    )
                }
            }
        val secondKeyBackground =
            if (dark) lightenColor(keyBackground, .2f) else darkenColor(keyBackground, .2f)
        val keyColor =
            if (ColorUtils.calculateLuminance(mainBackground) < .5) Color.WHITE else Color.BLACK
        val accentBg =
            when {
                monet && tertiary -> context.getColor(R.color.accent_300)
                monet -> context.getColor(R.color.accent_100)
                else -> {
                    val hsl = floatArrayOf(0f, 0f, 0f)
                    ColorUtilsC.colorToHSL(
                        ColorUtilsC.blendARGB(
                            saturateColor(color, 1.1f),
                            if (dark) Color.WHITE else Color.BLACK, .5f
                        ), hsl
                    )
                    hsl[0] = if (hsl[0] > 180) hsl[0] - 240 else hsl[0] + 120
                    hsl[1] *= if (dark) .9f else 1.1f
                    ColorUtilsC.HSLToColor(hsl)
                }
            }

        return ThemeColors(
            mainBackground,
            keyBackground,
            keyColor,
            secondKeyBackground,
            accentBg,
            template,
        )
    }

    fun parsePreview(colors: ThemeColors) {
        currentTemplate = colors.template
        MainActivity.webViews.forEach { (name, webView) ->
            val templateColors = ThemeColors(
                colors.mainBackground,
                colors.keyBackground,
                colors.keyColor,
                colors.secondaryKeyBackground,
                colors.accentBackground,
                name
            )
            parsePreview(templateColors, webView)
        }
    }

    @SuppressLint("NewApi", "ResourceType", "SetJavaScriptEnabled")
    fun parsePreview(
        colors: ThemeColors,
        webView: WebView
    ) {
        val repoHelper = RepoHelper.instance ?: return

        val template = repoHelper.getTemplate(colors.template) ?: return

        val rootStyle = """
            html, body {
                margin: 0;
                padding: 0;
                width: 100vw;
                height: 100vh;
                display: flex;
                justify-content: center;
                align-items: center;
            }
            :root {
                --key-border-radius: 0.2em;
                --font-size: min(8vw, 8vh);
            }
            * {
                transition: all 0.05s ease-in-out;
            }
            .keyboard_body {
                border-radius: 8px;
            }
            .simple_key {
                width: 1.25em !important;
            }
            .custom_key {
                width: 2.11em !important;
            }
            .space {
                width: 6.56em !important;
                overflow: hidden !important;
            }
        """.trimIndent()

        val setColorVars: (ThemeColors) -> String = { themeColors ->
            """
            (function () {
                document.documentElement.style.setProperty('--main-bg', '${themeColors.mainBackground.toHex()}');
                document.documentElement.style.setProperty('--key-bg', '${themeColors.keyBackground.toHex()}');
                document.documentElement.style.setProperty('--key-color', '${themeColors.keyColor.toHex()}');
                document.documentElement.style.setProperty('--second-key-bg', '${themeColors.secondaryKeyBackground.toHex()}');
                document.documentElement.style.setProperty('--accent-bg', '${themeColors.accentBackground.toHex()}');
            })()
        """.trimIndent()
        }

        val script = """
            <script src="https://appassets.androidplatform.net/assets/js/html2canvas.js"></script>
        """.trimIndent()

        val getImage: (ThemeColors) -> String = { themeColors ->
            """
            html2canvas(document.querySelector(".keyboard_body"), {
                backgroundColor: "${themeColors.mainBackground.toHex()}",
            }).then(canvas => {
                Android.imageBase64(canvas.toDataURL('image/png').replace(/^data:image\/png;base64,/, ''))
            })
        """.trimIndent()
        }

        val webViewAssetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(webView.context))
            .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(webView.context))
            .build()

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.addJavascriptInterface(WebViewInterface(), "Android")
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.loadDataWithBaseURL(
            null,
            template.preview.replace(
                "<style>",
                "$script\n<style>\n$rootStyle"
            ).replace(
                "<span class=\"letter lspace\">Rboard</span>",
                "<span class=\"letter lspace\">${template.name.capitalize()}</span>"
            ),
            "text/html; charset=utf-8",
            "UTF-8", null
        )
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return webViewAssetLoader.shouldInterceptRequest(request.url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webView.loadUrl(
                    "javascript:${getImage(colors)}"
                )
                webView.loadUrl(
                    "javascript:${setColorVars(colors)}"
                )
            }
        }

        getPreviewImage = { lifecycleOwner, themeColors, listener ->
            webView.loadUrl(
                "javascript:${getImage(themeColors)}"
            )
            var called = false
            var observer: Observer<String>? = null
            if (!currentPreview.containsKey(themeColors.template))
                currentPreview[themeColors.template] = MutableLiveData<String>()
            observer = Observer<String> { base64 ->
                listener(base64ToBitmap(base64))
                called = true
                observer?.let { observer ->
                    currentPreview[themeColors.template]?.removeObserver(
                        observer
                    )
                }
            }
            currentPreview[themeColors.template]?.observe(lifecycleOwner, observer)

            currentTemplate = themeColors.template

            Handler(Looper.getMainLooper()).postDelayed({
                currentPreview[themeColors.template]?.removeObserver(observer)
                if (!called) listener(null)
            }, 1000)
        }

        updateColors = { themeColors ->
            MainActivity.webViews.forEach { (_, webView) ->
                webView.loadUrl(
                    "javascript:${setColorVars(themeColors)}"
                )
            }
            currentTemplate = themeColors.template
        }
    }

    @Suppress("SameParameterValue")
    private fun shadeColor(color: Int, factor: Float): Int {
        val a = Color.alpha(color)
        val r = (Color.red(color) * factor).roundToInt()
        val g = (Color.green(color) * factor).roundToInt()
        val b = (Color.blue(color) * factor).roundToInt()
        return Color.argb(a, min(r, 255), min(g, 255), min(b, 255))
    }

    private fun base64ToBitmap(base64: String): Bitmap {
        val imageBytes = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private class WebViewInterface {
        @JavascriptInterface
        fun imageBase64(image: String) {
            currentPreview[currentTemplate]?.postValue(image)
            previewListeners.forEach { it(image) }
        }
    }
}