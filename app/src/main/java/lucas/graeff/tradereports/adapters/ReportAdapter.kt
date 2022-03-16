package lucas.graeff.tradereports.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import lucas.graeff.tradereports.R
import lucas.graeff.tradereports.room.AppDatabase
import lucas.graeff.tradereports.room.Report
import kotlin.concurrent.thread

class ReportAdapter(
    private val context: Context,
    private val db: AppDatabase,
    private var reports: List<Report>
) : RecyclerView.Adapter<ReportAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.post_row, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reports.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ticker_txt: TextView
        var price_txt: TextView
        var date_txt: TextView
        var recom_txt: TextView
        var est_esp_txt: TextView
        var volatility_txt: TextView
        var average_txt: TextView
        var insider_txt: TextView
        var short_txt: TextView
        var perform_txt: TextView
        var peg_txt: TextView
        var since_last_txt: TextView
        var guidance_txt: TextView
        var guidance_est_txt: TextView
        var time_txt: TextView
        var quarter_first: TextView
        var quarter_second: TextView
        var quarter_third: TextView
        var quarter_fourth: TextView
        var first_change: TextView
        var second_change: TextView
        var third_change: TextView
        var fourth_change: TextView
        var change_txt: TextView
        var actual_eps: TextView
        var bell_img: ImageView
        var card_view: CardView

        init {
            ticker_txt = itemView.findViewById(R.id.ticker_txt)
            price_txt = itemView.findViewById(R.id.txt_price)
            date_txt = itemView.findViewById(R.id.date_txt)
            recom_txt = itemView.findViewById(R.id.txt_recom)
            est_esp_txt = itemView.findViewById(R.id.txt_eps_est)
            volatility_txt = itemView.findViewById(R.id.txt_volatility)
            average_txt = itemView.findViewById(R.id.txt_average)
            insider_txt = itemView.findViewById(R.id.txt_insider)
            short_txt = itemView.findViewById(R.id.txt_short)
            perform_txt = itemView.findViewById(R.id.txt_perform)
            peg_txt = itemView.findViewById(R.id.txt_peg)
            since_last_txt = itemView.findViewById(R.id.txt_since_last)
            guidance_txt = itemView.findViewById(R.id.txt_guidance)
            guidance_est_txt = itemView.findViewById(R.id.txt_guidance_est)
            time_txt = itemView.findViewById(R.id.txt_time)


            //Quarter stats
            quarter_first = itemView.findViewById(R.id.quarter_first)
            quarter_second = itemView.findViewById(R.id.quarter_second)
            quarter_third = itemView.findViewById(R.id.quarter_third)
            quarter_fourth = itemView.findViewById(R.id.quarter_fourth)
            first_change = itemView.findViewById(R.id.txt_change_first)
            second_change = itemView.findViewById(R.id.txt_change_second)
            third_change = itemView.findViewById(R.id.txt_change_third)
            fourth_change = itemView.findViewById(R.id.txt_change_fourth)

            //Post
            change_txt = itemView.findViewById(R.id.txt_change)
            actual_eps = itemView.findViewById(R.id.txt_actual_eps)
            time_txt = itemView.findViewById(R.id.txt_time)
            bell_img = itemView.findViewById(R.id.bell_img)
            card_view = itemView.findViewById(R.id.card_view)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        //Listen for card click
        holder.card_view.setOnClickListener {
            thread {
                val id = reports[position].id
                if (reports[position].list == 1) {
                    //Remove from list
                    db.reportDao().setList(0, id)
                    reports[position].list = 0
                    holder.card_view.setBackgroundColor(Color.parseColor("#FFFFFF"))
                } else {
                    //Add to list
                    db.reportDao().setList(1, id)
                    reports[position].list = 1
                    holder.card_view.setBackgroundColor(Color.parseColor("#FFA7D8FF"))
                }
                println("Card listener: $id")
            }

        }

        //Set background colors based off of list value
        if (reports[position].list == 1) {
            holder.card_view.setBackgroundColor(Color.parseColor("#FFA7D8FF"))
        } else {
            holder.card_view.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        //Header
        holder.ticker_txt.text = reports[position].ticker
        holder.date_txt.text = reports[position].date.toString()
        if (reports[position].bell == 1) {
            holder.bell_img.setImageResource(R.drawable.ic_baseline_nights_stay_24)
        } else {
            holder.bell_img.setImageResource(R.drawable.ic_baseline_wb_sunny_24)
        }
        holder.time_txt.text = reports[position].time
        holder.guidance_txt.text =
            "${reports[position].guidanceMin} - ${reports[position].guidanceMax}"
        holder.guidance_est_txt.text = reports[position].guidanceEstimate.toString()


        //Body
        holder.recom_txt.text = reports[position].recommended.toString()
        holder.price_txt.text = reports[position].price.toString()
        holder.est_esp_txt.text = reports[position].predictedEps.toString()
        holder.volatility_txt.text = "${String.format("%.2f", reports[position].volatility)}%"
        holder.average_txt.text = String.format("%.2f", reports[position].average)
        holder.insider_txt.text = reports[position].insiderTrans.toString()
        holder.short_txt.text = reports[position].shortFloat.toString()
        holder.peg_txt.text = reports[position].peg.toString()
        holder.perform_txt.text = "${reports[position].performanceWeek}%"
        holder.since_last_txt.text = "${reports[position].sinceLast}%"


        //Post
        holder.change_txt.text = "${reports[position].resultChange}%"
        holder.actual_eps.text = "${reports[position].resultEps}%"

        //Footer
        holder.quarter_first.text = reports[position].epsFirst.toString()
        holder.quarter_second.text = reports[position].epsSecond.toString()
        holder.quarter_third.text = reports[position].epsThird.toString()
        holder.quarter_fourth.text = reports[position].epsFourth.toString()


        //Change
        try {
            holder.first_change.text = "${String.format("%.2f", reports[position].quarterPerformanceFirst)}%"
            if (reports[position].quarterPerformanceFirst > 0) {
                holder.first_change.setTextColor(Color.parseColor("#FF4CAF50"))
            } else {
                holder.first_change.setTextColor(Color.parseColor("#FFE91E63"))
            }

            holder.second_change.text = "${String.format("%.2f", reports[position].quarterPerformanceSecond)}%"
            if (reports[position].quarterPerformanceSecond > 0) {
                holder.second_change.setTextColor(Color.parseColor("#FF4CAF50"))
            } else {
                holder.second_change.setTextColor(Color.parseColor("#FFE91E63"))
            }

            holder.third_change.text = "${String.format("%.2f", reports[position].quarterPerformanceThird)}%"
            if (reports[position].quarterPerformanceThird > 0) {
                holder.third_change.setTextColor(Color.parseColor("#FF4CAF50"))
            } else {
                holder.third_change.setTextColor(Color.parseColor("#FFE91E63"))
            }

            holder.fourth_change.text = "${String.format("%.2f", reports[position].quarterPerformanceFourth)}%"
            if (reports[position].quarterPerformanceFourth > 0) {
                holder.fourth_change.setTextColor(Color.parseColor("#FF4CAF50"))
            } else {
                holder.fourth_change.setTextColor(Color.parseColor("#FFE91E63"))
            }
        } catch (e: Exception) {
            println(reports[position].ticker + " change arrows: " + e)
        }


        //Quarters
        try {
            //First
            if (reports[position].epsFirst > reports[position].epsSecond) {
                holder.quarter_first.setTextColor(Color.parseColor("#FF4CAF50"))
            } else {
                holder.quarter_first.setTextColor(Color.parseColor("#FFE91E63"))
            }
            //Second
            if (reports[position].epsSecond > reports[position].epsThird) {
                holder.quarter_second.setTextColor(Color.parseColor("#FF4CAF50"))
            } else {
                holder.quarter_second.setTextColor(Color.parseColor("#FFE91E63"))
            }
            //Third
            if (reports[position].epsThird > reports[position].epsFourth) {
                holder.quarter_third.setTextColor(Color.parseColor("#FF4CAF50"))
            } else {
                holder.quarter_third.setTextColor(Color.parseColor("#FFE91E63"))
            }
            //Fourth
            if (reports[position].epsFourth > reports[position].epsFifth) {
                holder.quarter_fourth.setTextColor(Color.parseColor("#FF4CAF50"))
            } else {
                holder.quarter_fourth.setTextColor(Color.parseColor("#FFE91E63"))
            }
        } catch (e: Exception) {
            holder.quarter_first.setTextColor(Color.parseColor("#000000"))
            holder.quarter_second.setTextColor(Color.parseColor("#000000"))
            holder.quarter_third.setTextColor(Color.parseColor("#000000"))
            holder.quarter_fourth.setTextColor(Color.parseColor("#000000"))
        }

        //Post stats
        if(reports[position].resultChange == 0.0 || reports[position].resultEps == 0.0) {
            holder.actual_eps.visibility = View.GONE
            holder.change_txt.visibility = View.GONE
            // TODO: Hide labels
        }


        //Insider
        try {
            if (reports[position].insiderTrans > -5) {
                holder.insider_txt.setTextColor(Color.parseColor("#FF4CAF50"))
            } else {
                holder.insider_txt.setTextColor(Color.parseColor("#FFE91E63"))
            }
        } catch (e: Exception) {
        }

        //Recom
        try {
            if (reports[position].recommended > 3) {
                holder.recom_txt.setTextColor(Color.parseColor("#FFE91E63"))
            } else {
                holder.recom_txt.setTextColor(Color.parseColor("#FF4CAF50"))
            }
        } catch (e: Exception) {
        }

        //Price
        try {
            if (reports[position].price > reports[position].targetPrice
            ) {
                holder.price_txt.setTextColor(Color.parseColor("#FFE91E63"))
            } else {
                holder.price_txt.setTextColor(Color.parseColor("#FF4CAF50"))
            }
        } catch (e: Exception) {
        }

        //Average
        try {
            if (reports[position].average > 0) {
                holder.average_txt.setTextColor(Color.parseColor("#FF4CAF50"))
            } else {
                holder.average_txt.setTextColor(Color.parseColor("#FFE91E63"))
            }
        } catch (e: Exception) {
        }

        //Short
        try {
            if (reports[position].shortFloat > 5) {
                holder.short_txt.setTextColor(Color.parseColor("#FFE91E63"))
            } else {
                holder.short_txt.setTextColor(Color.parseColor("#FF4CAF50"))
            }
        } catch (e: Exception) {
        }

        //peg
        try {
            if (reports[position].peg > 1) {
                holder.peg_txt.setTextColor(Color.parseColor("#FFE91E63"))
            } else {
                holder.peg_txt.setTextColor(Color.parseColor("#FF4CAF50"))
            }
        } catch (e: Exception) {
        }

        //Perform
        try {
            if (reports[position].performanceWeek > 0) {
                holder.perform_txt.setTextColor(Color.parseColor("#FF4CAF50"))
            } else {
                holder.perform_txt.setTextColor(Color.parseColor("#FFE91E63"))
            }
        } catch (e: Exception) {
        }

        //Change
        try {
            if (reports[position].resultChange > 0) {
                holder.change_txt.setTextColor(Color.parseColor("#FF4CAF50"))
            } else {
                holder.change_txt.setTextColor(Color.parseColor("#FFE91E63"))
            }
        } catch (e: Exception) {
        }

        //Actual EPS
        try {
            if (reports[position].resultEps > 0) {
                holder.actual_eps.setTextColor(Color.parseColor("#FF4CAF50"))
            } else {
                holder.actual_eps.setTextColor(Color.parseColor("#FFE91E63"))
            }
        } catch (e: Exception) {
        }

    }
}