package lucas.graeff.tradereports.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import lucas.graeff.tradereports.CustomAdapter;
import lucas.graeff.tradereports.MyDatabaseHelper;
import lucas.graeff.tradereports.PostAdapter;
import lucas.graeff.tradereports.R;

public class ListFragment extends Fragment {

    MyDatabaseHelper db;
    CustomAdapter customAdapter;
    CustomAdapter.RecyclerViewClickListener listener;

    ArrayList<String> report_ticker, report_vgm, report_momentum, report_date;
    ArrayList<Double> report_predictedMove, report_sinceLast, report_esp, report_surprise, report_change;
    ArrayList<Integer> report_id, report_zscore, report_time, report_list;

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
        customAdapter = new CustomAdapter(getActivity().getApplicationContext(), listener, report_id, report_ticker, report_date, report_predictedMove, report_esp, report_sinceLast, report_zscore, report_momentum, report_vgm, report_time, report_list);
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
}