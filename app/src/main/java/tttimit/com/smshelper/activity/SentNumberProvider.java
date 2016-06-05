package tttimit.com.smshelper.activity;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import tttimit.com.smshelper.Utils.DBHelper;

/**
 * Created by tttimit on 2016/6/2.
 */
public class SentNumberProvider extends ContentProvider {
    private static final String TAG = "SentNumberProvider";
    DBHelper dbHelper;
    UriMatcher matcher;
    public static final int TABLE_SENT_NUMBERS = 0;
    @Override
    public boolean onCreate() {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authorities = "com.tttimit.smshelper.sqlite.provider";
        matcher.addURI(authorities, "SentNumbers", TABLE_SENT_NUMBERS);
        dbHelper = new DBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long id=  db.insert("SentNumbers", null,  values);
        Log.i(TAG, "插入数据。。。在contentprivider里面");
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
