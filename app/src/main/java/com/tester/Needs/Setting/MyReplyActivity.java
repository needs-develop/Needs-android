package com.tester.Needs.Setting;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tester.Needs.Common.BoardList;
import com.tester.Needs.Main.BoardListAdapter;
import com.tester.Needs.Main.SubActivity;
import com.tester.Needs.R;
import com.tester.Needs.Service.MyService;

import java.util.ArrayList;

import static com.tester.Needs.Main.SubActivity.fragmentNumber;
//import static com.tester.Needs.Main.SubActivity.getActivity;


public class MyReplyActivity extends ListActivity {
    ListView listViewReply;
    BoardListAdapter boardListAdapter;
    ArrayList<BoardList> list_itemArrayList = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String writer = TextUtils.isEmpty(user.getDisplayName()) ? user.getEmail() : user.getDisplayName();

    int board_count;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myreply);
        stopService(new Intent(MyReplyActivity.this,MyService.class));
        //getActivity = MyReplyActivity.class;

        fragmentNumber = 0;

        listViewReply = getListView();
        list_itemArrayList = new ArrayList<BoardList>();
        String uid = user.getUid();

        CollectionReference colRef = db.collection("user").document(uid).collection("reply");
        colRef.orderBy("day", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            board_count = 1;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String number = Integer.toString(board_count);

                                String goodNum = document.getData().get("title").toString();

                                SpannableStringBuilder builder = new SpannableStringBuilder(goodNum);

                                list_itemArrayList.add(new BoardList(number, document.getData().get("title").toString(),
                                        document.getData().get("content").toString(), document.getData().get("id").toString(),
                                        document.getData().get("day").toString(), document.getData().get("visitnum").toString(),
                                        document.getData().get("good").toString(), document.getData().get("documentName").toString()
                                        , builder));
                                board_count = Integer.parseInt(number);
                                board_count++;
                            }
                            boardListAdapter = new BoardListAdapter(MyReplyActivity.this, list_itemArrayList);
                            listViewReply.setAdapter(boardListAdapter);
                        } else {
                            Log.d("태그", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //fragmentNumber = 2;
        //stopService(new Intent(MyReplyActivity.this, MyService.class));
        //Intent intent = new Intent(MyReplyActivity.this, SubActivity.class);
        //startActivity(intent);
        this.finish();
    }
/*
    @Override
    protected void onUserLeaveHint() {
        Intent intent = new Intent(MyReplyActivity.this, MyService.class);
        intent.setAction("startForeground");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            startForegroundService(intent);
        }else{
            startService(intent);
        }
    }

 */
}
