package com.example.project_basic;

import android.app.ListActivity;
import android.os.Bundle;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MyReplyActivity extends ListActivity {
    ListView listViewReply;
    BoardListAdapter boardListAdapter;
    ArrayList<BoardList> list_itemArrayList = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String writer = TextUtils.isEmpty(user.getDisplayName()) ? user.getEmail() : user.getDisplayName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myreply);

        listViewReply = getListView();
        list_itemArrayList = new ArrayList<BoardList>();
        String uid = user.getUid();

        CollectionReference colRef = db.collection("user").document(uid).collection("reply");
                colRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("태그", document.getId() + " => " + document.getData());
                                list_itemArrayList.add(new BoardList(i + ".", document.getData().get("title").toString(),
                                        document.getData().get("content").toString(), document.getData().get("id").toString(),
                                        document.getData().get("day").toString(), document.getData().get("visitnum").toString(),
                                        document.getData().get("good").toString(),document.getData().get("document_name").toString()));
                                i++;
                            }
                            boardListAdapter = new BoardListAdapter(MyReplyActivity.this, list_itemArrayList);
                            listViewReply.setAdapter(boardListAdapter);
                        } else {
                            Log.d("태그", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}
