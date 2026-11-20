package fr.formation.collecte.data

// Le « retraitement » : la liste devient un fichier CSV qu'Excel ouvre directement.
object CsvExporter {
    private const val SEPARATEUR = ';'          // Excel en français attend des « ; » (la virgule est le séparateur décimal)

    fun toCsv(releves: List<Releve>): String = buildString {
        append('\uFEFF')                        // BOM : sans lui, Excel affiche mal les accents
        appendLine(listOf("Date", "Site", "Opérateur", "Mesure", "Unité", "Commentaire").joinToString(SEPARATEUR.toString()))
        releves.forEach { r ->
            val mesure = r.mesure.toString().replace('.', ',')
            appendLine(listOf(r.date, r.site, r.operateur, mesure, r.unite, r.commentaire)
                .joinToString(SEPARATEUR.toString()) { echapper(it) })
        }
    }

    // Un champ qui contient ; ou " ou un retour à la ligne est entouré de guillemets
    private fun echapper(champ: String): String =
        if (champ.any { it == SEPARATEUR || it == '"' || it == '\n' }) "\"" + champ.replace("\"", "\"\"") + "\"" else champ
}
