package de.dertyp7214.rboardthemecreator.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dertyp7214.preferencesplus.core.dp
import com.google.android.material.card.MaterialCardView
import de.dertyp7214.rboardthemecreator.R
import de.dertyp7214.rboardthemecreator.core.getAttr

class CheckCard(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val strokeWidth = 5.dp(context)
    private val iconTintSelected = ColorStateList.valueOf(context.getAttr(R.attr.colorPrimary))
    private val iconTint = ColorStateList.valueOf(context.getAttr(R.attr.colorOnSurfaceVariant))

    private val card by lazy { findViewById<MaterialCardView>(R.id.card) }
    private val imageView by lazy { findViewById<ImageView>(R.id.icon) }
    private val textView by lazy { findViewById<TextView>(R.id.text) }

    var isChecked = false
        set(value) {
            card.strokeWidth = if (value) strokeWidth else 0
            imageView.imageTintList = if (value) iconTintSelected else iconTint

            field = value
        }

    @DrawableRes
    var icon: Int? = null
        set(value) {
            value?.let(imageView::setImageResource) ?: imageView.setImageDrawable(null)

            field = value
        }

    @StringRes
    var text: Int? = null
        set(value) {
            textView.text = value?.let(context::getString) ?: ""

            field = value
        }

    var name: String? = null

    init {
        inflate(context, R.layout.check_card, this)

        card.setOnClickListener { isChecked = !isChecked }
    }

    fun setOnClickListener(listener: (Boolean) -> Unit) {
        card.setOnClickListener {
            isChecked = !isChecked
            listener(isChecked)
        }
    }
}