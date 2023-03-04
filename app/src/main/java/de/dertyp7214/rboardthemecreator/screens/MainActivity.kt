package de.dertyp7214.rboardthemecreator.screens

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.get
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.madrapps.pikolo.ColorPicker
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import de.dertyp7214.colorutilsc.ColorUtilsC
import de.dertyp7214.rboardcomponents.components.CheckCard
import de.dertyp7214.rboardcomponents.components.CheckCardGroup
import de.dertyp7214.rboardthemecreator.R
import de.dertyp7214.rboardthemecreator.components.HexColorAdapter
import de.dertyp7214.rboardthemecreator.core.getAttr
import de.dertyp7214.rboardthemecreator.core.openShareThemeDialog
import de.dertyp7214.rboardthemecreator.data.RepoManifest
import de.dertyp7214.rboardthemecreator.data.ThemeColors
import de.dertyp7214.rboardthemecreator.utils.AppStartUp
import de.dertyp7214.rboardthemecreator.utils.RepoHelper
import de.dertyp7214.rboardthemecreator.utils.ThemeUtils
import de.dertyp7214.rboardthemecreator.utils.doInBackground

@SuppressLint("NotifyDataSetChanged")
class MainActivity : AppCompatActivity() {
    companion object {
        val webViews: MutableMap<String, WebView> = mutableMapOf()
    }
    private val colorPicker by lazy { findViewById<ColorPicker>(R.id.colorPicker) }
    private val checkCardGroup by lazy { findViewById<CheckCardGroup>(R.id.checkCardGroup) }

    private val viewPager by lazy { findViewById<ViewPager>(R.id.viewPager) }
    private val viewPager2 by lazy { findViewById<ViewPager>(R.id.viewPager2) }
    private val tabLayout by lazy { findViewById<LinearLayout>(R.id.tabLayout) }

    private val templateList = mutableListOf<String>()

