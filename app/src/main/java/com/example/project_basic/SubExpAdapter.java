package com.example.project_basic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class SubExpAdapter extends BaseExpandableListAdapter {

    public List<String> firstGroups;
    public HashMap<String,List<SubList>> firstItemGroups;
    public Context context;

    public SubExpAdapter(Context context, List<String>firstGroups,HashMap<String,List<SubList>>firstItemGroups){
        this.context = context;
        this.firstGroups = firstGroups;
        this.firstItemGroups = firstItemGroups;
    }
    public SubExpAdapter(){};


    @Override
    public int getGroupCount() {
        return firstGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return firstItemGroups.get(getGroup(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return firstGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return firstItemGroups.get(getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.activity_itemdraw, null);
        }

        TextView tvGroup = (TextView) convertView.findViewById(R.id.draw_text);

        tvGroup.setText((String) getGroup(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.activity_itemdrawin, null);
        }

        TextView tvItem = (TextView) convertView.findViewById(R.id.draw_text2);

        SubList investment = (SubList)getChild(groupPosition, childPosition);
        tvItem.setText(investment.getCountry());

        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /*
    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new BoardListAdapter.ListFilter() ;
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
                    if( item.getBtn_title().toUpperCase().contains(constraint.toString().toUpperCase()))
                        itemList.add(item);
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
    */
}
