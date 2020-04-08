package com.tester.Needs.Main;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tester.Needs.R;
import com.tester.Needs.Service.MyService;
import com.tester.Needs.Setting.MyLikeActivity;
import com.tester.Needs.Setting.MyReplyActivity;
import com.tester.Needs.Setting.MyWriteActivity;
import com.tester.Needs.Setting.PointConversionActivity;
import com.tester.Needs.Setting.PointHistoryActivity;
import com.tester.Needs.Setting.SettingItem;
import com.tester.Needs.Setting.SettingViewer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.tester.Needs.Main.MainActivity.id_name;
import static com.tester.Needs.Main.MainActivity.id_nickName;
import static com.tester.Needs.Main.MainActivity.id_uid;
import static com.tester.Needs.Main.MainActivity.id_value;
import static com.tester.Needs.Main.SubActivity.address;
import static com.tester.Needs.Main.MainActivity.photoUrl;
import static com.tester.Needs.Main.SubActivity.login_state;
import static com.tester.Needs.Main.SubActivity.sub_photoUrl;
import static com.tester.Needs.Main.SubActivity.fragmentNumber;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class SettingFragment extends Fragment {

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

    boolean boolean_nickname = false;
    boolean boolean_second = false;

    private ProgressDialog mProgressDialog;

    GoogleSignInClient googleSignInClient;

    String delete_value[];
    int delete_count = 0;

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
                    Glide.with(this).load(sub_photoUrl).into(iv_profile);
                    GoogleSignInOptions gso =
                            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(getString(R.string.default_web_client_id))
                                    .requestEmail()
                                    .build();
                    googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                } else if (FacebookAuthProvider.PROVIDER_ID.equals(providerId)) {
                    providerList.append("Facebook");
                    Glide.with(this).load(sub_photoUrl).into(iv_profile);
                } else if (TwitterAuthProvider.PROVIDER_ID.equals(providerId)) {
                    providerList.append("Twitter");
                } else if (EmailAuthProvider.PROVIDER_ID.equals(providerId)) {
                   // Glide.with(this).load(sub_photoUrl).into(iv_profile);
                    providerList.append("Email");
                } else {
                    providerList.append(providerId);
                }
            }
        }
        userenabled.setText(providerList);

        settingAdapter = new SettingAdapter();
        settingAdapter.addItem(new SettingItem("포인트 내역", R.drawable.icons8_history));
        settingAdapter.addItem(new SettingItem("포인트 전환", R.drawable.icons8_money));

        gridView.setAdapter(settingAdapter);
        gridView.setBackgroundResource(R.drawable.corner_no);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent i = new Intent(getActivity(), PointHistoryActivity.class);
                    startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent(getActivity(), PointConversionActivity.class);
                    startActivity(i);
                }
            }
        });

        settingAdapterInfo = new SettingAdapter();
        settingAdapterInfo.addItem(new SettingItem("내가 쓴 글", R.drawable.icons8_edit));
        settingAdapterInfo.addItem(new SettingItem("공감한 글", R.drawable.heart));
        settingAdapterInfo.addItem(new SettingItem("댓글 남긴 글", R.drawable.icons8_comment));

        gridViewInfo.setAdapter(settingAdapterInfo);
        gridViewInfo.setBackgroundResource(R.drawable.corner_no);
        gridViewInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent i = new Intent(getActivity(), MyWriteActivity.class);
                    startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent(getActivity(), MyLikeActivity.class);
                    startActivity(i);
                } else if (position == 2) {
                    Intent i = new Intent(getActivity(), MyReplyActivity.class);
                    startActivity(i);
                }
            }
        });

        settingAdapterUser = new SettingAdapter();
        settingAdapterUser.addItem(new SettingItem("로그아웃", R.drawable.icons8_logout));
        settingAdapterUser.addItem(new SettingItem("계정삭제", R.drawable.icons8_userdelete));
        settingAdapterUser.addItem(new SettingItem("계정설정", R.drawable.icons8_userimg));

        gridViewUser.setAdapter(settingAdapterUser);
        gridViewUser.setBackgroundResource(R.drawable.corner_no);
        gridViewUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(getApplicationContext(), "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
                    id_value = null;
                    signOut();
                } else if (position == 1) {
                    deleteAccountClicked();
                } else if (position == 2) {
                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }






    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getContext(), MainActivity.class);
        address = null;
        login_state = false;
        getActivity().finish();
        startActivity(i);

    }

    private void deleteAccountClicked() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage("이 계정을 삭제하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAccount();
                        login_state = false;
                        address = null;
                    }
                })
                .setNegativeButton("아니오", null)
                .create();

        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void deleteAccount() {
        mProgressDialog = ProgressDialog.show(getActivity(), "Loading"
                , "게정을 삭제하는 중입니다..");

        db.collection("user").document(id_uid).collection("pointDay")
                .document(id_value + "pointDay").delete();

        db.collection("user").document(id_uid).collection("pointHistory").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("user").document(id_uid).collection("pointHistory")
                                        .document(document.getId()).delete();
                            }

                        } else {
                            Log.d("태그", "Error getting documents: ", task.getException());
                        }
                    }
                });
        /*
        db.collection("user").document(id_uid).collection("myRegion").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("user").document(id_uid).collection("myRegion")
                                        .document(document.getId()).delete();
                            }

                        } else {
                            Log.d("태그", "Error getting documents: ", task.getException());
                        }
                    }
                });*/

        db.collection("user").document(id_uid).collection("write").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("user").document(id_uid).collection("write")
                                        .document(document.getId()).delete();
                            }

                        } else {
                            Log.d("태그", "Error getting documents: ", task.getException());
                        }
                    }
                });
        db.collection("user").document(id_uid).collection("action").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("user").document(id_uid).collection("action")
                                        .document(document.getId()).delete();
                            }

                        } else {
                            Log.d("태그", "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("user").document(id_uid).collection("like").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("user").document(id_uid).collection("like")
                                        .document(document.getId()).delete();
                            }

                        } else {
                            Log.d("태그", "Error getting documents: ", task.getException());
                        }
                    }
                });
        db.collection("user").document(id_uid).collection("reply").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("user").document(id_uid).collection("reply")
                                        .document(document.getId()).delete();
                            }

                        } else {
                            Log.d("태그", "Error getting documents: ", task.getException());
                        }
                    }
                });
        db.collection("user").document(id_uid).collection("favorites").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("user").document(id_uid).collection("favorites")
                                        .document(document.getId()).delete();
                            }

                        } else {
                            Log.d("태그", "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("user").document(id_uid).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                AuthUI.getInstance()
                        .delete(getContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    db.collection("user").document(id_uid).delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            });

                                } else {
                                }
                            }
                        });

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

                id_value = null;
                mProgressDialog.dismiss();
                getActivity().finish();

            }
        }, 5000);

    }


    class SettingAdapter extends BaseAdapter {
        ArrayList<SettingItem> items = new ArrayList<SettingItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(SettingItem settingItem) {
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