package tttimit.com.smshelper.Utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tttimit.com.smshelper.Domain.Item;
import tttimit.com.smshelper.Domain.Message;

/**
 * Created by tttimit on 2016/5/29.
 */
public class Dao {
    private static final String TAG = "Dao";
    private static Dao  singleDao;

    // 列定义
    private final String[] SENT_COLUMNS = new String[]{"Id", "Name", "Number", "Time"};
    private final String[] MESSAGE_COLUMNS = new String[]{"Id", "Content"};


    private Context mContext;
    private DBHelper dbHelper;

    private Dao(Context context) {
        this.mContext = context;
        dbHelper = new DBHelper(context);
    }

    public static Dao getSingleDao(Context context){
        if(singleDao == null){
            singleDao = new Dao(context);
        }
        return singleDao;
    }

    /**
     * 将数据插入已发送表
     *
     * @param name   联系人姓名
     * @param number 来电号码
     * @param time   来电时间
     * @return 返回true表明成功，false表明失败
     */
    public boolean insertSent(String name, String number, String time) {
        ContentValues values = new ContentValues();
        values.put("Name", name);
        values.put("Number", number);
        values.put("Time", time);
        return insertData(DBHelper.TABLE_SENT_NUMBERS, values);
    }

    /**
     * 将消息内容插入指定的消息库中
     *
     * @param tableName 需要确定插入到哪个库中
     * @param content   要插入的内容
     * @return
     */
    public boolean insertLib(String tableName, String content) {
        ContentValues values = new ContentValues();
        values.put("Content", content);
        return insertData(tableName, values);
    }


    private boolean insertData(String tableName, ContentValues contentValues) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();

            db.beginTransaction();

            db.insertOrThrow(tableName, null, contentValues);

            db.setTransactionSuccessful();
            return true;
        } catch (SQLiteConstraintException e) {
            Toast.makeText(mContext, "主键重复", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "插入数据时出错", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }


    /**
     * 删除一条数据, 根据id来操作
     */
    public boolean deleteItem(String tableName, int id) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            db.delete(tableName, "Id = ?", new String[]{String.valueOf(id)});

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    public boolean deleteAllSentNumbers(){
        SQLiteDatabase db = null;
        try{
            db =  dbHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete(DBHelper.TABLE_SENT_NUMBERS, null, null);
            db.setTransactionSuccessful();
            return true;
        }catch (Exception e){
            Log.e(TAG, "清空已发送表出错！");
        }finally{
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    /**
     * 判断表中是否有数据
     */
    public boolean isDataExist(String tableName) {
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(tableName, new String[]{"COUNT(Id)"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            if (count > 0) return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    public synchronized boolean isSent(String name, String number, String time) {
        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if("".equals(number)){
            number = "未知号码";
        }
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(DBHelper.TABLE_SENT_NUMBERS, SENT_COLUMNS, "Number = ? OR Number = ?",
                    new String[]{number, "+86" + number}, null, null, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            if (count > 0) {
                return true;
            }else{
                ContentValues values = new ContentValues();
                values.put("Name", name);
                values.put("Number", number);
                values.put("Time", time);
                ContentResolver resolver = mContext.getContentResolver();
                Uri uri = Uri.parse("content://com.tttimit.smshelper.sqlite.provider");
                resolver.insert(uri, values);
                resolver.notifyChange(uri, null);
//                insertSent(name, number, time);
//                String updateSentNumberLib = "com.example.BroadcastReceiverDemo";
//                Intent intent = new Intent(updateSentNumberLib);
//                mContext.sendBroadcast(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "读取已发送号码库出现错误！", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * 修改一条数据，目前可以想到的就是修改消息库中的消息
     */
    public boolean updateMessage(String tableName, int id, String msg) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            ContentValues cv = new ContentValues();
            cv.put("Content", msg);
            db.update(tableName, cv, "Id = ?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "修改消息出错！", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }

        return false;
    }

    /**
     * 执行自定义SQL语句
     */
    public void execSQL(String sql) {
        SQLiteDatabase db = null;

        try {
            if (sql.contains("select")) {
                Toast.makeText(mContext, "执行查找", Toast.LENGTH_SHORT).show();
            } else if (sql.contains("insert") || sql.contains("update") || sql.contains("delete")) {
                db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                db.execSQL(sql);
                db.setTransactionSuccessful();
                Toast.makeText(mContext, "执行插入/更新/删除", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "数据库读写出错！", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }


    /**
     * 从数据库中查询出所有的消息
     */
    public synchronized List<Message> getAllMessages(String tableName) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(tableName, MESSAGE_COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                List<Message> listOfMessages = new ArrayList<Message>();
                while(cursor.moveToNext()){
                    listOfMessages.add(parseMessage(cursor));
                }
                return listOfMessages;
            }
        } catch (Exception e) {
            Log.e(TAG, "读取消息库出现错误, 库：" + tableName, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return null;
    }

    public void delete1Number(int id){

    }


    /**
     * 查询是否存在已发送的号码
     */
    public synchronized List<Item> getSentNumbers() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(DBHelper.TABLE_SENT_NUMBERS, SENT_COLUMNS, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<Item> orderList = new ArrayList<Item>(cursor.getCount());
                while (cursor.moveToNext()) {
                    orderList.add(parseItem(cursor));
                }
                return orderList;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return null;
    }

    /**
     * 将查找到的数据转换成Item类，包含ID，来电号码，姓名，时间
     */
    private Item parseItem(Cursor cursor) {
        Item item = new Item();
        item.id = (cursor.getInt(cursor.getColumnIndex("Id")));
        item.name = (cursor.getString(cursor.getColumnIndex("Name")));
        item.number = (cursor.getString(cursor.getColumnIndex("Number")));
        item.time = (cursor.getString(cursor.getColumnIndex("Time")));
        return item;
    }

    /**
     * 传入库A或者库B的表名，获取其中的所有数据
     *
     * @param tableName
     * @return
     */
    public List<Message> getMessage(String tableName) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String colName[];

        try {
            db = dbHelper.getReadableDatabase();
            // select * from Orders
            cursor = db.query(tableName, MESSAGE_COLUMNS, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<Message> orderList = new ArrayList<Message>(cursor.getCount());
                while (cursor.moveToNext()) {
                    orderList.add(parseMessage(cursor));
                }
                return orderList;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return null;
    }

    /**
     * 将查找到的数据转换成Message类，包含id和预设消息内容
     */
    private Message parseMessage(Cursor cursor) {
        Message msg = new Message();
        msg.id = (cursor.getInt(cursor.getColumnIndex("Id")));
        msg.content = (cursor.getString(cursor.getColumnIndex("Content")));
        return msg;
    }
}
