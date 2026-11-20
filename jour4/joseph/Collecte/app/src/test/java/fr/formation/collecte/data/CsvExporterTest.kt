package fr.formation.collecte.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CsvExporterTest {
    @Test
    fun `une ligne par releve, separateur point-virgule, virgule decimale`() {
        val csv = CsvExporter.toCsv(listOf(Releve("17/11/2026 08:30", "Quai 3", "Julie", 3.2, "bar")))
        val lignes = csv.removePrefix("﻿").lines()
        assertEquals("Date;Site;Opérateur;Mesure;Unité;Commentaire", lignes[0])
        assertEquals("17/11/2026 08:30;Quai 3;Julie;3,2;bar;", lignes[1])
    }

    @Test
    fun `un champ avec point-virgule est entoure de guillemets`() {
        val csv = CsvExporter.toCsv(listOf(Releve("d", "A;B", "J", 1.0, "u", "dit \"ok\"")))
        assertEquals("d;\"A;B\";J;1,0;u;\"dit \"\"ok\"\"\"", csv.removePrefix("﻿").lines()[1])
    }

    @Test
    fun `la mesure doit etre un nombre`() {
        assertThrows(IllegalArgumentException::class.java) { creerReleve("A", "J", "abc", "°C", "") }
        assertEquals(21.5, creerReleve("A", "J", "21,5", "°C", "").mesure, 0.0)
    }
}
