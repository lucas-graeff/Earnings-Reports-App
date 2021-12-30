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

import lucas.graeff.tradereports.CustomAdapter;
import lucas.graeff.tradereports.MyDatabaseHelper;
import lucas.graeff.tradereports.R;
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
            retrieveWebInfo();

            prefs.edit().putBoolean("firstrun", false).commit();
        }

        ReadData(db.readAllData());
        Display(recyclerView);

        //Create dropdown menu
        String[] dropdownOptions = { "No filter", "Minimum Risk", "Medium Risk", "Shorting" };
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
                        query = "SELECT * FROM reports\n" +
                                "WHERE z_score < 3\n" +
                                "AND esp >= 0" +
                                " AND reports.date > date('now', '-1 day')" +
                                " AND predicted_move > 1" +
                                " AND (reports.vgm = 'A' OR reports.vgm = 'B')";
                        ReadData(db.readQuery(query));
                        break;
                    case 2:
                        ReadData(db.readFilteredData());
                        break;
                    case 3:
                        query = "SELECT * FROM reports\n" +
                                "WHERE z_score > 3\n" +
                                "AND esp <= 0" +
                                " AND reports.date > date('now', '-1 day')" +
                                " AND predicted_move > 1";
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
                String tempDate = "";
                String tempDateArray[] = tempArray.get(4).toString().replace("*BMO", "").replace("*AMC", "").split("/");
                tempDate += "20" + tempDateArray[2] + "-";
                if(tempDateArray[0].length() == 1) {
                    tempDate += "0";
                }
                tempDate += tempDateArray[0] + "-";
                if(tempDateArray[1].length() == 1) {
                    tempDate += "0";
                }
                tempDate += tempDateArray[1];

                date.add(tempDate);


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

//        for (int i = 0; i < ticker.size(); i++) {
//            if (!recent_tickers.contains(ticker.get(i)) && !inaccurate.contains(i)) {
//                db.addReport((String) ticker.get(i), (String) date.get(i), (Double) predicted.get(i), (Double) esp.get(i), (Integer) zscore.get(i), (String) momentum.get(i), (String) vgm.get(i), (Double) sinceLast.get(i), (Integer) time.get(i));
//            }
//        }

    }
}