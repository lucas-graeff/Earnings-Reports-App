package lucas.graeff.tradereports.webscraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class WebInfoZacks implements Runnable {
    private HashMap<Integer, ArrayList<String>> colInfo;
    ArrayList<String> tickerList;
    String tickerUrl;


    public WebInfoZacks(ArrayList<String> tickerList) {
        this.tickerList = tickerList;
    }

    @Override
    public void run() {
        colInfo = new HashMap<Integer, ArrayList<String>>();

        for(int i = 0; i < tickerList.size(); i++) {
            Document doc = null;
            ArrayList<String> dataList = new ArrayList<>();
            try {
                //Go to page for specific stock
                tickerUrl = tickerList.get(i).toString();
                doc = Jsoup.connect("https://www.zacks.com/stock/quote/" + tickerList.get(i).toString()).get();

                Elements elements = doc.getElementsByClass("rank_view");
                //zscore
                dataList.add(elements.get(0).text().substring(0, 1));
                //momentum and VGM
                Elements subElements = elements.get(1).getElementsByClass("composite_val");
                dataList.add(subElements.get(2).text());
                dataList.add(subElements.get(3).text());
                //esp
                Element element = doc.getElementById("stock_key_earnings").child(0).nextElementSibling().getElementsByTag("dd").first();
                dataList.add(element.text());
                //date
                element = doc.getElementById("stock_key_earnings").child(4).nextElementSibling().getElementsByTag("dd").last();
                dataList.add(element.text());

                colInfo.put(i, dataList);
            } catch (Exception e) {
                System.out.println("Skipped " + tickerList.get(i).toString());
                dataList.add(null);
                colInfo.put(i, dataList);
            }

        }


    }

    public HashMap<Integer, ArrayList<String>> getValue(){
        return colInfo;
    }

}
