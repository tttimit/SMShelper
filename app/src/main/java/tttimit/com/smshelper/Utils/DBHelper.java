package tttimit.com.smshelper.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by tttimit on 2016/5/29.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Data.db";
    public static final String TABLE_SENT_NUMBERS = "SentNumbers";  //已发号码的表
    public static final String TABLE_LIB_A = "LIB_A";   //信息库A的表
    public static final String TABLE_LIB_B = "LIB_B";   //信息库B的表
    private static final String TAG = "DBHelper";
    private ContentValues cv;


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
        String sql1 = "create table if not exists " + TABLE_SENT_NUMBERS + " (Id integer primary key, Name text, Number text, Time text)";
        db.execSQL(sql1);
        String sql2 = "create table if not exists " + TABLE_LIB_A + " (Id integer primary key, Content text)";
        db.execSQL(sql2);
        String sql3 = "create table if not exists " + TABLE_LIB_B + " (Id integer primary key, Content text)";
        db.execSQL(sql3);
        initMessageLib(db);
    }

    private void initMessageLib(SQLiteDatabase db) {
        //为消息库A初始化数据
        cv = new ContentValues();
        cv.put("Content", "不好意思，现在有点忙，稍后给你回复。");
        db.insert(TABLE_LIB_A, null, cv);
        cv.clear();
        cv.put("Content", "临时有点急事，不方便现在回复，稍后联系！");
        db.insert(TABLE_LIB_A, null, cv);

        //为消息库B初始化数据
        cv.clear();
        cv.put("Content", "刚才那会有点事情，现在还没忙完，稍后有时间了给你回复，请谅解！");
        db.insert(TABLE_LIB_B, null, cv);
        cv.clear();
        cv.put("Content", "抱歉，实在太忙抽不开身，晚点有时间了再联系您！");
        db.insert(TABLE_LIB_B, null, cv);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
