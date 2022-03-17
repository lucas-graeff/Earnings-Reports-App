package lucas.graeff.tradereports

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import lucas.graeff.tradereports.room.AppDatabase
import lucas.graeff.tradereports.webscraping.CollectData
import lucas.graeff.tradereports.webscraping.PostData
import kotlin.concurrent.thread

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val db: AppDatabase = AppDatabase.getInstance(context)
        thread {
            CollectData(db).run()
            PostData(db).run()
            upcomingReportsNotifications(db, context)
        }.join()
    }

    private fun upcomingReportsNotifications(db: AppDatabase, context: Context) {
        var time: String
        var period: String
        val dayOfWeek: Int = Calendar.getInstance()[Calendar.DAY_OF_WEEK]
        val reports = db.reportDao().notificationReports()
        for (report in reports) {
            period = if(report.bell == 0) {
                "morning"
            } else {
                "evening"
            }
            //Display time or morning/evening if no time is available
            time = if(report.time != "null") report.time else period
            if (dayOfWeek != 7 || dayOfWeek != 0) {
                val builder : NotificationCompat.Builder = NotificationCompat.Builder(context, "stocks")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Upcoming Earnings Report")
                    .setContentText("${report.ticker} is reporting on ${report.date} $time")
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                val notificationManagerCompat = NotificationManagerCompat.from(context)
                notificationManagerCompat.notify(report.id, builder.build())
            }
        }
    }
}