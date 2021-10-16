package lucas.graeff.tradereports;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WebInfo implements Runnable{
    private Elements cols;
    private ArrayList<String> colInfoString;
    private HashMap<Integer, ArrayList<String>> colInfo;

    @Override
    public void run() {
        Document doc = null;
        try {
            doc = Jsoup.connect("https://stocksearning.com/Upcoming-Earnings-Dates.aspx").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String title = doc.title();

        Element contentList = doc.getElementById("TodaysEarningTable");
        Elements rows = contentList.getElementsByTag("tr");

        //Find elements of UpcomingEarningReports
        colInfoString = new ArrayList<>();
        colInfo = new HashMap<Integer, ArrayList<String>>();

        for(int i = 1; i < rows.size(); i++) {
            ArrayList<String> dataList = new ArrayList<>();
            cols = rows.get(i).getElementsByTag("td");
            for(int j = 0; j < cols.size(); j++) {
                colInfoString.add(cols.get(j).text());
                dataList.add(cols.get(j).text());
            }
            colInfo.put(i, dataList);
        }
    }

    public HashMap<Integer, ArrayList<String>> getValue(){
        return colInfo;
    }
}
