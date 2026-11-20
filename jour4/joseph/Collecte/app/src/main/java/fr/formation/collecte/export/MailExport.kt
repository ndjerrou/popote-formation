package fr.formation.collecte.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Écrit le CSV dans le cache de l'appli, puis ouvre l'appli mail avec le fichier en pièce jointe.
// L'opérateur relit et appuie sur Envoyer : aucun mot de passe dans notre code.
object MailExport {

    fun envoyer(context: Context, csv: String, destinataire: String, nombre: Int) {
        val horodatage = SimpleDateFormat("yyyyMMdd-HHmm", Locale.FRANCE).format(Date())
        val dossier = File(context.cacheDir, "exports").apply { mkdirs() }
        val fichier = File(dossier, "releves-$horodatage.csv").apply { writeText(csv) }

        // content://fr.formation.collecte.fileprovider/exports/releves-….csv
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", fichier)

        val mail = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(destinataire))
            putExtra(Intent.EXTRA_SUBJECT, "Relevés du $horodatage ($nombre)")
            putExtra(Intent.EXTRA_TEXT, "Bonjour,\n\nVeuillez trouver ci-joint $nombre relevé(s).\n")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)        // l'appli mail a le droit de lire CE fichier
        }
        context.startActivity(Intent.createChooser(mail, "Envoyer les relevés"))
    }
}
