package tttimit.com.smshelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import tttimit.com.smshelper.service.SMSService;

/**
 * Created by tttimit on 2016/5/29.
 */
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context," 有未读短信！！", Toast.LENGTH_LONG).show();
        Log.i("SmsReveiver", "有未读短信！");
            Intent serviceIntent = new Intent(context, SMSService.class);
            context.startService(serviceIntent);
    }
}
