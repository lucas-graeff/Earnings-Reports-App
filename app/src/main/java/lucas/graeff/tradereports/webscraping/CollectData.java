package lucas.graeff.tradereports.webscraping;

import android.content.Context;
import android.database.Cursor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import lucas.graeff.tradereports.AlarmReceiver;
import lucas.graeff.tradereports.MainActivity;
import lucas.graeff.tradereports.MyDatabaseHelper;

public class CollectData implements Runnable{
    private Context context;
    MyDatabaseHelper db;
    Document doc = null;



    public ArrayList<String> tickers = new ArrayList<>();
    public ArrayList<LocalDate> dates = new ArrayList<>();
    public ArrayList<String> times = new ArrayList<>();
    public ArrayList<Integer> bells = new ArrayList<>();

    public ArrayList<String> recom = new ArrayList<>();
    public ArrayList<String> peg = new ArrayList<>();
    public ArrayList<String> predictedEps = new ArrayList<>();
    public ArrayList<String> insiderTrans = new ArrayList<>();
    public ArrayList<String> shortFloat = new ArrayList<>();
    public ArrayList<String> targetPrice = new ArrayList<>();
    public ArrayList<String> price = new ArrayList<>();
    public ArrayList<String> perfWeek = new ArrayList<>();



    public ArrayList<Double> firstEps = new ArrayList<>();
    public ArrayList<Double> secondEps = new ArrayList<>();
    public ArrayList<Double> thirdEps = new ArrayList<>();
    public ArrayList<Double> fourthEps = new ArrayList<>();
    public ArrayList<Double> firstEpsForecast = new ArrayList<>();
    public ArrayList<Double> secondEpsForecast = new ArrayList<>();
    public ArrayList<Double> thirdEpsForecast = new ArrayList<>();
    public ArrayList<Double> fourthEpsForecast = new ArrayList<>();




    public CollectData(Context context) {
        this.context = context;
    }


