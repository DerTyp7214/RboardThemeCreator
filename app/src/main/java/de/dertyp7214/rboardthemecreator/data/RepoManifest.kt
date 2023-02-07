package de.dertyp7214.rboardthemecreator.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class RepoManifest(
    val templates: Map<String, RepoTemplate>,
) {
    override fun toString(): String {
        return "{${
            templates.map { (key, value) ->
                "\"$key\":$value"
            }.joinToString(",")
        }}"
    }
}

data class RepoTemplate(
    val name: String,
    val styleSheetMd: String,
    val styleSheetMdBorder: String,
    val metadata: RepoTemplateMetadata,
    val preview: String,
    val imageBase64: List<RepoTemplateImage>
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}

data class RepoTemplateImage(
    val file: String,
    val base64: String
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}

data class RepoTemplateMetadata(
    @SerializedName("format_version")
    val formatVersion: Int? = null,
    val id: String? = null,
    val name: String? = null,
    @SerializedName("prefer_key_border")
    val preferKeyBorder: Boolean? = null,
    @SerializedName("lock_key_border")
    val lockKeyBorder: Boolean? = null,
    @SerializedName("is_light_theme")
    val isLightTheme: Boolean? = null,
    @SerializedName("style_sheets")
    val styleSheets: List<String>? = null,
    val flavors: List<RepoTemplateFlavor>? = null
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }

    fun merge(other: ThemeMetadata): ThemeMetadata {
        return ThemeMetadata(
            formatVersion ?: other.formatVersion,
            id ?: other.id,
            name ?: other.name,
            preferKeyBorder ?: other.preferKeyBorder,
            lockKeyBorder ?: other.lockKeyBorder,
            isLightTheme ?: other.isLightTheme,
            styleSheets ?: other.styleSheets,
            flavors?.map { flavor ->
                Flavor(
                    flavor.type ?: "BORDER",
                    flavor.styleSheets ?: listOf("style_sheet_md2_border.css")
                )
            } ?: other.flavors
        )
    }
}

data class RepoTemplateFlavor(
    val type: String? = null,
    @SerializedName("style_sheets")
    val styleSheets: List<String>? = null
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}