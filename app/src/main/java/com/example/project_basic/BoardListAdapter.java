package com.example.project_basic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.project_basic.BoardActivity.spinnerNumber;

public class BoardListAdapter extends BaseAdapter implements Filterable {

    ArrayList<BoardList> data = new ArrayList<BoardList>();
    private ArrayList<BoardList> list_itemArrayList = data;

    Context context;
    Filter listFilter;

    TextView btn_num;
    TextView btn_title;
    TextView btn_writer;
    TextView btn_date;
    TextView btn_visitnum;



    public BoardListAdapter(Context context, ArrayList<BoardList>data){
        this.data = data;
        this.context = context;
        this.list_itemArrayList = data;
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

        final int pos = position;

        if(convertView==null)
        {
            convertView =
                    LayoutInflater.from(context).inflate(R.layout.activity_item,null);
            btn_num =
                    (TextView)convertView.findViewById(R.id.btn_num);
            btn_title =
                    (TextView)convertView.findViewById(R.id.btn_title);
            btn_writer =
                    (TextView)convertView.findViewById(R.id.btn_writer);
            btn_date =
                    (TextView)convertView.findViewById(R.id.btn_date);
            btn_visitnum =
                    (TextView)convertView.findViewById(R.id.btn_visitnum);

        }
        btn_num.setText(data.get(position).getBtn_num());
        btn_title.setText(data.get(position).getBtn_title());
        btn_writer.setText(data.get(position).getBtn_writer());
        btn_date.setText(data.get(position).getBtn_date());
        btn_visitnum.setText(data.get(position).getBtn_visitnum());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter() ;
        }
        return listFilter ;
    }

    private class ListFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint == null || constraint.length() ==0){
                results.values = list_itemArrayList;
                results.count = list_itemArrayList.size();
            }
            else {
                ArrayList<BoardList> itemList = new ArrayList<>();
                for(BoardList item : list_itemArrayList){
                    if(spinnerNumber==0) {
                        if (item.getBtn_title().toUpperCase().contains(constraint.toString().toUpperCase()))
                            itemList.add(item);
                    }
                    else if (spinnerNumber ==1)
                    {
                        if (item.getBtn_writer().toUpperCase().contains(constraint.toString().toUpperCase()))
                            itemList.add(item);
                    }
                    else if(spinnerNumber==2)
                    {
                        if (item.getBtn_date().toUpperCase().contains(constraint.toString().toUpperCase()))
                            itemList.add(item);
                    }
                }
                results.values = itemList;
                results.count = itemList.size();
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data = (ArrayList<BoardList>) results.values;
            if(results.count>0) {
                notifyDataSetChanged();
            }
            else{
                notifyDataSetInvalidated();
            }
        }
    }
}

