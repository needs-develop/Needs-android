package com.example.project_basic;
//회원가입창//

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.example.project_basic.MainActivity.id_name;
import static com.example.project_basic.MainActivity.id_nickName;
import static com.example.project_basic.MainActivity.id_uid;
import static com.example.project_basic.MainActivity.id_value;

@RequiresApi(api = Build.VERSION_CODES.O)
public class RegisterActivity extends AppCompatActivity {


    private EditText m_id;
    private EditText m_pass;

    private Button btn_register;

    FirebaseAuth firebaseAuth; //이메일 로그인

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    String pointLimit = "5";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    int compareNum = 1;

    Instant nowUtc = Instant.now();
    ZoneId asiaSeoul = ZoneId.of("Asia/Seoul");
    ZonedDateTime nowAsiaSeoul = ZonedDateTime.ofInstant(nowUtc, asiaSeoul);

    String year = String.valueOf(nowAsiaSeoul.getYear());
    String month = String.valueOf(nowAsiaSeoul.getMonthValue());
    String day1 = String.valueOf(nowAsiaSeoul.getDayOfMonth());
    String hour = String.valueOf(nowAsiaSeoul.getHour());
    String minute = String.valueOf(nowAsiaSeoul.getMinute());

    String fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute;

    String email;
    String dupName;
    String pw;
    String rname;

    int dupliNum = 0;
    int compnum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (hour.length() == 1) {
            hour = "0" + hour;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute;
        }

        if (minute.length() == 1) {
            minute = "0" + minute;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute;
        }

        mAuth = FirebaseAuth.getInstance();

        final EditText emailTxt = (EditText) findViewById(R.id.m_id); //이메일
        final EditText nameTxt = (EditText) findViewById(R.id.btn_name);//닉네임
        final EditText pwdTxt = (EditText) findViewById(R.id.m_pass);//비번
        final EditText rnameTxt = (EditText) findViewById(R.id.btn_rname);


        final Button joinBtn = (Button) findViewById(R.id.btn_register);
        final Button btn_duplicate = findViewById(R.id.btn_duplicate);

        btn_duplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compnum = 1;
                dupName = nameTxt.getText().toString();
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
                                        if (!(dupName.equals(document.getData().get("id_nickName").toString()))) {
                                        } else {
                                            compnum = 2;
                                        }
                                    }
                                } else {
                                    Log.d("태그", "Error getting documents: ", task.getException());
                                }
                                //////////////////////////중복확인 toast////////////////////////////////

                                if (compnum == 2) {
                                    Log.d("확인을 위한 num", "중복일시");
                                    Toast.makeText(RegisterActivity.this, "중복된닉네임입니다", Toast.LENGTH_SHORT).show();
                                } else if (compnum == 1) {
                                    Log.d("확인을 위한 num", "중복이 아닐시");
                                    Toast.makeText(RegisterActivity.this, "사용가능한닉네임입니다", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
            }
        });

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailTxt.getText().toString();
                //dupName = nameTxt.getText().toString();
                pw = pwdTxt.getText().toString();
                rname = rnameTxt.getText().toString();

                if (compnum == 2)
                    Toast.makeText(RegisterActivity.this, "닉네임중복확인을해주세요", Toast.LENGTH_SHORT).show();
                else if (compnum == 1) {
                    joinStart(email, dupName, pw, rname);
                }
            }
        });



        /*
        m_id = (EditText) findViewById(R.id.m_id);
        m_pass = (EditText) findViewById(R.id.m_pass);

        btn_register = findViewById(R.id.btn_register);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = m_id.getText().toString().trim();
                String pwd = m_pass.getText().toString().trim();

                firebaseAuth.createUserWithEmailAndPassword(email,pwd)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    //intent.putExtra( "str", str);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(RegisterActivity.this,"등록실패",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });

            }
        });
         */
    }

    private void joinStart(final String email, final String name, String password, final String realname) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(RegisterActivity.this, "비밀번호가 간단해요..", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(RegisterActivity.this, "email 형식에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(RegisterActivity.this, "이미존재하는 email 입니다.", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(RegisterActivity.this, "다시 확인해주세요..", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            currentUser = mAuth.getCurrentUser();

                            id_value = email;
                            id_uid = mAuth.getUid();
                            id_name = realname;
                            id_nickName = name;

                            Log.d("닉네임 출력", id_nickName);

                            String point = "10";
                            CollectionReference title_content = db.collection("user");
                            Map<String, Object> user = new HashMap<>();
                            user.put("id_email", id_value);
                            user.put("id_uid", id_uid);
                            user.put("id_name", id_name);
                            user.put("id_nickName", id_nickName);
                            user.put("id_point", point);

                            title_content.document(id_uid).set(user);
                            Log.d("유저정보 id로그인 uid 확인", id_uid);
                            db.collection("user").document(id_uid)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });

                            CollectionReference pointDay = db.collection("user");
                            Map<String, Object> user1 = new HashMap<>();
                            user1.put("pointDay", day1);
                            user1.put("pointLimit", pointLimit);

                            pointDay.document(id_uid).collection("pointDay").document(id_value + "pointDay").set(user1);
                            Log.d("유저정보 id로그인 uid 확인", id_uid);
                            db.collection("user").document(id_uid).collection("pointDay").document(id_value + "pointDay")
                                    .set(user1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("point 데이터 확인", day1 + "  " + pointLimit);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });

                            Map<String, Object> member = new HashMap<>();
                            member.put("day", fullDay);
                            member.put("point", "+10");
                            member.put("type", "회원가입 지급 포인트");

                            db.collection("user").document(id_uid).collection("pointHistory")
                                    .add(member)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                        }
                                    });

                            Toast.makeText(RegisterActivity.this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
    }

    public void onBackPressed() {
        RegisterActivity.this.finish();
        super.onBackPressed();
    }

}
