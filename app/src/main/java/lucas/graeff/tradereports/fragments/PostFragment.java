package lucas.graeff.tradereports.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.HashMap;

import lucas.graeff.tradereports.CustomAdapter;
import lucas.graeff.tradereports.MyDatabaseHelper;
import lucas.graeff.tradereports.PostAdapter;
import lucas.graeff.tradereports.R;
import lucas.graeff.tradereports.webscraping.PostEarnings;
import lucas.graeff.tradereports.webscraping.WebInfo;


public class PostFragment extends Fragment {
    MyDatabaseHelper db;
    ArrayList<String> report_ticker, report_vgm, report_momentum, report_date;
    ArrayList<Double> report_predictedMove, report_sinceLast, report_esp, report_surprise, report_change;
    ArrayList<Integer> report_id, report_zscore, report_time;

    PostAdapter postAdapter;
    Switch filter_switch;
    int switchState;

    private static final String SWITCH_KEY = "switch";

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            System.out.println(savedInstanceState.getInt(SWITCH_KEY));
            switchState = savedInstanceState.getInt(SWITCH_KEY);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        if(filter_switch.isChecked()) {
            outState.putInt(SWITCH_KEY, 1);
        }
        else {
            outState.putInt(SWITCH_KEY, 0);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        db = new MyDatabaseHelper(getActivity().getApplicationContext());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        filter_switch = view.findViewById(R.id.filter_switch);

        if(switchState == 1){
            filter_switch.setChecked(true);
        }

        ReadData(db.PostInfo(false));
        Display(recyclerView);

        filter_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ReadData(db.PostInfo(true));
            } else {
                ReadData(db.PostInfo(false));
            }
            Display(recyclerView);
        });

        // Inflate the layout for this fragment
        return view;
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
        report_surprise = new ArrayList<>();
        report_change = new ArrayList<>();

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
            report_surprise.add(cursor.getDouble(10));
            report_change.add(cursor.getDouble(11));
        }
    }

    public void Display(RecyclerView recyclerView) {
        //Display
        postAdapter = new PostAdapter(getActivity().getApplicationContext(), report_id, report_ticker, report_date, report_predictedMove, report_esp, report_sinceLast, report_zscore, report_momentum, report_vgm, report_time, report_surprise, report_change);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    }
}
