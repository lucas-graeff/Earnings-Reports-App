package lucas.graeff.tradereports.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import lucas.graeff.tradereports.R
import lucas.graeff.tradereports.adapters.ReportAdapter
import lucas.graeff.tradereports.room.AppDatabase
import lucas.graeff.tradereports.room.Report
import kotlin.concurrent.thread

class ListFragment : Fragment(R.layout.fragment_list) {
    lateinit var data: List<Report>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db: AppDatabase = AppDatabase.getInstance(view.context)

        thread { data = db.reportDao().getList() }.join()
        //Create adapter
        val reportAdapter = ReportAdapter(view.context, db, data)

        //Create recyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.adapter = reportAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireView().context)


    }

}