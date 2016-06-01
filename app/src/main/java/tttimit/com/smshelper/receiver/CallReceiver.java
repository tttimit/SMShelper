package tttimit.com.smshelper.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;

import tttimit.com.smshelper.service.CallService;

/**
 * Created by tttimit on 2016/5/29.
 */
public class CallReceiver extends BroadcastReceiver {

    private static final String TAG = "CallReveiver";
    private static boolean incomingFlag = false;
    private static PhoneStateListener phoneStateListener;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("PhoneReceiver", "onReceive");
        //拨打电话
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            incomingFlag = false;
        } else {
            this.context = context;
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(getPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private PhoneStateListener getPhoneStateListener(){
        if (phoneStateListener != null)
            return phoneStateListener;

        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    //电话等待接听
                    case TelephonyManager.CALL_STATE_RINGING:
                        incomingFlag = true;
                        Log.i("PhoneReceiver", "CALL IN RINGING :" + incomingNumber);
                        break;

                    //电话接听
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if (incomingFlag) {
                            Log.i("PhoneReceiver", "CALL IN ACCEPT :" + incomingNumber);
                            incomingFlag = false;
                        }
                        break;
                    //电话挂机
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (incomingFlag) {
                            Log.i("PhoneReceiver", "CALL IDLE");
                            if ("".equals(incomingNumber))
                                incomingNumber = "未知号码";
                            Intent serviceIntent = new Intent(context, CallService.class);
                            serviceIntent.putExtra("INCOMING_NUMBER", incomingNumber);
                            serviceIntent.putExtra("INCOMING_TIME", new Date().toLocaleString());
                            Log.i(TAG, incomingNumber + " - " + new Date().toLocaleString());
                            context.startService(serviceIntent);
                        }

                }
            }
        };

        return phoneStateListener;


    }
}
