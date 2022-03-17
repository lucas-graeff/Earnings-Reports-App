package lucas.graeff.tradereports.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import lucas.graeff.tradereports.R
import lucas.graeff.tradereports.adapters.ReportAdapter
import lucas.graeff.tradereports.room.AppDatabase
import lucas.graeff.tradereports.room.Report
import kotlin.concurrent.thread

class PostFragment : Fragment(R.layout.fragment_post) {
    private var filterSwitch: Switch? = null
    var switchState = 0
    lateinit var data: List<Report>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            println(savedInstanceState.getInt(SWITCH_KEY))
            switchState = savedInstanceState.getInt(SWITCH_KEY)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (filterSwitch!!.isChecked) {
            outState.putInt(SWITCH_KEY, 1)
        } else {
            outState.putInt(SWITCH_KEY, 0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db: AppDatabase = AppDatabase.getInstance(view.context)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        createdRecycler(db, 0, recyclerView, view.context)

        filterSwitch = view.findViewById(R.id.filter_switch)
        val winRate = view.findViewById<TextView>(R.id.txt_winrate)
        if (switchState == 1) {
            filterSwitch?.isChecked = true
        }

//        winRate.text = SharedFunctions.WinRate(
//            db,
//            "select count(b.id) as Winners, count(*) as TotalCount from REPORTS a left join REPORTS b on b.id = a.id and b.change > 0 WHERE a.list = 1 AND a.change NOT NULL"
//        ).toString() + "%"

        filterSwitch?.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            createdRecycler(db, (if(isChecked) 1 else 0), recyclerView, view.context)
        }
    }

    private fun createdRecycler(db: AppDatabase, list: Int, recyclerView: RecyclerView, context: Context) {
        thread { data = db.reportDao().postResults(list) }.join()
        recyclerView.adapter = ReportAdapter(context, db, data)
        recyclerView.layoutManager = LinearLayoutManager(requireView().context)
    }

    companion object {
        private const val SWITCH_KEY = "switch"
    }
}