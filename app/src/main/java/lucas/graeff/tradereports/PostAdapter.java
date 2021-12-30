package lucas.graeff.tradereports;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private Context context;
    private MyDatabaseHelper db;
    private ArrayList report_id, report_ticker, report_date, report_predictedMove, report_esp, report_sinceLast, report_zscore, report_momentum, report_vgm, report_time, report_list;
    private RecyclerViewClickListener listener;

    public PostAdapter(Context context,
                         RecyclerViewClickListener listener,
                         ArrayList report_id,
                         ArrayList report_ticker, ArrayList report_date,
                         ArrayList report_predictedMove,
                         ArrayList report_esp,
                         ArrayList report_sinceLast,
                         ArrayList report_zscore,
                         ArrayList report_momentum,
                         ArrayList report_vgm,
                         ArrayList report_time,
                         ArrayList report_list){

        this.context = context;
        this.listener = listener;

        this.report_id = report_id;
        this.report_ticker = report_ticker;
        this.report_date = report_date;
        this.report_predictedMove = report_predictedMove;
        this.report_esp = report_esp;
        this.report_sinceLast = report_sinceLast;
        this.report_zscore = report_zscore;
        this.report_momentum = report_momentum;
        this.report_vgm = report_vgm;
        this.report_time = report_time;
        this.report_list = report_list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)  {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return report_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        TextView ticker_txt, date_txt, recom_txt, est_esp_txt, volatility_txt, insider_txt, short_txt, perform_txt, peg_txt, since_last_txt,
                guidance_txt, guidance_est_txt, time_txt, quarter_first, quarter_second, quarter_third, quarter_fourth;
        ImageView time_img, first_change, second_change, third_change, fourth_change;
        CardView card_view;

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            ticker_txt = itemView.findViewById(R.id.ticker_txt);
            date_txt = itemView.findViewById(R.id.date_txt);
            recom_txt = itemView.findViewById(R.id.txt_recom);
            est_esp_txt = itemView.findViewById(R.id.txt_eps_est);
            volatility_txt = itemView.findViewById(R.id.txt_volatility);
            insider_txt = itemView.findViewById(R.id.txt_insider);
            short_txt = itemView.findViewById(R.id.txt_short);
            perform_txt = itemView.findViewById(R.id.txt_perform);
            peg_txt = itemView.findViewById(R.id.txt_peg);
            since_last_txt = itemView.findViewById(R.id.txt_since_last);
            guidance_txt = itemView.findViewById(R.id.txt_guidance);
            guidance_est_txt = itemView.findViewById(R.id.txt_guidance_est);
            time_txt = itemView.findViewById(R.id.txt_time);

            //Quarter stats
            quarter_first = itemView.findViewById(R.id.quarter_first);
            quarter_second = itemView.findViewById(R.id.quarter_second);
            quarter_third = itemView.findViewById(R.id.quarter_third);
            quarter_fourth = itemView.findViewById(R.id.quarter_fourth);
            first_change = itemView.findViewById(R.id.first_change);
            second_change = itemView.findViewById(R.id.second_change);
            third_change = itemView.findViewById(R.id.third_change);
            fourth_change = itemView.findViewById(R.id.fourth_change);

            time_txt = itemView.findViewById(R.id.txt_time);
            card_view = itemView.findViewById(R.id.card_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, Integer.parseInt(String.valueOf(report_id.get(getAdapterPosition()))));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //Listen for card click
        holder.card_view.setOnClickListener(view -> {
            int id = Integer.valueOf((Integer) report_id.get(position));
            db = new MyDatabaseHelper(context.getApplicationContext());

            if(Integer.valueOf((Integer) report_list.get(position)) == 1) {
                //Remove from list
                db.UpdateList(id, 0);
                report_list.set(position, 0);
                holder.card_view.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            else {
                //Add to list
                db.UpdateList(id, 1);
                report_list.set(position, 1);
                holder.card_view.setBackgroundColor(Color.parseColor("#FFA7D8FF"));
            }

            System.out.println("Card listener: " + id);
        });

        //Set background colors based off of list value
        if(Integer.valueOf((Integer) report_list.get(position)) == 1) {
            holder.card_view.setBackgroundColor(Color.parseColor("#FFA7D8FF"));
        }
        else {
            holder.card_view.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        holder.ticker_txt.setText(String.valueOf(report_ticker.get(position)));
        holder.date_txt.setText(String.valueOf(report_date.get(position)).substring(5));

        holder.since_last_txt.setText(String.valueOf(report_sinceLast.get(position)));

        if(Integer.valueOf(String.valueOf(report_time.get(position))) == 1) {
            holder.time_img.setImageResource(R.drawable.ic_baseline_nights_stay_24);
        }
        else {
            holder.time_img.setImageResource(R.drawable.ic_baseline_wb_sunny_24);
        }

    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int id);
    }
}
