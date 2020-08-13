package com.tester.Needs.Common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tester.Needs.R;

import java.util.ArrayList;

public class SubListAdapter extends BaseAdapter {

    Context mcontext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<SubList> sample;

    public SubListAdapter(Context context, ArrayList<SubList> data) {
        mcontext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mcontext);
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
        View view = mLayoutInflater.inflate(R.layout.activity_itemdraw, null);
        TextView textView = (TextView) view.findViewById(R.id.draw_text);

        textView.setText(sample.get(position).getCountry());

        return view;
    }
}