    @Override
    public void run() {
        db = new MyDatabaseHelper(context);
        Elements cols;
        Elements secCols;
        LocalDate date;

        //EarningsWhispers
        //Collect ticker, date, bell time

        String ticker;
        String time;
        Integer bell;
        String reportTime;
        //Number of days ahead to get earnings dates
        for(int i = 0; i < 9; i++) {
            try {
                doc = Jsoup.connect("https://www.earningswhispers.com/calendar?sb=t&d=" + i + "&t=all&v=t").get();
                cols = doc.getElementsByClass("ticker");
                secCols = doc.getElementsByClass("time");

                //Get date
                String[] dateText = (doc.getElementById("calbox").text()).split(",");
                DateTimeFormatter format = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                date = LocalDate.parse(dateText[1].trim() + "," + dateText[2], format);


                // Get ticker, time, and bell
                for(int j = 0; j < cols.size(); j++) {
                    reportTime = null;
                    ticker = cols.get(j).text();
                    time = secCols.get(j).text();
                    if(time.contains("BMO")) {
                        bell = 0;
                    } else if (time.contains("AMC")) {
                        bell = 1;
                    }
                    else if (time.contains("DMH")) {
                        bell = 2;
                    }
                    else {
                        reportTime = time;
                        if(reportTime.contains("AM")) {
                            bell = 0;
                        }
                        else {
                            bell = 1;
                        }
                    }

                    tickers.add(ticker);
                    dates.add(date);
                    times.add(reportTime);
                    bells.add(bell);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Finviz

        for (int i = 0; i < tickers.size(); i++) {
            try {
                doc = Jsoup.connect("https://finviz.com/quote.ashx?t=" + tickers.get(i)).get();
//                Elements rows = doc.getElementsByClass("snapshot-table2").get(0).firstElementSibling().getElementsByTag("tr");
//                insiderTrans.add(Double.parseDouble(rows.get(1).getElementsByTag("td").get(7).text()));
//                perfWeek.add(Double.parseDouble(rows.get(0).getElementsByTag("td").get(9).text()));
//                peg.add(Double.parseDouble(rows.get(2).getElementsByTag("td").get(1).text()));
//                predictedEps.add(Double.parseDouble(rows.get(2).getElementsByTag("td").get(3).text()));
//                shortFloat.add(Double.parseDouble(rows.get(2).getElementsByTag("td").get(9).text()));
//                targetPrice.add(Double.parseDouble(rows.get(4).getElementsByTag("td").get(9).text()));
//                price.add(Double.parseDouble(rows.get(10).getElementsByTag("td").get(11).text()));
//                recom.add(Double.parseDouble(rows.get(11).getElementsByTag("td").get(1).text()));

                Elements rows = doc.getElementsByClass("table-dark-row");
                insiderTrans.add(rows.get(1).getElementsByTag("td").get(7).text());
                perfWeek.add(rows.get(0).getElementsByTag("td").get(11).text());
                peg.add(rows.get(2).getElementsByTag("td").get(3).text());
                predictedEps.add(rows.get(2).getElementsByTag("td").get(5).text());
                shortFloat.add(rows.get(2).getElementsByTag("td").get(9).text());
                targetPrice.add(rows.get(4).getElementsByTag("td").get(9).text());
                price.add(rows.get(10).getElementsByTag("td").get(11).text());
                recom.add(rows.get(11).getElementsByTag("td").get(1).text());



            } catch (IOException e) {
                e.printStackTrace();
                insiderTrans.add("-");
                perfWeek.add("-");
                peg.add("-");
                predictedEps.add("-");
                shortFloat.add("-");
                targetPrice.add("-");
                price.add("-");
                recom.add("-");
            }
        }

        try {
            doc = Jsoup.connect("https://api.benzinga.com/api/v2.1/calendar/earnings?token=1c2735820e984715bc4081264135cb90&parameters[date_from]=2016-12-24&parameters[date_to]=2021-12-24&parameters[tickers]=MU&pagesize=1000").get();
            Elements rows = doc.getElementsByTag("date");
            rows = rows;
        } catch (Exception e) {

        }

    }

    public ArrayList<String> GetValue() {
        return tickers;
    }

    public void EpsHistory(String ticker, int bell) {
        //Nasdaq
        //Get EPS and EPS Forecast for each quarter

        ArrayList<Date> dates = new ArrayList<>();

        SimpleDateFormat decodeFormatter = new SimpleDateFormat("dd/MM/yyyy");

        try {
            doc = Jsoup.connect("https://api.benzinga.com/api/v2.1/calendar/earnings?token=1c2735820e984715bc4081264135cb90&parameters[date_from]=2016-12-24&parameters[date_to]=2021-12-24&parameters[tickers]=MU&pagesize=1000").get();
            Elements rows = doc.getElementsByTag("date");
            rows = rows;

            dates.add(decodeFormatter.parse(rows.get(0).getElementsByTag("td").get(1).text()));
            firstEps.add(Double.parseDouble(rows.get(0).getElementsByTag("td").get(2).text()));
            firstEpsForecast.add(Double.parseDouble(rows.get(0).getElementsByTag("td").get(3).text()));
            dates.add(decodeFormatter.parse(rows.get(1).getElementsByTag("td").get(1).text()));
            secondEps.add(Double.parseDouble(rows.get(1).getElementsByTag("td").get(2).text()));
            secondEpsForecast.add(Double.parseDouble(rows.get(1).getElementsByTag("td").get(3).text()));
            dates.add(decodeFormatter.parse(rows.get(2).getElementsByTag("td").get(1).text()));
            thirdEps.add(Double.parseDouble(rows.get(2).getElementsByTag("td").get(2).text()));
            thirdEpsForecast.add(Double.parseDouble(rows.get(2).getElementsByTag("td").get(3).text()));
            dates.add(decodeFormatter.parse(rows.get(3).getElementsByTag("td").get(1).text()));
            fourthEps.add(Double.parseDouble(rows.get(3).getElementsByTag("td").get(2).text()));
            fourthEpsForecast.add(Double.parseDouble(rows.get(3).getElementsByTag("td").get(3).text()));

        } catch (IOException | ParseException e) {

        }
    }

    public void ChangeHistory(String ticker, ArrayList dates, int bell) {
        SimpleDateFormat encodeFormatter = new SimpleDateFormat("MMM dd, yyyy");

        try {
            doc = Jsoup.connect("https://finance.yahoo.com/quote/" + ticker + "/history").get();
            Elements rows = doc.getElementsByTag("table").get(0).getElementsByTag("tbody").get(0).getElementsByTag("tr");
            String firstDate = encodeFormatter.parse((String) dates.get(0)).toString();
            for(int i = 0; i < rows.size(); i++) {
                if(doc.getElementsByTag("td").get(0).firstElementSibling().text().contains(firstDate)) {
                    System.out.println("Found" + firstDate);
                }
            }


        } catch (Exception e) {

        }
    }

    public ArrayList duplicateCheck() {
        ArrayList recent_tickers = null;
        Cursor cursor = db.getRecentTickers();
        while (cursor.moveToNext()) {
            recent_tickers.add(cursor.getString(0));
        }
        return recent_tickers;
    }
}
