package com.tester.Needs.Main;
//첫 로그인창//

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.multidex.MultiDex;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tester.Needs.R;
import com.tester.Needs.Service.MyService;
import com.tester.Needs.Splash.SplashActivity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private EditText et_id; //아이디
    private EditText pw_id;  //비밀번호
    private EditText btn_name; //닉네임
    private EditText btn_rname;

    EditText emailTxt, pwTxt;

    private Button btn_test; // 로그인
    private Button btn2_test; //회원가입
    private String str;

    static String id_value;
    static String id_uid;
    static String id_nickName;
    static String id_name;
    static String photoUrl;
    static int changeNum = 0;

    String value = null;
    String googleString;

    private SignInButton btn_google; //구글로그인버튼

    private FirebaseAuth auth; // 파이어베이스 인증 객체 구글로그인
    private FirebaseAuth firebaseAuth;//이메일 로그인 인증객체

    private GoogleApiClient googleApiClient; //구글 API클라이언트
    private static final int REQ_SIGN_GOOGLE = 100; //GOOGLE로그인 결과 코드


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    boolean compareBoolean = false;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

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


    String pointLimit = "5";

    GoogleSignInAccount account;
    GoogleSignInClient ac;

    private CallbackManager callbackManager;

    private ProgressDialog mProgressDialog;
    private BackgroundThread mBackThread;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_main);



        mAuth = FirebaseAuth.getInstance();

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
        emailTxt = (EditText) findViewById(R.id.et_id);
        pwTxt = (EditText) findViewById(R.id.pw_id);


        et_id = findViewById(R.id.et_id);
        pw_id = findViewById(R.id.pw_id);
        btn_name = findViewById(R.id.username);
        btn_rname = findViewById(R.id.name);

        btn_test = findViewById(R.id.btn_test);
        btn2_test = findViewById(R.id.btn2_test);

        firebaseAuth = firebaseAuth.getInstance();//객체 초기화
        auth = FirebaseAuth.getInstance();//파이어베이스 인증객체 초기화

        /*만약 email인증을 해야된다면 이런예시로 구현할 예정
        Button buttonFind = (Button) findViewById(R.id.buttonFind);

        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //비밀번호 재설정 이메일 보내기

                firebaseAuth.sendPasswordResetEmail("oss5824@naver.com")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "이메일을 보냈습니다.", Toast.LENGTH_LONG).show();
                                    Log.d("성공","이메일전송 성공");

                                } else {
                                    Toast.makeText(MainActivity.this, "메일 보내기 실패!", Toast.LENGTH_LONG).show();
                                    Log.d("실패","이메일전송 실패");
                                }

                            }
                        });

            }

        });
        */


        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailTxt.getText().toString();
                id_value = email;

                CustomerLogin();

            }
        });

        btn2_test.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        }));


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, REQ_SIGN_GOOGLE);
            }
        });

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {

                } else {
                    Log.d("facebookUID", mAuth.getCurrentUser().getUid());
                    Log.d("facebookDisplayName", mAuth.getCurrentUser().getDisplayName());
                    Log.d("facebookEmail", mAuth.getCurrentUser().getEmail());
                    Log.d("facebookIProviderId", mAuth.getCurrentUser().getProviderId());
                    Log.d("facebookPhoto", mAuth.getCurrentUser().getPhotoUrl().toString());
                    id_value = mAuth.getCurrentUser().getEmail();
                    id_uid = mAuth.getCurrentUser().getUid();
                    id_name = mAuth.getCurrentUser().getDisplayName();
                    id_nickName = mAuth.getCurrentUser().getDisplayName();
                    photoUrl = String.valueOf(mAuth.getCurrentUser().getPhotoUrl());
                    try {
                        DocumentReference doc = db.collection("user").document(id_uid);
                        Log.d("compare false1", "compare false1");
                        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d("compare false2", "compare false2");
                                    try {
                                        String value = task.getResult().get("id_email").toString();
                                        id_value = task.getResult().get("id_email").toString();
                                        id_uid = task.getResult().get("id_uid").toString();
                                        id_name = task.getResult().get("id_name").toString();
                                        id_nickName = task.getResult().get("id_nickName").toString();
                                        photoUrl = task.getResult().get("photoUrl").toString();

                                        Log.d("compare false", value);
                                        compareBoolean = false;

                                        Intent intent = new Intent(MainActivity.this, SubActivity.class);
                                        mProgressDialog = ProgressDialog.show(MainActivity.this, "Loading"
                                                , "로그인중입니다..");

                                        mBackThread = new BackgroundThread();
                                        mBackThread.setRunning(true);
                                        mBackThread.start();
                                        startActivity(intent);

                                        if (!MainActivity.this.isFinishing())
                                            mProgressDialog.dismiss();
                                        finish();
                                    } catch (Exception e) {
                                        Log.d("compare false", "예외처리");
                                        compareBoolean = true;
                                        facebookFirstLogin();
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.d("compare true", "compare true");
                        compareBoolean = true;
                    }

                }
            }
        });
    }

    private void facebookFirstLogin() {

        Log.d("photourl값출력", photoUrl);
        String point = "10";
        CollectionReference title_content = db.collection("user");
        Map<String, Object> user = new HashMap<>();
        user.put("id_email", id_value);
        user.put("id_uid", id_uid);
        user.put("id_name", id_name);
        user.put("id_nickName", id_nickName);
        user.put("id_point", point);
        user.put("photoUrl", photoUrl);

        title_content.document(id_uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("firebase 등록", id_uid);
                    }
                });

        CollectionReference pointDay = db.collection("user");
        Map<String, Object> user1 = new HashMap<>();
        user1.put("pointDay", day1);
        user1.put("pointLimit", pointLimit);

        pointDay.document(id_uid).collection("pointDay").document(id_value + "pointDay").set(user1);
        db.collection("user").document(id_uid).collection("pointDay").document(id_value + "pointDay")
                .set(user1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("pointDay", id_uid);
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
                        Log.d("pointHistory", id_uid);
                    }
                });

        Intent intent = new Intent(MainActivity.this, SubActivity.class);
        mProgressDialog = ProgressDialog.show(MainActivity.this, "Loading"
                , "로그인중입니다..");

        mBackThread = new BackgroundThread();
        mBackThread.setRunning(true);
        mBackThread.start();
        //Toast.makeText(MainActivity.this,"회원가입이 완료되었습니다.다시로그인해주세요", Toast.LENGTH_SHORT).show();
        startActivity(intent);

        if (!MainActivity.this.isFinishing()) mProgressDialog.dismiss();
        finish();

    }

    private void CustomerLogin() {
        String email = emailTxt.getText().toString();
        String password = pwTxt.getText().toString();

        if (email.isEmpty()) {
            emailTxt.setError("Email is required");
            emailTxt.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTxt.setError("Enter a valid email");
            emailTxt.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            pwTxt.setError("Password is required");
            pwTxt.requestFocus();
            return;
        }

        loginStart(email, password);
    }

    public void loginStart(final String email, String password) {
        // Toast.makeText(MainActivity.this,"loginStart 함수 안으로" ,Toast.LENGTH_SHORT).show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //Toast.makeText(MainActivity.this,"mAuth. onComplete 함수" ,Toast.LENGTH_SHORT).show();
                if (!task.isSuccessful()) {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        emailTxt.setError("존재하지 않는 email 입니다");
                        emailTxt.requestFocus();
                        //Toast.makeText(MainActivity.this, "존재하지 않는 id 입니다.", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        pwTxt.setError("비밀번호를 다시 입력하세요");
                        pwTxt.requestFocus();
                        // Toast.makeText(MainActivity.this, "비밀번호를 다시 입력하세요.", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseNetworkException e) {
                        //  Toast.makeText(MainActivity.this,"Firebase NetworkException" ,Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        //  Toast.makeText(MainActivity.this,"Exception" ,Toast.LENGTH_SHORT).show();
                    }

                } else {
                    currentUser = mAuth.getCurrentUser();
                    id_uid = currentUser.getUid();
                    id_value = email;

                    //Toast.makeText(MainActivity.this, "로그인 성공" + "/" + currentUser.getEmail() + "/" + currentUser.getUid() ,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, SubActivity.class);
                    mProgressDialog = ProgressDialog.show(MainActivity.this, "Loading"
                            , "로그인중입니다..");

                    mBackThread = new BackgroundThread();
                    mBackThread.setRunning(true);
                    mBackThread.start();
                    //Toast.makeText(MainActivity.this,"회원가입이 완료되었습니다.다시로그인해주세요", Toast.LENGTH_SHORT).show();
                    startActivity(intent);

                    if (!MainActivity.this.isFinishing()) mProgressDialog.dismiss();
                    finish();
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null && id_value != null) {
            startActivity(new Intent(MainActivity.this, SubActivity.class));
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //구글로그인 인증요청했을때 결과 값을 되돌려 받는곳
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SIGN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {//인증결과 성공시
                account = result.getSignInAccount();//구글로그인정보를 담고있음
                resultLogin(account);//로그인 결과 값 출력 수행
            }
        }
    }

    private void resultLogin(final GoogleSignInAccount accountSuv) {
        AuthCredential credential = GoogleAuthProvider.getCredential(accountSuv.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {//로그인이 성공했으면
                            //Toast.makeText(MainActivity.this, "로그에 성공 하였습니다", Toast.LENGTH_SHORT).show();
                            boolean returnBoolean = true;

                            googleString = auth.getCurrentUser().getUid();
                            id_value = auth.getCurrentUser().getEmail();
                            id_uid = auth.getCurrentUser().getUid();
                            id_name = auth.getCurrentUser().getDisplayName();
                            id_nickName = auth.getCurrentUser().getDisplayName();
                            photoUrl = String.valueOf(auth.getCurrentUser().getPhotoUrl());

                            //returnBoolean = firstCheck(googleString);

                            try {
                                DocumentReference doc = db.collection("user").document(id_uid);
                                doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("compare false", "compare false");
                                            try {
                                                String value = task.getResult().get("id_email").toString();
                                                id_value = task.getResult().get("id_email").toString();
                                                id_uid = task.getResult().get("id_uid").toString();
                                                id_name = task.getResult().get("id_name").toString();
                                                id_nickName = task.getResult().get("id_nickName").toString();
                                                photoUrl = task.getResult().get("photoUrl").toString();
                                                Log.d("compare false", value);
                                                compareBoolean = false;
                                                Intent intent = new Intent(getApplicationContext(), SubActivity.class);
                                                startActivity(intent);


                                                mProgressDialog = ProgressDialog.show(MainActivity.this, "Loading"
                                                        , "로그인중입니다..");

                                                mBackThread = new BackgroundThread();
                                                mBackThread.setRunning(true);
                                                mBackThread.start();
                                                if (!MainActivity.this.isFinishing()) {
                                                    mProgressDialog.dismiss();
                                                }
                                                finish();

                                            } catch (Exception e) {
                                                Log.d("compare false", "예외처리");
                                                compareBoolean = true;
                                                firstLogin();
                                            }
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                Log.d("compare true", "compare true");
                                compareBoolean = true;
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "로그인을 실패하였습니다", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void firstLogin() {
        id_value = account.getEmail();
        id_uid = mAuth.getUid();
        id_name = account.getDisplayName();
        id_nickName = account.getDisplayName();
        photoUrl = String.valueOf(account.getPhotoUrl());

        String point = "10";
        CollectionReference title_content = db.collection("user");
        Map<String, Object> user = new HashMap<>();
        user.put("id_email", id_value);
        user.put("id_uid", id_uid);
        user.put("id_name", id_name);
        user.put("id_nickName", id_nickName);
        user.put("id_point", point);
        user.put("photoUrl", photoUrl);

        title_content.document(id_uid).set(user);
        Log.d("유저정보 uid 확인", id_uid);
        db.collection("user").document(id_uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("첫", "첫");
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
                        Log.d("둘", "둘");
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
                        Log.d("셋", "셋");
                    }
                });
        Intent intent = new Intent(getApplicationContext(), SubActivity.class);
        startActivity(intent);


        mProgressDialog = ProgressDialog.show(MainActivity.this, "Loading"
                , "로그인중입니다..");

        mBackThread = new BackgroundThread();
        mBackThread.setRunning(true);
        mBackThread.start();
        if (!MainActivity.this.isFinishing()) {
            mProgressDialog.dismiss();
        }
        finish();


    }

    public class BackgroundThread extends Thread {
        volatile boolean running = false;
        int cnt;

        void setRunning(boolean b) {
            running = b;
            cnt = 10;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    sleep(500);
                    if (cnt-- == 0) {
                        running = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handler.sendMessage(handler.obtainMessage());
        }

    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mProgressDialog.dismiss();

            boolean retry = true;
            while (retry) {
                try {
                    mBackThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }

    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "연결이 해제되었습니다", Toast.LENGTH_SHORT).show();
    }
}

