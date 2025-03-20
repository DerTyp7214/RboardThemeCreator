package de.dertyp7214.rboardthemecreator.components

import android.app.Activity
import android.graphics.Color
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.madrapps.pikolo.HSLColorPicker
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import de.dertyp7214.rboardthemecreator.R
import de.dertyp7214.rboardthemecreator.core.openDialog
import de.dertyp7214.rboardthemecreator.core.toHex
import java.util.Locale
import java.util.regex.Pattern
import androidx.core.graphics.toColorInt

class HexColorAdapter(
    private val activity: Activity,
    private val colors: Map<String, Int>,
    private val onChange: (index: Int, color: Int) -> Unit
) : RecyclerView.Adapter<HexColorAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val wrapper: View = v.findViewById(R.id.wrapper)
        val inputLayout: TextInputLayout = v.findViewById(R.id.inputLayout)
        val editText: EditText = v.findViewById(R.id.editText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(activity).inflate(R.layout.hex_color_item, parent, false))

    override fun getItemCount() = colors.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (name, color) = colors.entries.toList()[position]

        holder.inputLayout.hint = name
        holder.editText.setText(color.toHex())

        holder.wrapper.setOnClickListener {
            activity.openDialog(R.layout.color_picker_dialog, cancelable = false) { dialog ->
                val colorPicker: HSLColorPicker = findViewById(R.id.colorPicker)
                val editText: EditText = findViewById(R.id.editText)
                val btnDice: Button = findViewById(R.id.btnDice)
                val btnCancel: Button = findViewById(R.id.btnCancel)
                val btnPick: Button = findViewById(R.id.btnPick)

                editText.filters = arrayOf(
                    InputFilter { source, _, end, _, _, _ ->
                        val pattern = Pattern.compile("^\\p{XDigit}+$")

                        val stringBuilder = StringBuilder("")

                        for (i in 0 until end) {
                            if (!Character.isLetterOrDigit(source[i]) && !Character.isSpaceChar(
                                    source[i]
                                )
                            ) continue

                            val matcher = pattern.matcher(String(charArrayOf(source[i])))
                            if (!matcher.matches()) continue
                            if (i >= 6) continue

                            stringBuilder.append(source[i])
                        }

                        return@InputFilter stringBuilder.toString().uppercase(Locale.getDefault())
                    }
                )

                editText.setText(Integer.toHexString(color).substring(2))

                colorPicker.setColor(color)
                colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
                    override fun onColorSelected(color: Int) {
                        editText.setText(Integer.toHexString(color).substring(2))
                    }
                })

                editText.doAfterTextChanged {
                    val text = it?.toString()
                    btnPick.isEnabled = true
                    if (text != null && text.length == 6)
                        colorPicker.setColor(parseColor(text))
                    else btnPick.isEnabled = false
                }

                btnDice.setOnClickListener {
                    val randomColor = randomColor()
                    editText.setText(randomColor)
                    colorPicker.setColor(parseColor(randomColor))
                }
                btnCancel.setOnClickListener { dialog.dismiss() }
                btnPick.setOnClickListener {
                    dialog.dismiss()
                    onChange(position, parseColor(editText.text.toString()))
                }
            }
        }
    }

    private fun parseColor(string: String): Int {
        return try {
            string.let {
                if (it.startsWith("#")) it else "#$it"
            }.toColorInt()
        } catch (_: Exception) {
            Color.RED
        }
    }

    private fun randomColor(): String = List(6) {
        (('A'..'F') + ('0'..'9')).random()
    }.joinToString("")
}