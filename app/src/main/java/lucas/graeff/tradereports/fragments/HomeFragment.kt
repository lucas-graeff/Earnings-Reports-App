package lucas.graeff.tradereports.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import lucas.graeff.tradereports.R
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import lucas.graeff.tradereports.adapters.ReportAdapter
import lucas.graeff.tradereports.room.AppDatabase
import lucas.graeff.tradereports.webscraping.CollectData
import kotlin.concurrent.thread

class HomeFragment : Fragment(R.layout.fragment_home) {
    var reportAdapter: ReportAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs : SharedPreferences = view.context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

        //Create database
        var db = Room.databaseBuilder(
            view.context,
            AppDatabase::class.java, "reports"
        ).build()

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        //First app use
        if (prefs.getBoolean("firstrun", true)) {

            thread {
                CollectData(db).run()
            }.join()

//            thread {
//                PostAnalysis(view.context)
//            }.join()

            prefs.edit().putBoolean("firstrun", false).apply()
        }

        // TODO: Read data into recyclerview
        thread {
            reportAdapter = ReportAdapter(view.context, db, db.reportDao().getUpcomingReports())
            activity?.runOnUiThread  {
                recyclerView.adapter = reportAdapter
                recyclerView.layoutManager = LinearLayoutManager(requireView().context)
            }
        }


        //Create dropdown menu
        val dropdownOptions =
            arrayOf("No filter", "Strict", "Guidance", "Raising EPS", "Positive Change", "Averages")
        val dropdown = view.findViewById<View>(R.id.dropdown_menu) as Spinner
        val dropdownAdapter =
            ArrayAdapter(view.context, android.R.layout.simple_spinner_item, dropdownOptions)
        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dropdown.adapter = dropdownAdapter
        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                var query: String
                when (position) {

                    0 -> {
                        query =
                            "SELECT * FROM reports WHERE reports.date > date('now', '-1 day') AND recom < 2.5 AND perf_week > 0 AND predicted_eps > 0  AND peg < 1 ORDER BY date ASC"
                    } // Read data into Recycler
                    1 -> {
                        query =
                            "SELECT * FROM reports WHERE reports.date > date('now', '-1 day') AND recom < 2.5 AND perf_week > 0 AND predicted_eps > 0  AND peg < 1 ORDER BY date ASC"
                        // Read data into Recycler
                    }
                    2 -> {
                        query =
                            "SELECT * FROM reports WHERE reports.date > date('now', '-1 day') AND guidance_est > guidance_min ORDER BY date ASC"
                        // Read data into Recycler
                    }
                    3 -> {
                        query =
                            "SELECT * FROM reports WHERE reports.date > date('now', '-1 day') AND first_eps > second_eps AND second_eps > third_eps AND third_eps > fourth_eps AND fourth_eps > fifth_eps ORDER BY date ASC"
                        // Read data into Recycler
                        query =
                            "SELECT * FROM reports WHERE reports.date > date('now', '-1 day') AND first_from > first_to AND second_to > second_from AND third_to > third_from AND fourth_to > fourth_from ORDER BY date ASC"
                        // Read data into Recycler
                        query =
                            "SELECT * FROM REPORTS WHERE average > 0 AND average IS NOT '-' ORDER BY average DESC"
                        // Read data into Recycler
                    }
                    4 -> {
                        query =
                            "SELECT * FROM reports WHERE reports.date > date('now', '-1 day') AND first_from > first_to AND second_to > second_from AND third_to > third_from AND fourth_to > fourth_from ORDER BY date ASC"
                        // Read data into Recycler
                        query =
                            "SELECT * FROM REPORTS WHERE average > 0 AND average IS NOT '-' ORDER BY average DESC"
                        // Read data into Recycler
                    }
                    5 -> {
                        query =
                            "SELECT * FROM REPORTS WHERE average > 0 AND average IS NOT '-' ORDER BY average DESC"
                        // Read data into Recycler
                    }
                }
                // TODO: Notify data change
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    fun display(recyclerView: RecyclerView) {
        // customAdapter = CustomAdapter()
        recyclerView.adapter = reportAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireView().context)
    }


}