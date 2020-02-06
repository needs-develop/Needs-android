package com.example.project_basic;
//첫 로그인창//

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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


    private Button btn_test; // 로그인
    private Button btn2_test; //회원가입
    private String str;

    static String id_value;
    static String id_uid;
    static String id_nickName;
    static String id_name;
    static String photoUrl;
    static  int changeNum = 0;

    String value = null ;

    private SignInButton btn_google; //구글로그인버튼

    private FirebaseAuth auth; // 파이어베이스 인증 객체 구글로그인
    private FirebaseAuth firebaseAuth;//이메일 로그인 인증객체

    private GoogleApiClient googleApiClient; //구글 API클라이언트
    private static final int REQ_SIGN_GOOGLE = 100; //GOOGLE로그인 결과 코드


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    int compareNum = 1;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Instant nowUtc = Instant.now();
    ZoneId asiaSeoul = ZoneId.of("Asia/Seoul");
    ZonedDateTime nowAsiaSeoul = ZonedDateTime.ofInstant(nowUtc, asiaSeoul);

    String year = String.valueOf(nowAsiaSeoul.getYear());
    String  month = String.valueOf(nowAsiaSeoul.getMonthValue());
    String day1  = String.valueOf(nowAsiaSeoul.getDayOfMonth()) ;
    String hour = String.valueOf(nowAsiaSeoul.getHour());
    String minute = String.valueOf(nowAsiaSeoul.getMinute());

    String fullDay = year+"/"+month+"/"+day1+" "+hour+":"+minute;

    String pointLimit = "5";

    GoogleSignInAccount account;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if(hour.length()==1)
        {
            hour = "0"+hour;
            fullDay = year+"/"+month+"/"+day1+" "+hour+":"+minute;
        }

        if(minute.length()==1)
        {
            minute = "0"+minute;
            fullDay = year+"/"+month+"/"+day1+" "+hour+":"+minute;
        }


        final EditText emailTxt = (EditText)findViewById(R.id.et_id);
        final EditText pwTxt = (EditText)findViewById(R.id.pw_id);


        et_id = findViewById(R.id.et_id);
        pw_id = findViewById(R.id.pw_id);
        btn_name = findViewById(R.id.btn_name);
        btn_rname = findViewById(R.id.btn_rname);

        btn_test = findViewById(R.id.btn_test);
        btn2_test = findViewById(R.id.btn2_test);

        firebaseAuth = firebaseAuth.getInstance();//객체 초기화

        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTxt.getText().toString();
                String password = pwTxt.getText().toString();

                id_value = email;

                loginStart(email,password);
            }
        });

        /*
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_id.getText().toString().trim();
                String pwd = pw_id.getText().toString().trim();
                firebaseAuth.signInWithEmailAndPassword(email,pwd)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    str = et_id.getText().toString();//getText까지는 string 형태가 아님
                                    Toast.makeText(getApplicationContext(), "로그인되었습니다", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, SubActivity.class);
                                    intent.putExtra( "nickName", str);
                                    //intent.putExtra( "str", str);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this,"로그인 오류",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        */

        btn2_test.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        }));

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();

        auth = FirebaseAuth.getInstance();//파이어베이스 인증객체 초기화

        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,REQ_SIGN_GOOGLE);
            }
        });
    }

    public void loginStart(final String email, String password){
        // Toast.makeText(MainActivity.this,"loginStart 함수 안으로" ,Toast.LENGTH_SHORT).show();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //Toast.makeText(MainActivity.this,"mAuth. onComplete 함수" ,Toast.LENGTH_SHORT).show();
                if (!task.isSuccessful()) {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        Toast.makeText(MainActivity.this,"존재하지 않는 id 입니다." ,Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(MainActivity.this,"비밀번호를 다시 입력하세요." ,Toast.LENGTH_SHORT).show();
                    } catch (FirebaseNetworkException e) {
                        //  Toast.makeText(MainActivity.this,"Firebase NetworkException" ,Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        //  Toast.makeText(MainActivity.this,"Exception" ,Toast.LENGTH_SHORT).show();
                    }

                }else{
                    currentUser = mAuth.getCurrentUser();
                    //Toast.makeText(MainActivity.this, "로그인 성공" + "/" + currentUser.getEmail() + "/" + currentUser.getUid() ,Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this,"로그인 하였습니다",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();



                    startActivity(new Intent(MainActivity.this, SubActivity.class));

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

        if(currentUser != null && id_value!=null){
            startActivity(new Intent(MainActivity.this, SubActivity.class));
            finish();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //구글로그인 인증요청했을때 결과 값을 되돌려 받는곳
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_SIGN_GOOGLE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){//인증결과 성공시
                account = result.getSignInAccount();//구글로그인정보를 담고있음
                resultLogin(account);//로그인 결과 값 출력 수행
            }
        }
    }

    private void resultLogin(final GoogleSignInAccount accountSuv) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {//로그인이 성공했으면
                            Toast.makeText(MainActivity.this,"로그에 성공 하였습니다",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),SubActivity.class);
                            //Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
                            int returnNum = 0;

                            try {
                                returnNum = firstCheck(accountSuv);
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if(returnNum ==1)
                            {
                                Log.d("리턴넘버1","리턴넘버1");
                                firstLogin();
                            }
                            else if(returnNum ==0)
                            {
                                Log.d("리턴넘버2","리턴넘버2");
                                DocumentReference doc = db.collection("user").document(id_uid);
                                doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            id_value = (String)document.getData().get("id_email");
                                            id_uid = (String)document.getData().get("id_uid");
                                            id_name = (String)document.getData().get("id_name");
                                            id_nickName =  (String)document.getData().get("id_nickName");
                                            photoUrl = (String)document.getData().get("photoUrl");
                                            Log.d("순서체크","순서체크");
                                        }
                                        else {
                                        }
                                    }
                                });
                            }
                            startActivity(intent);

                        }
                        else{
                            Toast.makeText(MainActivity.this,"로그인을 실패하였습니다",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    public int firstCheck(GoogleSignInAccount account)
    {
        db.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(!(document.exists()))
                                {
                                    compareNum = 1;
                                    Log.d("존재여부","존재");

                                }
                                else if (id_uid == document.getData().get("id_uid").toString())
                                {
                                    //id_nickName = document.getData().get("id_nickName").toString();

                                    Log.d("확인둘",id_nickName);
                                    compareNum = 0;
                                    break;
                                }
                                else
                                {
                                    Log.d("파일이 없고 이름이 없다면","존재");
                                    compareNum = 1;
                                }
                            }
                        } else {

                        }
                    }
                });
        return compareNum;
    }

    public void firstLogin()
    {
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
        user.put("photoUrl",photoUrl);

        title_content.document(id_uid).set(user);
        Log.d("유저정보 uid 확인", id_uid);
        db.collection("user").document(id_uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });

        CollectionReference pointDay = db.collection("user");
        Map<String, Object> user1 = new HashMap<>();
        user1.put("pointDay", day1);
        user1.put("pointLimit",pointLimit);

        pointDay.document(id_uid).collection("pointDay").document(id_value+"pointDay").set(user1);
        Log.d("유저정보 id로그인 uid 확인", id_uid);
        db.collection("user").document(id_uid).collection("pointDay").document(id_value+"pointDay")
                .set(user1)
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
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this,"연결이 해제되었습니다",Toast.LENGTH_SHORT).show();
    }
}

 /*
                            if( compareNum != 0 &&changeNum==0)
                            {
                                DocumentReference doc = db.collection("user").document(id_uid);
                                doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            Log.d("닉네임확인","닉네임확인");
                                            if((String)document.getData().get("id_nickName")!=null)
                                            id_nickName =  (String)document.getData().get("id_nickName");
                                            else id_nickName = account.getDisplayName();
                                        }
                                        else {
                                        }
                                    }
                                });
                                String point = "10";
                                CollectionReference title_content = db.collection("user");
                                Map<String, Object> user = new HashMap<>();
                                user.put("id_email", id_value);
                                user.put("id_uid", id_uid);
                                user.put("id_name", id_name);
                                if(id_nickName == null)  id_nickName = account.getDisplayName();
                                user.put("id_nickName", id_nickName);
                                user.put("id_point", point);
                                user.put("photoUrl",photoUrl);
                                title_content.document(id_uid).set(user);
                                Log.d("유저정보 uid 확인", id_uid);
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
                                user1.put("pointDay", day);
                                user1.put("pointLimit",pointLimit);
                                pointDay.document(id_uid).collection("pointDay").document(id_value+"pointDay").set(user1);
                                Log.d("유저정보 id로그인 uid 확인", id_uid);
                                db.collection("user").document(id_uid).collection("pointDay").document(id_value+"pointDay")
                                        .set(user1)
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
                            }
                            */