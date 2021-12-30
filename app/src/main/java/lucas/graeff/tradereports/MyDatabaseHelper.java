package lucas.graeff.tradereports;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Report.db";
    private static final int DATABASE_VERSION = 2;

    //Report columns
    private static final String TABLE_NAME = "reports";
//    private static final String COLUMN_ID = "id";
//    private static final String COLUMN_TICKER = "ticker";
//    private static final String COLUMN_DATE = "date";
//    private static final String COLUMN_PREDICTED_MOVE = "predicted_move";
//    private static final String COLUMN_ESP = "esp";
//    private static final String COLUMN_Z_SCORE = "z_score";
//    private static final String COLUMN_MOMENTUM = "momentum";
//    private static final String COLUMN_VGM = "vgm";
//    private static final String COLUMN_SINCE_LAST = "since_last_earnings";
//    private static final String COLUMN_TIME = "time";
//    private static final String COLUMN_LIST = "list";

    private static final String COLUMN_ID = "id";
    private static final String  COLUMN_TICKER = "ticker";
    private static final String  COLUMN_DATE  = "date";
    private static final String  COLUMN_TIME  = "time";
    private static final String  COLUMN_BELL = "bell";

    private static final String  COLUMN_RECOM = "recom";
    private static final String  COLUMN_PEG = "peg";
    private static final String  COLUMN_PREDICTED_EPS = "predicted_eps";
    private static final String  COLUMN_INSIDER_TRANS = "insider_trans";
    private static final String  COLUMN_SHORT_FLOAT = "short_float";
    private static final String  COLUMN_TARGET_PRICE = "target_price";
    private static final String  COLUMN_PRICE = "price";
    private static final String  COLUMN_PERFORMANCE_WEEK = "perf_week";

    private static final String  COLUMN_FIRST_EPS = "first_eps";
    private static final String  COLUMN_SECOND_EPS = "second_eps";
    private static final String  COLUMN_THIRD_EPS = "third_eps";
    private static final String  COLUMN_FOURTH_EPS = "fourth_eps";
    private static final String  COLUMN_FIFTH_EPS = "fifth_eps";

    private static final String  COLUMN_FIRST_FROM = "first_from";
    private static final String  COLUMN_FIRST_TO = "first_to";
    private static final String  COLUMN_SECOND_FROM = "second_from";
    private static final String  COLUMN_SECOND_TO = "second_to";
    private static final String  COLUMN_THIRD_FROM = "third_from";
    private static final String  COLUMN_THIRD_TO = "third_to";
    private static final String  COLUMN_FOURTH_FROM = "fourth_from";
    private static final String  COLUMN_FOURTH_TO = "fourth_to";

    private static final String COLUMN_GUIDANCE_MIN = "guidance_min";
    private static final String COLUMN_GUIDANCE_MAX = "guidance_max";
    private static final String COLUMN_GUIDANCE_EST = "guidance_est";

    //Calculated Columns
    private static final String  COLUMN_VOLATILITY = "volatility";
    private static final String  COLUMN_SINCE_LAST = "since_last";

    //Post Analysis Columns
    private static final String  COLUMN_ACTUAL_EPS = "actual_eps";
    private static final String  COLUMN_ACTUAL_FROM = "actual_from";
    private static final String  COLUMN_ACTUAL_TO = "actual_to";

    //Post columns
    private static final String COLUMN_EPS_SURPRISE = "eps_surprise";
    private static final String COLUMN_CHANGE = "change";

    //List column
    private static final String  COLUMN_LIST = "list";




    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create report table
        String query =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TICKER + " TEXT, " +
                        COLUMN_DATE + " DATE, " +
                        COLUMN_BELL + " INTEGER, " +
                        COLUMN_VOLATILITY + " DOUBLE, " +
                        COLUMN_RECOM + " DOUBLE, " +
                        COLUMN_PEG + " DOUBLE, " +
                        COLUMN_PREDICTED_EPS + " DOUBLE, " +
                        COLUMN_SINCE_LAST + " DOUBLE," +
                        COLUMN_TIME + " TEXT," +
                        COLUMN_INSIDER_TRANS + " DOUBLE," +
                        COLUMN_SHORT_FLOAT + " DOUBLE," +
                        COLUMN_TARGET_PRICE + " DOUBLE," +
                        COLUMN_PRICE + " DOUBLE," +
                        COLUMN_PERFORMANCE_WEEK + " DOUBLE," +
                        COLUMN_FIRST_EPS+ " DOUBLE," +
                        COLUMN_SECOND_EPS + " DOUBLE," +
                        COLUMN_THIRD_EPS + " DOUBLE," +
                        COLUMN_FOURTH_EPS + " DOUBLE," +
                        COLUMN_FIFTH_EPS + " DOUBLE," +
                        COLUMN_FIRST_FROM + " DOUBLE," +
                        COLUMN_FIRST_TO + " DOUBLE," +
                        COLUMN_SECOND_FROM+ " DOUBLE," +
                        COLUMN_SECOND_TO + " DOUBLE," +
                        COLUMN_THIRD_FROM + " DOUBLE," +
                        COLUMN_THIRD_TO + " DOUBLE," +
                        COLUMN_FOURTH_FROM + " DOUBLE," +
                        COLUMN_FOURTH_TO + " DOUBLE," +
                        COLUMN_GUIDANCE_MIN + " DOUBLE," +
                        COLUMN_GUIDANCE_MAX+ " DOUBLE," +
                        COLUMN_GUIDANCE_EST + " DOUBLE," +
                        COLUMN_ACTUAL_EPS + " DOUBLE," +
                        COLUMN_ACTUAL_FROM + " DOUBLE," +
                        COLUMN_ACTUAL_TO + " DOUBLE," +
                        COLUMN_EPS_SURPRISE + " DOUBLE," +
                        COLUMN_CHANGE + " DOUBLE," +
                        COLUMN_LIST + " INTEGER DEFAULT 0);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addReport(String ticker, String date, int bell, String volatlity, String recom, String peg, String predictedEps, String time, String insiderTrans,
                          String shortFloat, String targetPrice, String price, String perfWeek, String firstEps, String secondEps, String thirdEps,
                          String fourthEps, String fifthEps, String firstFrom, String firstTo, String secondFrom, String secondTo, String thirdFrom, String thirdTo,
                          String fourthFrom, String fourthTo, String guidanceMin, String guidanceMax, String guidanceEst) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TICKER, ticker);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_BELL, bell);
        cv.put(COLUMN_VOLATILITY, volatlity);
        cv.put(COLUMN_RECOM, recom);
        cv.put(COLUMN_PEG, peg);
        cv.put(COLUMN_PREDICTED_EPS, predictedEps);
        cv.put(COLUMN_TIME, time);
        cv.put(COLUMN_INSIDER_TRANS, insiderTrans);
        cv.put(COLUMN_SHORT_FLOAT, shortFloat);
        cv.put(COLUMN_TARGET_PRICE, targetPrice);
        cv.put(COLUMN_PRICE, price);
        cv.put(COLUMN_PERFORMANCE_WEEK, perfWeek);
        cv.put(COLUMN_FIRST_EPS, firstEps);
        cv.put(COLUMN_SECOND_EPS, secondEps);
        cv.put(COLUMN_THIRD_EPS, thirdEps);
        cv.put(COLUMN_THIRD_EPS, thirdEps);
        cv.put(COLUMN_FOURTH_EPS, fourthEps);
        cv.put(COLUMN_FIFTH_EPS, fifthEps);
        cv.put(COLUMN_FIRST_FROM, firstFrom);
        cv.put(COLUMN_FIRST_TO, firstTo);
        cv.put(COLUMN_SECOND_FROM, secondFrom);
        cv.put(COLUMN_SECOND_TO, secondTo);
        cv.put(COLUMN_THIRD_FROM, thirdFrom);
        cv.put(COLUMN_THIRD_TO, thirdTo);
        cv.put(COLUMN_FOURTH_FROM, fourthFrom);
        cv.put(COLUMN_FOURTH_TO, fourthTo);
        cv.put(COLUMN_GUIDANCE_MIN, guidanceMin);
        cv.put(COLUMN_GUIDANCE_MAX, guidanceMax);
        cv.put(COLUMN_GUIDANCE_EST, guidanceEst);

        db.insert(TABLE_NAME, null, cv);

    }

    //Find the id related to the ticker within last 2 weeks
    public int FindId(String ticker) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query =
                "SELECT id, ticker, date FROM reports WHERE reports.date > date('now', '-14 days') AND ticker = '" + ticker + "'";
        cursor = db.rawQuery(query, null);

        try{
                cursor.moveToNext();
                return cursor.getInt(0);
        }
        catch (Exception e) {

        }

        return 0;
    }

    //Add post earnings info
    public void AddPost(int id, double surprise, double change) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE reports SET eps_surprise = ? , change = ? WHERE id = ?;";

        db.execSQL(query, new String[] {String.valueOf(surprise), String.valueOf(change), String.valueOf(id)});
    }

    public void UpdateList(int id, int list) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE reports SET list = ? WHERE id = ?;";

        db.execSQL(query, new String[] {String.valueOf(list), String.valueOf(id)});
    }


    public Cursor getRecentTickers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String query =
                "SELECT ticker, date FROM reports WHERE reports.date > date('now', '-1 month')";
        cursor = db.rawQuery(query, null);

        return cursor;
    }


    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE reports.date > date('now', '-1 day')";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readQuery(String query) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    //Query post earnings info
    public Cursor PostInfo(boolean list){
        String query;
        if(list) {
            query = "SELECT * FROM " + TABLE_NAME + " WHERE change NOT NULL AND list = 1  ORDER BY date DESC LIMIT 50";
        }
        else {
            query = "SELECT * FROM " + TABLE_NAME + " WHERE change NOT NULL  ORDER BY date DESC LIMIT 50";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readListData() {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE reports.list = 1 LIMIT 100";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readFilteredData() {
        String query = "SELECT * FROM reports\n" +
                "WHERE z_score < 3\n" +
                "AND esp >= 0" +
                " AND reports.date > date('now', '-2 day')" +
                " AND predicted_move > 1" +
                " OR (z_score = 3 AND reports.vgm = 'A'  OR  z_score = 3 AND reports.vgm = 'B' )\n" +
                "                AND esp > 0\n" +
                "                AND reports.date > date('now', '-1 day')\n" +
                "                 AND predicted_move > 1";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}
