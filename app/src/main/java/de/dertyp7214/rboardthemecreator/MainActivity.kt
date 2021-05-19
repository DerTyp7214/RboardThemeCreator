package de.dertyp7214.rboardthemecreator

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
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
    var currentColor = Color.RED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val generateTheme = findViewById<MaterialButton>(R.id.generate_theme)
        val shareTheme = findViewById<MaterialButton>(R.id.share_theme)
        colorPicker = findViewById(R.id.colorPicker)
        switch = findViewById(R.id.dark)
        monet = findViewById(R.id.monet)

        monet.visibility =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) View.VISIBLE else View.GONE
        monet.isChecked = true

        currentColor = ThemeUtils.getSystemAccent(this)
        colorPicker.setColor(currentColor)

        generateTheme.setOnClickListener {
            ThemeUtils.shareTheme(
                this,
                ThemeUtils.generateTheme(
                    this, getColor(),
                    switch.isChecked,
                    monet.visibility == View.VISIBLE && monet.isChecked,
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
                    (findViewById<ImageView>(R.id.keyboard).drawable as VectorDrawableCompat).getBitmap()
                ),
                false
            )
        }

        refresh()

        switch.setOnCheckedChangeListener { _, _ -> refresh() }
        monet.setOnCheckedChangeListener { _, _ -> refresh() }

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
            findViewById(R.id.keyboard)
        )
    }

    private fun getColor(): Int {
        return currentColor
    }
}