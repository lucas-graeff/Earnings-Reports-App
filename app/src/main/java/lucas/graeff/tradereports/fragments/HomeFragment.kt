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
import lucas.graeff.tradereports.room.Report
import lucas.graeff.tradereports.webscraping.CollectData
import kotlin.concurrent.thread

class HomeFragment : Fragment(R.layout.fragment_home) {
    lateinit var data: List<Report>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs : SharedPreferences = view.context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

        //Create database
        val db = Room.databaseBuilder(
            view.context,
            AppDatabase::class.java, "reports"
        ).build()

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        //First app use
        if (prefs.getBoolean("firstrun", true)) {

            thread {
                CollectData(db).run()
            }.join()

            prefs.edit().putBoolean("firstrun", false).apply()
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
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                thread {
                    when (position) {

                        0 -> {
                            data = db.reportDao().getUpcomingReports()
                        } // Read data into Recycler
                        1 -> {
                            data = db.reportDao().filterStrict()
                            // Read data into Recycler
                        }
                        2 -> {
                            data = db.reportDao().filterGuidance()
                            // Read data into Recycler
                        }
                        3 -> {
                            data = db.reportDao().filterRaisingEps()
                            // Read data into Recycler
                        }
                        4 -> {
                            data = db.reportDao().filterPositiveChange()
                            // Read data into Recycler
                        }
                        5 -> {
                            data = db.reportDao().filterPositiveChange()
                            // Read data into Recycler
                        }
                    }
                }.join()
                // Recyclerview Settings
                recyclerView.adapter = ReportAdapter(requireContext(), db, data)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }
}