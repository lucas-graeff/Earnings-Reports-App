package lucas.graeff.tradereports.webscraping;

import android.content.Context;
import android.database.Cursor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import lucas.graeff.tradereports.MyDatabaseHelper;

public class CollectData implements Runnable{
    private Context context;
    MyDatabaseHelper db;
    Document doc = null;
    public ArrayList<String> recent_tickers = new ArrayList<>();


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

    public ArrayList<String> firstEps = new ArrayList<>();
    public ArrayList<String> secondEps = new ArrayList<>();
    public ArrayList<String> thirdEps = new ArrayList<>();
    public ArrayList<String> fourthEps = new ArrayList<>();
    public ArrayList<String> fifthEps = new ArrayList<>();

    public HashMap<Integer, ArrayList> priceChange = new HashMap<>();

    public ArrayList<String> guidanceMin = new ArrayList<>();
    public ArrayList<String> guidanceMax = new ArrayList<>();
    public ArrayList<String> guidanceEst = new ArrayList<>();

    public ArrayList<String> volatility = new ArrayList<>();

    // 0 = Add 1 = Update
    public ArrayList<Integer> updateFlag = new ArrayList<>();




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
        //Collect ticker, date, bell time, report time

        String ticker;
        String time;
        Integer bell;
        String reportTime;

