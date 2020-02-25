package com.tester.Needs.Main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tester.Needs.Common.FavoritesList;
import com.tester.Needs.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.tester.Needs.Main.MainActivity.id_uid;
import static com.tester.Needs.Main.MainActivity.id_value;
import static com.tester.Needs.Main.SubActivity.address;
import static com.tester.Needs.Main.SubActivity.fragmentNumber;

public class HomeFragment extends Fragment {
    ListView home_listView;
    Favorites_Adapter favorites_adapter;
    ArrayList<FavoritesList> list_itemArrayList = null;

    ListView homefree_listView;
    Favorites_Adapter favorites_adapter1;
    ArrayList<FavoritesList> list_itemArrayList1 = null;

    FirebaseFirestore db = FirebaseFirestore.getInstance();//firestore객체

    ImageView btn_position;
    TextView text_position;

    int home = 1;
    int free= 1;

    static String positionName = null;
    static boolean text_boolean = false;
    private View v;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);
        //MultiDex.install(this);
        fragmentNumber = 0;

        home_listView = (ListView) v.findViewById(R.id.home_hot);

        list_itemArrayList = new ArrayList<FavoritesList>();
        text_position = v.findViewById(R.id.text_position);
        btn_position = v.findViewById(R.id.btn_position);

        String path = db.collection("user").document(id_uid).collection("write").getPath();
        Log.d("path이름",path);

        db.collection("user").document(id_uid).collection("myRegion").document(id_value+"myRegion")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    try {
                        if (task.getResult().get("positionName").toString() != null && task.getResult().get("positionName").toString() != "") {
                            positionName = task.getResult().get("positionName").toString();
                            Log.d("포지션 네임 first", positionName);
                            address = positionName;

                            text_position.setText(positionName);
                            db.collection("data").document("allData").collection(address).orderBy("visit_num", Query.Direction.DESCENDING).limit(5)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    String number = Integer.toString(home);
                                                    int num2 = Integer.parseInt(document.getData().get("good_num").toString());
                                                    String stringNum = Integer.toString(num2);
                                                    int count = stringNum.length();

                                                    String goodNum = document.getData().get("title").toString() + "     [" + num2 + "]";
                                                    int length = goodNum.length();
                                                    int start = 0;
                                                    if (count == 1) start = length - 3;
                                                    else if (count == 2) start = length - 4;
                                                    else if (count == 3) start = length - 5;
                                                    else if (count == 4) start = length - 6;

                                                    SpannableStringBuilder builder = new SpannableStringBuilder(goodNum);
                                                    builder.setSpan(new StyleSpan(Typeface.BOLD), start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    builder.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")), start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    Log.d("태그", document.getId() + " => " + document.getData().get("title")
                                                    );
                                                    list_itemArrayList.add(new FavoritesList(number, document.getData().get("title").toString(),
                                                            document.getData().get("content").toString(), document.getData().get("id_nickName").toString(),
                                                            document.getData().get("day").toString(), document.getData().get("visit_num").toString(),
                                                            document.getData().get("good_num").toString(), document.getData().get("document_name").toString()
                                                            , builder));
                                                    home = Integer.parseInt(number);
                                                    home++;
                                                }
                                                if(home==1) {
                                                    v.findViewById(R.id.regionText_visible).setVisibility(View.VISIBLE);
                                                    v.findViewById(R.id.home_hot).setVisibility(View.GONE);
                                                }
                                                favorites_adapter = new Favorites_Adapter(getActivity(), list_itemArrayList);
                                                home_listView.setAdapter(favorites_adapter);
                                                favorites_adapter.notifyDataSetChanged();
                                                home_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        Intent intent = new Intent(getActivity(), HomeContent.class);

                                                        String title = list_itemArrayList.get(position).getBtn_title();
                                                        String content = list_itemArrayList.get(position).getContent();
                                                        String day = list_itemArrayList.get(position).getBtn_date();
                                                        String conId = list_itemArrayList.get(position).getBtn_writer();
                                                        String goodNum = list_itemArrayList.get(position).getContent_good();
                                                        String visitString = list_itemArrayList.get(position).getBtn_visitnum();
                                                        String documentName = list_itemArrayList.get(position).getDocument_name();

                                                        int visitInt = Integer.parseInt(visitString);
                                                        visitInt = visitInt + 1;
                                                        visitString = Integer.toString(visitInt);

                                                        intent.putExtra("title", title);
                                                        intent.putExtra("content", content);
                                                        intent.putExtra("day", day);
                                                        intent.putExtra("id", conId);
                                                        intent.putExtra("good", goodNum);
                                                        intent.putExtra("visitnum", visitString);
                                                        intent.putExtra("documentName", documentName);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                Log.d("태그", "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        }
                    }catch (Exception e)
                    {
                            v.findViewById(R.id.region_visible).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.home_hot).setVisibility(View.GONE);
                            Log.d("포지션 네임 first", "널일 때에");
                    }
                }
            }
        });
        /*
        db.collection("user").document(id_uid).collection("myRegion")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("positionName").toString() != null && document.getData().get("positionName").toString() != "") {

                                    v.findViewById(R.id.region_visible).setVisibility(View.GONE);
                                    v.findViewById(R.id.home_hot).setVisibility(View.VISIBLE);
                                    positionName = document.getData().get("positionName").toString();
                                    Log.d("포지션 네임 first", positionName);
                                    address = positionName;

                                    text_position.setText(positionName);
                                    db.collection("data").document("allData").collection(address).orderBy("visit_num", Query.Direction.DESCENDING).limit(5)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        int i = 1;
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            String number = Integer.toString(i);
                                                            int num2 = Integer.parseInt(document.getData().get("good_num").toString());
                                                            String stringNum = Integer.toString(num2);
                                                            int count = stringNum.length();

                                                            String goodNum = document.getData().get("title").toString()+"     ["+num2+"]";
                                                            int length =  goodNum.length();
                                                            int start = 0;
                                                            if(count==1) start = length-3;
                                                            else if(count==2) start = length-4;
                                                            else if(count==3) start = length-5;
                                                            else if(count==4) start = length-6;

                                                            SpannableStringBuilder builder = new SpannableStringBuilder(goodNum);
                                                            builder.setSpan(new StyleSpan(Typeface.BOLD),start,length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                            builder.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")),start,length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                            Log.d("태그", document.getId() + " => " + document.getData().get("title")
                                                            );
                                                            list_itemArrayList.add(new FavoritesList(number, document.getData().get("title").toString(),
                                                                    document.getData().get("content").toString(), document.getData().get("write").toString(),
                                                                    document.getData().get("day").toString(), document.getData().get("visit_num").toString(),
                                                                    document.getData().get("good_num").toString(), document.getData().get("document_name").toString()
                                                            ,builder));
                                                            i = Integer.parseInt(number);
                                                            i++;
                                                        }
                                                        favorites_adapter = new Favorites_Adapter(getActivity(), list_itemArrayList);
                                                        home_listView.setAdapter(favorites_adapter);
                                                        favorites_adapter.notifyDataSetChanged();
                                                        home_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                Intent intent = new Intent(getActivity(), HomeContent.class);

                                                                String title = list_itemArrayList.get(position).getBtn_title();
                                                                String content = list_itemArrayList.get(position).getContent();
                                                                String day = list_itemArrayList.get(position).getBtn_date();
                                                                String conId = list_itemArrayList.get(position).getBtn_writer();
                                                                String goodNum = list_itemArrayList.get(position).getContent_good();
                                                                String visitString = list_itemArrayList.get(position).getBtn_visitnum();
                                                                String documentName = list_itemArrayList.get(position).getDocument_name();

                                                                int visitInt = Integer.parseInt(visitString);
                                                                visitInt = visitInt + 1;
                                                                visitString = Integer.toString(visitInt);

                                                                intent.putExtra("title", title);
                                                                intent.putExtra("content", content);
                                                                intent.putExtra("day", day);
                                                                intent.putExtra("id", conId);
                                                                intent.putExtra("good", goodNum);
                                                                intent.putExtra("visitnum", visitString);
                                                                intent.putExtra("documentName", documentName);
                                                                startActivity(intent);
                                                            }
                                                        });
                                                    } else {
                                                        Log.d("태그", "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });

                                }


                                else if (positionName == null || positionName == "") {
                                    positionName = null;
                                    Log.d("포지션 네임 null", "null");
                                    list_itemArrayList.add(new FavoritesList(" ", "상단의 내위치 정보를 등록한 후 목록이 갱신됩니다",
                                            " ", ""," ", " ", " ", " ", new SpannableStringBuilder()));
                                    favorites_adapter = new Favorites_Adapter(getActivity(), list_itemArrayList);
                                    home_listView.setAdapter(favorites_adapter);
                                }
                            }
                        } else {
                        }
                    }
                });*/


        btn_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("지역등록").setMessage("ex)강남구xx동, 띄어쓰기사용x");
                final EditText et = new EditText(getActivity());
                builder.setView(et);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("포지션 네임 등록", "1");
                        if (positionName == null) {
                            Log.d("포지션 네임 등록", "2");
                            positionName = et.getText().toString();
                            text_position.setText(positionName);

                            CollectionReference title_content = db.collection("user").document(id_uid).collection("myRegion");
                            Map<String, Object> user = new HashMap<>();
                            user.put("positionName", positionName);

                            title_content.document(id_value + "myRegion").set(user);

                            db.collection("user").document(id_uid).collection("myRegion").document(id_value + "myRegion")
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

                        } else {
                            Log.d("포지션 네임 등록", "3");
                            positionName = et.getText().toString();
                            if (positionName == null) {
                                positionName = null;
                                db.collection("user").document(id_uid).collection("myRegion")
                                        .document(id_value + "myRegion")
                                        .update(
                                                "positionName", positionName
                                        );
                            } else {
                                db.collection("user").document(id_uid).collection("myRegion")
                                        .document(id_value + "myRegion")
                                        .update(
                                                "positionName", positionName
                                        );
                            }
                        }

                        Intent intent = new Intent(getActivity(), SubActivity.class);
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
        });
        /*
        if(positionName != null)
        {
            Log.d("포지션 네임2번째위치",positionName);
            text_position.setText(positionName);
            db.collection(positionName).orderBy("visit_num", Query.Direction.DESCENDING).limit(5)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int i = 0;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("태그", document.getId() + " => " + document.getData().get("title")
                                    );
                                    list_itemArrayList.add(new FavoritesList(i + ".", document.getData().get("title").toString(),
                                            document.getData().get("content").toString(), document.getData().get("write").toString(),
                                            document.getData().get("day").toString(), document.getData().get("visit_num").toString(),
                                            document.getData().get("good_num").toString()));
                                    i++;
                                }
                                favorites_adapter = new Favorites_Adapter(getActivity(), list_itemArrayList);
                                home_listView.setAdapter(favorites_adapter);
                            } else {
                                Log.d("태그", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else
        {
            Log.d("포지션 네임 null", "null");
            list_itemArrayList.add(new FavoritesList(" ","상단의 내위치 정보를 등록한 후 목록이 갱신됩니다"," "," "," "," "," "));
            favorites_adapter = new Favorites_Adapter(getActivity(), list_itemArrayList);
            home_listView.setAdapter(favorites_adapter);
        }
        */
        homefree_listView = (ListView) v.findViewById(R.id.home_free);

        list_itemArrayList1 = new ArrayList<FavoritesList>();

        db.collection("freeData").orderBy("visit_num", Query.Direction.DESCENDING).limit(5)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String number = Integer.toString(free);
                                int num2 = Integer.parseInt(document.getData().get("good_num").toString());
                                String stringNum = Integer.toString(num2);
                                int count = stringNum.length();

                                String goodNum = document.getData().get("title").toString()+"     ["+num2+"]";
                                int length =  goodNum.length();
                                int start = 0;
                                if(count==1) start = length-3;
                                else if(count==2) start = length-4;
                                else if(count==3) start = length-5;
                                else if(count==4) start = length-6;

                                SpannableStringBuilder builder = new SpannableStringBuilder(goodNum);
                                builder.setSpan(new StyleSpan(Typeface.BOLD),start,length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                builder.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")),start,length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                list_itemArrayList1.add(new FavoritesList(number, document.getData().get("title").toString(),
                                        document.getData().get("content").toString(), document.getData().get("id_nickName").toString(),
                                        document.getData().get("day").toString(), document.getData().get("visit_num").toString(),
                                        document.getData().get("good_num").toString(), document.getData().get("document_name").toString()
                                ,builder));
                                free = Integer.parseInt(number);
                                free++;
                            }
                            if(free==1) {
                                v.findViewById(R.id.homeFreeText_visible).setVisibility(View.VISIBLE);
                                v.findViewById(R.id.home_free).setVisibility(View.GONE);
                            }
                            favorites_adapter1 = new Favorites_Adapter(getActivity(), list_itemArrayList1);
                            homefree_listView.setAdapter(favorites_adapter1);
                            favorites_adapter1.notifyDataSetChanged();


                            homefree_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(getActivity(), HomeFreeContent.class);

                                    String title = list_itemArrayList1.get(position).getBtn_title();
                                    String content = list_itemArrayList1.get(position).getContent();
                                    String day = list_itemArrayList1.get(position).getBtn_date();
                                    String conId = list_itemArrayList1.get(position).getBtn_writer();
                                    String goodNum = list_itemArrayList1.get(position).getContent_good();
                                    String visitString = list_itemArrayList1.get(position).getBtn_visitnum();
                                    String documentName = list_itemArrayList1.get(position).getDocument_name();

                                    int visitInt = Integer.parseInt(visitString);
                                    visitInt = visitInt + 1;
                                    visitString = Integer.toString(visitInt);

                                    intent.putExtra("title", title);
                                    intent.putExtra("content", content);
                                    intent.putExtra("day", day);
                                    intent.putExtra("id", conId);
                                    intent.putExtra("good", goodNum);
                                    intent.putExtra("visitnum", visitString);
                                    intent.putExtra("documentName", documentName);
                                    startActivity(intent);
                                }
                            });
                       

                        } else {
                            Log.d("태그", "Error getting documents: ", task.getException());
                        }

                    }
                });

        return v;
    }
}

