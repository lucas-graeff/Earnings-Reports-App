package lucas.graeff.tradereports;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    MyDatabaseHelper db;
    ArrayList<String> report_ticker, report_vgm, report_momentum, report_date, recent_tickers;
    ArrayList<Double> report_predictedMove, report_sinceLast, report_esp;
    ArrayList<Integer> report_id, report_zscore;
    CustomAdapter customAdapter;

    private Object[] rows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        db = new MyDatabaseHelper(this);

        retrieveWebInfo();

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



        readData();

        //Display
        customAdapter = new CustomAdapter(MainActivity.this, report_id, report_ticker, report_date, report_predictedMove, report_esp, report_sinceLast, report_zscore, report_momentum, report_vgm);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

    }

    public void readData() {
        Cursor cursor = db.readAllData();
        while(cursor.moveToNext()) {
            report_id.add(cursor.getInt(0));
            report_ticker.add(cursor.getString(1));
            report_date.add(cursor.getString(2));
            report_predictedMove.add(cursor.getDouble(3));
            report_esp.add(cursor.getDouble(4));
            report_zscore.add(cursor.getInt(5));
            report_momentum.add(cursor.getString(6));
            report_vgm.add(cursor.getString(7));
            report_sinceLast.add(cursor.getDouble(8));
        }
    }

    public void duplicateCheck() {
        Cursor cursor = db.getRecentTickers();
        while(cursor.moveToNext()) {
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


        Log.d("Truffles", webInfo.getValue().toString());

        ArrayList ticker = new ArrayList<String>();
        ArrayList predicted = new ArrayList<Double>();
        ArrayList sinceLast = new ArrayList<Double>();
        ArrayList zscore = new ArrayList<Integer>();
        ArrayList momentum = new ArrayList<String>();
        ArrayList vgm = new ArrayList<String>();
        ArrayList esp = new ArrayList<Double>();
        ArrayList date = new ArrayList<>();

        colInfo = webInfo.getValue();

        for(int i = 1; i < colInfo.size(); i++) {
            tempArray = colInfo.get(i);
            ticker.add(tempArray.get(0).toString().substring(0, 4).replace("-", ""));
            predicted.add(Double.parseDouble((tempArray.get(2).toString().replace("%", ""))));
            sinceLast.add(Double.parseDouble((tempArray.get(3).toString().replace("%", ""))));
        }

        //Web scrape Zacks
        WebInfoZacks webInfoZacks = new WebInfoZacks(ticker);
        Thread threadZacks = new Thread(webInfoZacks);
        threadZacks.start();
        try {
            threadZacks.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        colInfo = webInfoZacks.getValue();
        Log.d("Truffles2", webInfoZacks.getValue().toString());

        for(int i = 0; i < colInfo.size(); i++) {
            tempArray = colInfo.get(i);
            zscore.add(Integer.parseInt(tempArray.get(0).toString()));
            momentum.add(tempArray.get(1).toString());
            vgm.add(tempArray.get(2).toString());
            esp.add(Double.parseDouble(tempArray.get(3).toString().replace("%", "")));

            String tempDate = tempArray.get(4).toString().replace("*BMO" , "").replace("*AMC", "");
            date.add(String.format("20%1$s-%2$s", tempDate.substring(6), tempDate.substring(0, 5).replace("/", "-")));
        }

        //Add to database
        recent_tickers = new ArrayList<>();
        duplicateCheck();

//        Date date1 = new Date();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        String format = formatter.format(date);


        for(int i = 0; i < ticker.size(); i++) {
            if(!recent_tickers.contains(ticker.get(i))){
                db.addReport((String) ticker.get(i), (String) date.get(i), (Double) predicted.get(i), (Double) esp.get(i), (Integer) zscore.get(i), (String) momentum.get(i), (String) vgm.get(i), (Double) sinceLast.get(i));
            }
        }

    }
}