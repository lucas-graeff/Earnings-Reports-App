package lucas.graeff.tradereports;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import lucas.graeff.tradereports.webscraping.CollectData;
import lucas.graeff.tradereports.webscraping.PostAnalysis;

public class AlarmReceiver extends BroadcastReceiver {
    MyDatabaseHelper db;

    public Context context;
    public AlarmReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        db = new MyDatabaseHelper(context.getApplicationContext());

        CollectData collectData = new CollectData(context.getApplicationContext());
        Thread thread = new Thread(collectData);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PostAnalysis postAnalysis = new PostAnalysis(context.getApplicationContext());
        thread = new Thread(postAnalysis);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        upcomingReportsNotifications(context);
    }

    public void buildNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "stocks")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Upcoming Earnings Report")
                .setContentText("GOOG is reporting after hours today")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());
    }

    public void upcomingReportsNotifications(Context context) {
        NotificationCompat.Builder builder;
        ArrayList ticker, time;
        String day;
        int dayOfWeek;
        Cursor cursor;

        for(int i = 0; i < 2; i++) {
            ticker = new ArrayList();
            time = new ArrayList();
            dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek != 7 || dayOfWeek != 0) {
                //Today After hours
                if(i == 0){
                    day = "today";
                    cursor = db.readQuery("SELECT ticker, time FROM REPORTS WHERE list = \"1\"AND bell = \"1\"  AND reports.date = date('now') ");
                }
                //Tomorrow morning
                else {
                    if(dayOfWeek == 6) {
                        day = "on Monday";
                        cursor = db.readQuery("SELECT ticker, time FROM REPORTS WHERE list = \"1\"AND bell = \"0\"  AND reports.date = date('now', \"+3 days\") ");
                    }
                    else {
                        day = "tomorrow";
                        cursor = db.readQuery("SELECT ticker, time FROM REPORTS WHERE list = \"1\"AND bell = \"0\"  AND reports.date = date('now', \"+1 days\") ");
                    }
                }
                if(cursor.getCount() == 0) {
                    continue;
                }
                while(cursor.moveToNext()) {
                    ticker.add(cursor.getString(0));
                    time.add(cursor.getString(1));
                }
                for(int j = 0; j < ticker.size(); j++) {
                    builder = new NotificationCompat.Builder(context, "stocks")
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle("Upcoming Earnings Report")
                            .setContentText(ticker.get(j) + " is reporting at " + time.get(j) + " " + day)
                            .setAutoCancel(true)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                    notificationManagerCompat.notify(j * (i + 1) + 1, builder.build());
                }
            }

        }


    }


}
