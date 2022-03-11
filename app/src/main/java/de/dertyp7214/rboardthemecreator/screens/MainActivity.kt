package de.dertyp7214.rboardthemecreator.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.devs.vectorchildfinder.VectorDrawableCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.madrapps.pikolo.ColorPicker
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import de.dertyp7214.rboardthemecreator.R
import de.dertyp7214.rboardthemecreator.utils.ThemeUtils
import de.dertyp7214.rboardthemecreator.core.content
import de.dertyp7214.rboardthemecreator.core.getBitmap
import de.dertyp7214.rboardthemecreator.core.openDialog
import de.dertyp7214.rboardthemecreator.core.toHumanReadableBytes
import de.dertyp7214.rboardthemecreator.utils.AppStartUp
import de.dertyp7214.rboardthemecreator.utils.PackageUtils
import de.dertyp7214.rboardthemecreator.utils.UpdateHelper
import java.io.File

class MainActivity : AppCompatActivity() {
    private val updateUrl by lazy {
        "https://github.com/DerTyp7214/RboardThemeCreator/releases/download/latest-debug/app-debug.apk"
    }
    private lateinit var downloadResultLauncher: ActivityResultLauncher<Intent>
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
        downloadResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    content.setRenderEffect(null)
                }
            }
        AppStartUp(this).apply {
            setUp()
            onCreate { intent ->
                checkUpdate(intent)

                val generateTheme = findViewById<Button>(R.id.generate_theme)
                val shareTheme = findViewById<MaterialButton>(R.id.share_theme)
                colorPicker = findViewById(R.id.colorPicker)
                switch = findViewById(R.id.dark)
                monet = findViewById(R.id.monet)
                tertiary = findViewById(R.id.tertiary)
                tertiary.visibility =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) View.VISIBLE else View.GONE
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
        }
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


    private fun checkUpdate(intent: Intent) {


        if (intent.getBooleanExtra(
                "update",
                this@MainActivity.intent.getBooleanExtra("update", false)
            )
        ) {
            openDialog(R.string.update_ready, R.string.update) { update() }
        }
    }

    private fun update() {
        val maxProgress = 100
        val notificationId = 42069
        val builder =
            NotificationCompat.Builder(this, getString(R.string.download_notification_channel_id))
                .apply {
                    setContentTitle(getString(R.string.update))
                    setContentText(getString(R.string.download_update))
                    setSmallIcon(R.drawable.ic_baseline_get_app_24)
                    priority = NotificationCompat.PRIORITY_LOW
                }
        val manager = NotificationManagerCompat.from(this).apply {
            builder.setProgress(maxProgress, 0, false)
            notify(notificationId, builder.build())
        }
        var finished = false
        UpdateHelper(updateUrl, this).apply {
            addOnProgressListener { progress, bytes, total ->
                if (!finished) {
                    builder
                        .setContentText(
                            getString(
                                R.string.download_update_progress,
                                "${bytes.toHumanReadableBytes(this@MainActivity)}/${
                                    total.toHumanReadableBytes(this@MainActivity)
                                }"
                            )
                        )
                        .setProgress(maxProgress, progress.toInt(), false)
                    manager.notify(notificationId, builder.build())
                }
            }
            setFinishListener { path, _ ->
                finished = true
                manager.cancel(notificationId)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    content.setRenderEffect(
                        RenderEffect.createBlurEffect(
                            10F,
                            10F,
                            Shader.TileMode.REPEAT
                        )
                    )
                }
                PackageUtils.install(this@MainActivity, File(path), downloadResultLauncher) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        content.setRenderEffect(null)
                    }
                    Toast.makeText(this@MainActivity, R.string.error, Toast.LENGTH_LONG).show()
                }
            }
            setErrorListener {
                finished = true
                builder.setContentText(getString(R.string.download_error))
                    .setProgress(0, 0, false)
                manager.notify(notificationId, builder.build())
                it?.connectionException?.printStackTrace()
                Log.d("ERROR", it?.serverErrorMessage ?: "NOO")
            }
        }.start()
    }
}