package lucas.graeff.tradereports.webscraping

import lucas.graeff.tradereports.room.AppDatabase
import lucas.graeff.tradereports.room.Report
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs

class CollectData(var db: AppDatabase) : Runnable {
    private var calendar = Calendar.getInstance()
    private lateinit var recentTickers: List<String>

    override fun run() {
        var cols: Elements
        var date: LocalDate
        var updateFlag: Boolean

        //EarningsWhispers
        //Collect ticker, date, bell time, report time
        var time: String
        var bell: Int
        var reportTime: String?
        recentTickers = db.reportDao().recentReports()

        //Number of days ahead to get earnings dates
        run {
            var i = 0
            while (i < 7) {
                try {
                    val doc =
                        Jsoup.connect("https://www.earningswhispers.com/calendar?sb=t&d=" + (i + 1) + "&t=all&v=t")
                            .get()
                    cols = doc.getElementsByClass("ticker")

                    //Get date
                    val dateText = doc.getElementById("calbox").text().split(",").toTypedArray()
                    val format = DateTimeFormatter.ofPattern("MMMM d, yyyy")
                    date =
                        LocalDate.parse(dateText[1].trim { it <= ' ' } + "," + dateText[2], format)


                    // Get ticker, time, and bell
                    for (j in cols.indices) {
                        var report = Report()
                        reportTime = null
                        report.ticker = cols[j].text()

                        //Skip if ticker is a repeat
//                        if (j > 0) {
//                            if (tickers.contains(ticker)) {
//                                continue
//                            }
//                        }

                        //get time
                        time = try {
                            doc.getElementById("T-${report.ticker}")
                                .getElementsByClass("time")[0].text()
                        } catch (e: Exception) {
                            //Set time manually
                            "AMC"
                        }
                        //get sinceLast
                        report.sinceLast = doc.getElementById("T-${report.ticker}")
                            .getElementsByClass("revgrowthprint")[0].text().replace("%", "")
                            .toDoubleOrNull() ?: 0.0

                        if (time.contains("BMO")) {
                            bell = 0
                        } else if (time.contains("AMC")) {
                            bell = 1
                        } else if (time.contains("DMH")) {
                            bell = 2
                        } else {
                            reportTime = time.replace(" ET", "")
                            bell = if (reportTime!!.contains("AM")) {
                                0
                            } else {
                                1
                            }
                        }
                        report.date = date.toString()
                        report.time = reportTime.toString()
                        report.bell = bell

                        //Mark for add or update to db
                            updateFlag = !recentTickers.contains(report.ticker)

                        //Pass into function
                        if (updateFlag) {
                                report = guidanceHistory(report)
                        }
                            epsHistory(report)
                    }

                    //Iterate over weekends
                    calendar.time = Date.from(
                        date.atStartOfDay()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                    )
                    if (calendar[Calendar.DAY_OF_WEEK] == 6) {
                        i += 2
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Main: $e")
                }
                i++
            }
        }

    }

    private fun guidanceHistory(report: Report): Report {
        try {
            val currentDate = LocalDate.now()
            val sinceDate = currentDate.minusYears(2)
            val doc =
                Jsoup.connect("https://api.benzinga.com/api/v2.1/calendar/guidance?token=1c2735820e984715bc4081264135cb90&parameters[date_from]=$sinceDate&parameters[date_to]=$currentDate&parameters[tickers]=${report.ticker}&pagesize=1000")
                    .get()
            val min = doc?.getElementsByTag("eps_guidance_min")
            val max = doc?.getElementsByTag("eps_guidance_max")
            val est = doc?.getElementsByTag("eps_guidance_est")
            report.guidanceMin = min?.get(0)?.text()?.toDouble()!!
            report.guidanceMax = max?.get(0)?.text()?.toDouble()!!
            report.guidanceEstimate = est?.get(0)?.text()?.toDouble()!!
        } catch (e: Exception) {
            println("GuidanceHistory: ${report.ticker}: $e")
        }
        return report
    }

    private fun epsHistory(report: Report) {
        val decodeFormatter = SimpleDateFormat("yyyy-MM-dd")
        val dateFormat: DateFormat = SimpleDateFormat("hh:mm:ss")
        val quarterDates = ArrayList<Date>()
        val quarterBells = ArrayList<Int>()
        try {
            val noon = dateFormat.parse("12:00:00")
            val currentDate = LocalDate.now()
            val sinceDate = currentDate.minusYears(2)
            val doc =
                Jsoup.connect("https://api.benzinga.com/api/v2.1/calendar/earnings?token=1c2735820e984715bc4081264135cb90&parameters[date_from]=$sinceDate&parameters[date_to]=$currentDate&parameters[tickers]=${report.ticker}&pagesize=1000")
                    .get()
            val rows = doc.getElementsByTag("eps")
            val earningsDates = doc.getElementsByTag("date")
            val timeRows = doc.getElementsByTag("time")
            for (i in 0..3) {
                if (dateFormat.parse(timeRows[i].text()).before(noon)) {
                    quarterBells.add(0)
                } else {
                    quarterBells.add(1)
                }
            }
            report.epsFirst = rows[0].text().toDouble()
            report.epsSecond = rows[1].text().toDouble()
            report.epsThird = rows[2].text().toDouble()
            report.epsFourth = rows[3].text().toDouble()
            report.epsFifth = rows[4].text().toDouble()

            val firstDate = decodeFormatter.parse(earningsDates[0].text())
            val secondDate = decodeFormatter.parse(earningsDates[1].text())
            val thirdDate = decodeFormatter.parse(earningsDates[2].text())
            val fourthDate = decodeFormatter.parse(earningsDates[3].text())
            quarterDates.add(firstDate)
            quarterDates.add(secondDate)
            quarterDates.add(thirdDate)
            quarterDates.add(fourthDate)
            priceHistory(report, quarterDates, quarterBells)
        } catch (e: Exception) {
            println("epsHistory ${report.ticker}: $e")
        }
    }

    private fun priceHistory(
        report: Report,
        quarterDates: ArrayList<Date>,
        quarterBells: ArrayList<Int>
    ) {
        calendar = Calendar.getInstance()
        var fromDate: Long
        var toDate: Long
        var from: String
        var to: String
        var day: Int
        var total = 0.00
        var totalAbs = 0.00
        val unfinishedVolatility = ArrayList<Double>()
        try {
            for (i in quarterDates.indices) {
                calendar.time = quarterDates[i]
                day = calendar[Calendar.DAY_OF_WEEK]
                if (quarterBells[i] == 0) {
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                    if (day == 2) {
                        calendar.add(Calendar.DAY_OF_YEAR, -2)
                    }
                    fromDate = calendar.timeInMillis / 1000
                    calendar.time = quarterDates[i]
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                } else {
                    fromDate = calendar.timeInMillis / 1000
                    if (day == 6) {
                        calendar.add(Calendar.DAY_OF_YEAR, 2)
                    }
                    calendar.add(Calendar.DAY_OF_YEAR, 2)
                }
                toDate = calendar.timeInMillis / 1000
                val doc =
                    Jsoup.connect("https://finance.yahoo.com/quote/${report.ticker}/history?period1=$fromDate&period2=$toDate&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true")
                        .get()
                val cell = doc.getElementsByTag("td")
                if (quarterBells[i] == 0) {
                    from = cell[11].text()
                    to = cell[4].text()
                } else {
                    from = cell[8].text()
                    to = cell[1].text()
                }
                //Calculate quarterly performances
                when (i) {
                    0 -> report.quarterPerformanceFirst = to.toDouble() - from.toDouble()
                    1 -> report.quarterPerformanceSecond = to.toDouble() - from.toDouble()
                    2 -> report.quarterPerformanceThird = to.toDouble() - from.toDouble()
                    3 -> report.quarterPerformanceFourth = to.toDouble() - from.toDouble()
                }
                unfinishedVolatility.add((to.toDouble() - from.toDouble()) / abs(from.toDouble()) * 100)
            }
            for (i in unfinishedVolatility.indices) {
                total += unfinishedVolatility[i]
                totalAbs += abs(unfinishedVolatility[i])
            }
            report.average = total / unfinishedVolatility.size
            report.volatility = totalAbs / unfinishedVolatility.size
        } catch (e: Exception) {
            println("PriceHistory: ${report.ticker}: $e")
        }
        finviz(report)
    }

    private fun finviz(report: Report) {
        try {
            val doc = Jsoup.connect("https://finviz.com/quote.ashx?t=" + report.ticker).get()
            val rows = doc.getElementsByClass("table-dark-row")
            report.insiderTrans =
                rows[1].getElementsByTag("td")[7].text().replace("%", "").toDoubleOrNull() ?: 0.0
            report.performanceWeek =
                rows[0].getElementsByTag("td")[11].text().replace("%", "").toDoubleOrNull() ?: 0.0
            report.peg = rows[2].getElementsByTag("td")[3].text().toDoubleOrNull() ?: 0.0
            report.predictedEps = rows[2].getElementsByTag("td")[5].text().toDoubleOrNull() ?: 0.0
            report.shortFloat =
                rows[2].getElementsByTag("td")[9].text().replace("%", "").toDoubleOrNull() ?: 0.0
            report.targetPrice = rows[4].getElementsByTag("td")[9].text().toDoubleOrNull() ?: 0.0
            report.price = rows[10].getElementsByTag("td")[11].text().toDoubleOrNull() ?: 0.0
            report.recommended = rows[11].getElementsByTag("td")[1].text().toDoubleOrNull() ?: 0.0
        } catch (e: IOException) {
            e.printStackTrace()
            println("${report.ticker}: $e")
        }
        db.reportDao().addReport(report)
    }

}