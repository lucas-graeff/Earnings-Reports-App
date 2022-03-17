package lucas.graeff.tradereports

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import lucas.graeff.tradereports.fragments.HomeFragment
import lucas.graeff.tradereports.fragments.ListFragment
import lucas.graeff.tradereports.fragments.PostFragment
import java.util.*

class MainActivity : FragmentActivity() {
    private var fragmentId = 0
    var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Handle notification alarm
        createNotificationChannel()
        setAlarm()

        //Initiate fragments
        val homeFragment = HomeFragment()
        val postFragment = PostFragment()
        val listFragment = ListFragment()
        fragmentId = 1

        //Handle rotation data
        if (savedInstanceState != null) {
            fragmentId = savedInstanceState.getInt(FRAGMENT_KEY)
            when (fragmentId) {
                0 -> fragment = supportFragmentManager.findFragmentByTag("POST")
                1 -> fragment = supportFragmentManager.findFragmentByTag("HOME")
                2 -> fragment = supportFragmentManager.findFragmentByTag("LIST")
            }
            supportFragmentManager.beginTransaction().replace(R.id.fl_wrapper, fragment!!).commit()
        } else {
            makeCurrentFragment(homeFragment, "HOME")
        }


        //Bottom Navigation
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.ic_post_analysis -> {
                    makeCurrentFragment(postFragment, "POST")
                    fragmentId = 0
                }
                R.id.ic_home -> {
                    makeCurrentFragment(homeFragment, "HOME")
                    fragmentId = 1
                }
                R.id.ic_list -> {
                    makeCurrentFragment(listFragment, "LIST")
                    fragmentId = 2
                }
            }
            false
        }
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(FRAGMENT_KEY, fragmentId)
    }

    private fun makeCurrentFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().replace(R.id.fl_wrapper, fragment, tag).commit()
    }

    private fun createNotificationChannel() {
            val name: CharSequence = "stocks"
            val description = "For displaying upcoming stocks"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("stocks", name, importance)
            channel.description = description
            // Register the channel with the system
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
    }

    private fun setAlarm() {
        val alarmMgr: AlarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 5555, Intent(this, AlarmReceiver::class.java), 0)


        // Set the alarm to start at approximately 5 am.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[Calendar.HOUR_OF_DAY] = 5

        // Check if the Calendar time is in the past
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            Log.e("setAlarm", "time is in past")
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        alarmMgr.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, alarmIntent
        )
    }

    companion object {
        private const val FRAGMENT_KEY = "fragment"
    }
}