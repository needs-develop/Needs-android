package com.tester.Needs.Common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tester.Needs.R;

import java.util.ArrayList;

public class RecordListAdapter extends BaseAdapter {

    Context context = null;
    LayoutInflater layoutInflater = null;
    ArrayList<RecordList> sample;

    public RecordListAdapter(Context context,ArrayList<RecordList>data){
        this.context = context;
        this.sample = data;
        layoutInflater = LayoutInflater.from(context);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public Object getItem(int position) {
        return sample.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView =
                    LayoutInflater.from(context).inflate(R.layout.activity_itemrecord, null);
            holder.text = (TextView) convertView.findViewById(R.id.record_text);
            holder.day = (TextView) convertView.findViewById(R.id.record_day);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(sample.get(position).getWriter()+"님이 게시물에 관심을 가졌습니다");
        holder.day.setText(sample.get(position).getDay());

        return convertView;
    }

    public class ViewHolder {
        TextView text;
        TextView day;
    }
}

