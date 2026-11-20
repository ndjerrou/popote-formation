package fr.formation.mescourses

import android.content.Context
import androidx.core.content.edit

// Le stockage local : un petit "carnet" clé -> valeur, enregistré dans le téléphone.
class Stockage(context: Context) {

    private val prefs = context.getSharedPreferences("mes_courses", Context.MODE_PRIVATE)

    fun sauver(articles: List<Article>) {
        val texte = articles.joinToString("\n") { it.versLigne() }
        prefs.edit { putString("articles", texte) }
    }

    fun charger(): List<Article> {
        val texte = prefs.getString("articles", "") ?: ""
        return texte.lines().mapNotNull { it.versArticle() }
    }
}
