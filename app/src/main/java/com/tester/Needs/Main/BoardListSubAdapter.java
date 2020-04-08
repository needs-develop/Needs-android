package com.tester.Needs.Main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.tester.Needs.Common.BoardList;
import com.tester.Needs.Common.BoardListSetting;
import com.tester.Needs.Common.BoardListSub;
import com.tester.Needs.R;

import java.util.ArrayList;

import static com.tester.Needs.Main.BoardActivity.spinnerNumber;

public class BoardListSubAdapter extends BaseAdapter {

    ArrayList<BoardListSub> data = new ArrayList<BoardListSub>();
    private ArrayList<BoardListSub> list_itemArrayList = data;

    Context context;

    LayoutInflater mInflater;


    public BoardListSubAdapter(Context context, ArrayList<BoardListSub> data) {
        this.data = data;
        this.context = context;
        this.list_itemArrayList = data;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView =
                    LayoutInflater.from(context).inflate(R.layout.activity_item, null);
            holder = new ViewHolder();
            holder.btn_num =
                    (TextView) convertView.findViewById(R.id.btn_num);
            holder.btn_title =
                    (TextView) convertView.findViewById(R.id.btn_title);
            holder.btn_writer =
                    (TextView) convertView.findViewById(R.id.btn_writer);
            holder.btn_date =
                    (TextView) convertView.findViewById(R.id.btn_date);
            holder.btn_visitnum =
                    (TextView) convertView.findViewById(R.id.btn_visitnum);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.btn_num.setText(data.get(position).getBtn_num());
        holder.btn_title.setText(data.get(position).getSpannableStringBuilder());
        holder.btn_writer.setText(data.get(position).getBtn_writer());
        holder.btn_date.setText(data.get(position).getBtn_date());
        holder.btn_visitnum.setText(data.get(position).getBtn_visitnum());

        return convertView;
    }
    private class ViewHolder {
        TextView btn_num;
        TextView btn_title;
        TextView btn_writer;
        TextView btn_date;
        TextView btn_visitnum;
    }
}
