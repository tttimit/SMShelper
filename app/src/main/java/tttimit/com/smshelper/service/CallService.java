package tttimit.com.smshelper.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.*;
import android.support.annotation.Nullable;
import android.util.Log;

import tttimit.com.smshelper.Utils.Dao;

/**
 * Created by tttimit on 2016/5/29.
 * <p>
 * * 当接到未接来电时，本服务启动，开始以下流程：
 * <pr>
 * 0、该号码已经存在于已发送库中
 * 1、什么都不做
 * 0、该号码不存在于已发送库
 * 1、直接开始计时，到达延时A时，随机发送短信库A中的短信到目标号码，当到达延时B时，随机发送短信库B中的内容到目标号码
 * 2、将目标号码存储到已发送库中
 * </pr>
 */
public class CallService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static final String TAG = "CallService";
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;
    private Handler mHandler;
    private Dao dao = new Dao(this);
    private Handler handler = new Handler();
    private int timeA;
    private int timeB;

    public Runnable runnableA = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "此处应为发送消息A");
        }
    };

    public Runnable runnableB = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "此处应为发送消息B");
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String number = intent.getStringExtra("INCOMING_NUMBER");
        if(number == "" ) number = "未知号码";
        String time = intent.getStringExtra("INCOMING_TIME");
        SharedPreferences sp = getSharedPreferences("setting", MODE_PRIVATE);
        int APP_STATUS = sp.getInt("app_status", 0);
        int TIME_FOR_A = sp.getInt("time_for_a", 10);
        int TIME_FOR_B = sp.getInt("time_for_b", 120);
//        Log.i(TAG, "接到广播，准备进行处理 appstatus: " + APP_STATUS + " time: " +
//                TIME_FOR_A + " " + TIME_FOR_B);

            if (APP_STATUS == 1 && !dao.isSent(number)) {
                Log.i(TAG, "开关已开启，该号码未发送过短信");
                handler.postDelayed(runnableA, TIME_FOR_A * 1000);
                handler.postDelayed(runnableB, TIME_FOR_B * 1000);
                if (dao.insertSent("name" + Math.random(), number, time))
                    Log.i(TAG, "CallService 处理完毕，为号码：" + number + " - " + time);
            }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    protected void (Intent intent) {
//        String number = intent.getStringExtra("INCOMING_NUMBER");
//        if(number == "" ) number = "未知号码";
//        String time = intent.getStringExtra("INCOMING_TIME");
//        SharedPreferences sp = getSharedPreferences("setting", MODE_PRIVATE);
//        int APP_STATUS = sp.getInt("app_status", 0);
//        int TIME_FOR_A = sp.getInt("time_for_a", 10);
//        int TIME_FOR_B = sp.getInt("time_for_b", 120);
////        Log.i(TAG, "接到广播，准备进行处理 appstatus: " + APP_STATUS + " time: " +
////                TIME_FOR_A + " " + TIME_FOR_B);
//        if (APP_STATUS == 1 && !dao.isSent(number)) {
//            Log.i(TAG, "开关已开启，该号码未发送过短信");
//            handler.postDelayed(runnableA, TIME_FOR_A * 1000);
//            handler.postDelayed(runnableB, TIME_FOR_B * 1000);
//            if(dao.insertSent("name" + Math.random(), number, time))
//                Log.i(TAG, "CallService 处理完毕，为号码：" + number + " - " + time);
//        }
//    }

}
