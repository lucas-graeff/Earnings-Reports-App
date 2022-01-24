package lucas.graeff.tradereports.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import lucas.graeff.tradereports.CustomAdapter;
import lucas.graeff.tradereports.MyDatabaseHelper;
import lucas.graeff.tradereports.PostAdapter;
import lucas.graeff.tradereports.R;
import lucas.graeff.tradereports.SharedFunctions;

public class ListFragment extends Fragment {

    MyDatabaseHelper db;
    CustomAdapter customAdapter;
    CustomAdapter.RecyclerViewClickListener listener;

    ArrayList<String> report_ticker, report_date, recent_tickers, report_bell, report_volatility, report_average,
            report_recom,
            report_peg,
            report_predicted_eps,
            report_insider_trans,
            report_short,
            report_target_price,
            report_time,
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
            report_guidance_est;
    ArrayList<Double> report_since_last;
    ArrayList<Integer> report_id, report_list;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        db = new MyDatabaseHelper(getActivity().getApplicationContext());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);


        ReadData((db.readListData()));
        Display(recyclerView);

        // Inflate the layout for this fragment
        return view;
    }

    public void Display(RecyclerView recyclerView) {
        //Display
        setOnClickListener();
        customAdapter = new CustomAdapter(getActivity().getApplicationContext(), listener, report_id, report_ticker, report_date, report_bell, report_volatility, report_average, report_recom, report_peg, report_predicted_eps, report_since_last, report_time, report_insider_trans, report_short, report_target_price, report_price, report_performace, report_first_eps, report_second_eps, report_third_eps, report_fourth_eps, report_fifth_eps, report_first_from, report_first_to, report_second_from, report_second_to, report_third_from, report_third_to, report_fourth_from, report_fourth_to, report_guidance_min, report_guidance_max, report_guidance_est, report_list);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    }

    private void setOnClickListener() {
        listener = new CustomAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int id) {
                System.out.println("Card listener: " + id);

            }
        };
    }

    public void ReadData(Cursor data) {
        //Store onto readable arrays
        report_id = new ArrayList<>();
        report_ticker = new ArrayList<>();
        report_date = new ArrayList<>();
        report_bell = new ArrayList<>();
        report_volatility = new ArrayList<>();
        report_average = new ArrayList<>();
        report_recom = new ArrayList<>();
        report_peg = new ArrayList<>();
        report_predicted_eps = new ArrayList<>();
        report_since_last = new ArrayList<>();
        report_time = new ArrayList<>();
        report_insider_trans = new ArrayList<>();
        report_short = new ArrayList<>();
        report_target_price = new ArrayList<>();
        report_price = new ArrayList<>();
        report_performace = new ArrayList<>();
        report_first_eps = new ArrayList<>();
        report_second_eps = new ArrayList<>();
        report_third_eps = new ArrayList<>();
        report_fourth_eps = new ArrayList<>();
        report_fifth_eps = new ArrayList<>();
        report_first_from = new ArrayList<>();
        report_first_to = new ArrayList<>();
        report_second_from = new ArrayList<>();
        report_second_to = new ArrayList<>();
        report_third_from = new ArrayList<>();
        report_third_to = new ArrayList<>();
        report_fourth_from = new ArrayList<>();
        report_fourth_to = new ArrayList<>();
        report_guidance_min = new ArrayList<>();
        report_guidance_max = new ArrayList<>();
        report_guidance_est = new ArrayList<>();
        report_list = new ArrayList<>();


        Cursor cursor = data;
        while (cursor.moveToNext()) {
            report_id.add(cursor.getInt(0));
            report_ticker.add(cursor.getString(1));
            report_date.add(cursor.getString(2));
            report_bell.add(cursor.getString(3));
            report_volatility.add(cursor.getString(4));
            report_average.add(cursor.getString(5));
            report_recom.add(cursor.getString(6));
            report_peg.add(cursor.getString(7));
            report_predicted_eps.add(cursor.getString(8));
            report_since_last.add(cursor.getDouble(9));
            report_time.add(cursor.getString(10));
            report_insider_trans.add(cursor.getString(11));
            report_short.add(cursor.getString(12));
            report_target_price.add(cursor.getString(13));
            report_price.add(cursor.getString(14));
            report_performace.add(cursor.getString(15));
            report_first_eps.add(cursor.getString(16));
            report_second_eps.add(cursor.getString(17));
            report_third_eps.add(cursor.getString(18));
            report_fourth_eps.add(cursor.getString(19));
            report_fifth_eps.add(cursor.getString(20));
            report_first_from.add(cursor.getString(21));
            report_first_to.add(cursor.getString(22));
            report_second_from.add(cursor.getString(23));
            report_second_to.add(cursor.getString(24));
            report_third_from.add(cursor.getString(25));
            report_third_to.add(cursor.getString(26));
            report_fourth_from.add(cursor.getString(27));
            report_fourth_to.add(cursor.getString(28));
            report_guidance_min.add(cursor.getString(29));
            report_guidance_max.add(cursor.getString(30));
            report_guidance_est.add(cursor.getString(31));
            report_list.add(cursor.getInt(37));

        }
    }


}