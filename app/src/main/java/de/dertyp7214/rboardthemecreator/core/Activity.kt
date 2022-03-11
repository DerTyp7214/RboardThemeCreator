package de.dertyp7214.rboardthemecreator.core

import android.app.Activity
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.RenderEffect
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.dertyp7214.rboardthemecreator.R

val Activity.preferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)

inline val Activity.content: View
    get() {
        return findViewById(android.R.id.content)
    }

fun Activity.openUrl(url: String) {
    CustomTabsIntent.Builder()
        .setShowTitle(true)
        .setDefaultColorSchemeParams(
            CustomTabColorSchemeParams
                .Builder()
                .setToolbarColor(getAttr(R.attr.colorBackgroundFloating))
                .setNavigationBarColor(getAttr(R.attr.colorBackgroundFloating))
                .setSecondaryToolbarColor(getAttr(R.attr.colorBackgroundFloating))
                .setNavigationBarDividerColor(getAttr(R.attr.colorBackgroundFloating))
                .build()
        )
        .build()
        .launchUrl(this, Uri.parse(url))
}

operator fun <T : ViewModel> FragmentActivity.get(modelClass: Class<T>): T =
    run(::ViewModelProvider)[modelClass]


fun Activity.openDialog(
    message: CharSequence,
    title: String,
    positiveText: String,
    negativeText: String,
    cancelable: Boolean = false,
    negative: ((dialogInterface: DialogInterface) -> Unit)? = { it.dismiss() },
    positive: (dialogInterface: DialogInterface) -> Unit
): AlertDialog {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        content.setRenderEffect(
            RenderEffect.createBlurEffect(
                10F,
                10F,
                Shader.TileMode.REPEAT
            )
        )
    }
    return MaterialAlertDialogBuilder(this)
        .setCancelable(cancelable)
        .setCancelable(false)
        .setMessage(message)
        .setTitle(title)
        .setPositiveButton(positiveText) { dialogInterface, _ -> positive(dialogInterface) }
        .setOnDismissListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                content.setRenderEffect(null)
            }
        }
        .apply {
            if (negative != null) setNegativeButton(negativeText) { dialogInterface, _ ->
                negative.invoke(
                    dialogInterface
                )
            }
        }
        .create().also { it.show() }
}

fun Activity.openDialog(
    message: CharSequence,
    title: String,
    cancelable: Boolean = false,
    @StringRes negativeText: Int = android.R.string.cancel,
    negative: ((dialogInterface: DialogInterface) -> Unit)? = { it.dismiss() },
    positive: (dialogInterface: DialogInterface) -> Unit
): AlertDialog = openDialog(
    message,
    title,
    getString(android.R.string.ok),
    getString(negativeText),
    cancelable,
    negative,
    positive
)

fun Activity.openDialog(
    @StringRes message: Int,
    @StringRes title: Int,
    cancelable: Boolean = false,
    negative: ((dialogInterface: DialogInterface) -> Unit)? = { it.dismiss() },
    positive: (dialogInterface: DialogInterface) -> Unit
): AlertDialog = openDialog(
    getString(message),
    getString(title),
    cancelable,
    negative = negative,
    positive = positive
)

fun Activity.openDialog(
    @StringRes message: Int,
    @StringRes title: Int,
    @StringRes positiveText: Int,
    @StringRes negativeText: Int,
    cancelable: Boolean = false,
    negative: ((dialogInterface: DialogInterface) -> Unit)? = { it.dismiss() },
    positive: (dialogInterface: DialogInterface) -> Unit
): AlertDialog = openDialog(
    getString(message),
    getString(title),
    getString(positiveText),
    getString(negativeText),
    cancelable,
    negative,
    positive
)


fun Activity.openDialog(
    @LayoutRes layout: Int,
    cancelable: Boolean = true,
    block: View.(DialogInterface) -> Unit
): AlertDialog {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        content.setRenderEffect(
            RenderEffect.createBlurEffect(
                10F,
                10F,
                Shader.TileMode.REPEAT
            )
        )
    }
    val view = layoutInflater.inflate(layout, null)
    return MaterialAlertDialogBuilder(this)
        .setCancelable(cancelable)
        .setView(view)
        .setOnDismissListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                content.setRenderEffect(null)
            }
        }
        .create().also { dialog ->
            block(view, dialog)
            dialog.show()
        }
}