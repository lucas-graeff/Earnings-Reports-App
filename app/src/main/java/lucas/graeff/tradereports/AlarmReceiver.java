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
import lucas.graeff.tradereports.webscraping.PostEarnings;
import lucas.graeff.tradereports.webscraping.WebInfo;
import lucas.graeff.tradereports.webscraping.WebInfoZacks;

public class AlarmReceiver extends BroadcastReceiver {
    MyDatabaseHelper db;
    ArrayList<String> recent_tickers;

    private Context context;
    public AlarmReceiver(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        buildNotification(context);

        db = new MyDatabaseHelper(context.getApplicationContext());
//        retrieveWebInfo();
//        GetPost();
//        new CollectData(context.getApplicationContext());

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

    public void retrieveWebInfo() {
        HashMap<Integer, ArrayList<String>> colInfo;
        ArrayList tempArray;

        //Start web scrape of Upcoming
        WebInfo webInfo = new WebInfo();
        Thread thread = new Thread(webInfo);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Initialize arrays for storage
        ArrayList ticker = new ArrayList<String>();
        ArrayList predicted = new ArrayList<Double>();
        ArrayList sinceLast = new ArrayList<Double>();
        ArrayList zscore = new ArrayList<Integer>();
        ArrayList momentum = new ArrayList<String>();
        ArrayList vgm = new ArrayList<String>();
        ArrayList esp = new ArrayList<Double>();
        ArrayList date = new ArrayList<>();
        ArrayList time = new ArrayList<>();

        //Get stocksearning.com info
        colInfo = webInfo.getValue();

        //Parse and prepare
        for (int i = 1; i < colInfo.size() + 1; i++) {
            tempArray = colInfo.get(i);
            ticker.add(tempArray.get(0).toString().substring(0, 4).replace("-", ""));
            predicted.add(Double.parseDouble((tempArray.get(2).toString().replace("%", ""))));
            sinceLast.add(Double.parseDouble((tempArray.get(3).toString().replace("%", ""))));
        }

        //Web scrape Zacks.com
        WebInfoZacks webInfoZacks = new WebInfoZacks(ticker);
        Thread threadZacks = new Thread(webInfoZacks);
        threadZacks.start();
        try {
            threadZacks.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Assign scraped info
        colInfo = webInfoZacks.getValue();

        //Parse and prepare scraped info for database insertion

        //For marking inaccurate stocks
        ArrayList inaccurate = new ArrayList<>();
        boolean marked = false;

        for (int i = 0; i < colInfo.size(); i++) {
            marked = false;
            tempArray = colInfo.get(i);

            try {
                zscore.add(Integer.parseInt(tempArray.get(0).toString()));
            } catch (Exception e) {
                zscore.add(6);
                marked = true;
            }

            try {
                momentum.add(tempArray.get(1).toString());
            } catch (Exception e) {
                momentum.add("-");
                marked = true;
            }

            try {
                vgm.add(tempArray.get(2).toString());
            } catch (Exception e) {
                vgm.add("-");
                marked = true;
            }

            try {
                if (tempArray.get(3).toString().contains("NA")) {
                    esp.add(0.00);
                } else {
                    esp.add(Double.parseDouble(tempArray.get(3).toString().replace("%", "")));
                }
            } catch (Exception e) {
                esp.add(0.00);
                marked = true;
            }

            try {
                if (tempArray.get(4).toString().contains("AMC")) {
                    time.add(1);
                } else {
                    time.add(0);
                }
            } catch (Exception e) {
                time.add(0);
                marked = true;
            }

            try {
                String tempDate = tempArray.get(4).toString().replace("*BMO", "").replace("*AMC", "");
                if(tempDate.length() == 8) {
                    date.add(String.format("20%1$s-%2$s", tempDate.substring(6), tempDate.substring(0, 5).replace("/", "-")));
                }
                else {
                    date.add(String.format("20%1$s-%2$s%3$s", tempDate.substring(5), tempDate.substring(0, 3).replace("/", "-"), tempDate.substring(3, 4)));
                }

            } catch (Exception e) {
                date.add("NANANA");
                marked = true;
            }

            if(marked) {
                inaccurate.add(i);
            }

        }

        recent_tickers = new ArrayList<>();
        duplicateCheck();

//        for (int i = 0; i < ticker.size(); i++) {
//            if (!recent_tickers.contains(ticker.get(i)) && !inaccurate.contains(i)) {
//                db.addReport((String) ticker.get(i), (String) date.get(i), (Double) predicted.get(i), (Double) esp.get(i), (Integer) zscore.get(i), (String) momentum.get(i), (String) vgm.get(i), (Double) sinceLast.get(i), (Integer) time.get(i));
//            }
//        }

    }

    public void duplicateCheck() {
        Cursor cursor = db.getRecentTickers();
        while (cursor.moveToNext()) {
            recent_tickers.add(cursor.getString(0));
        }
    }

    //Update database with post analysis cols
    public void GetPost() {
        HashMap<Integer, ArrayList<String>> colInfo;
        int id;

        //Start web scrape of Upcoming
        PostEarnings postEarnings = new PostEarnings();
        Thread thread = new Thread(postEarnings);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        colInfo = postEarnings.getValue();

        for (int i = 1; i < colInfo.size(); i++) {
            ArrayList<String> tempArray = colInfo.get(i);
            id = db.FindId(tempArray.get(0).substring(0, 4).replace("-", "").trim());
            System.out.println("ID:" + id);
            if(id != 0) {
                db.AddPost(id, Double.parseDouble(tempArray.get(2).replace("%", "")), Double.parseDouble(tempArray.get(3).replace("%", "")));
            }

        }
    }
}
