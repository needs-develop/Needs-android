package com.tester.Needs.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.tester.Needs.Common.RecordList;
import com.tester.Needs.Common.RecordListAdapter;
import com.tester.Needs.R;

import java.util.ArrayList;

public class RecordActivity extends AppCompatActivity {

    public static ArrayList<RecordList> recordList = new ArrayList<RecordList>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        ListView listView = findViewById(R.id.record_listview);
        final RecordListAdapter recordListAdapter = new RecordListAdapter(this,recordList);
        listView.setAdapter(recordListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(RecordActivity.this, recordList.get(position).getContext(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RecordActivity.this,SubActivity.class);
        startActivity(intent);
        this.finish();
    }
}
