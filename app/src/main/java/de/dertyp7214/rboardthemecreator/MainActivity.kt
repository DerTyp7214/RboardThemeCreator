package de.dertyp7214.rboardthemecreator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val generateTheme = findViewById<MaterialButton>(R.id.generate_theme)

        generateTheme.setOnClickListener {
            ThemeUtils.shareTheme(
                this,
                ThemeUtils.generateTheme(
                    this, ThemeUtils.getSystemAccent(this),
                    findViewById<SwitchMaterial>(R.id.dark).isChecked
                )
            )
        }
    }
}