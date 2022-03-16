package lucas.graeff.tradereports.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Report(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "ticker") var ticker: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "bell") var bell: Int,
    @ColumnInfo(name = "volatility") var volatility: Double,
    @ColumnInfo(name = "average") var average: Double,
    @ColumnInfo(name = "recommended") var recommended: Double,
    @ColumnInfo(name = "peg") var peg: Double,
    @ColumnInfo(name = "predicted_eps") var predictedEps: Double,
    @ColumnInfo(name = "since_last") var sinceLast: Double,
    @ColumnInfo(name = "time") var time: String,
    @ColumnInfo(name = "insider_transactions") var insiderTrans: Double,
    @ColumnInfo(name = "short_float") var shortFloat: Double,
    @ColumnInfo(name = "target_price") var targetPrice: Double,
    @ColumnInfo(name = "price") var price: Double,
    @ColumnInfo(name = "performance_week") var performanceWeek: Double,
    @ColumnInfo(name = "eps_first") var epsFirst: Double,
    @ColumnInfo(name = "eps_second") var epsSecond: Double,
    @ColumnInfo(name = "eps_third") var epsThird: Double,
    @ColumnInfo(name = "eps_fourth") var epsFourth: Double,
    @ColumnInfo(name = "eps_fifth") var epsFifth: Double,
    @ColumnInfo(name = "quarter_performance_first") var quarterPerformanceFirst: Double,
    @ColumnInfo(name = "quarter_performance_second") var quarterPerformanceSecond: Double,
    @ColumnInfo(name = "quarter_performance_third") var quarterPerformanceThird: Double,
    @ColumnInfo(name = "quarter_performance_fourth") var quarterPerformanceFourth: Double,
    @ColumnInfo(name = "guidance_min") var guidanceMin: Double,
    @ColumnInfo(name = "guidance_max") var guidanceMax: Double,
    @ColumnInfo(name = "guidance_estimate") var guidanceEstimate: Double,
    @ColumnInfo(name = "result_eps") var resultEps: Double,
    @ColumnInfo(name = "result_from") var resultFrom: Double,
    @ColumnInfo(name = "result_to") var resultTo: Double,
    @ColumnInfo(name = "result_change") var resultChange: Double,
    @ColumnInfo(name = "list") var list: Int
) {
    constructor() : this(0, "", "", -1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0) {

    }
}
