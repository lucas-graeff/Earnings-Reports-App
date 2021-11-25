package lucas.graeff.tradereports.webscraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

public class PostEarnings implements Runnable {
    private Elements cols;
    private ArrayList<String> colInfoString;
    private HashMap<Integer, ArrayList<String>> colInfo;

    @Override
    public void run() {
        Document doc = null;
        try {
            //Go to past notable earnings page
            doc = Jsoup.connect("https://stocksearning.com/Past-Notable-Earnings-Dates.aspx").get();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
        Element contentList = doc.getElementById("PostEarningTable");
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
