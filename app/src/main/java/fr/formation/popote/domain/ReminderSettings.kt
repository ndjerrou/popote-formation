package fr.formation.popote.domain

import kotlinx.coroutines.flow.Flow

/** Contrat pour le rappel de courses (implémenté avec DataStore + WorkManager). */
interface ReminderSettings {
    val enabled: Flow<Boolean>
    suspend fun setEnabled(enabled: Boolean)
    fun runNow()
}
