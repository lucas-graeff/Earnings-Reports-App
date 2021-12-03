package lucas.graeff.tradereports;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
    private ArrayList report_id, report_ticker, report_date, report_predictedMove, report_esp, report_sinceLast, report_zscore, report_momentum, report_vgm, report_time, report_surprise, report_change;

    public PostAdapter(Context context,
                         ArrayList report_id,
                         ArrayList report_ticker, ArrayList report_date,
                         ArrayList report_predictedMove,
                         ArrayList report_esp,
                         ArrayList report_sinceLast,
                         ArrayList report_zscore,
                         ArrayList report_momentum,
                         ArrayList report_vgm,
                         ArrayList report_time,
                         ArrayList report_surprise,
                         ArrayList report_change){

        this.context = context;
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
        this.report_surprise = report_surprise;
        this.report_change = report_change;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return report_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView ticker_txt, date_txt, predicted_move_txt, esp_txt, since_last_txt, zscore_txt, momentum_txt, vgm_txt, surprise_txt, change_txt;
        ImageView time_img;
        CardView card_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ticker_txt = itemView.findViewById(R.id.ticker_txt);
            date_txt = itemView.findViewById(R.id.date_txt);
            predicted_move_txt = itemView.findViewById(R.id.predicted_move_txt);
            esp_txt = itemView.findViewById(R.id.esp_txt);
            since_last_txt = itemView.findViewById(R.id.since_last_txt);
            zscore_txt = itemView.findViewById(R.id.zscore_txt);
            momentum_txt = itemView.findViewById(R.id.momentum_txt);
            vgm_txt = itemView.findViewById(R.id.vgm_txt);
            time_img = itemView.findViewById(R.id.time_img);
            surprise_txt = itemView.findViewById(R.id.surprise_txt);
            change_txt = itemView.findViewById(R.id.change_txt);
            card_view = itemView.findViewById(R.id.card_view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.ticker_txt.setText(String.valueOf(report_ticker.get(position)));
        holder.date_txt.setText(String.valueOf(report_date.get(position)).substring(5));
        holder.predicted_move_txt.setText(String.valueOf(report_predictedMove.get(position)).replace(".0", "") + "%");
        holder.esp_txt.setText((String.valueOf(report_esp.get(position))));
        holder.since_last_txt.setText(String.valueOf(report_sinceLast.get(position)));
        holder.zscore_txt.setText(String.valueOf(report_zscore.get(position)));
        holder.momentum_txt.setText(String.valueOf(report_momentum.get(position)));
        holder.vgm_txt.setText(String.valueOf(report_vgm.get(position)));
        if(Integer.valueOf(String.valueOf(report_time.get(position))) == 1) {
            holder.time_img.setImageResource(R.drawable.ic_baseline_nights_stay_24);
        }
        else {
            holder.time_img.setImageResource(R.drawable.ic_baseline_wb_sunny_24);
        }
        holder.surprise_txt.setText(String.valueOf(report_surprise.get(position)));
        holder.change_txt.setText(String.valueOf(report_change.get(position)));
        if(Double.valueOf((Double) report_change.get(position)) > 0) {
            holder.card_view.setBackgroundColor(Color.parseColor("#B5E6B7"));
        }
        else {
            holder.card_view.setBackgroundColor(Color.parseColor("#FFEABCBC"));
        }

    }
}
