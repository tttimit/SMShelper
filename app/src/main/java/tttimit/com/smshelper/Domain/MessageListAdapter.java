package tttimit.com.smshelper.Domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import tttimit.com.smshelper.R;
import tttimit.com.smshelper.Utils.Dao;

/**
 * Created by tttimit on 2016/5/29.
 */
public class MessageListAdapter extends BaseAdapter {

    private Context context;
    private List<Message> msgList;
    private String tableName;

    public MessageListAdapter(Context context, String tableName, List<Message> msgList) {
        this.context = context;
        this.msgList = msgList;
        this.tableName = tableName;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        Message msg = msgList.get(position);

        if (msg == null) {
            return null;
        }

        ViewHolder holder = null;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.msg_item, null);

            holder = new ViewHolder();
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_msg_content);
            holder.bt_remove = (Button) convertView.findViewById(R.id.bt_msg_item_delete);
            holder.id = msg.id;

            convertView.setTag(holder);
        }

        holder.tv_content.setText(msg.content);
        final ViewHolder finalHolder = holder;
        holder.bt_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgList.size() > 1) {
                    Dao dao = Dao.getSingleDao(context);
                    dao.deleteItem(tableName, finalHolder.id);
                    msgList.remove(position);
                    MessageListAdapter.this.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "消息库中至少应保留一条消息", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        private int id;
        public TextView tv_content;
        public Button bt_remove;
    }
}
