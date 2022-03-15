package lucas.graeff.tradereports.fragments

import android.database.Cursor
import lucas.graeff.tradereports.adapters.ReportAdapter
import android.os.Bundle
import android.view.Display
import android.view.View
import lucas.graeff.tradereports.R
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import lucas.graeff.tradereports.SharedFunctions
import android.widget.CompoundButton
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import lucas.graeff.tradereports.room.AppDatabase
import java.util.ArrayList

class PostFragment : Fragment(R.layout.fragment_post) {
    var reportAdapter: ReportAdapter? = null
    var filter_switch: Switch? = null
    var switchState = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            println(savedInstanceState.getInt(SWITCH_KEY))
            switchState = savedInstanceState.getInt(SWITCH_KEY)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (filter_switch!!.isChecked) {
            outState.putInt(SWITCH_KEY, 1)
        } else {
            outState.putInt(SWITCH_KEY, 0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db: AppDatabase = AppDatabase.getInstance(view.context)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        filter_switch = view.findViewById(R.id.filter_switch)
        val winRate = view.findViewById<TextView>(R.id.txt_winrate)
        if (switchState == 1) {
            filter_switch?.isChecked = true
        }

//        winRate.text = SharedFunctions.WinRate(
//            db,
//            "select count(b.id) as Winners, count(*) as TotalCount from REPORTS a left join REPORTS b on b.id = a.id and b.change > 0 WHERE a.list = 1 AND a.change NOT NULL"
//        ).toString() + "%"

        filter_switch?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
//                ReadData(db.PostInfo(true))
            } else {
//                ReadData(db.PostInfo(false))
            }

        })

    }

    companion object {
        private const val SWITCH_KEY = "switch"
    }
}