        DuplicateCheck();
        //Number of days ahead to get earnings dates
        for(int i = 0; i < 9; i++) {
            try {
                doc = Jsoup.connect("https://www.earningswhispers.com/calendar?sb=t&d=" + i + "&t=all&v=t").get();
                cols = doc.getElementsByClass("ticker");
                secCols = doc.getElementsByClass("time");

                //TODO: Account for desynced columns (esp in time column)
                if(cols.size() != secCols.size()) {
                    throw new Exception("demo");
                }

                //Get date
                String[] dateText = (doc.getElementById("calbox").text()).split(",");
                DateTimeFormatter format = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                date = LocalDate.parse(dateText[1].trim() + "," + dateText[2], format);


                // Get ticker, time, and bell
                for(int j = 0; j < cols.size(); j++) {
                    reportTime = null;
                    ticker = cols.get(j).text();

                    //Mark for add or update to db
                    try {
                        if(recent_tickers.contains(ticker)) {
                            updateFlag.add(1);
                        }
                        else {
                            updateFlag.add(0);
                        }
                    } catch (Exception e) {

                    }

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

                    GuidanceHistory(ticker);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Finviz

        for (int i = 0; i < tickers.size(); i++) {
            EpsHistory(tickers.get(i), bells.get(i), i);
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

                db.addReport(tickers.get(i),
                        dates.get(i).toString(),
                        bells.get(i),
                        volatility.get(i),
                        recom.get(i),
                        peg.get(i),
                        predictedEps.get(i),
                        times.get(i),
                        insiderTrans.get(i),
                        shortFloat.get(i),
                        targetPrice.get(i),
                        price.get(i),
                        perfWeek.get(i),
                        firstEps.get(i),
                        secondEps.get(i),
                        thirdEps.get(i),
                        fourthEps.get(i),
                        fifthEps.get(i),
                        priceChange.get(i).get(0).toString(),
                        priceChange.get(i).get(1).toString(),
                        priceChange.get(i).get(2).toString(),
                        priceChange.get(i).get(3).toString(),
                        priceChange.get(i).get(4).toString(),
                        priceChange.get(i).get(5).toString(),
                        priceChange.get(i).get(6).toString(),
                        priceChange.get(i).get(7).toString(),
                        guidanceMin.get(i),
                        guidanceMax.get(i),
                        guidanceEst.get(i),
                        updateFlag.get(i));

        }

    }


    public void EpsHistory(String ticker, int bell, int j) {
        SimpleDateFormat decodeFormatter = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<Date> quarterDates = new ArrayList<>();

        try {
            LocalDate currentDate = LocalDate.now();
            LocalDate sinceDate = currentDate.minusYears(2);
            doc = Jsoup.connect("https://api.benzinga.com/api/v2.1/calendar/earnings?token=1c2735820e984715bc4081264135cb90&parameters[date_from]=" + sinceDate + "&parameters[date_to]=" + currentDate + "&parameters[tickers]=" + ticker + "&pagesize=1000").get();
            Elements rows = doc.getElementsByTag("eps");
            Elements earningsDates = doc.getElementsByTag("date");
            firstEps.add(rows.get(0).text());
            secondEps.add(rows.get(1).text());
            thirdEps.add(rows.get(2).text());
            fourthEps.add(rows.get(3).text());
            fifthEps.add(rows.get(4).text());
            Date firstDate = decodeFormatter.parse(earningsDates.get(0).text());
            Date secondDate = decodeFormatter.parse(earningsDates.get(1).text());
            Date thirdDate = decodeFormatter.parse(earningsDates.get(2).text());
            Date fourthDate = decodeFormatter.parse(earningsDates.get(3).text());
            quarterDates.add(firstDate);
            quarterDates.add(secondDate);
            quarterDates.add(thirdDate);
            quarterDates.add(fourthDate);

            PriceHistory(ticker, quarterDates, bell, j);

        } catch (Exception e) {
            firstEps.add("-");
            secondEps.add("-");
            thirdEps.add("-");
            fourthEps.add("-");
            fifthEps.add("-");

            ArrayList fromTo = new ArrayList<String>();
            while(fromTo.size() < 8) {
                fromTo.add("-");
            }
            priceChange.put(j, fromTo);
            volatility.add("-");

        }

    }

    public void GuidanceHistory(String ticker) {
        try {
            LocalDate currentDate = LocalDate.now();
            LocalDate sinceDate = currentDate.minusYears(2);
            doc = Jsoup.connect("https://api.benzinga.com/api/v2.1/calendar/guidance?token=1c2735820e984715bc4081264135cb90&parameters[date_from]=" + sinceDate + "&parameters[date_to]=" + currentDate + "&parameters[tickers]=" + ticker + "&pagesize=1000").get();
            Elements min = doc.getElementsByTag("eps_guidance_min");
            Elements max = doc.getElementsByTag("eps_guidance_max");
            Elements est = doc.getElementsByTag("eps_guidance_est");
            guidanceMin.add(min.get(0).text());
            guidanceMax.add(max.get(0).text());
            guidanceEst.add(est.get(0).text());
        } catch (Exception e) {
            guidanceMin.add("-");
            guidanceMax.add("-");
            guidanceEst.add("-");
        }

    }

    public void PriceHistory(String ticker, ArrayList<Date> quarterDates, int bell, int j) {
        Calendar calendar = Calendar.getInstance();
        ArrayList fromTo = new ArrayList<String>();
        long fromDate;
        long toDate;
        String from;
        String to;
        int day;
        double average = 0.00;
        double total = 0.00;

        ArrayList<Double> unfinishedVolatility = new ArrayList<>();

            try {
                for (int i = 0; i < quarterDates.size(); i++) {
                    if(bell == 0) {
                        calendar.setTime(quarterDates.get(i));
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        day = calendar.get(Calendar.DAY_OF_WEEK);
                        if(day == 1) {
                            calendar.add(Calendar.DAY_OF_YEAR, -2);
                        }
                        fromDate = (calendar.getTimeInMillis() / 1000);
                        calendar.setTime(quarterDates.get(i));
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                    }
                    else {
                        calendar.setTime(quarterDates.get(i));
                        fromDate = (calendar.getTimeInMillis() / 1000);
                        day = calendar.get(Calendar.DAY_OF_WEEK);
                        if(day == 5) {
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                        }
                        calendar.add(Calendar.DAY_OF_YEAR, 2);

                    }
                    toDate = calendar.getTimeInMillis() / 1000;
                    doc = Jsoup.connect("https://finance.yahoo.com/quote/" + ticker + "/history?period1=" + fromDate + "&period2=" + toDate + "&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true").get();
                    Elements cell = doc.getElementsByTag("td");
                    if(bell == 0){
                        from = cell.get(11).text();
                        to = cell.get(4).text();
                    }
                    else {
                        from = cell.get(8).text();
                        to = cell.get(1).text();
                    }
                    //from
                    fromTo.add(from);
                    //to
                    fromTo.add(to);

                    unfinishedVolatility.add(((((Double.parseDouble(to) - Double.parseDouble(from)) / Math.abs(Double.parseDouble(from))) * 100)));

                }
                for(int i = 0; i < unfinishedVolatility.size(); i++) {
                    total += unfinishedVolatility.get(i);
                    average = total / unfinishedVolatility.size();
                }
                volatility.add((average + "").substring(0,4));

            } catch (Exception e) {
                System.out.println(e);
                while(fromTo.size() < 8) {
                    fromTo.add("-");
                }
                volatility.add("-");
            }

        priceChange.put(j, fromTo);

    }//end PriceHistory


    public ArrayList DuplicateCheck() {
        Cursor cursor = db.getRecentTickers();
        while (cursor.moveToNext()) {
            recent_tickers.add(cursor.getString(0));
        }
        return recent_tickers;
    }
}
