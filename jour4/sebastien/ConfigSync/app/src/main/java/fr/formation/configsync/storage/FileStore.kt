package fr.formation.configsync.storage

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import java.io.File

// Où écrire le fichier. Les 4 cas, du plus simple au plus contraint.
enum class Destination(val label: String, val detail: String) {
    PHONE_APP("Téléphone · dossier de l'appli", "Aucune permission. Supprimé avec l'appli."),
    SD_APP("Carte SD · dossier de l'appli", "Aucune permission. Seulement si une carte SD est présente."),
    DOCUMENTS("Téléphone · Documents/ConfigSync", "Aucune permission (MediaStore). Visible dans Fichiers."),
    SD_ROOT("Carte SD · racine", "L'utilisateur choisit le dossier une fois (SAF)."),
}

class FileStore(private val context: Context) {

    // getExternalFilesDirs : [0] = mémoire interne partagée, [1] = carte SD (si présente)
    fun hasSdCard(): Boolean = context.getExternalFilesDirs(null).getOrNull(1) != null

    // ① Téléphone, dossier privé de l'appli : /Android/data/fr.formation.configsync/files/
    fun writeInPhoneAppDir(name: String, text: String): String {
        val dir = context.getExternalFilesDir(null) ?: context.filesDir
        return File(dir, name).apply { writeText(text) }.absolutePath
    }

    // ② Carte SD, dossier privé de l'appli : /storage/XXXX-XXXX/Android/data/fr.formation.configsync/files/
    fun writeInSdAppDir(name: String, text: String): String {
        val dir = context.getExternalFilesDirs(null).getOrNull(1)
            ?: throw IllegalStateException("Aucune carte SD détectée")
        return File(dir, name).apply { writeText(text) }.absolutePath
    }

    // ③ Dossier public Documents/ConfigSync, via MediaStore (Android 10+). Pas de permission.
    //    Limite : l'appli ne peut réécrire que les fichiers qu'ELLE a créés.
    fun writeInDocuments(name: String, text: String): String {
        val resolver = context.contentResolver
        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val folder = "Documents/ConfigSync/"
        val existing = resolver.query(
            collection,
            arrayOf(MediaStore.MediaColumns._ID),
            "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} = ?",
            arrayOf(name, folder),
            null,
        )?.use { cursor -> if (cursor.moveToFirst()) Uri.withAppendedPath(collection, cursor.getLong(0).toString()) else null }

        val uri = existing ?: resolver.insert(collection, ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
            put(MediaStore.MediaColumns.RELATIVE_PATH, folder)
        }) ?: throw IllegalStateException("Impossible de créer $name dans Documents")

        resolver.openOutputStream(uri, "wt")?.use { it.write(text.toByteArray()) }
        return "$folder$name"
    }

    // ④ N'importe quel dossier choisi par l'utilisateur, y compris la RACINE de la carte SD (SAF).
    //    treeUri vient de l'écran système « Choisir un dossier » ; la permission est gardée.
    fun writeInChosenFolder(treeUri: Uri, name: String, text: String): String {
        val folder = DocumentFile.fromTreeUri(context, treeUri)
            ?: throw IllegalStateException("Dossier introuvable")
        folder.findFile(name)?.delete()
        val file = folder.createFile("application/octet-stream", name)
            ?: throw IllegalStateException("Impossible de créer $name")
        context.contentResolver.openOutputStream(file.uri, "wt")?.use { it.write(text.toByteArray()) }
        return "${folder.name ?: "dossier choisi"}/$name"
    }
}
