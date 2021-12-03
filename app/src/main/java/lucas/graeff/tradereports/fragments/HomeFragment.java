package lucas.graeff.tradereports.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.HashMap;

import lucas.graeff.tradereports.CustomAdapter;
import lucas.graeff.tradereports.MainActivity;
import lucas.graeff.tradereports.MyDatabaseHelper;
import lucas.graeff.tradereports.R;
import lucas.graeff.tradereports.webscraping.WebInfo;
import lucas.graeff.tradereports.webscraping.WebInfoZacks;


public class HomeFragment extends Fragment {

    MyDatabaseHelper db;
    ArrayList<String> report_ticker, report_vgm, report_momentum, report_date, recent_tickers;
    ArrayList<Double> report_predictedMove, report_sinceLast, report_esp;
    ArrayList<Integer> report_id, report_zscore, report_time, report_list;
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

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (prefs.getBoolean("firstrun", true)) {
            retrieveWebInfo();

            prefs.edit().putBoolean("firstrun", false).commit();
        }

//        retrieveWebInfo();
        ReadData(db.readAllData());
        Display(recyclerView);



        filter_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ReadData(db.readFilteredData());
            } else {
                ReadData(db.readAllData());
            }
            Display(recyclerView);
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void Display(RecyclerView recyclerView) {
        //Display
        setOnClickListener();
        customAdapter = new CustomAdapter(getActivity().getApplicationContext(), listener, report_id, report_ticker, report_date, report_predictedMove, report_esp, report_sinceLast, report_zscore, report_momentum, report_vgm, report_time, report_list);
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
        report_predictedMove = new ArrayList<>();
        report_esp = new ArrayList<>();
        report_sinceLast = new ArrayList<>();
        report_zscore = new ArrayList<>();
        report_momentum = new ArrayList<>();
        report_vgm = new ArrayList<>();
        report_time = new ArrayList<>();
        report_list = new ArrayList<>();

        Cursor cursor = data;
        while (cursor.moveToNext()) {
            report_id.add(cursor.getInt(0));
            report_ticker.add(cursor.getString(1));
            report_date.add(cursor.getString(2));
            report_predictedMove.add(cursor.getDouble(3));
            report_esp.add(cursor.getDouble(4));
            report_zscore.add(cursor.getInt(5));
            report_momentum.add(cursor.getString(6));
            report_vgm.add(cursor.getString(7));
            report_sinceLast.add(cursor.getDouble(8));
            report_time.add(cursor.getInt(9));
            report_list.add(cursor.getInt(12));
        }
    }

    public void duplicateCheck() {
        Cursor cursor = db.getRecentTickers();
        while (cursor.moveToNext()) {
            recent_tickers.add(cursor.getString(0));
        }
    }


    public void retrieveWebInfo() {
        HashMap<Integer, ArrayList<String>> colInfo;
        ArrayList tempArray;

        //Start web scrape of Upcoming
        WebInfo webInfo = new WebInfo();
        Thread thread = new Thread(webInfo);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Initialize arrays for storage
        ArrayList ticker = new ArrayList<String>();
        ArrayList predicted = new ArrayList<Double>();
        ArrayList sinceLast = new ArrayList<Double>();
        ArrayList zscore = new ArrayList<Integer>();
        ArrayList momentum = new ArrayList<String>();
        ArrayList vgm = new ArrayList<String>();
        ArrayList esp = new ArrayList<Double>();
        ArrayList date = new ArrayList<>();
        ArrayList time = new ArrayList<>();

        //Get stocksearning.com info
        colInfo = webInfo.getValue();

        //Parse and prepare
        for (int i = 1; i < colInfo.size() + 1; i++) {
            tempArray = colInfo.get(i);
            ticker.add(tempArray.get(0).toString().substring(0, 4).replace("-", ""));
            predicted.add(Double.parseDouble((tempArray.get(2).toString().replace("%", ""))));
            sinceLast.add(Double.parseDouble((tempArray.get(3).toString().replace("%", ""))));
        }

        //Web scrape Zacks.com
        WebInfoZacks webInfoZacks = new WebInfoZacks(ticker);
        Thread threadZacks = new Thread(webInfoZacks);
        threadZacks.start();
        try {
            threadZacks.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Assign scraped info
        colInfo = webInfoZacks.getValue();

        //Parse and prepare scraped info for database insertion

        //For marking inaccurate stocks
        ArrayList inaccurate = new ArrayList<>();
        boolean marked = false;

        for (int i = 0; i < colInfo.size(); i++) {
            marked = false;
            tempArray = colInfo.get(i);

            try {
                zscore.add(Integer.parseInt(tempArray.get(0).toString()));
            } catch (Exception e) {
                zscore.add(6);
                marked = true;
            }

            try {
                momentum.add(tempArray.get(1).toString());
            } catch (Exception e) {
                momentum.add("-");
                marked = true;
            }

            try {
                vgm.add(tempArray.get(2).toString());
            } catch (Exception e) {
                vgm.add("-");
                marked = true;
            }

            try {
                if (tempArray.get(3).toString().contains("NA")) {
                    esp.add(0.00);
                } else {
                    esp.add(Double.parseDouble(tempArray.get(3).toString().replace("%", "")));
                }
            } catch (Exception e) {
                esp.add(0.00);
                marked = true;
            }

            try {
                if (tempArray.get(4).toString().contains("AMC")) {
                    time.add(1);
                } else {
                    time.add(0);
                }
            } catch (Exception e) {
                time.add(0);
                marked = true;
            }

            try {
                String tempDate = tempArray.get(4).toString().replace("*BMO", "").replace("*AMC", "");
                if(tempDate.length() == 8) {
                    date.add(String.format("20%1$s-%2$s", tempDate.substring(6), tempDate.substring(0, 5).replace("/", "-")));
                }
                else {
                    date.add(String.format("20%1$s-%2$s", tempDate.substring(5), tempDate.substring(0, 4).replace("/", "-")));
                }

            } catch (Exception e) {
                date.add("NANANA");
                marked = true;
            }

            if(marked) {
                inaccurate.add(i);
            }

        }

        recent_tickers = new ArrayList<>();
        duplicateCheck();

        for (int i = 0; i < ticker.size(); i++) {
            if (!recent_tickers.contains(ticker.get(i)) && !inaccurate.contains(i)) {
                db.addReport((String) ticker.get(i), (String) date.get(i), (Double) predicted.get(i), (Double) esp.get(i), (Integer) zscore.get(i), (String) momentum.get(i), (String) vgm.get(i), (Double) sinceLast.get(i), (Integer) time.get(i));
            }
        }

    }
}