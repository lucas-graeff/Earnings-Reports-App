package lucas.graeff.tradereports.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Report(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "ticker") val ticker: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "bell") val bell: Int,
    @ColumnInfo(name = "volatility") val volatility: Double,
    @ColumnInfo(name = "average") val average: Double,
    @ColumnInfo(name = "recommended") val recommended: Double,
    @ColumnInfo(name = "peg") val peg: Double,
    @ColumnInfo(name = "predicted_eps") val predictedEps: Double,
    @ColumnInfo(name = "since_last") val sinceLast: Double,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "insider_transactions") val insiderTrans: Double,
    @ColumnInfo(name = "short_float") val shortFloat: Double,
    @ColumnInfo(name = "target_price") val targetPrice: Double,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "performance_week") val performanceWeek: Double,
    @ColumnInfo(name = "eps_first") val epsFirst: Double,
    @ColumnInfo(name = "eps_second") val epsSecond: Double,
    @ColumnInfo(name = "eps_third") val epsThird: Double,
    @ColumnInfo(name = "eps_fourth") val epsFourth: Double,
    @ColumnInfo(name = "eps_fifth") val epsFifth: Double,
    @ColumnInfo(name = "quarter_performance_first") val quarterPerformanceFirst: Double,
    @ColumnInfo(name = "quarter_performance_second") val quarterPerformanceSecond: Double,
    @ColumnInfo(name = "quarter_performance_third") val quarterPerformanceThird: Double,
    @ColumnInfo(name = "quarter_performance_fourth") val quarterPerformanceFourth: Double,
    @ColumnInfo(name = "guidance_min") val guidanceMin: Double,
    @ColumnInfo(name = "guidance_max") val guidanceMax: Double,
    @ColumnInfo(name = "guidance_estimate") val guidanceEstimate: Double,
    @ColumnInfo(name = "result_eps") val resultEps: Double,
    @ColumnInfo(name = "result_from") val resultFrom: Double,
    @ColumnInfo(name = "result_to") val resultTo: Double,
    @ColumnInfo(name = "result_change") val resultChange: Double,
    @ColumnInfo(name = "list") var list: Int
)
