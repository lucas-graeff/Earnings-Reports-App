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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.List;

import lucas.graeff.tradereports.fragments.HomeFragment;
import lucas.graeff.tradereports.fragments.ListFragment;
import lucas.graeff.tradereports.fragments.PostFragment;

public class MainActivity extends FragmentActivity {
    MyDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new MyDatabaseHelper(this);

        createNotificationChannel();
        setAlarm();

        //Initiate fragments
        Fragment homeFragment = new HomeFragment();
        Fragment postFragment = new PostFragment();
        Fragment listFragment = new ListFragment();

        makeCurrentFragment(homeFragment);

        //Bottom Navigation
        BottomNavigationView bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.ic_post_analysis:
                    makeCurrentFragment(postFragment);
                    break;

                case R.id.ic_home:
                    makeCurrentFragment(homeFragment);
                    break;

                case R.id.ic_list:
                    makeCurrentFragment(listFragment);
                    break;

            }
            return false;
        });

    }

    private void makeCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_wrapper, fragment).commit();
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
        alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);


        // Set the alarm to start at approximately 10 am.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);

// With setInexactRepeating(), you have to use one of the AlarmManager interval
// constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }



}