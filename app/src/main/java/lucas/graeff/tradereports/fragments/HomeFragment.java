package lucas.graeff.tradereports.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.HashMap;

import lucas.graeff.tradereports.AlarmReceiver;
import lucas.graeff.tradereports.CustomAdapter;
import lucas.graeff.tradereports.MyDatabaseHelper;
import lucas.graeff.tradereports.R;
import lucas.graeff.tradereports.webscraping.CollectData;
import lucas.graeff.tradereports.webscraping.PostAnalysis;
import lucas.graeff.tradereports.webscraping.WebInfo;
import lucas.graeff.tradereports.webscraping.WebInfoZacks;


public class HomeFragment extends Fragment {

    MyDatabaseHelper db;
    ArrayList<String> report_ticker;
    ArrayList<String> report_date;
    ArrayList<String> recent_tickers;
    ArrayList<String> report_bell;
    ArrayList<String> report_volatility;
    ArrayList<String> report_recom;
    ArrayList<String> report_peg;
    ArrayList<String> report_predicted_eps;
    ArrayList<String> report_insider_trans;
    ArrayList<String> report_short;
    ArrayList<String> report_target_price;
    ArrayList<String> report_time;
    ArrayList<String> report_price;
    ArrayList<String> report_performace;
    ArrayList<String> report_first_eps;
    ArrayList<String> report_second_eps;
    ArrayList<String> report_third_eps;
    ArrayList<String> report_fourth_eps;
    ArrayList<String> report_fifth_eps;
    ArrayList<String> report_first_from;
    ArrayList<String> report_first_to;
    ArrayList<String> report_second_from;
    ArrayList<String> report_second_to;
    ArrayList<String> report_third_from;
    ArrayList<String> report_third_to;
    ArrayList<String> report_fourth_from;
    ArrayList<String> report_fourth_to;
    ArrayList<String> report_guidance_min;
    ArrayList<String> report_guidance_max;
    ArrayList<String> report_guidance_est;
    ArrayList<Double> report_since_last;
    ArrayList<Integer> report_id, report_list;
    CustomAdapter customAdapter;
    CustomAdapter.RecyclerViewClickListener listener;
    SharedPreferences prefs = null;


    private Object[] rows;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new MyDatabaseHelper(getActivity().getApplicationContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        Switch filter_switch = view.findViewById(R.id.filter_switch);
        db = new MyDatabaseHelper(getActivity().getApplicationContext());


        //First app use
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (prefs.getBoolean("firstrun", true)) {
            CollectData collectData = new CollectData(getActivity().getApplicationContext());
            Thread thread = new Thread(collectData);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            PostAnalysis postAnalysis = new PostAnalysis(getActivity().getApplicationContext());
            thread = new Thread(postAnalysis);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            prefs.edit().putBoolean("firstrun", false).commit();
        }

        ReadData(db.readAllData());
        Display(recyclerView);

        //Create dropdown menu
        String[] dropdownOptions = { "No filter", "Strict", "Guidance", "Raising EPS", "Positive Change"};
        Spinner dropdown = (Spinner) view.findViewById(R.id.dropdown_menu);
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dropdownOptions);
        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(dropdownAdapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String query;
                switch(position) {
                    case 0:
                        ReadData(db.readAllData());
                        break;
                    case 1:
                        query = "SELECT * FROM reports WHERE reports.date > date('now', '-1 day') AND recom < 2.5 AND perf_week > 0 AND predicted_eps > 0  AND peg < 1 ORDER BY date ASC";
                        ReadData(db.readQuery(query));
                        break;
                    case 2:
                        query = "SELECT * FROM reports WHERE reports.date > date('now', '-1 day') AND guidance_est > guidance_min ORDER BY date ASC";
                        ReadData(db.readQuery(query));
                        break;
                    case 3:
                        query = "SELECT * FROM reports WHERE reports.date > date('now', '-1 day') AND first_eps > second_eps AND second_eps > third_eps AND third_eps > fourth_eps AND fourth_eps > fifth_eps ORDER BY date ASC";
                        ReadData(db.readQuery(query));
                    case 4:
                        query = "SELECT * FROM reports WHERE reports.date > date('now', '-1 day') AND first_from > first_to AND second_to > second_from AND third_to > third_from AND fourth_to > fourth_from ORDER BY date ASC";
                        ReadData(db.readQuery(query));
                }
                Display(recyclerView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("onNothing");
            }
        });

        // Inflate the layout for this fragment
        return view;
    }


    public void Display(RecyclerView recyclerView) {
        //Display
        setOnClickListener();
        customAdapter = new CustomAdapter(getActivity().getApplicationContext(), listener, report_id, report_ticker, report_date, report_bell, report_volatility, report_recom, report_peg, report_predicted_eps, report_since_last, report_time, report_insider_trans, report_short, report_target_price, report_price, report_performace, report_first_eps, report_second_eps, report_third_eps, report_fourth_eps, report_fifth_eps, report_first_from, report_first_to, report_second_from, report_second_to, report_third_from, report_third_to, report_fourth_from, report_fourth_to, report_guidance_min, report_guidance_max, report_guidance_est, report_list);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    }

    // TODO: Remove
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
            report_recom.add(cursor.getString(5));
            report_peg.add(cursor.getString(6));
            report_predicted_eps.add(cursor.getString(7));
            report_since_last.add(cursor.getDouble(8));
            report_time.add(cursor.getString(9));
            report_insider_trans.add(cursor.getString(10));
            report_short.add(cursor.getString(11));
            report_target_price.add(cursor.getString(12));
            report_price.add(cursor.getString(13));
            report_performace.add(cursor.getString(14));
            report_first_eps.add(cursor.getString(15));
            report_second_eps.add(cursor.getString(16));
            report_third_eps.add(cursor.getString(17));
            report_fourth_eps.add(cursor.getString(18));
            report_fifth_eps.add(cursor.getString(19));
            report_first_from.add(cursor.getString(20));
            report_first_to.add(cursor.getString(21));
            report_second_from.add(cursor.getString(22));
            report_second_to.add(cursor.getString(23));
            report_third_from.add(cursor.getString(24));
            report_third_to.add(cursor.getString(25));
            report_fourth_from.add(cursor.getString(26));
            report_fourth_to.add(cursor.getString(27));
            report_guidance_min.add(cursor.getString(28));
            report_guidance_max.add(cursor.getString(29));
            report_guidance_est.add(cursor.getString(30));
            report_list.add(cursor.getInt(36));

        }
    }

    public void duplicateCheck() {
        Cursor cursor = db.getRecentTickers();
        while (cursor.moveToNext()) {
            recent_tickers.add(cursor.getString(0));
        }
    }

}