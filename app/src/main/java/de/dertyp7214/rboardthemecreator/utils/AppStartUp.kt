package de.dertyp7214.rboardthemecreator.utils

import android.animation.ObjectAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import de.dertyp7214.rboardthemecreator.BuildConfig
import de.dertyp7214.rboardthemecreator.Config
import de.dertyp7214.rboardthemecreator.R
import de.dertyp7214.rboardthemecreator.core.*
import de.dertyp7214.rboardthemecreator.data.OutputMetadata
import java.net.URL


class AppStartUp(private val activity: AppCompatActivity) {
    private val checkUpdateUrl by lazy {
        "https://github.com/DerTyp7214/RboardThemeCreator/releases/download/latest-debug/output-metadata.json"
    }
    private val gboardPlayStoreUrl by lazy {
        "https://play.google.com/store/apps/details?id=${Config.GBOARD_PACKAGE_NAME}"
    }
    val RBOARD_URL by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            "https://github.com/DerTyp7214/RboardThemeManagerV3/releases/download/latest-release/app-release.apk"
        } else {
            "https://github.com/DerTyp7214/RboardThemeManagerV3/releases/download/latest-rCompatible/app-release.apk"
        }
    }
    private var checkedForUpdate = false
    private var gboardInstalled = false
    private var rboardInstalled = false
    private var isReady = false

    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(activity) }

    fun setUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            activity.splashScreen.setOnExitAnimationListener { splashScreenView ->
                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.TRANSLATION_Y,
                    0f,
                    -splashScreenView.height.toFloat()
                )
                slideUp.interpolator = AnticipateInterpolator()
                slideUp.duration = 200L

                slideUp.doOnEnd { splashScreenView.remove() }

                slideUp.start()
            }
        }
    }

    fun onCreate(onCreate: AppCompatActivity.(Intent) -> Unit) {
        val block: AppCompatActivity.(Intent) -> Unit = {
            isReady = true
            onCreate(this, it)
        }
        activity.apply {
            content.viewTreeObserver.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (isReady) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else false
                }
            })


            val initialized = preferences.getBoolean("initialized", false)

            val scheme = intent.scheme
            val data = intent.data


            when {
                initialized && data != null -> {
                    isReady = true
                }
                else -> {
                    gboardInstalled =
                        PackageUtils.isPackageInstalled(Config.GBOARD_PACKAGE_NAME, packageManager)
                    rboardInstalled =
                        PackageUtils.isPackageInstalled(
                            Config.RBOARD_THEME_PACKAGE_NAME,
                            packageManager
                        )

                    isReady = !gboardInstalled || !rboardInstalled
                    when {
                        !gboardInstalled -> openDialog(
                            R.string.install_gboard,
                            R.string.gboard_not_installed
                        ) {
                            openUrl(gboardPlayStoreUrl)
                        }
                        !rboardInstalled -> openDialog(
                            R.string.install_rboard,
                            R.string.rboard_not_installed,
                            false,
                            null
                        ) {
                            openUrl(RBOARD_URL)
                        }
                        else -> checkForUpdate { update ->
                            checkedForUpdate = true
                            isReady = true
                            validApp(this) {
                                preferences.edit { putBoolean("initialized", true) }

                                if (it) block(this, Intent().putExtra("update", update))
                                else finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkForUpdate(callback: (update: Boolean) -> Unit) {
        if (preferences.getLong(
                "lastCheck",
                0
            ) + 5 * 60 * 100 > System.currentTimeMillis()
        ) callback(false)
        else doAsync(URL(checkUpdateUrl)::getTextFromUrl) { text ->
            try {
                val outputMetadata = Gson().fromJson(text, OutputMetadata::class.java)
                val versionCode = outputMetadata.elements.first().versionCode
                preferences.edit { putLong("lastCheck", System.currentTimeMillis()) }
                callback(versionCode > BuildConfig.VERSION_CODE)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }

    private fun validApp(activity: AppCompatActivity, callback: (valid: Boolean) -> Unit) {
        preferences.apply {
            var valid = getBoolean("verified", false)
            if (valid) callback(valid)
            else activity.openDialog(R.string.unreleased, R.string.notice, false, {
                it.dismiss()
                callback(valid)
            }) {
                it.dismiss()
                valid = true
                callback(valid)
                edit { putBoolean("verified", true) }
            }
        }
    }
}