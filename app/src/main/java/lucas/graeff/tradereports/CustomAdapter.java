package lucas.graeff.tradereports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList report_id, report_ticker, report_date, report_predictedMove, report_esp, report_sinceLast, report_zscore, report_momentum, report_vgm;

    public CustomAdapter(Context context,
                  ArrayList report_id,
                  ArrayList report_ticker, ArrayList report_date,
                  ArrayList report_predictedMove,
                  ArrayList report_esp,
                  ArrayList report_sinceLast,
                  ArrayList report_zscore,
                  ArrayList report_momentum,
                  ArrayList report_vgm){

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
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_row, parent, false);
        return new MyViewHolder(view);
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
    }

    @Override
    public int getItemCount() {
        return report_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView ticker_txt, date_txt, predicted_move_txt, esp_txt, since_last_txt, zscore_txt, momentum_txt, vgm_txt;

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
        }
    }
}
