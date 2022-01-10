package lucas.graeff.tradereports;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.HashMap;

import lucas.graeff.tradereports.webscraping.CollectData;
import lucas.graeff.tradereports.webscraping.PostAnalysis;
import lucas.graeff.tradereports.webscraping.PostEarnings;
import lucas.graeff.tradereports.webscraping.WebInfo;
import lucas.graeff.tradereports.webscraping.WebInfoZacks;

public class AlarmReceiver extends BroadcastReceiver {
    MyDatabaseHelper db;

    private Context context;
    public AlarmReceiver(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        buildNotification(context);

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


}
