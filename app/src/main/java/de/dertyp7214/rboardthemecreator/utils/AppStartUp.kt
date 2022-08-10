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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import de.dertyp7214.rboardthemecreator.R
import de.dertyp7214.rboardthemecreator.core.content


class AppStartUp(private val activity: AppCompatActivity) {
    private var isReady = false

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

            createNotificationChannels(this)

            isReady = true
            onCreate(this, Intent())
        }
    }

    private fun createNotificationChannels(activity: AppCompatActivity) {
        activity.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val nameDownload = getString(R.string.channel_name_download)
                val channelIdDownload = getString(R.string.download_notification_channel_id)
                val descriptionTextDownload = getString(R.string.channel_description_download)
                val importanceDownload = NotificationManager.IMPORTANCE_LOW
                val channelDownload =
                    NotificationChannel(channelIdDownload, nameDownload, importanceDownload).apply {
                        description = descriptionTextDownload
                    }

                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channelDownload)
            }
        }
    }
}