package com.tester.Needs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class pointHistoryAdapter extends RecyclerView.Adapter<pointHistoryAdapter.ViewHolder> {
    ArrayList<pointHistory> items = new ArrayList<pointHistory>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemview = inflater.inflate(R.layout.personhistory_item, parent, false);

        return new ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        pointHistory item = items.get(position);
        holder.setItem(item);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textView1;
        TextView textView2;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
        }

        public void setItem(pointHistory item) {
            textView.setText(item.getType());
            textView1.setText(item.getPoint());
            textView2.setText(item.getDay());
        }
    }

    public void addItem(pointHistory item) {
        items.add(item);
    }

    public void setItems(ArrayList<pointHistory> items) {
        this.items = items;
    }

    public pointHistory getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, pointHistory item) {
        items.set(position, item);
    }
}
