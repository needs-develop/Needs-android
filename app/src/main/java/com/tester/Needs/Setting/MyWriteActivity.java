package com.tester.Needs.Setting;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tester.Needs.Common.BoardList;
import com.tester.Needs.Common.BoardListSetting;
import com.tester.Needs.Main.BoardListAdapter;
import com.tester.Needs.Main.BoardListSettingAdapter;
import com.tester.Needs.Main.HomeContent;
import com.tester.Needs.Main.HomeFreeContent;
import com.tester.Needs.Main.SubActivity;
import com.tester.Needs.R;
import com.tester.Needs.Service.MyService;

import java.util.ArrayList;

import static com.tester.Needs.Main.SubActivity.fragmentNumber;
//import static com.tester.Needs.Main.SubActivity.getActivity;

public class MyWriteActivity extends AppCompatActivity {
    ListView listViewWrite;
    BoardListSettingAdapter boardListAdapter;
    ArrayList<BoardListSetting> list_itemArrayList = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    int board_count = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywrite);
        stopService(new Intent(MyWriteActivity.this, MyService.class));
        //getActivity = MyLikeActivity.class;FreeContent

        //fragmentNumber = 0;
        list_itemArrayList = new ArrayList<BoardListSetting>();

        CollectionReference colRef = db.collection("user").document(uid).collection("write");
        colRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // if data equals "data"
                            if (document.getData().get("data").toString().equals("data")) {
                                db.collection("data").document("allData").
                                        collection(document.getData().get("address").toString()).
                                        document(document.getData().get("document_name").toString()).get().
                                        addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("글테스트", "테스트");
                                                    DocumentSnapshot document = task.getResult();

                                                    String number = Integer.toString(board_count);
                                                    int num2 = Integer.parseInt(document.getData().get("good_num").toString());
                                                    String stringNum = Integer.toString(num2);
                                                    int count = stringNum.length();

                                                    String goodNum = document.getData().get("title").toString() + "     [" + num2 + "]";
                                                    int length = goodNum.length();
                                                    int start = 0;
                                                    if (count == 1)
                                                        start = length - 3;
                                                    else if (count == 2)
                                                        start = length - 4;
                                                    else if (count == 3)
                                                        start = length - 5;
                                                    else if (count == 4)
                                                        start = length - 6;

                                                    SpannableStringBuilder builder = new SpannableStringBuilder(goodNum);
                                                    builder.setSpan(new StyleSpan(Typeface.BOLD), start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")), start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                                    list_itemArrayList.add(new BoardListSetting(number, document.getData().get("title").toString(),
                                                            document.getData().get("content").toString(), document.getData().get("id_nickName").toString(),
                                                            //write 수정
                                                            document.getData().get("day").toString(), document.getData().get("visit_num").toString(),
                                                            document.getData().get("good_num").toString(), document.getData().get("document_name").toString()
                                                            , builder, "data"));
                                                    Log.d("테스트", number + document.getData().get("title").toString() +
                                                            document.getData().get("content").toString() + document.getData().get("id_nickName").toString() +
                                                            document.getData().get("day").toString() + document.getData().get("visit_num").toString() +
                                                            document.getData().get("good_num").toString() + document.getData().get("document_name").toString()
                                                            + builder + "data");
                                                    Log.d("사이즈 테스트", String.valueOf(list_itemArrayList.size()));
                                                    board_count = Integer.parseInt(number);
                                                    board_count++;
                                                }
                                            }
                                        });
                            }
                            // if data equals "freedata"
                            else if (document.getData().get("data").toString().equals("freedata")) {

                                db.collection("freeData").document(document.getData().get("document_name").toString()).
                                        get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();

                                            String number = Integer.toString(board_count);
                                            int num2 = Integer.parseInt(document.getData().get("good_num").toString());
                                            String stringNum = Integer.toString(num2);
                                            int count = stringNum.length();

                                            String goodNum = document.getData().get("title").toString() + "     [" + num2 + "]";
                                            int length = goodNum.length();
                                            int start = 0;
                                            if (count == 1)
                                                start = length - 3;
                                            else if (count == 2)
                                                start = length - 4;
                                            else if (count == 3)
                                                start = length - 5;
                                            else if (count == 4)
                                                start = length - 6;

                                            SpannableStringBuilder builder = new SpannableStringBuilder(goodNum);
                                            builder.setSpan(new StyleSpan(Typeface.BOLD), start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            builder.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")), start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                            list_itemArrayList.add(new BoardListSetting(number, document.getData().get("title").toString(),
                                                    document.getData().get("content").toString(), document.getData().get("id_nickName").toString(),
                                                    //write 수정
                                                    document.getData().get("day").toString(), document.getData().get("visit_num").toString(),
                                                    document.getData().get("good_num").toString(), document.getData().get("document_name").toString()
                                                    , builder, "freedata"));
                                            Log.d("테스트", number + document.getData().get("title").toString() +
                                                    document.getData().get("content").toString() + document.getData().get("id_nickName").toString() +
                                                    document.getData().get("day").toString() + document.getData().get("visit_num").toString() +
                                                    document.getData().get("good_num").toString() + document.getData().get("document_name").toString()
                                                    + builder + "freedata");
                                            Log.d("사이즈 테스트", String.valueOf(list_itemArrayList.size()));
                                            board_count = Integer.parseInt(number);
                                            board_count++;
                                        }
                                    }
                                });

                            }
                        }

                    }
                });


        Log.d("마지막전테스트", "마지막전테스트");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            public void run() {
                listViewWrite = findViewById(R.id.mywrite_list);
                boardListAdapter = new BoardListSettingAdapter(MyWriteActivity.this, list_itemArrayList);
                listViewWrite.setAdapter(boardListAdapter);

                // elements do not exist
                if (list_itemArrayList.size() == 0) {
                    Log.d("시간테스트", "시간테스트");
                    findViewById(R.id.mywrite_no_list).setVisibility(View.VISIBLE);
                } else {
                    Log.d("시간 풀 테스트", "시간 풀 테스트");
                    listViewWrite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // if data equals "data"
                            if (list_itemArrayList.get(position).getData().equals("data")) {
                                Intent intent2 = new Intent(MyWriteActivity.this, HomeContent.class);

                                String title = list_itemArrayList.get(position).getBtn_title();
                                String content = list_itemArrayList.get(position).getContent();
                                String day = list_itemArrayList.get(position).getBtn_date();
                                String conId = list_itemArrayList.get(position).getBtn_writer();
                                String goodNum = list_itemArrayList.get(position).getContent_good();
                                String visitString = list_itemArrayList.get(position).getBtn_visitnum();
                                String document_name = list_itemArrayList.get(position).getDocument_name();

                                intent2.putExtra("title", title);
                                intent2.putExtra("content", content);
                                intent2.putExtra("day", day);
                                intent2.putExtra("id", conId);
                                intent2.putExtra("good", goodNum);
                                intent2.putExtra("visitnum", visitString);
                                intent2.putExtra("documentName", document_name);
                                startActivity(intent2);
                            }
                            // if data equals "freedata"
                            else if (list_itemArrayList.get(position).getData().equals("freedata")) {
                                Intent intent2 = new Intent(MyWriteActivity.this, HomeFreeContent.class);

                                String title = list_itemArrayList.get(position).getBtn_title();
                                String content = list_itemArrayList.get(position).getContent();
                                String day = list_itemArrayList.get(position).getBtn_date();
                                String conId = list_itemArrayList.get(position).getBtn_writer();
                                String goodNum = list_itemArrayList.get(position).getContent_good();
                                String visitString = list_itemArrayList.get(position).getBtn_visitnum();
                                String document_name = list_itemArrayList.get(position).getDocument_name();

                                intent2.putExtra("title", title);
                                intent2.putExtra("content", content);
                                intent2.putExtra("day", day);
                                intent2.putExtra("id", conId);
                                intent2.putExtra("good", goodNum);
                                intent2.putExtra("visitnum", visitString);
                                intent2.putExtra("documentName", document_name);
                                startActivity(intent2);
                            }
                        }
                    });
                }
            }
        }, 1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        // fragmentNumber = 2;
        super.onBackPressed();
        //stopService(new Intent(MyLikeActivity.this, MyService.class));
        //Intent intent = new Intent(MyLikeActivity.this,SubActivity.class);
        //startActivity(intent);
        this.finish();
    }
}
/*
    @Override
    protected void onUserLeaveHint() {
        Intent intent = new Intent(MyWriteActivity.this, MyService.class);
        intent.setAction("startForeground");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            startForegroundService(intent);
        }else{
            startService(intent);
        }
    }

 */

