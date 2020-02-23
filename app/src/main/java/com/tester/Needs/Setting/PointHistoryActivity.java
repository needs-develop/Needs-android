package com.tester.Needs.Setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tester.Needs.Main.SubActivity;
import com.tester.Needs.R;
import com.tester.Needs.Service.MyService;

import static com.tester.Needs.Main.SubActivity.fragmentNumber;
//import static com.tester.Needs.Main.SubActivity.getActivity;

public class PointHistoryActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid;
    String point;
    TextView curPoint;
    pointHistoryAdapter pointHistoryAdapter;
    RecyclerView recyclerView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_history);
        TextView id = findViewById(R.id.id);
        curPoint = findViewById(R.id.curPoint);
        uid = user.getUid();

        //getActivity = PointHistoryActivity.class;

        //fragmentNumber = 0;

        id.setText(TextUtils.isEmpty(user.getDisplayName()) ? user.getEmail() : user.getDisplayName());


        DocumentReference docR = db.collection("user").document(uid);
        docR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    point = document.getData().get("id_point").toString();
                    curPoint.setText(point);
                }
            }
        });


        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        pointHistoryAdapter = new pointHistoryAdapter();

        db.collection("user").document(uid).collection("pointHistory").orderBy("day", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                pointHistoryAdapter.addItem(new pointHistory(
                                        document.getData().get("day").toString()
                                        , document.getData().get("point").toString(),
                                        document.getData().get("type").toString()));
                            }
                            recyclerView.setAdapter(pointHistoryAdapter);
                        }
                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //fragmentNumber = 2;
        //stopService(new Intent(PointHistoryActivity.this, MyService.class));

        //Intent intent = new Intent(PointHistoryActivity.this, SubActivity.class);
        //startActivity(intent);
        this.finish();
    }
    /*
    @Override
    protected void onUserLeaveHint() {
        Intent intent = new Intent(PointHistoryActivity.this, MyService.class);
        intent.setAction("startForeground");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            startForegroundService(intent);
        }else{
            startService(intent);
        }
    }*/
}
