package fr.formation.popote.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import fr.formation.popote.MainActivity
import fr.formation.popote.R
import fr.formation.popote.data.local.ShoppingDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

// region worker
/** Tâche de fond garantie (même si l'app est fermée ou le téléphone redémarré). */
class ShoppingReminderWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val shoppingDao: ShoppingDao by inject()

    override suspend fun doWork(): Result {
        val count = shoppingDao.countToBuy()
        if (count > 0) Notifications.showShoppingReminder(applicationContext, count)
        return Result.success()
    }
}

object ReminderScheduler {
    private const val WORK_NAME = "shopping_reminder"

    fun enable(context: Context) {
        val request = PeriodicWorkRequestBuilder<ShoppingReminderWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    fun disable(context: Context) = WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)

    /** Pour la démo : exécution immédiate. */
    fun runNow(context: Context) =
        WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<ShoppingReminderWorker>().build())
}
// endregion worker

// region notif
object Notifications {
    const val CHANNEL_ID = "shopping"

    /** À appeler au démarrage (Application) : obligatoire depuis Android 8. */
    fun createChannel(context: Context) {
        val channel = NotificationChannel(CHANNEL_ID, "Rappels de courses", NotificationManager.IMPORTANCE_DEFAULT)
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun showShoppingReminder(context: Context, count: Int) {
        // Android 13+ : POST_NOTIFICATIONS est une permission "dangereuse" (demandée à l'exécution)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val intent = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_cart)
            .setContentTitle("Popote 🛒")
            .setContentText("Il vous reste $count article(s) à acheter")
            .setContentIntent(intent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(1, notification)
    }
}
// endregion notif
