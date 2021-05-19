package de.dertyp7214.rboardthemecreator.data

import com.google.gson.Gson

data class ThemeMetadata(
    val format_version: Int = 3,
    val id: String = "gboard_theme_creator_${Math.random()}",
    val name: String,
    val prefer_key_border: Boolean = true,
    val lock_key_border: Boolean = false,
    val is_light_theme: Boolean,
    val is_redesign_theme: Boolean = true,
    val style_sheets: List<String> = listOf(
        "style_sheet_variables.css",
        "style_sheet_md2.css"
    ),
    val flavors: List<Flavor> = listOf(
        Flavor()
    )
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}

data class Flavor(
    val type: String = "BORDER",
    val style_sheets: List<String> = listOf(
        "style_sheet_md2_border.css"
    )
)