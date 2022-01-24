package lucas.graeff.tradereports.webscraping;

import android.content.Context;
import android.database.Cursor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
    Document secondDoc = null;
    Calendar calendar = Calendar.getInstance();

    public ArrayList<String> recent_tickers = new ArrayList<>();


    public ArrayList<String> tickers = new ArrayList<>();
    public ArrayList<LocalDate> dates = new ArrayList<>();
    public ArrayList<String> sinceLast = new ArrayList<>();
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
    public ArrayList<String> average = new ArrayList<>();

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
        int flag = 0;

        //EarningsWhispers
        //Collect ticker, date, bell time, report time

        String ticker = null;
        String time;
        String growth = null;
        Integer bell;
        String reportTime;

        recent_tickers = DuplicateCheck();

        //Number of days ahead to get earnings dates
        for(int i = 0; i < 7; i++) {
            try {
                doc = Jsoup.connect("https://www.earningswhispers.com/calendar?sb=t&d=" + (i + 1) + "&t=all&v=t").get();
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
                    //Skip if ticker is a repeat
                    if(j > 0) {
                        if(tickers.contains(ticker)) {
                            continue;
                        }
                    }
                    //get time
                    try {
                        time = doc.getElementById("T-" + ticker + "").getElementsByClass("time").get(0).text();
                    } catch (Exception e) {
                        //Set time manually
                        time = "AMC";
                    }
                    //get sinceLast
                    try {
                        growth = doc.getElementById("T-" + ticker + "").getElementsByClass("revgrowthprint").get(0).text().replace("%", "");
                    } catch (Exception e) {
                        //Set sinceLast manually
                        growth = "-";
                    }

                    //Mark for add or update to db
                    try {
                        if(recent_tickers.contains(ticker)) {
                            updateFlag.add(1);
                            flag = 1;
                        }
                        else {
                            updateFlag.add(0);
                            flag = 0;
                        }
                    } catch (Exception e) {
                        System.out.println(ticker + ": " +e);
                    }

                    if(time.contains("BMO")) {
                        bell = 0;
                    } else if (time.contains("AMC")) {
                        bell = 1;
                    }
                    else if (time.contains("DMH")) {
                        bell = 2;
                    }
                    else {
                        reportTime = time.replace(" ET", "");
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
                    sinceLast.add(growth);

                    if(flag == 0) {
                        GuidanceHistory(ticker);
                    }
                    else {
                        guidanceMin.add("-");
                        guidanceMax.add("-");
                        guidanceEst.add("-");
                    }
                }


                calendar.setTime(Date.from(date.atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()));
                if(calendar.get(Calendar.DAY_OF_WEEK) == 6) {
                    i += 2;
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Main: " + ticker + ": " +e);
            }
        }

        //Finviz

        for (int i = 0; i < tickers.size(); i++) {
            if(updateFlag.get(i) == 0) {
                EpsHistory(tickers.get(i), i);
            }
            else {
                firstEps.add("-");
                secondEps.add("-");
                thirdEps.add("-");
                fourthEps.add("-");
                fifthEps.add("-");

                ArrayList fromTo = new ArrayList<String>();
                while(fromTo.size() < 8) {
                    fromTo.add("-");
                }
                priceChange.put(i, fromTo);
                volatility.add("-");
                average.add("-");
            }
            try {
                doc = Jsoup.connect("https://finviz.com/quote.ashx?t=" + tickers.get(i)).get();

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
                System.out.println(ticker + ": " +e);
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
                        average.get(i),
                        recom.get(i),
                        peg.get(i),
                        predictedEps.get(i),
                        sinceLast.get(i),
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
            db.close();

        }

    }


    public void EpsHistory(String ticker, int j) {
        SimpleDateFormat decodeFormatter = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        ArrayList<Date> quarterDates = new ArrayList<>();
        ArrayList<Integer> quarterBells = new ArrayList<>();


        try {
            Date noon = dateFormat.parse("12:00:00");
            LocalDate currentDate = LocalDate.now();
            LocalDate sinceDate = currentDate.minusYears(2);
            doc = Jsoup.connect("https://api.benzinga.com/api/v2.1/calendar/earnings?token=1c2735820e984715bc4081264135cb90&parameters[date_from]=" + sinceDate + "&parameters[date_to]=" + currentDate + "&parameters[tickers]=" + ticker + "&pagesize=1000").get();
            Elements rows = doc.getElementsByTag("eps");
            Elements earningsDates = doc.getElementsByTag("date");
            Elements timeRows = doc.getElementsByTag("time");
            for(int i = 0; i < 4; i++) {
                if(dateFormat.parse(timeRows.get(i).text()).before(noon)) {
                    quarterBells.add(0);
                }
                else {
                    quarterBells.add(1);
                }
            }
            try {
                firstEps.add(rows.get(0).text());
            }
            catch (Exception e) {
                firstEps.add("-");
            }
            try {
                secondEps.add(rows.get(1).text());
            }
            catch (Exception e) {
                secondEps.add("-");
            }
            try {
                thirdEps.add(rows.get(2).text());
            }
            catch (Exception e) {
                thirdEps.add("-");
            }
            try {
                fourthEps.add(rows.get(3).text());
            }
            catch (Exception e) {
                fourthEps.add("-");
            }
            try {
                fifthEps.add(rows.get(4).text());
            }
            catch (Exception e) {
                fifthEps.add("-");
            }
            Date firstDate = decodeFormatter.parse(earningsDates.get(0).text());
            Date secondDate = decodeFormatter.parse(earningsDates.get(1).text());
            Date thirdDate = decodeFormatter.parse(earningsDates.get(2).text());
            Date fourthDate = decodeFormatter.parse(earningsDates.get(3).text());
            quarterDates.add(firstDate);
            quarterDates.add(secondDate);
            quarterDates.add(thirdDate);
            quarterDates.add(fourthDate);

            PriceHistory(ticker, quarterDates, quarterBells, j);

        } catch (Exception e) {
            System.out.println("EpsHistory: " + ticker + ": " +e);
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
            average.add("-");

        }

    }

    public void GuidanceHistory(String ticker) {
        try {
            LocalDate currentDate = LocalDate.now();
            LocalDate sinceDate = currentDate.minusYears(2);
            secondDoc = Jsoup.connect("https://api.benzinga.com/api/v2.1/calendar/guidance?token=1c2735820e984715bc4081264135cb90&parameters[date_from]=" + sinceDate + "&parameters[date_to]=" + currentDate + "&parameters[tickers]=" + ticker + "&pagesize=1000").get();
            Elements min = secondDoc.getElementsByTag("eps_guidance_min");
            Elements max = secondDoc.getElementsByTag("eps_guidance_max");
            Elements est = secondDoc.getElementsByTag("eps_guidance_est");
            guidanceMin.add(min.get(0).text());
            guidanceMax.add(max.get(0).text());
            guidanceEst.add(est.get(0).text());
        } catch (Exception e) {
            System.out.println("GuidanceHistory: " + ticker + ": " +e);
            guidanceMin.add("-");
            guidanceMax.add("-");
            guidanceEst.add("-");
        }

    }

    public void PriceHistory(String ticker, ArrayList<Date> quarterDates, ArrayList<Integer> quarterBells, int j) {
        calendar = Calendar.getInstance();
        ArrayList fromTo = new ArrayList<String>();
        long fromDate;
        long toDate;
        String from;
        String to;
        int day;
        double avg = 0.00;
        double total = 0.00;
        double totalAbs = 0.00;

        ArrayList<Double> unfinishedVolatility = new ArrayList<>();

            try {
                for (int i = 0; i < quarterDates.size(); i++) {
                    calendar.setTime(quarterDates.get(i));
                    day = calendar.get(Calendar.DAY_OF_WEEK);
                    if(quarterBells.get(i) == 0) {
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        if(day == 2) {
                            calendar.add(Calendar.DAY_OF_YEAR, -2);
                        }
                        fromDate = (calendar.getTimeInMillis() / 1000);
                        calendar.setTime(quarterDates.get(i));
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                    }
                    else {
                        fromDate = (calendar.getTimeInMillis() / 1000);
                        if(day == 6) {
                            calendar.add(Calendar.DAY_OF_YEAR, 2);
                        }
                        calendar.add(Calendar.DAY_OF_YEAR, 2);
                    }
                    toDate = calendar.getTimeInMillis() / 1000;
                     doc = Jsoup.connect("https://finance.yahoo.com/quote/" + ticker + "/history?period1=" + fromDate + "&period2=" + toDate + "&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true").get();
                    Elements cell = doc.getElementsByTag("td");
                    if(quarterBells.get(i) == 0){
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
                    totalAbs += Math.abs(unfinishedVolatility.get(i));
                }
                avg = total / unfinishedVolatility.size();
                average.add((avg + "").substring(0,4));
                volatility.add((totalAbs / unfinishedVolatility.size() + "").substring(0,4));

            } catch (Exception e) {
                System.out.println("PriceHistory: " + ticker + ": " +e);
                while(fromTo.size() < 8) {
                    fromTo.add("-");
                }
                average.add("-");
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
