package com.tester.Needs.Main;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tester.Needs.Service.MyService;
import com.tester.Needs.R;
import com.tester.Needs.Setting.MyLikeActivity;

import java.io.FileNotFoundException;
import java.io.IOException;

import static com.tester.Needs.Main.MainActivity.id_name;
import static com.tester.Needs.Main.MainActivity.id_nickName;
import static com.tester.Needs.Main.MainActivity.id_uid;
import static com.tester.Needs.Main.MainActivity.id_value;
import static com.tester.Needs.Main.SubActivity.fragmentNumber;
//import static com.tester.Needs.Main.SubActivity.getActivity;
import static com.tester.Needs.Main.SubActivity.point;
import static com.tester.Needs.Main.SubActivity.pointLimit;
import static com.tester.Needs.Main.SubActivity.sub_photoUrl;

public class UserInfoActivity extends AppCompatActivity {


    String change_nickname;
    int compnum = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private static final int REQ_CODE_SELECT_IMAGE = 900;
    GoogleSignInClient googleSignInClient;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        stopService(new Intent(UserInfoActivity.this,MyService.class));
        //fragmentNumber = 0;

        //getActivity = UserInfoActivity.class;


        /*
        Switch foreGroundSwitch = findViewById(R.id.foreSwitch);
        foreGroundSwitch.setOnCheckedChangeListener(new foreGroundSwitchListener());
        */
        ImageView userinfo_profile= findViewById(R.id.userInfo_profile);
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


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getProviderData() == null || user.getProviderData().isEmpty()) {
        } else {
            for (UserInfo profile : user.getProviderData()) {
                String providerId = profile.getProviderId();
                if (GoogleAuthProvider.PROVIDER_ID.equals(providerId)) {
                    Glide.with(this).load(sub_photoUrl).into(userinfo_profile);
                    GoogleSignInOptions gso =
                            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(getString(R.string.default_web_client_id))
                                    .requestEmail()
                                    .build();
                    googleSignInClient = GoogleSignIn.getClient(UserInfoActivity.this, gso);
                } else if (FacebookAuthProvider.PROVIDER_ID.equals(providerId)) {
                    Glide.with(this).load(sub_photoUrl).into(userinfo_profile);
                } else if (TwitterAuthProvider.PROVIDER_ID.equals(providerId)) {
                } else if (EmailAuthProvider.PROVIDER_ID.equals(providerId)) {
                  //  Glide.with(this).load(sub_photoUrl).into(userinfo_profile);
                } else {
                }
            }
        }
        userinfo_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        Button password_btn = findViewById(R.id.password_btn);
        password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //비밀번호 재설정 이메일 보내기
                firebaseAuth.sendPasswordResetEmail(id_value)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UserInfoActivity.this, "이메일을 보냈습니다.", Toast.LENGTH_LONG).show();
                                    Log.d("성공","이메일전송 성공");

                                } else {
                                    Toast.makeText(UserInfoActivity.this, "메일 보내기 실패!", Toast.LENGTH_LONG).show();
                                    Log.d("실패","이메일전송 실패");
                                }

                            }
                        });

            }

        });


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getBaseContext(), "resultCode : " + resultCode, Toast.LENGTH_SHORT).show();

        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    String name_Str = getImageNameToUri(data.getData());
                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView) findViewById(R.id.userInfo_profile);
                    //배치해놓은 ImageView에 set
                    image.setImageBitmap(image_bitmap);
                    sub_photoUrl = name_Str;
                    Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();
                    db.collection("user").document(id_uid)
                            .update(
                                    "photoUrl",name_Str
                                    );

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }
    /*
    class foreGroundSwitchListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked)
            {
                Intent intent = new Intent(UserInfoActivity.this, MyService.class);
                intent.setAction("startForeground");
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                    startForegroundService(intent);
                }else{
                    startService(intent);
                }
            }
            else
            {
                Intent intent = new Intent(UserInfoActivity.this, MyService.class);
                startService(intent);
            }

        }
    }*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //fragmentNumber = 2;
        //stopService(new Intent(UserInfoActivity.this, MyService.class));
        //Intent intent = new Intent(UserInfoActivity.this,SubActivity.class);
        //startActivity(intent);
        this.finish();
    }
/*
    @Override
    protected void onUserLeaveHint() {
        Intent intent = new Intent(UserInfoActivity.this, MyService.class);
        intent.setAction("startForeground");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            startForegroundService(intent);
        }else{
            startService(intent);
        }
    }

 */

}
