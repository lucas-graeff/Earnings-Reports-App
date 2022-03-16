package lucas.graeff.tradereports.room

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery


@Dao
interface ReportDao {
    @Query("SELECT * FROM Report")
    fun getUpcomingReports(): List<Report>

    @Query("SELECT ticker FROM Report WHERE date > date('now', '-7 days')")
    fun recentReports(): List<String>

    @RawQuery
    fun rawQuery(query: SupportSQLiteQuery): List<Report>

    @Query("UPDATE Report SET list = :list WHERE id = :id")
    fun setList(list: Int, id: Int)

    @Insert
    fun addReport(report: Report)

    @Update
    fun updateReport(report: Report)

}