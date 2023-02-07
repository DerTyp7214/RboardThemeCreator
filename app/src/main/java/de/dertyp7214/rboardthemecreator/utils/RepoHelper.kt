@file:Suppress("MemberVisibilityCanBePrivate")

package de.dertyp7214.rboardthemecreator.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.dertyp7214.rboardthemecreator.data.RepoManifest
import de.dertyp7214.rboardthemecreator.data.RepoTemplate
import java.io.File
import java.net.URL

@Suppress("unused")
class RepoHelper private constructor(val context: Context) {
    private val gson = Gson()
    private val manifestUrl =
        "https://raw.githubusercontent.com/GboardThemes/ThemeCreatorRepo/main/manifest.json"

    private val repoDir = File(context.filesDir, "repo")
    private var manifest: RepoManifest? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        var instance: RepoHelper? = null
            private set

        fun init(context: Context): RepoHelper {
            instance = RepoHelper(context)
            return instance!!
        }
    }

    fun getManifest(): RepoManifest? {
        return try {
            val templates: Map<String, RepoTemplate> = gson.fromJson(
                URL(manifestUrl).readText(),
                object : TypeToken<Map<String, RepoTemplate>>() {}.type
            )
            RepoManifest(templates)
        } catch (e: Exception) {
            null
        }
    }

    fun cacheAndGetManifest(manifest: RepoManifest? = getManifest()): RepoManifest? {
        if (!repoDir.exists()) repoDir.mkdirs()
        if (manifest != null) {
            this.manifest = manifest
            manifest.templates.firstNotNullOfOrNull { (key, value) ->
                if (key == "default") (key to value) else null
            }?.let { (key, value) ->
                val templateDir = File(repoDir, key)
                if (!templateDir.exists()) templateDir.mkdirs()
                File(
                    templateDir,
                    "manifest.json"
                ).writeText(RepoManifest(mapOf(key to value)).toString())
                File(templateDir, "style_sheet_md2.css").writeText(value.styleSheetMd)
                File(templateDir, "style_sheet_md2_border.css").writeText(value.styleSheetMdBorder)
                File(templateDir, "metadata.json").writeText(value.metadata.toString())
                File(templateDir, "preview.html").writeText(value.preview)
                value.imageBase64.forEach {
                    File(templateDir, it.file).writeBytes(it.base64.decodeBase64()!!)
                }
            }
        } else {
            this.manifest = gson.fromJson(
                File(repoDir, "default/manifest.json").readText(),
                RepoManifest::class.java
            )
        }
        return manifest
    }

    fun getTemplate(name: String): RepoTemplate? {
        return manifest?.templates?.get(name)
    }

    fun getTemplateDir(template: String): File {
        return File(File(context.filesDir, "repo"), template)
    }

    fun getTemplateFile(template: String, file: String): File {
        return File(getTemplateDir(template), file)
    }

    fun getTemplateImage(template: String, file: String): ByteArray {
        return Base64.decode(
            getTemplateFile(template, file).readText(),
            Base64.DEFAULT
        )
    }

    private fun String.decodeBase64(): ByteArray? {
        return try {
            Base64.decode(this, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }
}