package fr.formation.configsync.data

// Transforme les sections en texte au format .ini :
// ; commentaire
// [reseau]
// port=502
object IniWriter {
    fun write(sections: Sections, comment: String? = null): String = buildString {
        comment?.let { appendLine("; $it") }
        sections.forEach { (section, pairs) ->
            appendLine("[$section]")
            pairs.forEach { (key, value) -> appendLine("$key=$value") }
            appendLine()
        }
    }
}
