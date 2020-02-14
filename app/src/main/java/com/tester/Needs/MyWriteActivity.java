package com.tester.Needs;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyWriteActivity extends ListActivity {
    ListView listViewWrite;
    BoardListAdapter boardListAdapter;
    ArrayList<BoardList> list_itemArrayList = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String writer = TextUtils.isEmpty(user.getDisplayName()) ? user.getEmail() : user.getDisplayName();

    int board_count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywrite);

        listViewWrite = getListView();
        list_itemArrayList = new ArrayList<BoardList>();
        String uid = user.getUid();

        CollectionReference colRef = db.collection("user").document(uid).collection("write");
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
                                        ,builder));
                                board_count = Integer.parseInt(number);
                                board_count++;
                            }
                            //document.getData().get("title").toString()+"["+document.getData().get("good_num").toString()+"]")
                            boardListAdapter = new BoardListAdapter(MyWriteActivity.this, list_itemArrayList);
                            listViewWrite.setAdapter(boardListAdapter);
                        } else {
                            Log.d("태그", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
