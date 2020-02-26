package com.tester.Needs.Main;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tester.Needs.Common.RecordList;
import com.tester.Needs.Common.RecordListAdapter;
import com.tester.Needs.R;

import java.util.ArrayList;


public class RecordActivity extends AppCompatActivity {

    public ArrayList<RecordList> recordList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();//firestore객체
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    String title;
    String content;
    String day;
    String conId;
    String goodNum;
    String visitString;
    String documentName;
    ListView listView ;
    RecordListAdapter recordListAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        recordList = new ArrayList<RecordList>();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                try{
                    db.collection("user").document(uid).collection("action").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot document : task.getResult())
                                    {
                                        if(document.getData().get("value").toString().equals("data"))
                                        {
                                            findViewById(R.id.record_no_list).setVisibility(View.GONE);
                                            recordList.add(new RecordList(document.getData().get("value").toString(),
                                                    document.getData().get("address").toString(),document.getData().get("document_name").toString(),
                                                    document.getData().get("writer").toString(),document.getData().get("day").toString()));
                                            Log.d("test",document.getData().get("day").toString());
                                        }
                                        else if(document.getData().get("value").toString().equals("freedata"))
                                        {
                                            findViewById(R.id.record_no_list).setVisibility(View.GONE);
                                            recordList.add(new RecordList(document.getData().get("value").toString(),
                                                    "",document.getData().get("document_name").toString(),
                                                    document.getData().get("writer").toString(),document.getData().get("day").toString()));
                                            Log.d("test",document.getData().get("day").toString());
                                        }
                                    }

                                    listView = findViewById(R.id.record_listview);
                                    recordListAdapter = new RecordListAdapter(RecordActivity.this,recordList);
                                    listView.setAdapter(recordListAdapter);
                                    //recordListAdapter.notifyDataSetChanged();
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                            if(recordList.get(position).getData().equals("data"))
                                            {
                                                db.collection("data").document("allData")
                                                        .collection(recordList.get(position).getAddress()).document(recordList.get(position).getDocument_name())
                                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            Intent intent = new Intent(RecordActivity.this,HomeContent.class);
                                                            DocumentSnapshot document = task.getResult();
                                                            title = document.getData().get("title").toString();
                                                            content =  document.getData().get("content").toString();
                                                            day =  document.getData().get("day").toString();
                                                            documentName =  document.getData().get("document_name").toString();
                                                            goodNum =  document.getData().get("good_num").toString();
                                                            visitString =  document.getData().get("visit_num").toString();
                                                            conId =  document.getData().get("id_nickName").toString();

                                                            int visitInt = Integer.parseInt(visitString);
                                                            visitInt = visitInt + 1;
                                                            visitString = Integer.toString(visitInt);

                                                            intent.putExtra("title", title);
                                                            intent.putExtra("content", content);
                                                            intent.putExtra("day", day);
                                                            intent.putExtra("id", conId);
                                                            intent.putExtra("good", goodNum);
                                                            intent.putExtra("visitnum", visitString);
                                                            intent.putExtra("documentName", documentName);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });
                                            }
                                            else if(recordList.get(position).getData().equals("freedata"))
                                            {
                                                db.collection("freeData").document(recordList.get(position).getDocument_name())
                                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            Intent intent = new Intent(RecordActivity.this,HomeFreeContent.class);
                                                            DocumentSnapshot document = task.getResult();
                                                            title = document.getData().get("title").toString();
                                                            content =  document.getData().get("content").toString();
                                                            day =  document.getData().get("day").toString();
                                                            documentName =  document.getData().get("document_name").toString();
                                                            goodNum =  document.getData().get("good_num").toString();
                                                            visitString =  document.getData().get("visit_num").toString();
                                                            conId =  document.getData().get("id_nickName").toString();

                                                            int visitInt = Integer.parseInt(visitString);
                                                            visitInt = visitInt + 1;
                                                            visitString = Integer.toString(visitInt);

                                                            intent.putExtra("title", title);
                                                            intent.putExtra("content", content);
                                                            intent.putExtra("day", day);
                                                            intent.putExtra("id", conId);
                                                            intent.putExtra("good", goodNum);
                                                            intent.putExtra("visitnum", visitString);
                                                            intent.putExtra("documentName", documentName);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                    });
                                }
                            });

                }catch (Exception e){
                }
            }

        }, 500);

        if(recordList.size()==0)
        {
            findViewById(R.id.record_no_list).setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RecordActivity.this,SubActivity.class);
        startActivity(intent);
        this.finish();
    }
}
