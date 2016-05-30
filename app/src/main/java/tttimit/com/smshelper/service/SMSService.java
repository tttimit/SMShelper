package tttimit.com.smshelper.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by tttimit on 2016/5/29.
 *
 * * 当收到短信时，本服务启动，开始以下流程：
 *      0、该号码已经存在于已发送库中
 *          1、什么都不做
 *      0、该号码不存在于已发送库
 *          1、开启计时器，20秒后检查短信库，如果存在未读短信
 *              2、发送短信库B中的短信到目标号码
 *              3、将目标号码存储到已发送库中
 *          1、如果20秒检查收件箱发现不存在未读短信（已经看过了）
 *              2、什么都不做
 */
public class SMSService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
