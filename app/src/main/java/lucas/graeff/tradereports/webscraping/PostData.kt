package lucas.graeff.tradereports.webscraping

import lucas.graeff.tradereports.room.AppDatabase
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class PostData(var db: AppDatabase) : Runnable {

    override fun run() {
        val decodeFormatter = SimpleDateFormat("yyyy-MM-dd")
        var day: Int
        var from: String
        var to: String
        var fromDate: Long
        var toDate: Long

        //Get tickers from past week that don't have change value
        val reports = db.reportDao().postAnalysis()
        for (report in reports) {
            try {

                val calendar = Calendar.getInstance()
                calendar.time = decodeFormatter.parse(report.date);

                val dateStringFrom = report.date
                val dateStringTo = decodeFormatter.format(Date())

                //Use date and bell values to get actuals
                if (report.bell == 0) {
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                    day = calendar[Calendar.DAY_OF_WEEK]
                    if (day == 1) {
                        calendar.add(Calendar.DAY_OF_YEAR, -2)
                    }
                    fromDate = (calendar.timeInMillis / 1000)
                    //calendar.time = decodeFormatter.parse(report.date);
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                } else {
                    fromDate = (calendar.timeInMillis / 1000)
                    day = calendar[Calendar.DAY_OF_WEEK]
                    if (day == 5) {
                        calendar.add(Calendar.DAY_OF_YEAR, 1)
                    }
                    calendar.add(Calendar.DAY_OF_YEAR, 2)
                }
                toDate = calendar.timeInMillis / 1000
                var doc =
                    Jsoup.connect("https://finance.yahoo.com/quote/${report.ticker}/history?period1=$fromDate&period2=$toDate&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true")
                        .get()
                val cell = doc.getElementsByTag("td")
                if (report.bell == 0) {
                    from = cell[11].text()
                    to = cell[4].text()
                } else {
                    from = cell[8].text()
                    to = cell[1].text()
                }

                //Calculate change
                report.resultChange = (to.toDouble() - from.toDouble()) / abs(from.toDouble()) * 100.0

                //Get eps
                doc =
                    Jsoup.connect("https://api.benzinga.com/api/v2.1/calendar/earnings?token=1c2735820e984715bc4081264135cb90&parameters[date_from]=$dateStringFrom&parameters[date_to]=$dateStringTo&parameters[tickers]=${report.ticker}&pagesize=1000")
                        .get()
                val eps = doc.getElementsByTag("eps")
                //TODO: Get eps surprise
                //val cols = doc.getElementsByTag("eps_surprise")
                report.resultEps = eps[0].text().toDouble()

                db.reportDao().updateReport(report)
            } catch (e: Exception) {
                println("Post Scrape:" + e.stackTrace)
            }
        }
    }
}