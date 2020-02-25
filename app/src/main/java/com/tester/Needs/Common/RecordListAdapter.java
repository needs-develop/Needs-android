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
        View view = layoutInflater.inflate(R.layout.activity_itemrecord,null);

        TextView text = view.findViewById(R.id.record_text);
        TextView day = view.findViewById(R.id.record_day);

        text.setText(sample.get(position).getWriter()+"님이 게시물에 관심을 가졌습니다");
        day.setText(sample.get(position).getDay());
        return view;
    }
}
