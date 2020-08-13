package com.tester.Needs.Common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tester.Needs.Main.RecordActivity;
import com.tester.Needs.R;

import java.util.ArrayList;

public class RecordListAdapter extends BaseAdapter {

    Context context = null;
    LayoutInflater layoutInflater = null;
    ArrayList<RecordList> sample;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView =
                    LayoutInflater.from(context).inflate(R.layout.activity_itemrecord, null);
            holder.text = (TextView) convertView.findViewById(R.id.record_text);
            holder.day = (TextView) convertView.findViewById(R.id.record_day);
            holder.data = (TextView) convertView.findViewById(R.id.record_data);
            holder.profile = (ImageView) convertView.findViewById(R.id.record_profile);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(sample.get(position).getProfile().equals("no")){
        }
        if(sample.get(position).getData().equals("data"))
        {
            holder.data.setText(sample.get(position).getAddress()+"게시판");
        }
        else if(sample.get(position).getData().equals("freedata"))
        {
            holder.data.setText("자유게시판");
        }
        holder.text.setText(sample.get(position).getWriter()+"님이 게시물에 관심을 가졌습니다");
        holder.day.setText(sample.get(position).getDay());

        return convertView;
    }

    public class ViewHolder {
        ImageView profile;
        TextView text;
        TextView day;
        TextView data;
    }
}

