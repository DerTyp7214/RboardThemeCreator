@file:Suppress("unused")

package de.dertyp7214.rboardthemecreator.core

import android.app.Activity
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.RenderEffect
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.annotation.LayoutRes
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

operator fun <T : ViewModel> FragmentActivity.get(modelClass: Class<T>): T =
    run(::ViewModelProvider)[modelClass]

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

fun Activity.openDialog(
    @LayoutRes layout: Int,
    cancelable: Boolean = true,
    block: View.(DialogInterface) -> Unit
): AlertDialog {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) content.setRenderEffect(
        RenderEffect.createBlurEffect(
            10F,
            10F,
            Shader.TileMode.REPEAT
        )
    )
    val view = layoutInflater.inflate(layout, null)
    return MaterialAlertDialogBuilder(this)
        .setCancelable(cancelable)
        .setView(view)
        .setOnDismissListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) content.setRenderEffect(null)
        }
        .create().also { dialog ->
            block(view, dialog)
            dialog.show()
        }
}

fun Activity.openShareThemeDialog(
    negative: ((dialogInterface: DialogInterface) -> Unit) = { it.dismiss() },
    positive: (dialogInterface: DialogInterface, name: String, author: String) -> Unit
) = openDialog(R.layout.share_popup, false) { dialog ->
    val nameInput = findViewById<EditText>(R.id.editTextName)
    val authorInput = findViewById<EditText>(R.id.editTextAuthor)

    findViewById<Button>(R.id.ok)?.setOnClickListener {
        positive(
            dialog,
            nameInput?.text?.toString() ?: "Shared Pack",
            authorInput?.text?.toString() ?: "Rboard Theme Manager"
        )
    }
    findViewById<Button>(R.id.cancel)?.setOnClickListener {
        negative(dialog)
    }
}