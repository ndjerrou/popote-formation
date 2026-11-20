package fr.formation.popote.work

import android.content.Context
import fr.formation.popote.data.prefs.SettingsRepository
import fr.formation.popote.domain.ReminderSettings
import kotlinx.coroutines.flow.Flow

class ReminderManager(private val context: Context, private val settings: SettingsRepository) : ReminderSettings {
    override val enabled: Flow<Boolean> = settings.reminderEnabled

    override suspend fun setEnabled(enabled: Boolean) {
        settings.setReminderEnabled(enabled)
        if (enabled) ReminderScheduler.enable(context) else ReminderScheduler.disable(context)
    }

    override fun runNow() { ReminderScheduler.runNow(context) }
}
