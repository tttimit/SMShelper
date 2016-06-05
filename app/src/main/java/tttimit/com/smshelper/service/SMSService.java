package tttimit.com.smshelper.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tttimit.com.smshelper.Utils.ContactUtils;
import tttimit.com.smshelper.Utils.DBHelper;
import tttimit.com.smshelper.Utils.Dao;
import tttimit.com.smshelper.Utils.MsgSender;

/**
 * Created by tttimit on 2016/5/29.
 * <p/>
 * * 当收到短信时，本服务启动，开始以下流程：
 * 0、该号码已经存在于已发送库中
 * 1、什么都不做
 * 0、该号码不存在于已发送库
 * 1、开启计时器，20秒后检查短信库，如果存在未读短信
 * 2、发送短信库B中的短信到目标号码
 * 3、将目标号码存储到已发送库中
 * 1、如果20秒检查收件箱发现不存在未读短信（已经看过了）
 * 2、什么都不做
 */
public class SMSService extends Service {

    private static final String TAG = "SMSService";
    private Context mContext;
    private Handler handler;
    private String number;
    private Dao dao;

    private Runnable runnableB = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "时间到，执行短信相关的任务！");
            boolean isSent = dao.isSent(ContactUtils.getContactNameByPhoneNumber(mContext, number),
                    number, new Date().toLocaleString());
            if (isUnread(number) && !isSent) {
                new MsgSender().sentMsg(mContext, number, dao.getAllMessages(DBHelper.TABLE_LIB_B));
            }
            Log.i(TAG, "短信已发送至：" + number);
        }
    };

//    private String getUnreadSmsNumber() {
//        ContentResolver resolver = getContentResolver();
//        Cursor cursor = resolver.query(Uri.parse("content://sms"), null, "type = 1 and read = 0",
//                null, null);
//        int phoneColumn = cursor.getColumnIndex("address");
//        List<String> numbers = new ArrayList<String>();
//        while(cursor.moveToNext()){
//            numbers.add(cursor.getString(phoneColumn));
//        }
//        for(int i = 0;i < numbers.size(); i++){
//
//        }
//    }

    private boolean isUnread(String number) {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(Uri.parse("content://sms"), null, "address = ?",
                new String[]{number}, null);
        int count = cursor.getCount();
        if (count > 1) {
            Log.i(TAG, "短信数量超过一条，实际为：" + count + "条。");
        }
        if (cursor.moveToFirst()) {
            int typeColumn = cursor.getColumnIndex("type");
            int readColumn = cursor.getColumnIndex("read");
            if (cursor.getInt(typeColumn) == 1 && cursor.getInt(readColumn) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sp = getSharedPreferences("setting", MODE_PRIVATE);
        int APP_STATUS = sp.getInt("app_status", 0);
        if (APP_STATUS == 1) {
            dao = Dao.getSingleDao(mContext);
            number = intent.getStringExtra("number");
            handler.postDelayed(runnableB, 40 * 1000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
