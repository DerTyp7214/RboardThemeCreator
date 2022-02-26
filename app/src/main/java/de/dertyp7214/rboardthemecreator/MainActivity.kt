package de.dertyp7214.rboardthemecreator

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.devs.vectorchildfinder.VectorDrawableCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.madrapps.pikolo.ColorPicker
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import de.dertyp7214.rboardthemecreator.core.getBitmap

class MainActivity : AppCompatActivity() {

    private lateinit var colorPicker: ColorPicker
    private lateinit var switch: SwitchMaterial
    private lateinit var monet: SwitchMaterial
    private lateinit var tertiary: SwitchMaterial
    private lateinit var hidesecondarylabel: SwitchMaterial
    private lateinit var amoled: SwitchMaterial
    var currentColor = Color.RED

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val generateTheme = findViewById<Button>(R.id.generate_theme)
        val shareTheme = findViewById<MaterialButton>(R.id.share_theme)
        colorPicker = findViewById(R.id.colorPicker)
        switch = findViewById(R.id.dark)
        monet = findViewById(R.id.monet)
        tertiary = findViewById(R.id.tertiary)
        hidesecondarylabel = findViewById(R.id.hide_secondary_label)
        amoled = findViewById(R.id.amoled)
        monet.visibility =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) View.VISIBLE else View.GONE
        monet.isChecked = true
        monet.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && monet.isChecked) {
                tertiary.visibility = View.VISIBLE
                tertiary.isEnabled = true
            } else {
                tertiary.isEnabled = false
                tertiary.isChecked = false
            }
        }
        amoled.isEnabled = false
        switch.setOnClickListener {
            if (switch.isChecked) {
                amoled.visibility = View.VISIBLE
                amoled.isEnabled = true
            } else {
                amoled.isEnabled = false
                amoled.isChecked = false
            }
        }
        currentColor = ThemeUtils.getSystemAccent(this)
        colorPicker.setColor(currentColor)

        generateTheme.setOnClickListener {
            ThemeUtils.shareTheme(
                this,
                ThemeUtils.generateTheme(
                    this, getColor(),
                    switch.isChecked,
                    monet.visibility == View.VISIBLE && monet.isChecked,
                    tertiary.visibility == View.VISIBLE && monet.isChecked && tertiary.isChecked,
                    hidesecondarylabel.isChecked,
                    amoled.isChecked,
                    (findViewById<ImageView>(R.id.keyboard).drawable as VectorDrawableCompat).getBitmap()
                )
            )
        }

        shareTheme.setOnClickListener {
            ThemeUtils.shareTheme(
                this,
                ThemeUtils.generateTheme(
                    this, getColor(),
                    switch.isChecked,
                    monet.visibility == View.VISIBLE && monet.isChecked,
                    tertiary.visibility == View.VISIBLE && monet.isChecked && tertiary.isChecked,
                    hidesecondarylabel.isChecked,
                    amoled.visibility == View.VISIBLE && switch.isChecked,
                    (findViewById<ImageView>(R.id.keyboard).drawable as VectorDrawableCompat).getBitmap()
                ),
                false
            )
        }

        refresh()

        switch.setOnCheckedChangeListener { _, _ -> refresh() }
        monet.setOnCheckedChangeListener { _, _ -> refresh() }
        tertiary.setOnCheckedChangeListener { _, _ -> refresh() }
        hidesecondarylabel.setOnCheckedChangeListener { _, _ -> refresh() }
        amoled.setOnCheckedChangeListener { _, _ -> refresh() }

        colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                currentColor = color
                refresh()
            }
        })
    }

    private fun refresh() {
        ThemeUtils.parseImage(
            this,
            getColor(),
            switch.isChecked,
            monet.visibility == View.VISIBLE && monet.isChecked,
            tertiary.visibility == View.VISIBLE && monet.isChecked && tertiary.isChecked,
            hidesecondarylabel.isChecked,
            amoled.isChecked,
            findViewById(R.id.keyboard)
        )
    }

    private fun getColor(): Int {
        return currentColor
    }
}