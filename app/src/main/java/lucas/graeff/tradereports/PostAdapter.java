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
    private ArrayList report_id, report_ticker, report_date, recent_tickers, report_bell, report_volatility,
            report_recom,
            report_peg,
            report_predicted_eps,
            report_insider_trans,
            report_short,
            report_target_price,
            report_price,
            report_performace,
            report_first_eps,
            report_second_eps,
            report_third_eps,
            report_fourth_eps,
            report_fifth_eps,
            report_first_from,
            report_first_to,
            report_second_from,
            report_second_to,
            report_third_from,
            report_third_to,
            report_fourth_from,
            report_fourth_to,
            report_guidance_min,
            report_guidance_max,
            report_guidance_est,
            report_list, report_since_last , report_time, report_actual_eps, report_change;

    private RecyclerViewClickListener listener;

    public PostAdapter(Context context,
                         RecyclerViewClickListener listener,
                         ArrayList report_id,
                         ArrayList report_ticker,
                         ArrayList report_date,
                         ArrayList report_bell,
                         ArrayList report_volatility,
                         ArrayList report_recom,
                         ArrayList report_peg,
                         ArrayList report_predicted_eps,
                         ArrayList report_since_last,
                         ArrayList report_time,
                         ArrayList report_insider_trans,
                         ArrayList report_short,
                         ArrayList report_target_price,
                         ArrayList report_price,
                         ArrayList report_performace,
                         ArrayList report_first_eps,
                         ArrayList report_second_eps,
                         ArrayList report_third_eps,
                         ArrayList report_fourth_eps,
                         ArrayList report_fifth_eps,
                         ArrayList report_first_from,
                         ArrayList report_first_to,
                         ArrayList report_second_from,
                         ArrayList report_second_to,
                         ArrayList report_third_from,
                         ArrayList report_third_to,
                         ArrayList report_fourth_from,
                         ArrayList report_fourth_to,
                         ArrayList report_guidance_min,
                         ArrayList report_guidance_max,
                         ArrayList report_guidance_est,
                         ArrayList report_list,
                       ArrayList report_actual_eps,
                       ArrayList report_change){

        this.context = context;
        this.listener = listener;

        this.report_id = report_id;
        this.report_ticker = report_ticker;
        this.report_date = report_date;
        this.report_bell = report_bell;
        this.report_volatility = report_volatility;
        this.report_recom = report_recom;
        this.report_peg = report_peg;
        this.report_predicted_eps = report_predicted_eps;
        this.report_since_last = report_since_last;
        this.report_time = report_time;
        this.report_insider_trans = report_insider_trans;
        this.report_short = report_short;
        this.report_target_price = report_target_price;
        this.report_price = report_price;
        this.report_performace = report_performace;
        this.report_first_eps = report_first_eps;
        this.report_second_eps = report_second_eps;
        this.report_third_eps = report_third_eps;
        this.report_fourth_eps = report_fourth_eps;
        this.report_fifth_eps = report_fifth_eps;
        this.report_first_from = report_first_from;
        this.report_first_to = report_first_to;
        this.report_second_from = report_second_from;
        this.report_second_to = report_second_to;
        this.report_third_from = report_third_from;
        this.report_third_to = report_third_to;
        this.report_fourth_from = report_fourth_from;
        this.report_fourth_to = report_fourth_to;
        this.report_guidance_min = report_guidance_min;
        this.report_guidance_max = report_guidance_max;
        this.report_guidance_est = report_guidance_est;
        this.report_list = report_list;
        this.report_actual_eps = report_actual_eps;
        this.report_change = report_change;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)  {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return report_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        TextView ticker_txt, price_txt, date_txt, recom_txt, est_esp_txt, volatility_txt, insider_txt, short_txt, perform_txt, peg_txt, since_last_txt,
                guidance_txt, guidance_est_txt, time_txt, quarter_first, quarter_second, quarter_third, quarter_fourth, change_txt, actual_eps;
        ImageView bell_img, first_change, second_change, third_change, fourth_change;
        CardView card_view;

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            ticker_txt = itemView.findViewById(R.id.ticker_txt);
            price_txt = itemView.findViewById(R.id.txt_price);
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

            //Post
            change_txt = itemView.findViewById(R.id.txt_change);
            actual_eps = itemView.findViewById(R.id.txt_eps_actual);

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
            bell_img = itemView.findViewById(R.id.bell_img);
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

        //Left side
        holder.ticker_txt.setText(String.valueOf(report_ticker.get(position)));
        holder.date_txt.setText(String.valueOf(report_date.get(position)).substring(5));

        if(Integer.valueOf(String.valueOf(report_bell.get(position))) == 1) {
            holder.bell_img.setImageResource(R.drawable.ic_baseline_nights_stay_24);
        }
        else {
            holder.bell_img.setImageResource(R.drawable.ic_baseline_wb_sunny_24);
        }

        holder.time_txt.setText(String.valueOf(report_time.get(position)));
        holder.guidance_txt.setText(report_guidance_min.get(position) + " - " + report_guidance_max.get(position));
        holder.guidance_est_txt.setText(String.valueOf(report_guidance_est.get(position)));


        //Right side
        holder.recom_txt.setText(String.valueOf(report_recom.get(position)));
        holder.price_txt.setText(String.valueOf(report_price.get(position)));
        holder.est_esp_txt.setText(String.valueOf(report_predicted_eps.get(position)));
        holder.volatility_txt.setText(report_volatility.get(position) + "%");
        holder.insider_txt.setText(String.valueOf(report_insider_trans.get(position)));
        holder.short_txt.setText(String.valueOf(report_short.get(position)));
        holder.perform_txt.setText(String.valueOf(report_performace.get(position)));
        holder.peg_txt.setText(String.valueOf(report_peg.get(position)));
        holder.perform_txt.setText(String.valueOf(report_performace.get(position)));
        holder.since_last_txt.setText(String.valueOf(report_since_last.get(position)));

        //Post
        holder.change_txt.setText(String.valueOf(report_change.get(position)));
        holder.actual_eps.setText(String.valueOf(report_actual_eps.get(position)));

        //Change
        if(String.valueOf(report_performace.get(position)) != null && !String.valueOf(report_performace.get(position)).contains("-")) {

            try {
                Double firstFrom = ConvertToDouble(report_first_from.get(position));
                Double firstTo = ConvertToDouble(report_first_to.get(position));
                if(firstTo - firstFrom > 0){
                    holder.first_change.setImageResource(R.drawable.ic_baseline_arrow_green_up_24);
                } else {
                    holder.first_change.setImageResource(R.drawable.ic_baseline_arrow_red_down_24);
                }
                Double secondFrom = ConvertToDouble(report_second_from.get(position));
                Double secondTo = ConvertToDouble(report_second_to.get(position));
                if(secondTo - secondFrom > 0){
                    holder.second_change.setImageResource(R.drawable.ic_baseline_arrow_green_up_24);
                } else {
                    holder.second_change.setImageResource(R.drawable.ic_baseline_arrow_red_down_24);
                }
                Double thirdFrom = ConvertToDouble(report_third_from.get(position));
                Double thirdTo = ConvertToDouble(report_third_to.get(position));
                if(thirdTo - thirdFrom > 0){
                    holder.third_change.setImageResource(R.drawable.ic_baseline_arrow_green_up_24);
                } else {
                    holder.third_change.setImageResource(R.drawable.ic_baseline_arrow_red_down_24);
                }
                Double fourthFrom = ConvertToDouble(report_fourth_from.get(position));
                Double fourthTo = ConvertToDouble(report_fourth_to.get(position));
                if(fourthTo - fourthFrom > 0){
                    holder.fourth_change.setImageResource(R.drawable.ic_baseline_arrow_green_up_24);
                } else {
                    holder.fourth_change.setImageResource(R.drawable.ic_baseline_arrow_red_down_24);
                }
            } catch (Exception e) {

            }
        }


        //Quarters
        try {
            //First
            if(ConvertToDouble(report_first_eps.get(position)) > ConvertToDouble(report_second_eps.get(position))) {
                holder.quarter_first.setTextColor(Color.parseColor("#FF4CAF50"));
            }
            else {
                holder.quarter_first.setTextColor(Color.parseColor("#FFE91E63"));
            }
            //Second
            if(ConvertToDouble(report_second_eps.get(position)) > ConvertToDouble(report_third_eps.get(position))) {
                holder.quarter_second.setTextColor(Color.parseColor("#FF4CAF50"));
            }
            else {
                holder.quarter_second.setTextColor(Color.parseColor("#FFE91E63"));
            }
            //Third
            if(ConvertToDouble(report_third_eps.get(position)) > ConvertToDouble(report_fourth_eps.get(position))) {
                holder.quarter_third.setTextColor(Color.parseColor("#FF4CAF50"));
            }
            else {
                holder.quarter_third.setTextColor(Color.parseColor("#FFE91E63"));
            }
            //Fourth
            if(ConvertToDouble(report_fourth_eps.get(position)) > ConvertToDouble(report_fifth_eps.get(position))) {
                holder.quarter_fourth.setTextColor(Color.parseColor("#FF4CAF50"));
            }
            else {
                holder.quarter_fourth.setTextColor(Color.parseColor("#FFE91E63"));
            }
        } catch (Exception e) {

        }

        //Insider
        try {
            if(Double.parseDouble(report_insider_trans.get(position).toString().replace("%", "")) > -5) {
                holder.insider_txt.setTextColor(Color.parseColor("#FF4CAF50"));
            }
            else {
                holder.insider_txt.setTextColor(Color.parseColor("#FFE91E63"));
            }
        } catch (Exception e) {

        }

        //Recom
        try {
            if(Double.parseDouble(report_recom.get(position).toString()) > 3) {
                holder.recom_txt.setTextColor(Color.parseColor("#FFE91E63"));
            }
            else {
                holder.recom_txt.setTextColor(Color.parseColor("#FF4CAF50"));
            }
        } catch (Exception e) {

        }

        //Price
        try {
            if(Double.parseDouble(report_price.get(position).toString()) > Double.parseDouble(report_target_price.get(position).toString())) {
                holder.price_txt.setTextColor(Color.parseColor("#FFE91E63"));
            }
            else {
                holder.price_txt.setTextColor(Color.parseColor("#FF4CAF50"));
            }
        } catch (Exception e) {

        }

        //Short
        try {
            if(Double.parseDouble(report_short.get(position).toString().replace("%", "")) > 5) {
                holder.short_txt.setTextColor(Color.parseColor("#FFE91E63"));
            }
            else {
                holder.short_txt.setTextColor(Color.parseColor("#FF4CAF50"));
            }
        } catch (Exception e) {

        }

        //peg
        try {
            if(Double.parseDouble(report_peg.get(position).toString().replace("%", "")) > 1) {
                holder.peg_txt.setTextColor(Color.parseColor("#FFE91E63"));
            }
            else {
                holder.peg_txt.setTextColor(Color.parseColor("#FF4CAF50"));
            }
        } catch (Exception e) {

        }

        //Perform
        try {
            if(Double.parseDouble(report_performace.get(position).toString().replace("%", "")) > 0) {
                holder.perform_txt.setTextColor(Color.parseColor("#FF4CAF50"));
            }
            else {
                holder.perform_txt.setTextColor(Color.parseColor("#FFE91E63"));
            }
        } catch (Exception e) {

        }



    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int id);
    }

    public Double ConvertToDouble(Object object) {
        return Double.parseDouble(String.valueOf(object).replace("%", ""));
    }
}
