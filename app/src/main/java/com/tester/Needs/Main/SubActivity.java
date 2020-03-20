package com.tester.Needs.Main;

//로그인 되고 메인화면//

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.tester.Needs.Common.FavoritesList;
import com.tester.Needs.Service.MyService;
import com.tester.Needs.Common.SubExpAdapter;
import com.tester.Needs.Common.SubList;
import com.tester.Needs.R;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tester.Needs.Main.MainActivity.id_name;
import static com.tester.Needs.Main.MainActivity.id_nickName;
import static com.tester.Needs.Main.MainActivity.id_uid;
import static com.tester.Needs.Main.MainActivity.id_value;


@RequiresApi(api = Build.VERSION_CODES.O)
public class SubActivity extends AppCompatActivity {

    //ㄴㅅpublic static Class getActivity = SubActivity.class;

    private TextView sub_id;
    private DrawerLayout drawerLayout;
    private View drawerView;

    final List<String> firstGroups = new ArrayList<>();
    final HashMap<String, List<SubList>> firstItemGroup = new HashMap<>();
    List<SubList> gangone = new ArrayList<>();
    List<SubList> gangtwo = new ArrayList<>();
    List<SubList> gangthree = new ArrayList<>();
    List<SubList> gangfour = new ArrayList<>();

    final List<String> goodGroups = new ArrayList<>();
    final HashMap<String, List<SubList>> goodItemGroup = new HashMap<>();
    List<SubList> goodone = new ArrayList<>();

    String strict; //parent value

    static String address;//living value?!
    static String sub_photoUrl;

    String first;//parent_value
    String second;//child_value
    String third;//added value to goot_list
    String fourth;//deleted value by good_list

    SubExpAdapter subExpAdapter1 = new SubExpAdapter(SubActivity.this, goodGroups, goodItemGroup);

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView mTextMessage;


    ListView home_listView;
    Favorites_Adapter favorites_adapter;
    ArrayList<FavoritesList> list_itemArrayList = null;

    ListView homefree_listView;
    Favorites_Adapter favorites_adapter1;
    ArrayList<FavoritesList> list_itemArrayList1 = null;

    Instant nowUtc = Instant.now();
    ZoneId asiaSeoul = ZoneId.of("Asia/Seoul");
    ZonedDateTime nowAsiaSeoul = ZonedDateTime.ofInstant(nowUtc, asiaSeoul);

    String year = String.valueOf(nowAsiaSeoul.getYear());
    String month = String.valueOf(nowAsiaSeoul.getMonthValue());
    String day1 = String.valueOf(nowAsiaSeoul.getDayOfMonth());
    String hour = String.valueOf(nowAsiaSeoul.getHour());
    String minute = String.valueOf(nowAsiaSeoul.getMinute());

    String pointDay = null;

    FragmentTransaction transaction;

    public static int fragmentNumber = 0;

