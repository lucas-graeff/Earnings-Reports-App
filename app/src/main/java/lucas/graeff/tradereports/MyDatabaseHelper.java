package lucas.graeff.tradereports;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Date;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Report.db";
    private static final int DATABASE_VERSION = 2;

    //Report columns
    private static final String TABLE_NAME = "reports";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TICKER = "ticker";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_PREDICTED_MOVE = "predicted_move";
    private static final String COLUMN_ESP = "esp";
    private static final String COLUMN_Z_SCORE = "z_score";
    private static final String COLUMN_MOMENTUM = "momentum";
    private static final String COLUMN_VGM = "vgm";
    private static final String COLUMN_SINCE_LAST = "since_last_earnings";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_LIST = "list";

    //Post columns
    private static final String COLUMN_EPS_SURPRISE = "eps_surprise";
    private static final String COLUMN_CHANGE = "change";


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
                        COLUMN_PREDICTED_MOVE + " INTEGER, " +
                        COLUMN_ESP + " DOUBLE, " +
                        COLUMN_Z_SCORE+ " INTEGER, " +
                        COLUMN_MOMENTUM + " TEXT, " +
                        COLUMN_VGM + " TEXT, " +
                        COLUMN_SINCE_LAST + " DOUBLE," +
                        COLUMN_TIME + " INTEGER," +
                        COLUMN_EPS_SURPRISE + " DOUBLE," +
                        COLUMN_CHANGE + " DOUBLE," +
                        COLUMN_LIST + " INTEGER);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addReport(String ticker, String date, double predictedMove, double esp, int zscore, String momentum, String vgm, double sinceLast, int time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TICKER, ticker);
        cv.put(COLUMN_DATE, String.valueOf(date));
        cv.put(COLUMN_PREDICTED_MOVE, predictedMove);
        cv.put(COLUMN_ESP, esp);
        cv.put(COLUMN_Z_SCORE, zscore);
        cv.put(COLUMN_MOMENTUM, momentum);
        cv.put(COLUMN_VGM, vgm);
        cv.put(COLUMN_SINCE_LAST, sinceLast);
        cv.put(COLUMN_TIME, time);

        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
        }

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

    //Query post earnings info
    public Cursor PostInfo(boolean list){
        String query;
        if(list) {
            query = "SELECT * FROM " + TABLE_NAME + " WHERE change NOT NULL AND list = 1 LIMIT 50";
        }
        else {
            query = "SELECT * FROM " + TABLE_NAME + " WHERE change NOT NULL LIMIT 50";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
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
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE reports.date > date('now', '-2 day')";
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
                "                AND reports.date > date('now', '-2 day')\n" +
                "                 AND predicted_move > 1";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}
