package com.example.project_basic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ReplyListAdapter extends BaseAdapter {

    Context context;
    ArrayList<ReplyList> list_replyArrayList;

    public ReplyListAdapter(Context context, ArrayList<ReplyList> list_replyArrayList) {
        this.context = context;
        this.list_replyArrayList = list_replyArrayList;
    }

    @Override
    public int getCount() {
        return this.list_replyArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return list_replyArrayList.get(position);
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
                    LayoutInflater.from(context).inflate(R.layout.activity_itemreply, null);
            holder.comment_reply = (TextView) convertView.findViewById(R.id.comment_reply);
            holder.writer_reply = (TextView) convertView.findViewById(R.id.writer_reply);
            holder.time_reply = (TextView) convertView.findViewById(R.id.time_reply);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.comment_reply.setText(list_replyArrayList.get(position).getComment_reply());
        holder.writer_reply.setText(list_replyArrayList.get(position).getWriter_reply());
        holder.time_reply.setText(list_replyArrayList.get(position).getTime_reply());

        return convertView;
    }

    public class ViewHolder {
        TextView comment_reply;
        TextView writer_reply;
        TextView time_reply;

    }
}
