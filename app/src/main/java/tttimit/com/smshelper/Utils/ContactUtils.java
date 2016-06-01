package tttimit.com.smshelper.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created by tttimit on 2016/5/31.
 */
public class ContactUtils {

    private static final String TAG = "ContactUtils";

    /*
     * 根据电话号码取得联系人姓名
     */
    public static String getContactNameByPhoneNumber(Context context, String address) {
        String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER };

        // 将自己添加到 msPeers 中
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, // Which columns to return.
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = '"
                        + address + "'", // WHERE clause.
                null, // WHERE clause value substitution
                null); // Sort order.

        if (cursor == null) {
            Log.d(TAG, "getPeople null");
            return null;
        }
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);

            // 取得联系人名字
            int nameFieldColumnIndex = cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String name = cursor.getString(nameFieldColumnIndex);
            return name;
        }
        return "未保存联系人";
    }
}
