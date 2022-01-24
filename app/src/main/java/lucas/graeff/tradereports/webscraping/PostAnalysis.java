package lucas.graeff.tradereports.webscraping;

import android.content.Context;
import android.database.Cursor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lucas.graeff.tradereports.MyDatabaseHelper;

public class PostAnalysis implements Runnable{
    private final Context context;
    MyDatabaseHelper db;

    public PostAnalysis(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        db = new MyDatabaseHelper(context);
        SimpleDateFormat decodeFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Cursor cursor;
        int id, bell, day;
        String ticker, from, to, eps, surpriseEps, dateStringFrom, dateStringTo;
        Date date;
        long fromDate, toDate;
        String change;
        Calendar calendar;
        Document doc;

        //Get tickers from past week that don't have actuals
        cursor = db.readQuery("SELECT id, ticker, date, bell FROM reports" +
                " WHERE reports.date < date('now', '-1 day')" +
                " AND change IS NULL");
        while (cursor.moveToNext()) {
            try {
                id = cursor.getInt(0);
                ticker = cursor.getString(1);
                date = decodeFormatter.parse(cursor.getString(2));
                dateStringFrom = cursor.getString(2);
                bell = cursor.getInt(3);
                calendar = Calendar.getInstance();

                dateStringTo = decodeFormatter.format(new Date());


                calendar.setTime(date);
                //Use date and bell values to get actuals
                if(bell == 0) {
                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                    day = calendar.get(Calendar.DAY_OF_WEEK);
                    if(day == 1) {
                        calendar.add(Calendar.DAY_OF_YEAR, -2);
                    }
                    fromDate = (calendar.getTimeInMillis() / 1000);
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }
                else {
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

                //Calculate change
                change = String.format("%.2f", (Double.parseDouble(to) - Double.parseDouble(from)) / Math.abs(Double.parseDouble(from)) * 100.0);

                //Get eps
                doc = Jsoup.connect("https://api.benzinga.com/api/v2.1/calendar/earnings?token=1c2735820e984715bc4081264135cb90&parameters[date_from]=" + dateStringFrom + "&parameters[date_to]=" + dateStringTo + "&parameters[tickers]=" + ticker + "&pagesize=1000").get();
                Elements rows = doc.getElementsByTag("eps");
                Elements cols = doc.getElementsByTag("eps_surprise");
                eps = rows.get(0).text();
                surpriseEps = cols.get(0).text();

                db.AddPost(id, from, to, change, eps, surpriseEps);

            } catch (Exception e) {
                System.out.println("Post Scrape:" + e.getStackTrace());
            }

        }

    }
}