    private val colorRecyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerViewColors) }

    private val themeColorMap by lazy {
        mutableMapOf<String, Int>().apply {
            refreshThemeColorMap(
                this
            )
        }
    }

    private val colorSetAdapter by lazy {
        HexColorAdapter(this, themeColorMap) { index, color ->
            when (index) {
                0 -> themeColors.mainBackground = color
                1 -> themeColors.keyBackground = color
                2 -> themeColors.keyColor = color
                3 -> themeColors.secondaryKeyBackground = color
                4 -> themeColors.accentBackground = color
            }
            refreshThemeColorMap()
            refresh(index)
        }
    }

    private val darkMode by lazy {
        CheckCard(this).apply {
            name = "dark"
            text = R.string.dark
            icon = R.drawable.ic_dark_mode
        }
    }
    private val monet by lazy {
        CheckCard(this).apply {
            name = "monet"
            text = R.string.monet
            icon = R.drawable.ic_monet
        }
    }
    private val tertiary by lazy {
        CheckCard(this).apply {
            name = "tertiary"
            text = R.string.tertiary
            icon = R.drawable.ic_tertiary
        }
    }
    private val amoled by lazy {
        CheckCard(this).apply {
            name = "amoled"
            text = R.string.amoled
            icon = R.drawable.ic_amoled
        }
    }

    private var template = "default"

    private val themeColors by lazy {
        ThemeUtils.buildColorSets(
            this, getColor(),
            dark = false, monet = false,
            tertiary = false, amoled = false,
            template = template
        )
    }

    private val repoHelper by lazy { RepoHelper.init(this) }

    private val repoManifestLiveData = MutableLiveData<RepoManifest>()

    var currentColor = Color.MAGENTA
    var usingHex = false

    @SuppressLint("ResourceType", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doInBackground {
            repoHelper.cacheAndGetManifest()?.let { manifest ->
                repoManifestLiveData.postValue(manifest)
                templateList.clear()
                templateList.addAll(manifest.templates.map { it.value.name })
                runOnUiThread {
                    viewPager2.adapter?.notifyDataSetChanged()
                    ThemeUtils.parsePreview(getColorSets())
                }
            }
        }

        AppStartUp(this).apply {
            setUp()
            onCreate {
                currentColor = ThemeUtils.getSystemAccent(this)

                colorRecyclerView.adapter = colorSetAdapter
                colorRecyclerView.setHasFixedSize(true)

                viewPager2.adapter = object : PagerAdapter() {
                    override fun instantiateItem(container: ViewGroup, position: Int): Any {
                        val pageTemplate = templateList[position]
                        if (webViews.containsKey(pageTemplate)) {
                            return webViews[pageTemplate]!!
                        } else {
                            val webView = WebView(this@MainActivity).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                container.addView(this)
                            }
                            webViews[pageTemplate] = webView
                            val customColors = ThemeColors(
                                themeColors.mainBackground,
                                themeColors.keyBackground,
                                themeColors.keyColor,
                                themeColors.secondaryKeyBackground,
                                themeColors.accentBackground,
                                pageTemplate
                            )
                            ThemeUtils.parsePreview(customColors)
                            return webView
                        }
                    }

                    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
                        container.removeView(obj as View)
                    }

                    override fun getCount() = templateList.size
                    override fun isViewFromObject(view: View, any: Any) = view == any
                }
                viewPager2.currentItem = 0

                viewPager2.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {

                    }

                    override fun onPageScrollStateChanged(state: Int) {}
                    override fun onPageSelected(position: Int) {
                        template = templateList[position]
                    }
                })

                viewPager.adapter = object : PagerAdapter() {
                    override fun instantiateItem(container: ViewGroup, position: Int) =
                        viewPager[position]

                    override fun getCount() = viewPager.childCount
                    override fun isViewFromObject(view: View, any: Any) = view == any
                }
                viewPager.currentItem = 0

                viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        val primaryContainer =
                            getAttr(com.google.android.material.R.attr.colorPrimaryContainer)
                        val onPrimaryContainer =
                            getAttr(com.google.android.material.R.attr.colorOnPrimaryContainer)
                        val surfaceVariant =
                            getAttr(com.google.android.material.R.attr.colorSurfaceVariant)
                        val onSurfaceVariant =
                            getAttr(com.google.android.material.R.attr.colorOnSurfaceVariant)

                        fun MaterialCardView.blend(
                            color1a: Int, color1b: Int,
                            color2a: Int, color2b: Int,
                            factor: Float
                        ) {
                            setCardBackgroundColor(
                                ColorUtilsC.blendARGB(color1a, color1b, factor)
                            )
                            get(0).let { text ->
                                if (text is TextView) text.setTextColor(
                                    ColorUtilsC.blendARGB(color2a, color2b, factor)
                                )
                            }
                        }
                        tabLayout[position].let { card ->
                            if (card is MaterialCardView) card.blend(
                                primaryContainer, surfaceVariant,
                                onPrimaryContainer, onSurfaceVariant,
                                positionOffset
                            )
                        }
                        if (tabLayout.childCount > position + 1) tabLayout[position + 1].let { card ->
                            if (card is MaterialCardView) card.blend(
                                surfaceVariant, primaryContainer,
                                onSurfaceVariant, onPrimaryContainer,
                                positionOffset
                            )
                        }
                    }

                    override fun onPageScrollStateChanged(state: Int) {}
                    override fun onPageSelected(position: Int) {
                        usingHex = position == 1
                        refreshThemeColorMap()
                        refresh()
                    }
                })

                tabLayout.children.forEachIndexed { index, tab ->
                    if (tab is MaterialCardView) {
                        tab.setOnClickListener {
                            viewPager.setCurrentItem(index, true)
                        }
                    }
                }

                checkCardGroup.addCard(darkMode)
                checkCardGroup.addCard(monet)
                checkCardGroup.addCard(tertiary)
                checkCardGroup.addCard(amoled)

                val generateTheme = findViewById<Button>(R.id.generateTheme)
                val shareTheme = findViewById<MaterialButton>(R.id.shareButton)

                monet.isChecked = true
                amoled.isEnabled = false

                tertiary.visibility =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) View.VISIBLE else View.GONE
                monet.visibility =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) View.VISIBLE else View.GONE
                amoled.visibility = View.GONE
                monet.setOnClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && it) {
                        tertiary.visibility = View.VISIBLE
                        tertiary.isEnabled = true
                    } else {
                        tertiary.visibility = View.GONE
                        tertiary.isEnabled = false
                        tertiary.isChecked = false
                    }
                    refresh()
                }
                darkMode.setOnClickListener {
                    if (it) {
                        amoled.visibility = View.VISIBLE
                        amoled.isEnabled = true
                    } else {
                        amoled.visibility = View.GONE
                        amoled.isEnabled = false
                        amoled.isChecked = false
                    }
                    refresh()
                }
                colorPicker.setColor(currentColor)

                generateTheme.setOnClickListener { shareTheme(true) }
                shareTheme.setOnClickListener { shareTheme(false) }

                refresh()

                tertiary.setOnClickListener { refresh() }
                amoled.setOnClickListener { refresh() }

                colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
                    override fun onColorSelected(color: Int) {
                        currentColor = color
                        refresh()
                    }
                })
            }
        }
    }

    private fun shareTheme(install: Boolean) {
        openShareThemeDialog { dialog, name, author ->
            val colors = getColorSets()
            ThemeUtils.getPreviewImage(this, colors) { bitmap ->
                ThemeUtils.shareTheme(
                    this,
                    ThemeUtils.generateTheme(
                        this, colors, name, author.ifEmpty { null },
                        bitmap
                    ),
                    install
                )
                dialog.dismiss()
            }
        }
    }

    private fun refresh(index: Int = -1) {
        if (index >= 0) colorSetAdapter.notifyItemChanged(index)
        else colorSetAdapter.notifyDataSetChanged()
        ThemeUtils.updateColors(getColorSets())
    }

    private fun getColorSets(): ThemeColors {
        if (usingHex) return themeColors.apply { this.template = this@MainActivity.template }

        val dBool = darkMode.isChecked
        val mBool = monet.visibility == View.VISIBLE && monet.isChecked
        val tBool = mBool && tertiary.visibility == View.VISIBLE && tertiary.isChecked
        val aBool = amoled.visibility == View.VISIBLE && dBool && amoled.isChecked

        return ThemeUtils.buildColorSets(
            this, getColor(),
            dBool, mBool, tBool, aBool,
            template
        )
    }

    private fun getColor(): Int {
        return currentColor
    }

    private fun refreshThemeColorMap(map: MutableMap<String, Int> = themeColorMap): MutableMap<String, Int> {
        map.clear()
        map.putAll(
            listOf(
                "Main Background" to themeColors.mainBackground,
                "Key Background" to themeColors.keyBackground,
                "Key Color" to themeColors.keyColor,
                "Secondary Key Background" to themeColors.secondaryKeyBackground,
                "Accent Background" to themeColors.accentBackground
            )
        )
        return map
    }
}