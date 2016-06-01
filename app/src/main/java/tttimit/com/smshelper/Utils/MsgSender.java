package tttimit.com.smshelper.Utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.List;

import tttimit.com.smshelper.Domain.Message;

/**
 * Created by tttimit on 2016/5/30.
 */
public class MsgSender {
    public void sentMsg(Context context, String number, List<Message> msgList) {
        int size = msgList.size();
        int random = (int) (Math.random() * size);
        Message msg = msgList.get(random);
//        SmsManager smsManager = SmsManager.getDefault();
//        PendingIntent sentIntent = PendingIntent.getBroadcast(
//                context, 0, new Intent(), 0);
//        smsManager.sendTextMessage(number, null, msg.content, sentIntent, null);
        Toast.makeText(context, "发给：" + number + " 内容：" + msg.content, Toast.LENGTH_LONG).show();
    }
}
