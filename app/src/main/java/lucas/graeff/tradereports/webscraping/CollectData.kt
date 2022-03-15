package lucas.graeff.tradereports.webscraping

import android.content.Context
import android.database.Cursor
import lucas.graeff.tradereports.room.AppDatabase
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class CollectData {
    val db: AppDatabase = AppDatabase.getInstance(context)

    companion object {
        @JvmStatic
        fun webScrape() {
            var cols: Elements
            var secCols: Elements
            var date: LocalDate
            var flag = 0

            //EarningsWhispers
            //Collect ticker, date, bell time, report time
            var ticker: String? = null
            var time: String
            var growth: String? = null
            var bell: Int
            var reportTime: String?
            recent_tickers = DuplicateCheck()

            //Number of days ahead to get earnings dates
            run {
                var i = 0
                while (i < 7) {
                    try {
                        doc =
                            Jsoup.connect("https://www.earningswhispers.com/calendar?sb=t&d=" + (i + 1) + "&t=all&v=t")
                                .get()
                        cols = doc.getElementsByClass("ticker")
                        secCols = doc.getElementsByClass("time")

                        //Get date
                        val dateText: Array<String> = doc.getElementById("calbox").text()
                            .split(",").toTypedArray()
                        val format = DateTimeFormatter.ofPattern("MMMM d, yyyy")
                        date = LocalDate.parse(dateText[1].trim { it <= ' ' } + "," + dateText[2],
                            format)


                        // Get ticker, time, and bell
                        for (j in cols.indices) {
                            reportTime = null
                            ticker = cols[j].text()
                            //Skip if ticker is a repeat
                            if (j > 0) {
                                if (tickers.contains(ticker)) {
                                    continue
                                }
                            }
                            //get time
                            time = try {
                                doc.getElementById("T-$ticker").getElementsByClass("time").get(0)
                                    .text()
                            } catch (e: Exception) {
                                //Set time manually
                                "AMC"
                            }
                            //get sinceLast
                            growth = try {
                                doc.getElementById("T-$ticker").getElementsByClass("revgrowthprint")
                                    .get(0).text().replace("%", "")
                            } catch (e: Exception) {
                                //Set sinceLast manually
                                "-"
                            }

                            //Mark for add or update to db
                            try {
                                flag = if (recent_tickers.contains(ticker)) {
                                    updateFlag.add(1)
                                    1
                                } else {
                                    updateFlag.add(0)
                                    0
                                }
                            } catch (e: Exception) {
                                println("$ticker: $e")
                            }
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
                            tickers.add(ticker)
                            dates.add(date)
                            times.add(reportTime)
                            bells.add(bell)
                            sinceLast.add(growth)
                            if (flag == 0) {
                                GuidanceHistory(ticker)
                            } else {
                                guidanceMin.add("-")
                                guidanceMax.add("-")
                                guidanceEst.add("-")
                            }
                        }
                        calendar.setTime(
                            Date.from(
                                date.atStartOfDay()
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()
                            )
                        )
                        if (calendar.get(Calendar.DAY_OF_WEEK) == 6) {
                            i += 2
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("Main: $ticker: $e")
                    }
                    i++
                }
            }

            //Finviz
            for (i in tickers.indices) {
                if (updateFlag.get(i) == 0) {
                    EpsHistory(tickers.get(i), i)
                } else {
                    firstEps.add("-")
                    secondEps.add("-")
                    thirdEps.add("-")
                    fourthEps.add("-")
                    fifthEps.add("-")
                    val fromTo: ArrayList<*> = ArrayList<String>()
                    while (fromTo.size < 8) {
                        fromTo.add("-")
                    }
                    priceChange.put(i, fromTo)
                    volatility.add("-")
                    average.add("-")
                }
                try {
                    doc = Jsoup.connect("https://finviz.com/quote.ashx?t=" + tickers.get(i)).get()
                    val rows: Elements = doc.getElementsByClass("table-dark-row")
                    insiderTrans.add(rows[1].getElementsByTag("td")[7].text())
                    perfWeek.add(rows[0].getElementsByTag("td")[11].text())
                    peg.add(rows[2].getElementsByTag("td")[3].text())
                    predictedEps.add(rows[2].getElementsByTag("td")[5].text())
                    shortFloat.add(rows[2].getElementsByTag("td")[9].text())
                    targetPrice.add(rows[4].getElementsByTag("td")[9].text())
                    price.add(rows[10].getElementsByTag("td")[11].text())
                    recom.add(rows[11].getElementsByTag("td")[1].text())
                } catch (e: IOException) {
                    e.printStackTrace()
                    println("$ticker: $e")
                    insiderTrans.add("-")
                    perfWeek.add("-")
                    peg.add("-")
                    predictedEps.add("-")
                    shortFloat.add("-")
                    targetPrice.add("-")
                    price.add("-")
                    recom.add("-")
                }
                db.addReport(
                    tickers.get(i),
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
                    updateFlag.get(i)
                )
                db.close()
            }
        }
    }
    private val context: Context? = null
    var secondDoc: Document? = null
    var calendar: Calendar = Calendar.getInstance()
    var tickers = ArrayList<Int>()

    // 0 = Add 1 = Update
    var updateFlag = ArrayList<Int>()
    var db = AppDatabase.getInstance(context!!)

    fun EpsHistory(ticker: String, j: Int) {
        val decodeFormatter = SimpleDateFormat("yyyy-MM-dd")
        val dateFormat: DateFormat = SimpleDateFormat("hh:mm:ss")
        val quarterDates = ArrayList<Date>()
        val quarterBells = ArrayList<Int>()
        try {
            val noon = dateFormat.parse("12:00:00")
            val currentDate = LocalDate.now()
            val sinceDate = currentDate.minusYears(2)
            var doc =
                Jsoup.connect("https://api.benzinga.com/api/v2.1/calendar/earnings?token=1c2735820e984715bc4081264135cb90&parameters[date_from]=$sinceDate&parameters[date_to]=$currentDate&parameters[tickers]=$ticker&pagesize=1000")
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
            try {
                firstEps.add(rows[0].text())
            } catch (e: Exception) {
                firstEps.add("-")
            }
            try {
                secondEps.add(rows[1].text())
            } catch (e: Exception) {
                secondEps.add("-")
            }
            try {
                thirdEps.add(rows[2].text())
            } catch (e: Exception) {
                thirdEps.add("-")
            }
            try {
                fourthEps.add(rows[3].text())
            } catch (e: Exception) {
                fourthEps.add("-")
            }
            try {
                fifthEps.add(rows[4].text())
            } catch (e: Exception) {
                fifthEps.add("-")
            }
            val firstDate = decodeFormatter.parse(earningsDates[0].text())
            val secondDate = decodeFormatter.parse(earningsDates[1].text())
            val thirdDate = decodeFormatter.parse(earningsDates[2].text())
            val fourthDate = decodeFormatter.parse(earningsDates[3].text())
            quarterDates.add(firstDate)
            quarterDates.add(secondDate)
            quarterDates.add(thirdDate)
            quarterDates.add(fourthDate)
            PriceHistory(ticker, quarterDates, quarterBells, j)
        } catch (e: Exception) {
            println("EpsHistory: $ticker: $e")
            firstEps.add("-")
            secondEps.add("-")
            thirdEps.add("-")
            fourthEps.add("-")
            fifthEps.add("-")
            val fromTo: ArrayList<*> = ArrayList<String>()
            while (fromTo.size < 8) {
                fromTo.add("-")
            }
            priceChange.put(j, fromTo)
            volatility.add("-")
            average.add("-")
        }
    }
}


fun GuidanceHistory(ticker: String) {
    try {
        val currentDate = LocalDate.now()
        val sinceDate = currentDate.minusYears(2)
        secondDoc =
            Jsoup.connect("https://api.benzinga.com/api/v2.1/calendar/guidance?token=1c2735820e984715bc4081264135cb90&parameters[date_from]=$sinceDate&parameters[date_to]=$currentDate&parameters[tickers]=$ticker&pagesize=1000")
                .get()
        val min = secondDoc.getElementsByTag("eps_guidance_min")
        val max = secondDoc.getElementsByTag("eps_guidance_max")
        val est = secondDoc.getElementsByTag("eps_guidance_est")
        guidanceMin.add(min[0].text())
        guidanceMax.add(max[0].text())
        guidanceEst.add(est[0].text())
    } catch (e: Exception) {
        println("GuidanceHistory: $ticker: $e")
        guidanceMin.add("-")
        guidanceMax.add("-")
        guidanceEst.add("-")
    }
}

fun PriceHistory(
    ticker: String,
    quarterDates: ArrayList<Date>,
    quarterBells: ArrayList<Int>,
    j: Int
) {
    calendar = Calendar.getInstance()
    val fromTo: ArrayList<*> = ArrayList<String>()
    var fromDate: Long
    var toDate: Long
    var from: String
    var to: String
    var day: Int
    var avg = 0.00
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
            doc =
                Jsoup.connect("https://finance.yahoo.com/quote/$ticker/history?period1=$fromDate&period2=$toDate&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true")
                    .get()
            val cell = doc.getElementsByTag("td")
            if (quarterBells[i] == 0) {
                from = cell[11].text()
                to = cell[4].text()
            } else {
                from = cell[8].text()
                to = cell[1].text()
            }
            //from
            fromTo.add(from)
            //to
            fromTo.add(to)
            unfinishedVolatility.add((to.toDouble() - from.toDouble()) / Math.abs(from.toDouble()) * 100)
        }
        for (i in unfinishedVolatility.indices) {
            total += unfinishedVolatility[i]
            totalAbs += Math.abs(unfinishedVolatility[i])
        }
        avg = total / unfinishedVolatility.size
        average.add((avg.toString() + "").substring(0, 4))
        volatility.add((totalAbs / unfinishedVolatility.size.toString() + "").substring(0, 4))
    } catch (e: Exception) {
        println("PriceHistory: $ticker: $e")
        while (fromTo.size < 8) {
            fromTo.add("-")
        }
        average.add("-")
        volatility.add("-")
    }
    priceChange.put(j, fromTo)
} //end PriceHistory

fun DuplicateCheck(): ArrayList<*> {
    val cursor: Cursor = db.getRecentTickers()
    while (cursor.moveToNext()) {
        recent_tickers.add(cursor.getString(0))
    }
    return recent_tickers
}
}