package com.tester.Needs.Main;
//회원가입창//

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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
import com.tester.Needs.R;
import com.tester.Needs.Splash.Guide1Activity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.tester.Needs.Main.MainActivity.id_name;
import static com.tester.Needs.Main.MainActivity.id_nickName;
import static com.tester.Needs.Main.MainActivity.id_uid;
import static com.tester.Needs.Main.MainActivity.id_value;

@RequiresApi(api = Build.VERSION_CODES.O)
public class RegisterActivity extends AppCompatActivity {

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

    String second = String.valueOf(nowAsiaSeoul.getSecond());

    String fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute + ":" + second;

    String email;
    String dupName;
    String pw;
    String rname;
    String changeDupName;

    int dupliNum = 0;
    int compnum = 2;

    private EditText nameTxt, usernameTxt, emailTxt, passwordTxt;
    private Button btn_duplicate;
    private ImageView btnHelp;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (month.length() == 1) {
            month = "0" + month;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute + ":" + second;
        }
        if (day1.length() == 1) {
            day1 = "0" + day1;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute + ":" + second;
        }


        if (hour.length() == 1) {
            hour = "0" + hour;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute + ":" + second;
        }

        if (minute.length() == 1) {
            minute = "0" + minute;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute + ":" + second;
        }

        if (second.length() == 1) {
            second = "0" + second;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute + ":" + second;
        }
        mAuth = FirebaseAuth.getInstance();

        nameTxt = (EditText) findViewById(R.id.name);
        usernameTxt = (EditText) findViewById(R.id.username);// 닉네임
        emailTxt = (EditText) findViewById(R.id.email);
        passwordTxt = (EditText) findViewById(R.id.password);// 비밀번호

        btn_duplicate = findViewById(R.id.btn_duplicate);
        btn_duplicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                compnum = 1;
                dupName = usernameTxt.getText().toString();
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
                                            break;
                                        }
                                    }
                                } else {
                                    Log.d("태그", "Error getting documents: ", task.getException());
                                }
                                //////////////////////////중복확인 toast////////////////////////////////
                                if (dupName.isEmpty()) {
                                    usernameTxt.setError("닉네임이 필요합니다");
                                    usernameTxt.requestFocus();
                                } else if (dupName.length() < 2) {
                                    usernameTxt.setError("닉네임은 2 자 이상이어야합니다");
                                    usernameTxt.requestFocus();
                                } else if (compnum == 2) {
                                    // Log.d("확인을 위한 num", "중복일시");
                                    usernameTxt.setError("중복된 닉네임");
                                    usernameTxt.requestFocus();
                                } else if (compnum == 1) {
                                    // Log.d("확인을 위한 num", "중복이 아닐시");
                                    showAlert();
                                }
                            }
                        });
            }
        });


        Button registerBtn = (Button) findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!changeDupName.equals(usernameTxt.getText().toString())) {
                        compnum = 2;
                        usernameTxt.requestFocus();
                    }
                } catch (Exception e) {

                }
                if (compnum == 2) {
                    usernameTxt.setError("닉네임 중복 확인을 해주세요");
                    usernameTxt.requestFocus();
                } else
                    customerRegister();
            }
        });

        btnHelp = (ImageView) findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, Guide1Activity.class));
            }
        });
    }


    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("닉네임 중복확인");
        builder.setMessage("사용 가능한 닉네임입니다.");

        builder.setPositiveButton("사용하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                usernameTxt.setClickable(false);
                usernameTxt.setFocusable(false);
                changeDupName = usernameTxt.getText().toString();
                btn_duplicate.setEnabled(false);
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void customerRegister() {
        String name = nameTxt.getText().toString();
        String username = usernameTxt.getText().toString();
        String email = emailTxt.getText().toString();
        String password = passwordTxt.getText().toString();

        if (name.isEmpty()) {
            nameTxt.setError("이름이 필요합니다");
            nameTxt.requestFocus();
            return;
        }

        if (name.length() < 2) {
            nameTxt.setError("이름은 2 자 이상이어야합니다");
            nameTxt.requestFocus();
            return;
        }

        if (!name.matches("^[가-힣]*$")) {
            nameTxt.setError("한글만 허용합니다");
            nameTxt.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailTxt.setError("이메일이 필요합니다");
            emailTxt.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTxt.setError("유효한 이메일을 입력하십시오");
            emailTxt.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordTxt.setError("비밀번호가 필요합니다");
            passwordTxt.requestFocus();
            return;
        }
        registerStart(email, name, password, username);
    }

    private void registerStart(final String email, final String name, String password, final String realname) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();

                            id_value = email;
                            id_uid = mAuth.getUid();
                            id_name = name;
                            id_nickName = realname;

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
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                passwordTxt.setError("비밀번호가 간단해요..");
                                passwordTxt.requestFocus();
                                // Toast.makeText(RegisterActivity.this, "비밀번호가 간단해요..", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                emailTxt.setError("Enter a valid email");
                                emailTxt.requestFocus();
                                // Toast.makeText(RegisterActivity.this, "email 형식에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthUserCollisionException e) {
                                emailTxt.setError("유효한 이메일을 입력하십시오");
                                emailTxt.requestFocus();
                                // Toast.makeText(RegisterActivity.this, "이미 존재하는 email 입니다.", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(RegisterActivity.this, "다시 확인해주세요..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void onBackPressed() {
        RegisterActivity.this.finish();
        super.onBackPressed();
    }

}
