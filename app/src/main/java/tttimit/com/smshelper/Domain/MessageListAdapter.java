package tttimit.com.smshelper.Domain;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by tttimit on 2016/5/29.
 */
public class MessageListAdapter extends BaseAdapter {

    private Context context;
    private List<Message> msgList;

    public MessageListAdapter(Context context, List<Message> msgList) {
        this.context = context;
        this.msgList = msgList;
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public static class ViewHolder{
        public TextView nameText;
    }
}
