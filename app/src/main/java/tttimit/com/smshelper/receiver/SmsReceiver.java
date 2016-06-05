package tttimit.com.smshelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import tttimit.com.smshelper.service.SMSService;

/**
 * Created by tttimit on 2016/5/29.
 */
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.i("SmsReveiver", "收到短信，开启服务！");
//        Intent serviceIntent = new Intent(context, SMSService.class);
//        Object pduses = intent.getExtras().get("pdus");
//        byte[][] pdusmessage = (byte[][]) pduses;
//        SmsMessage sms = SmsMessage.createFromPdu(pdusmessage[0]);
//        String mobile = sms.getOriginatingAddress();//得到电话号码
//        serviceIntent.putExtra("number", mobile);
//        context.startService(serviceIntent);

        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Intent serviceIntent = new Intent(context, SMSService.class);
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                String number = msg.getOriginatingAddress();
//                System.out.println("number:" + msg.getOriginatingAddress()
//                        + "   body:" + msg.getDisplayMessageBody() + "  time:"
//                        + msg.getTimestampMillis());
                Log.i(TAG, "收到短信，将启动服务。号码：" + msg.getOriginatingAddress() + " 内容：" +
                        msg.getDisplayMessageBody() + " time:" + msg.getTimestampMillis());
                serviceIntent.putExtra("number", number);
                context.startService(serviceIntent);
            }
        }
    }
}
