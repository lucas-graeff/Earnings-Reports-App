package lucas.graeff.tradereports

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import lucas.graeff.tradereports.room.AppDatabase
import lucas.graeff.tradereports.webscraping.CollectData
import kotlin.concurrent.thread

class AlarmReceiver : BroadcastReceiver() {
    var context: Context? = null
    override fun onReceive(context: Context, intent: Intent) {
        val db: AppDatabase = AppDatabase.getInstance(context)
        thread {
            CollectData(db)
        }.join()

//        thread {
//            PostAnalysis(context)
//        }.join()

        //upcomingReportsNotifications(context)
    }

    fun buildNotification(context: Context?) {
        val builder = NotificationCompat.Builder(
            context!!, "stocks"
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Upcoming Earnings Report")
            .setContentText("GOOG is reporting after hours today")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notificationManagerCompat = NotificationManagerCompat.from(
            context
        )
        notificationManagerCompat.notify(123, builder.build())
    }

//    private fun upcomingReportsNotifications(context: Context?) {
//        //val db: AppDatabase = AppDatabase.getInstance(context)
//        var builder: NotificationCompat.Builder
//        var ticker: ArrayList<*>
//        var time: ArrayList<*>
//        var day: String
//        var dayOfWeek: Int
//        var cursor: Cursor
//        for (i in 0..1) {
//            ticker = ArrayList<Any?>()
//            time = ArrayList<Any?>()
//            dayOfWeek = Calendar.getInstance()[Calendar.DAY_OF_WEEK]
//            if (dayOfWeek != 7 || dayOfWeek != 0) {
//                //Today After hours
//                if (i == 0) {
//                    day = "today"
//                    cursor =
//                        db.readQuery("SELECT ticker, time FROM REPORTS WHERE list = \"1\"AND bell = \"1\"  AND reports.date = date('now') ")
//                } else {
//                    if (dayOfWeek == 6) {
//                        day = "on Monday"
//                        cursor =
//                            db.readQuery("SELECT ticker, time FROM REPORTS WHERE list = \"1\"AND bell = \"0\"  AND reports.date = date('now', \"+3 days\") ")
//                    } else {
//                        day = "tomorrow"
//                        cursor =
//                            db.readQuery("SELECT ticker, time FROM REPORTS WHERE list = \"1\"AND bell = \"0\"  AND reports.date = date('now', \"+1 days\") ")
//                    }
//                }
//                if (cursor.count == 0) {
//                    continue
//                }
//                while (cursor.moveToNext()) {
//                    ticker.add(cursor.getString(0))
//                    time.add(cursor.getString(1))
//                }
//                for (j in ticker.indices) {
//                    builder = NotificationCompat.Builder(context!!, "stocks")
//                        .setSmallIcon(R.drawable.ic_launcher_foreground)
//                        .setContentTitle("Upcoming Earnings Report")
//                        .setContentText(ticker[j].toString() + " is reporting at " + time[j] + " " + day)
//                        .setAutoCancel(true)
//                        .setDefaults(NotificationCompat.DEFAULT_ALL)
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    val notificationManagerCompat = NotificationManagerCompat.from(
//                        context
//                    )
//                    notificationManagerCompat.notify(j * (i + 1) + 1, builder.build())
//                }
//            }
//        }
//    }
}