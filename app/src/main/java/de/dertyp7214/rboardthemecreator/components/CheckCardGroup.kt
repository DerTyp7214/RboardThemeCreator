@file:Suppress("unused")

package de.dertyp7214.rboardthemecreator.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import de.dertyp7214.rboardthemecreator.R

class CheckCardGroup(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private val linearLayout by lazy { findViewById<LinearLayout>(R.id.linearLayout) }

    init {
        inflate(context, R.layout.check_card_group, this)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addCard(checkCard: CheckCard) {
        linearLayout.addView(checkCard)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeCard(checkCard: CheckCard) {
        linearLayout.removeView(checkCard)
    }
}