    static String pointLimit = null;
    static String point = null;
    public static int record_count = 0;




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stopService(new Intent(SubActivity.this, MyService.class));

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SubActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);

            }
        });

        if (fragmentNumber == 0) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new HomeFragment()).commitAllowingStateLoss();
        } else if (fragmentNumber == 1) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new FavoritesFragment()).commitAllowingStateLoss();
        }
        /*
        else if (fragmentNumber == 2) {
            stopService(new Intent(SubActivity.this,MyService.class));
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new SettingFragment()).commitAllowingStateLoss();
        }
         */

        setContentView(R.layout.activity_sub);
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        navView.setOnNavigationItemSelectedListener(navListner);

        mAuth = FirebaseAuth.getInstance();

        final ExpandableListView expandableListView = findViewById(R.id.draw_listView);
        final ExpandableListView expandableListView1 = findViewById(R.id.good_listView);
        this.InitializeDrawList();


        final SubExpAdapter subExpAdapter = new SubExpAdapter(SubActivity.this, firstGroups, firstItemGroup);
        expandableListView.setAdapter(subExpAdapter);

        subExpAdapter1.notifyDataSetChanged();

        ImageView btn_notification;
        btn_notification = findViewById(R.id.btn_notification);

        try {
            id_uid = mAuth.getCurrentUser().getUid();
            id_value = mAuth.getCurrentUser().getEmail();
        }catch(Exception e){

        }

        try {
            db.collection("user").document(id_uid).collection("action").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            record_count = 0;
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                record_count++;
                            }
                        }
                    });
        } catch (Exception e) {
            record_count = 0;
        }

        btn_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent record_intent = new Intent(SubActivity.this, RecordActivity.class);
                startActivity(record_intent);
                // SubActivity.this.finish();
            }
        });

        db.collection("user").document(id_uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    id_name = document.getData().get("id_name").toString();
                    id_nickName = document.getData().get("id_nickName").toString();
                    try {
                        sub_photoUrl = document.getData().get("photoUrl").toString();
                    } catch (Exception e) {

                    }
                }
            }
        });

        ///////////////////포인트 가져오기////////////////////////////////////////////////
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawer);

        ImageView btn = (ImageView) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        db.collection("user").document(id_uid).collection("favorites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                goodone.add(new SubList(document.getData().get("region").toString()));
                            }
                            goodItemGroup.put(goodGroups.get(0), goodone);
                            expandableListView1.setAdapter(subExpAdapter1);
                            subExpAdapter1.notifyDataSetChanged();
                        } else {
                            Log.d("태그", "Error getting documents: ", task.getException());
                        }
                    }
                });


        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Snackbar.make(v, firstGroups.get(groupPosition) + "지역으로 이동합니다.", Snackbar.LENGTH_SHORT).show();
                strict = firstGroups.get(groupPosition);
                Log.d("strict", strict);
                return false;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String childname = firstItemGroup.get(firstGroups.get(groupPosition)).get(childPosition).getCountry();
                Log.d("아이들 지역", childname);
                address = strict +" " +childname;
                Snackbar.make(v, address + "게시판으로 이동합니다.", Snackbar.LENGTH_SHORT).show();

                finish();
                overridePendingTransition(R.anim.rightin_activity, R.anim.not_move_activity);
                startActivity(new Intent(SubActivity.this, BoardActivity.class));
                return false;
            }
        });
        expandableListView1.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });
        expandableListView1.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                fourth = goodone.get(childPosition).getCountry();
                Snackbar.make(v, fourth + "게시판으로 이동합니다", Snackbar.LENGTH_SHORT).show();

                address = fourth;

                finish();
                overridePendingTransition(R.anim.rightin_activity, R.anim.not_move_activity);
                startActivity(new Intent(SubActivity.this, BoardActivity.class));

                return false;
            }
        });

        // Append data to 'user - favorites' collection
        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                int itemType = ExpandableListView.getPackedPositionType(id);
                boolean retVal = true;

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int childPosition = ExpandableListView.getPackedPositionChild(id);
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);

                    AlertDialog.Builder builder = new AlertDialog.Builder(SubActivity.this);
                    first = firstGroups.get(groupPosition);
                    String childname = firstItemGroup.get(firstGroups.get(groupPosition)).get(childPosition).getCountry();
                    Log.d("아이들 지역", childname);

                    second = first + " "+ childname;
                    third = second;

                    builder.setTitle("즐겨찾기추가").setMessage("즐겨찾기 추가하시겠습니까?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            for(int i=0; i<goodone.size();i++)
                            {
                                if(  goodone.get(i).getCountry().equals(second))
                                {
                                    Snackbar.make(view, "즐겨찾기에 이미 존재합니다", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                            }


                            CollectionReference title_content = db.collection("user").document(id_uid).collection("favorites");
                            Map<String, Object> user = new HashMap<>();
                            user.put("region", second);

                            title_content.document(id_value + third).set(user);

                            db.collection("user").document(id_uid).collection("favorites")
                                    .document(id_value + third)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Snackbar.make(view, "즐겨찾기에 추가되었습니다", Snackbar.LENGTH_SHORT).show();
                                            goodone.add(new SubList(second));
                                            goodItemGroup.put(goodGroups.get(0), goodone);
                                            expandableListView1.setAdapter(subExpAdapter1);
                                            subExpAdapter1.notifyDataSetChanged();
                                            // refresh activity
                                            //Intent intent = getIntent();
                                            //finish();
                                            //startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    return retVal;

                } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    return retVal;

                } else {
                    return false;
                }
            }
        });

        // Delete data in 'user - favorites' collection
        expandableListView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                int itemType = ExpandableListView.getPackedPositionType(id);
                boolean retVal = true;
                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    final int childPosition = ExpandableListView.getPackedPositionChild(id);
                    final int groupPosition = ExpandableListView.getPackedPositionGroup(id);

                    AlertDialog.Builder builder = new AlertDialog.Builder(SubActivity.this);

                    fourth = goodone.get(childPosition).getCountry();

                    builder.setTitle("즐겨찾기목록 삭제").setMessage("즐겨찾기목록에서 삭제하시겠습니까?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Snackbar.make(view, fourth + "가 즐겨찾기에서 삭제되었습니다.", Snackbar.LENGTH_SHORT).show();
                            db.collection("user").document(id_uid).collection("favorites")
                                    .document(id_value + fourth)
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            goodItemGroup.remove(subExpAdapter1.getChild(0,childPosition));
                                            goodone.remove(childPosition);
                                            expandableListView1.setAdapter(subExpAdapter1);
                                            subExpAdapter1.notifyDataSetChanged();
                                            //refresh activity
                                            //Intent intent = getIntent();
                                            //finish();
                                            //startActivity(intent);
                                        }
                                    })

                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    return retVal;

                } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    return retVal;
                } else {
                    return false;
                }
            }
        });
        ImageView draw_cancel = (ImageView) findViewById(R.id.draw_cancel);
        draw_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });
        drawerLayout.setDrawerListener(listener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(SubActivity.this, MainActivity.class));
            finish();
        }
    }

    public void InitializeDrawList() {
        firstGroups.add("강남구");
        firstGroups.add("강동구");
        firstGroups.add("강북구");
        firstGroups.add("강서구");

        gangone.add(new SubList("논현동"));
        gangone.add(new SubList("신사동"));
        gangone.add(new SubList("청담동"));

        gangtwo.add(new SubList("둔촌동"));
        gangtwo.add(new SubList("상일동"));
        gangtwo.add(new SubList("천호동"));

        gangthree.add(new SubList("미아동"));
        gangthree.add(new SubList("수유동"));
        gangthree.add(new SubList("우이동"));

        gangfour.add(new SubList("가양동"));
        gangfour.add(new SubList("등촌동"));
        gangfour.add(new SubList("방화동"));

        firstItemGroup.put(firstGroups.get(0), gangone);
        firstItemGroup.put(firstGroups.get(1), gangtwo);
        firstItemGroup.put(firstGroups.get(2), gangthree);
        firstItemGroup.put(firstGroups.get(3), gangfour);

        goodGroups.add("즐겨찾기 목록");
    }

    // BottomNavigationView
    private BottomNavigationView.OnNavigationItemSelectedListener navListner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()) {
                        case R.id.navigation_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.navigation_dashboard:
                            selectedFragment = new FavoritesFragment();
                            break;
                        case R.id.navigation_notifications:
                            selectedFragment = new SettingFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };


    private void revokeAccess() {
        mAuth.getCurrentUser().delete();
    }

    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(SubActivity.this, MyService.class);
        intent.setAction("startForeground");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }


/*
    @Override
    protected void onUserLeaveHint() {
        Intent intent = new Intent(SubActivity.this, MyService.class);
        intent.setAction("startForeground");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            startForegroundService(intent);
        }else{
            startService(intent);
        }
    }

 */
}
