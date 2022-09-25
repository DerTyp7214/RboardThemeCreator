package de.dertyp7214.rboardthemecreator.screens

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import androidx.core.view.get
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
import de.dertyp7214.rboardthemecreator.utils.AppStartUp
import de.dertyp7214.rboardthemecreator.utils.ThemeUtils

class MainActivity : AppCompatActivity() {
    private val colorPicker by lazy { findViewById<ColorPicker>(R.id.colorPicker) }
    private val checkCardGroup by lazy { findViewById<CheckCardGroup>(R.id.checkCardGroup) }

    private val viewPager by lazy { findViewById<ViewPager>(R.id.viewPager) }
    private val tabLayout by lazy { findViewById<LinearLayout>(R.id.tabLayout) }

    private val colorRecyclerView by lazy { findViewById<RecyclerView>(R.id.recyclerViewColors) }

    private val colorSetAdapter by lazy {
        HexColorAdapter(this, colorSets) { index, color ->
            colorSets[index] = color
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

    private val colorSets by lazy {
        ArrayList(
            ThemeUtils.buildColorSets(
                this, getColor(),
                dark = false, monet = false,
                tertiary = false, amoled = false
            )
        )
    }

    var currentColor = Color.MAGENTA
    var usingHex = false

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppStartUp(this).apply {
            setUp()
            onCreate {
                currentColor = ThemeUtils.getSystemAccent(this)

                colorRecyclerView.adapter = colorSetAdapter
                colorRecyclerView.setHasFixedSize(true)

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
                        val primaryContainer = getAttr(R.attr.colorPrimaryContainer)
                        val onPrimaryContainer = getAttr(R.attr.colorOnPrimaryContainer)
                        val surfaceVariant = getAttr(R.attr.colorSurfaceVariant)
                        val onSurfaceVariant = getAttr(R.attr.colorOnSurfaceVariant)
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
            ThemeUtils.shareTheme(
                this,
                ThemeUtils.generateTheme(
                    this, getColorSets(), name, author.ifEmpty { null },
                    findViewById<ImageView>(R.id.keyboard).drawable.toBitmap()
                ),
                install
            )
            dialog.dismiss()
        }
    }

    private fun refresh(index: Int = -1) {
        if (index >= 0) colorSetAdapter.notifyItemChanged(index)
        ThemeUtils.parseImage(
            this,
            getColorSets(),
            findViewById(R.id.keyboard)
        )
    }

    private fun getColorSets(): List<Int> {
        if (usingHex) return colorSets

        val dBool = darkMode.isChecked
        val mBool = monet.visibility == View.VISIBLE && monet.isChecked
        val tBool = mBool && tertiary.visibility == View.VISIBLE && tertiary.isChecked
        val aBool = amoled.visibility == View.VISIBLE && dBool && amoled.isChecked

        return ThemeUtils.buildColorSets(
            this, getColor(),
            dBool, mBool, tBool, aBool
        )
    }

    private fun getColor(): Int {
        return currentColor
    }
}