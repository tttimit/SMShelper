package tttimit.com.smshelper.Utils;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tttimit on 2016/5/29.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Data.db";
    public static final String TABLE_SENT_NUMBERS = "SentNumbers";  //已发号码的表
    public static final String TABLE_LIB_A = "LIB_A";   //信息库A的表
    public static final String TABLE_LIB_B = "LIB_B";   //信息库B的表


    private Context mContext;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }



    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table Orders(Id integer primary key, CustomName text, OrderPrice integer, Country text);
        String sql1 = "create table if not exists " + TABLE_SENT_NUMBERS + " (Id integer primary key, Name text, Number text, Time text)";
        db.execSQL(sql1);
        String sql2 = "create table if not exists " + TABLE_LIB_A + " (Id integer primary key, Content text)";
        db.execSQL(sql2);
        String sql3 = "create table if not exists " + TABLE_LIB_B + " (Id integer primary key, Content text)";
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
