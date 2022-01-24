package lucas.graeff.tradereports;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.List;

import lucas.graeff.tradereports.fragments.HomeFragment;
import lucas.graeff.tradereports.fragments.ListFragment;
import lucas.graeff.tradereports.fragments.PostFragment;
import lucas.graeff.tradereports.webscraping.CollectData;
import lucas.graeff.tradereports.webscraping.PostAnalysis;

public class MainActivity extends FragmentActivity {
    MyDatabaseHelper db;
    int fragment_id;

    Fragment homeFragment;
    Fragment postFragment;
    Fragment listFragment;
    Fragment fragment;

    SharedPreferences prefs = null;

    private static final String FRAGMENT_KEY = "fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new MyDatabaseHelper(this);

        createNotificationChannel();
        setAlarm();

        //Initiate fragments
        homeFragment = new HomeFragment();
        postFragment = new PostFragment();
        listFragment = new ListFragment();
        fragment_id = 1;

        //Handle rotation data
        if(savedInstanceState != null) {
            fragment_id = savedInstanceState.getInt(FRAGMENT_KEY);
            switch(fragment_id) {
                case 0:
                    fragment = getSupportFragmentManager().findFragmentByTag("POST");
                    break;
                case 1:
                    fragment = getSupportFragmentManager().findFragmentByTag("HOME");
                    break;
                case 2:
                    fragment = getSupportFragmentManager().findFragmentByTag("LIST");
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_wrapper, fragment).commit();
        }
        else {
            makeCurrentFragment(homeFragment, "HOME");
        }


        //Bottom Navigation
        BottomNavigationView bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.ic_post_analysis:
                    makeCurrentFragment(postFragment, "POST");
                    fragment_id = 0;
                    break;

                case R.id.ic_home:
                    makeCurrentFragment(homeFragment, "HOME");
                    fragment_id = 1;
                    break;

                case R.id.ic_list:
                    makeCurrentFragment(listFragment, "LIST");
                    fragment_id = 2;
                    break;

            }
            return false;
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(FRAGMENT_KEY, fragment_id);

    }

    private void makeCurrentFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_wrapper, fragment, tag).commit();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "stocks";
            String description = "For displaying upcoming stocks";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("stocks", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(this, 5555, new Intent(this, AlarmReceiver.class), 0);


        // Set the alarm to start at approximately 10 am.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        System.out.println(calendar.getTimeInMillis());

        // Check if the Calendar time is in the past
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            Log.e("setAlarm","time is in past");
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }



}