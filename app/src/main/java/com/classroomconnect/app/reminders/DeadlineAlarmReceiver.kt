package com.classroomconnect.app.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.classroomconnect.app.R

class DeadlineAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Assignment deadline"
        val notification = NotificationCompat.Builder(context, "school_updates")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Due soon: $title")
            .setContentText("Open Classroom Connect to review your work.")
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(title.hashCode(), notification)
    }

    companion object {
        private const val EXTRA_TITLE = "title"
        fun schedule(context: Context, assignmentId: String, title: String, dueAtMillis: Long) {
            val intent = Intent(context, DeadlineAlarmReceiver::class.java).putExtra(EXTRA_TITLE, title)
            val pending = PendingIntent.getBroadcast(context, assignmentId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            context.getSystemService(AlarmManager::class.java).setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dueAtMillis, pending)
        }
    }
}
