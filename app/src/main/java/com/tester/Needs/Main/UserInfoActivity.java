package com.tester.Needs.Main;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tester.Needs.R;

import static com.tester.Needs.Main.MainActivity.id_name;
import static com.tester.Needs.Main.MainActivity.id_nickName;
import static com.tester.Needs.Main.MainActivity.id_uid;
import static com.tester.Needs.Main.MainActivity.id_value;
import static com.tester.Needs.Main.SubActivity.point;
import static com.tester.Needs.Main.SubActivity.pointLimit;

public class UserInfoActivity extends AppCompatActivity {


    String change_nickname;
    int compnum = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        TextView userInfo_name = findViewById(R.id.userInfo_name);
        TextView userInfo_nickName = findViewById(R.id.userInfo_nickName);
        TextView userInfo_email = findViewById(R.id.userInfo_email);

        TextView userInfo_point = findViewById(R.id.userInfo_point);
        TextView userInfo_limit_point = findViewById(R.id.userInfo_limit_point);

        final EditText userInfo_edit = findViewById(R.id.userInfo_edit);

        final Button userInfo_change_btn = findViewById(R.id.userInfo_change_btn);
        final Button userInfo_dup_btn = findViewById(R.id.userInfo_dup_btn);
        final Button userInfo_change_ok = findViewById(R.id.userInfo_change_ok);

        userInfo_name.setText(id_name);
        userInfo_nickName.setText(id_nickName);
        userInfo_email.setText(id_value);

        userInfo_point.setText(point);
        userInfo_limit_point.setText(pointLimit);


        userInfo_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.userInfo_layout).setVisibility(View.VISIBLE);

                userInfo_dup_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        compnum = 1;

                        change_nickname = userInfo_edit.getText().toString();
                        db.collection("user")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (!document.exists()) {
                                                    break;
                                                }
                                                if (!(change_nickname.equals(document.getData().get("id_nickName").toString()))) {

                                                } else {
                                                    compnum = 2;
                                                    break;
                                                }
                                            }
                                        } else {
                                            Log.d("태그", "Error getting documents: ", task.getException());
                                        }
                                        //////////////////////////중복확인 toast////////////////////////////////
                                        if(change_nickname.equals(id_nickName)){
                                            compnum =3;
                                            Log.d("확인을 위한 num", "중복일시");
                                            userInfo_edit.setError("이전과 같은 닉네임");
                                            userInfo_edit.requestFocus();
                                        }
                                        else if (compnum == 2) {
                                            Log.d("확인을 위한 num", "중복일시");
                                            userInfo_edit.setError("중복된 닉네임");
                                            userInfo_edit.requestFocus();
                                        } else if (compnum == 1) {
                                            Log.d("확인을 위한 num", "중복이 아닐시");
                                            userInfo_edit.setError("사용가능한 닉네임");
                                            userInfo_edit.requestFocus();
                                        }
                                    }
                                });
                        findViewById(R.id.userInfo_layout_ok).setVisibility(View.VISIBLE);

                        userInfo_change_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(compnum==1){

                                    AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);

                                    builder.setTitle("닉네임 변경").setMessage("닉네임을 변경하시겠습니까?");

                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            id_nickName = change_nickname;
                                            db.collection("user").document(id_uid)
                                                    .update(
                                                            "id_nickName", change_nickname
                                                    );
                                            Intent intent = new Intent(UserInfoActivity.this, SubActivity.class);
                                            startActivity(intent);
                                        }
                                    });

                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }
                                else if(compnum==2){

                                    AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);

                                    builder.setTitle("닉네임 중복").setMessage("닉네임이중복됩니다");

                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();

                                }
                                else if(compnum==3){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);

                                    builder.setTitle("닉네임 동일").setMessage("이전닉네임과 동일합니다");

                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();

                                }
                            }
                        });
                    }
                });
            }
        });

    }
}
