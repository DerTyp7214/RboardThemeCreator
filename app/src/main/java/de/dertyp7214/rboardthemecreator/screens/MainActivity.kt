package de.dertyp7214.rboardthemecreator.screens

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.button.MaterialButton
import com.madrapps.pikolo.ColorPicker
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import de.dertyp7214.rboardthemecreator.R
import de.dertyp7214.rboardthemecreator.components.CheckCard
import de.dertyp7214.rboardthemecreator.components.CheckCardGroup
import de.dertyp7214.rboardthemecreator.core.openShareThemeDialog
import de.dertyp7214.rboardthemecreator.utils.AppStartUp
import de.dertyp7214.rboardthemecreator.utils.ThemeUtils

class MainActivity : AppCompatActivity() {
    private val colorPicker by lazy { findViewById<ColorPicker>(R.id.colorPicker) }
    private val checkCardGroup by lazy { findViewById<CheckCardGroup>(R.id.checkCardGroup) }

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

    var currentColor = Color.RED

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppStartUp(this).apply {
            setUp()
            onCreate {
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
                currentColor = ThemeUtils.getSystemAccent(this)
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
        val dBool = darkMode.isChecked
        val mBool = monet.visibility == View.VISIBLE && monet.isChecked
        val tBool = mBool && tertiary.visibility == View.VISIBLE && tertiary.isChecked
        val aBool = amoled.visibility == View.VISIBLE && dBool && amoled.isChecked
        openShareThemeDialog { dialog, name, author ->
            ThemeUtils.shareTheme(
                this,
                ThemeUtils.generateTheme(
                    this, getColor(),
                    dBool, mBool, tBool, name, author.ifEmpty { null }, aBool,
                    findViewById<ImageView>(R.id.keyboard).drawable.toBitmap()
                ),
                install
            )
            dialog.dismiss()
        }
    }

    private fun refresh() {
        ThemeUtils.parseImage(
            this,
            getColor(),
            darkMode.isChecked,
            monet.visibility == View.VISIBLE && monet.isChecked,
            tertiary.visibility == View.VISIBLE && monet.isChecked && tertiary.isChecked,
            amoled.visibility == View.VISIBLE && amoled.isChecked,
            findViewById(R.id.keyboard)
        )
    }

    private fun getColor(): Int {
        return currentColor
    }
}