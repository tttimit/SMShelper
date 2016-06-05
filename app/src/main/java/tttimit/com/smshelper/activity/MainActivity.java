package tttimit.com.smshelper.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tttimit.com.smshelper.Domain.Item;
import tttimit.com.smshelper.Domain.Message;
import tttimit.com.smshelper.Domain.MessageListAdapter;
import tttimit.com.smshelper.Domain.SentNumberListAdapter;
import tttimit.com.smshelper.R;
import tttimit.com.smshelper.Utils.DBHelper;
import tttimit.com.smshelper.Utils.Dao;
import tttimit.com.smshelper.receiver.CallReceiver;
import tttimit.com.smshelper.receiver.SmsReceiver;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher {

    private Context mContext = this;
    private TabHost mTabHost;
    //    private FrameLayout mFrameLayout;
//    private LinearLayout ll_home;       //主页面
//    private LinearLayout ll_lib;        //库A和库B
//    private LinearLayout ll_sent;       //已发送的号码
    private Switch switch1;         //应用开关
    private EditText time_a;        //A库延时
    private EditText time_b;        //B库延时
    private Button bt_add_a;        //为A库添加消息
    private Button bt_add_b;        //为B库添加消息
    private Button bt_remove_all;        //移除已发送中所有的记录
    private ListView lv_msgLib_a;
    private ListView lv_msgLib_b;
    private ListView lv_sent;
    private static final String TAG = "SMS_MainActivity";
    public static String SMS_RECEIVE_INTENT = "android.provider.Telephony.SMS_RECEIVED";
    public static String MISSED_CALL_INTENT = "com.android.phone.NotificationMgr.MissedCall_intent";
    public static int APP_STATUS;
    public static int TIME_FOR_A;
    public static int TIME_FOR_B;

//    private IntentFilter filter_sms;
//    private IntentFilter filter_call;
//    private SmsReceiver smsReceiver;
//    private CallReceiver callReveiver;

    private Dao dao;
    private List<Message> msgList_A;
    private List<Message> msgList_B;
    private List<Item> sentNumberList;
    private MessageListAdapter msgLibAdapter_A;
    private MessageListAdapter msgLibAdapter_B;
    private SentNumberListAdapter sentNumbersAdapter;

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
        initMessageList_SentNumbers();
    }

    /**
     * 初始化消息库A和消息库B以及已发送号码库
     */
    private void initMessageList_SentNumbers() {
        dao = Dao.getSingleDao(this);
        msgList_A = dao.getAllMessages(DBHelper.TABLE_LIB_A);
        msgList_B = dao.getAllMessages(DBHelper.TABLE_LIB_B);
        sentNumberList = dao.getSentNumbers();

        if (msgList_A != null) {
            msgLibAdapter_A = new MessageListAdapter(this, DBHelper.TABLE_LIB_A, msgList_A);
            lv_msgLib_a.setAdapter(msgLibAdapter_A);
        }
        if (msgList_B != null) {
            msgLibAdapter_B = new MessageListAdapter(this, DBHelper.TABLE_LIB_B, msgList_B);
            lv_msgLib_b.setAdapter(msgLibAdapter_B);
        }

        if (sentNumberList != null) {
            sentNumbersAdapter = new SentNumberListAdapter(this, sentNumberList);
            lv_sent.setAdapter(sentNumbersAdapter);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        mTabHost = (TabHost) findViewById(R.id.tabHost);
        switch1 = (Switch) findViewById(R.id.switch1);
        time_a = (EditText) findViewById(R.id.time_a);
        time_b = (EditText) findViewById(R.id.time_b);
        bt_add_a = (Button) findViewById(R.id.bt_add_a);
        bt_add_b = (Button) findViewById(R.id.bt_add_b);
        bt_remove_all = (Button) findViewById(R.id.bt_remove_all);
        lv_msgLib_a = (ListView) findViewById(R.id.lv_lib_a);
        lv_msgLib_b = (ListView) findViewById(R.id.lv_lib_b);
        lv_sent = (ListView) findViewById(R.id.lv_sent);
    }

    private void initData() {
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec("tab_home")
                .setIndicator("设置", getResources().getDrawable(R.drawable.setting))
                .setContent(R.id.ll_home));
        mTabHost.addTab(mTabHost.newTabSpec("tab_lib")
                .setIndicator("消息库", getResources().getDrawable(R.drawable.lib))
                .setContent(R.id.ll_lib));
        mTabHost.addTab(mTabHost.newTabSpec("tab_sent")
                .setIndicator("已发送", getResources().getDrawable(R.drawable.sent))
                .setContent(R.id.ll_sent));

//        filter_sms = new IntentFilter(SMS_RECEIVE_INTENT);
//        filter_call = new IntentFilter(MISSED_CALL_INTENT);
//        smsReceiver = new SmsReceiver();
//        callReveiver = new CallReceiver();

        pref = getSharedPreferences("setting", MODE_PRIVATE);
        APP_STATUS = pref.getInt("app_status", 0);
        TIME_FOR_A = pref.getInt("time_for_a", 10);
        TIME_FOR_B = pref.getInt("time_for_b", 120);

        switch1.setChecked(APP_STATUS == 1 ? true : false);
        time_a.setText(TIME_FOR_A + "");
        time_b.setText(TIME_FOR_B + "");

        registerSentNumberLibObserver();

    }

    private void registerSentNumberLibObserver() {
        Uri uri = Uri.parse("content://com.tttimit.smshelper.sqlite.provider");
        getContentResolver().registerContentObserver(uri, true, new MyContentObselver(new Handler()));
    }

    class MyContentObselver extends ContentObserver {
        public MyContentObselver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            sentNumberList = dao.getSentNumbers();
            if (sentNumberList != null) {
                sentNumbersAdapter = new SentNumberListAdapter(mContext, sentNumberList);
                lv_sent.setAdapter(sentNumbersAdapter);
                Log.i(TAG, "已发送号码列表已更新！");
            }
        }
    }

    private void initEvent() {
        time_a.addTextChangedListener(this);
        time_b.addTextChangedListener(this);
        time_b.setOnEditorActionListener(this);
        bt_add_a.setOnClickListener(this);
        bt_add_b.setOnClickListener(this);
        bt_remove_all.setOnClickListener(this);
        switch1.setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        int viewId = v.getId();
        if (viewId == R.id.time_a || viewId == R.id.time_b) {
            Log.i(TAG, "由于修改了时间，所以开关置为-关闭-");
            Toast.makeText(this, "由于修改了设置，应用开关已关闭，请在设置完成后手动开启", Toast.LENGTH_SHORT).show();
            switch1.setChecked(false);
            APP_STATUS = 0;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add_a:
                Log.i(TAG, "click bt_add_a");
                final EditText et_contentA = new EditText(this);
                new AlertDialog.Builder(this).setTitle("请输入消息").setIcon(
                        android.R.drawable.ic_dialog_info).setView(et_contentA)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String content = et_contentA.getText().toString();
                                if (dao.insertLib(DBHelper.TABLE_LIB_A, content)) {
                                    Toast.makeText(MainActivity.this, "消息添加成功！",
                                            Toast.LENGTH_SHORT).show();
                                    refreshList(DBHelper.TABLE_LIB_A);
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.bt_add_b:
                Log.i(TAG, "click bt_add_b");
                final EditText et_contentB = new EditText(this);
                new AlertDialog.Builder(this).setTitle("请输入消息").setIcon(
                        android.R.drawable.ic_dialog_info).setView(et_contentB)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String content = et_contentB.getText().toString();
                                if (dao.insertLib(DBHelper.TABLE_LIB_B, content)) {
                                    Toast.makeText(MainActivity.this, "消息添加成功！",
                                            Toast.LENGTH_SHORT).show();
                                    refreshList(DBHelper.TABLE_LIB_B);
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.bt_remove_all:
                Log.i(TAG, "click remove_all");
                dao.deleteAllSentNumbers();
                if (sentNumberList != null) {
                    sentNumberList.clear();
                    sentNumbersAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "已发送列表已清空！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void refreshList(String tableName) {
        switch (tableName) {
            case DBHelper.TABLE_LIB_A:
                msgList_A = dao.getAllMessages(tableName);
                lv_msgLib_a.setAdapter(new MessageListAdapter(this, DBHelper.TABLE_LIB_A, msgList_A));
                break;
            case DBHelper.TABLE_LIB_B:
                msgList_B = dao.getAllMessages(tableName);
                lv_msgLib_b.setAdapter(new MessageListAdapter(this, DBHelper.TABLE_LIB_B, msgList_B));
                break;
            case DBHelper.TABLE_SENT_NUMBERS:
                sentNumberList = dao.getSentNumbers();
                lv_sent.setAdapter(new SentNumberListAdapter(this, sentNumberList));
                break;
            default:
                initMessageList_SentNumbers();
                break;
        }

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (timeIsOk()) {
                Log.i(TAG, "switch is on!");
                APP_STATUS = 1;
            } else {
                Toast.makeText(this, "试用期已过！请联系开发者购买！", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.i(TAG, "switch is off!");
            APP_STATUS = 0;
        }

        TIME_FOR_A = Integer.parseInt(time_a.getText().toString());
        TIME_FOR_B = Integer.parseInt(time_b.getText().toString());

        SharedPreferences.Editor editor = pref.edit();

        editor.putInt("app_status", APP_STATUS);
        editor.putInt("time_for_a", TIME_FOR_A);
        editor.putInt("time_for_b", TIME_FOR_B);

        editor.commit();
        Log.i(TAG, "appstatus: " + pref.getInt("app_status", -5) + "time: " +
                pref.getInt("time_for_a", -5) + " " + pref.getInt("time_for_b", -5));

    }

    private boolean timeIsOk() {
        return new Date(2016, 6, 8).after(new Date());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (switch1.isChecked()) {
            Log.i(TAG, "由于您修改了，所以开关置为-关闭-");
            Toast.makeText(this, "由于修改了设置，应用开关已关闭，请在设置完成后手动开启", Toast.LENGTH_SHORT).show();
            switch1.setChecked(false);
        }
        int time;
        try {
            time = Integer.parseInt(s + "");
            if (time < 0 || time > 6 * 60 * 60)
                throw new Exception("时间设置不合法！");
        } catch (Exception e) {
            time_a.setText("10");
            time_b.setText("120");
            Toast.makeText(this, "由于您设置的延时不合法，系统已经重置其为默认值", Toast.LENGTH_SHORT).show();
        }
    }
}
