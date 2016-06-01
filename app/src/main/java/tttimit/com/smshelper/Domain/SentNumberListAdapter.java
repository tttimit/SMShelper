package tttimit.com.smshelper.Domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import tttimit.com.smshelper.R;
import tttimit.com.smshelper.Utils.DBHelper;
import tttimit.com.smshelper.Utils.Dao;

/**
 * Created by tttimit on 2016/5/29.
 */
public class SentNumberListAdapter extends BaseAdapter {
    private Context context;
    private List<Item> itemList;

    public SentNumberListAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }


    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Item item = itemList.get(position);

        if (item == null) {
            return null;
        }

        ViewHolder holder = null;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sent, null);

            holder = new ViewHolder();
            holder.nameText = (TextView) convertView.findViewById(R.id.tv_name);
            holder.numberText = (TextView) convertView.findViewById(R.id.tv_number);
            holder.timeText = (TextView) convertView.findViewById(R.id.tv_time);
            holder.remove = (Button) convertView.findViewById(R.id.bt_remove);
            holder.id = item.id;
            convertView.setTag(holder);
        }

        holder.nameText.setText(item.name);
        holder.numberText.setText(item.number);
        holder.timeText.setText(item.time);

        final ViewHolder finalHolder = holder;
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dao dao = Dao.getSingleDao(context);
                dao.deleteItem(DBHelper.TABLE_SENT_NUMBERS, finalHolder.id);
                itemList.remove(position);
                SentNumberListAdapter.this.notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        public int id;
        public TextView nameText;
        public TextView numberText;
        public TextView timeText;
        public Button remove;
    }
}
