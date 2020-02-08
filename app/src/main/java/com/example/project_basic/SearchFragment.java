package com.example.project_basic;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static com.example.project_basic.MainActivity.changeNum;
import static com.example.project_basic.MainActivity.id_name;
import static com.example.project_basic.MainActivity.id_nickName;
import static com.example.project_basic.MainActivity.id_uid;
import static com.example.project_basic.MainActivity.photoUrl;
import static com.example.project_basic.SubActivity.fragmentNumber;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class SearchFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();//firestore객체
    String nickName = "noDisplay";
    String change_nickName;
    String name;

    GridView gridView;
    GridView gridViewInfo;
    GridView gridViewUser;

    SettingAdapter settingAdapter;
    SettingAdapter settingAdapterInfo;
    SettingAdapter settingAdapterUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_notifications, container,
                false);

        fragmentNumber = 0;

        TextView nametxt = rootView.findViewById(R.id.user_name);
        TextView emailtxt = rootView.findViewById(R.id.user_email);
        final TextView usernametxt = rootView.findViewById(R.id.user_display_name);
        TextView userenabled = rootView.findViewById(R.id.user_enabled_providers);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ImageView iv_profile;
        iv_profile = rootView.findViewById(R.id.user_profile_picture);
        gridView = rootView.findViewById(R.id.gridView);
        gridViewInfo = rootView.findViewById(R.id.gridViewInfo);
        gridViewUser = rootView.findViewById(R.id.gridViewUserInfo);

        nametxt.setText(id_name);
        emailtxt.setText(
                TextUtils.isEmpty(user.getEmail()) ? "No email" : user.getEmail());

        /*
        DocumentReference docRef = db.collection("user").document(id_uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                        nickName =  (String)document.getData().get("id_nickName");
                        usernametxt.setText(nickName);
                }
                else {
                       usernametxt.setText(nickName);
                }
            }
        });
        */

        usernametxt.setText(id_nickName);
        StringBuilder providerList = new StringBuilder(100);

        providerList.append("Providers used: ");

        if (user.getProviderData() == null || user.getProviderData().isEmpty()) {
            providerList.append("none");
        } else {
            for (UserInfo profile : user.getProviderData()) {
                String providerId = profile.getProviderId();
                if (GoogleAuthProvider.PROVIDER_ID.equals(providerId)) {
                    providerList.append("Google");
                    DocumentReference doc = db.collection("user").document(id_uid);
                    doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Log.d("사진출력", (String) document.getData().get("photoUrl"));
                                photoUrl = (String) document.getData().get("photoUrl");
                                Log.d("사진출력 photoUrl", photoUrl);
                            } else {
                            }
                        }
                    });
                    Log.d("사진출력 photoUrl 두번째", photoUrl);
                    Glide.with(this).load(photoUrl).into(iv_profile);
                } else if (FacebookAuthProvider.PROVIDER_ID.equals(providerId)) {
                    providerList.append("Facebook");
                    DocumentReference doc = db.collection("user").document(id_uid);
                    doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Log.d("사진출력", (String) document.getData().get("photoUrl"));
                                photoUrl = (String) document.getData().get("photoUrl");
                                Log.d("사진출력 photoUrl", photoUrl);
                            } else {
                            }
                        }
                    });
                    Log.d("사진출력 photoUrl 두번째", photoUrl);
                    Glide.with(this).load(photoUrl).into(iv_profile);
                } else if (TwitterAuthProvider.PROVIDER_ID.equals(providerId)) {
                    providerList.append("Twitter");
                } else if (EmailAuthProvider.PROVIDER_ID.equals(providerId)) {
                    providerList.append("Email");
                } else {
                    providerList.append(providerId);
                }
            }
        }
        userenabled.setText(providerList);

        Button user_nickName_change = rootView.findViewById(R.id.user_nickName_change);
        user_nickName_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("닉네임변경").setMessage("OK를 누르시면 로그인창으로 이동합니다");
                final EditText et = new EditText(getActivity());
                builder.setView(et);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        change_nickName = et.getText().toString();
                        db.collection("user").document(id_uid)
                                .update(
                                        "id_nickName", change_nickName
                                );
                        changeNum = 1;
                        signOut();
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
        });



        settingAdapter = new SettingAdapter();
        settingAdapter.addItem(new SettingItem("포인트내역",R.drawable.history));
        settingAdapter.addItem(new SettingItem("포인트전환",R.drawable.money));

        gridView.setAdapter(settingAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    Intent i = new Intent(getActivity(), PointHistoryActivity.class);
                    startActivity(i);
                }
                else if(position==1){
                    Intent i = new Intent(getActivity(), PointConversionActivity.class);
                    startActivity(i);
                }
            }
        });

        settingAdapterInfo = new SettingAdapter();
        settingAdapterInfo.addItem(new SettingItem("내가쓴글",R.drawable.write));
        settingAdapterInfo.addItem(new SettingItem("공감한글",R.drawable.w_heart));
        settingAdapterInfo.addItem(new SettingItem("댓글쓴글",R.drawable.comment));

       gridViewInfo.setAdapter(settingAdapterInfo);

       gridViewInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @SuppressLint("RestrictedApi")
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               if(position == 0)
               {
                   Intent i = new Intent(getActivity(), MyWriteActivity.class);
                   startActivity(i);
               }
               else if(position==1){
                   Intent i = new Intent(getActivity(), MyLikeActivity.class);
                   startActivity(i);
               }
               else if(position==2){
                   Intent i = new Intent(getActivity(), MyReplyActivity.class);
                   startActivity(i);
               }
           }
       });

       settingAdapterUser = new SettingAdapter();
        settingAdapterUser.addItem(new SettingItem("로그아웃",R.drawable.logout));
        settingAdapterUser.addItem(new SettingItem("계정삭제",R.drawable.userdelete));

        gridViewUser.setAdapter(settingAdapterUser);

        gridViewUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Toast.makeText(getApplicationContext(), "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
                    signOut();
                }
                else if(position==1){
                    deleteAccountClicked();
                }
            }
        });

        return rootView;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        mAuth.getInstance().signOut();
        Intent i = new Intent(getContext(), MainActivity.class);
        startActivity(i);
    }

    private void deleteAccountClicked() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage("이 계정을 삭제하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("아니오", null)
                .create();

        dialog.show();
    }

    private void deleteAccount() {
        AuthUI.getInstance()
                .delete(getContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            startActivity(i);
                        } else {
                        }
                    }
                });
    }

    class SettingAdapter extends BaseAdapter{
        ArrayList<SettingItem> items = new ArrayList<SettingItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(SettingItem settingItem){
            items.add(settingItem);
        }

        @Override
        public SettingItem getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("RestrictedApi") SettingViewer settingViewer = new SettingViewer(getApplicationContext());
            settingViewer.setItem(items.get(position));
            return settingViewer;
        }
    }

}