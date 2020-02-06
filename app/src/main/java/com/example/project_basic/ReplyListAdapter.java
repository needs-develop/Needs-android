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

    TextView comment_reply;
    TextView writer_reply;
    TextView time_reply;

    public ReplyListAdapter(Context context, ArrayList<ReplyList>list_replyArrayList){
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
        if(convertView==null)
        {
            convertView =
                    LayoutInflater.from(context).inflate(R.layout.activity_itemreply,null);
             comment_reply = (TextView) convertView.findViewById(R.id.comment_reply);
             writer_reply = (TextView)convertView.findViewById(R.id.writer_reply);
             time_reply = (TextView)convertView.findViewById(R.id.time_reply);

        }

        comment_reply.setText(list_replyArrayList.get(position).getComment_reply());
        writer_reply.setText(list_replyArrayList.get(position).getWriter_reply());
        time_reply.setText(list_replyArrayList.get(position).getTime_reply());

        return convertView;
    }
}
