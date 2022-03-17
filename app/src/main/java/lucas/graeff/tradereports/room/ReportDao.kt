package lucas.graeff.tradereports.room

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery


@Dao
interface ReportDao {
    @Query("SELECT * FROM Report ORDER BY date ASC")
    fun getUpcomingReports(): List<Report>

    @Query("SELECT * FROM Report WHERE report.date > date('now', '-1 day') AND recommended < 2.5 AND performance_week > 0 AND predicted_eps > 0  AND peg < 1 ORDER BY date ASC")
    fun filterStrict(): List<Report>

    @Query("SELECT * FROM Report WHERE report.date > date('now', '-1 day') AND guidance_estimate > guidance_min ORDER BY date ASC")
    fun filterGuidance(): List<Report>

    @Query("SELECT * FROM Report WHERE report.date > date('now', '-1 day') AND eps_first > eps_second > eps_third> eps_fourth > eps_fifth ORDER BY date ASC")
    fun filterRaisingEps(): List<Report>

    @Query("SELECT * FROM Report WHERE report.date > date('now', '-1 day') AND quarter_performance_first > quarter_performance_second > quarter_performance_third > quarter_performance_fourth ORDER BY date ASC")
    fun filterPositiveChange(): List<Report>

    @Query("SELECT * FROM Report WHERE report.date > date('now', '-1 day') AND average > 0 ORDER BY average DESC")
    fun filterAverages(): List<Report>

    @Query("SELECT ticker FROM Report WHERE date > date('now', '-7 days')")
    fun recentReports(): List<String>

    @Query("SELECT * FROM Report WHERE report.date < date('now', '-1 day') AND result_change = 0.0")
    fun postAnalysis(): List<Report>

    @Query("SELECT * FROM Report WHERE list = 1 AND (report.date = date('now') OR report.date = date('now', '+1 days'))")
    fun notificationReports(): List<Report>

    @RawQuery
    fun rawQuery(query: SupportSQLiteQuery): List<Report>

    @Query("SELECT * FROM Report WHERE list = 1 ORDER BY date")
    fun getList(): List<Report>

    @Query("UPDATE Report SET list = :list WHERE id = :id")
    fun setList(list: Int, id: Int)

    @Query("SELECT * FROM Report WHERE result_change != 0.0 AND (list = :list OR list = 1) ORDER BY date DESC LIMIT 50")
    fun postResults(list: Int): List<Report>


    @Insert
    fun addReport(report: Report)

    @Update
    fun updateReport(report: Report)

}