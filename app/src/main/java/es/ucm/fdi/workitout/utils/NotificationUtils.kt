package es.ucm.fdi.workitout.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import es.ucm.fdi.workitout.R

fun Context.createTrainingNotification(
    requestCode: Int,
    title: String,
    message: String
) {
    val trainingNotificationChannelID = "trainingChannel"

    createNotificationChannel(trainingNotificationChannelID, getString(R.string.training_notifications))

    val notification = createNotification(
        channelId = trainingNotificationChannelID,
        smallIcon = R.drawable.ic_app_notification,
        title = title,
        text = message
    )

    //TODO Añadir click en notificación para abrir el entrenamiento

    val  manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(requestCode, notification)
}

fun Context.deleteNotification(requestCode: Int) {
    val  manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.cancel(requestCode)
}

fun Context.createNotification(channelId: String, smallIcon: Int, title: String?, text: String?, intent: PendingIntent? = null) =
    NotificationCompat.Builder(this, channelId)
        .setSmallIcon(smallIcon)
        .setContentTitle(title)
        .setContentText(text)
        .setContentIntent(intent)
        .build()

fun Context.createNotificationChannel(channelId: String, channelName: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,channelName, NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            lightColor = Color.BLUE
            enableVibration(true)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}