package com.example.project_basic;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.example.project_basic.SubActivity.address;

public class BoardActivity extends AppCompatActivity {

    ListView listView;
    BoardListAdapter boardListAdapter;
    ArrayList<BoardList> list_itemArrayList = null;

    ImageView btn_write;
    EditText edit_board;

    int board_count;

    static int spinnerNumber = 0;
    static int spinnerCmpNum = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();//firestore객체

    String spinnerValue;
    Spinner spinner;
    TextView spinnerView;

    static String spinnerText="day";
    //static String spinnerContent="날짜순";

    int num = 0;
    static int checkNum = 0;
    String top_spinnerValue;
    Spinner top_spinner;
    TextView top_spinnerView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_board);

        final Intent intent = getIntent();

        spinner = findViewById(R.id.spinner);
        spinnerView = findViewById(R.id.spinner_text);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerValue = parent.getItemAtPosition(position).toString();
                spinnerView.setText(spinnerValue);
                spinnerNumber = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        top_spinner = findViewById(R.id.top_spinner);
        top_spinnerView = findViewById(R.id.top_spinner_text);


        top_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                top_spinnerValue = parent.getItemAtPosition(position).toString();
                if(position == 0)
                {
                    spinnerText = "day";
                    if(spinnerCmpNum ==0)
                    {
                        top_spinnerView.setText("기본");
                    }
                    else if(spinnerCmpNum==1)
                    {
                        top_spinnerView.setText("날짜순");
                    }
                    else if(spinnerCmpNum==2)
                    {
                        top_spinnerView.setText("조회순");
                    }
                }
                else if(position==1)
                {
                    spinnerText = "day";
                    spinnerCmpNum = 1;
                    BoardActivity.this.finish();
                    Intent intent2 = new Intent(BoardActivity.this,BoardActivity.class);
                    startActivity(intent2);
                }
                else if(position==2)
                {
                    spinnerText = "visit_num";
                    spinnerCmpNum = 2;
                    BoardActivity.this.finish();
                    Intent intent = new Intent(BoardActivity.this,BoardActivity.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });





        listView = (ListView)findViewById(R.id._listView);
        list_itemArrayList = new ArrayList<BoardList>();

        db.collection("data").document("allData").collection(address).orderBy(spinnerText,Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            board_count=1;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String number = Integer.toString(board_count);
                                list_itemArrayList.add(new BoardList(number,document.getData().get("title").toString() ,
                                        document.getData().get("content").toString(), document.getData().get("write").toString() ,
                                        document.getData().get("day").toString(),document.getData().get("visit_num").toString(),
                                        document.getData().get("good_num").toString(),document.getData().get("document_name").toString()));
                                board_count = Integer.parseInt(number);
                                board_count++;
                            }
                            spinnerText = "day";
                            spinnerCmpNum = 0;
                            boardListAdapter = new BoardListAdapter(BoardActivity.this,list_itemArrayList);
                            listView.setAdapter(boardListAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent2 = new Intent(BoardActivity.this,BoardContent.class);

                                    String title = list_itemArrayList.get(position).getBtn_title();
                                    String content = list_itemArrayList.get(position).getContent();
                                    String day = list_itemArrayList.get(position).getBtn_date();
                                    String conId = list_itemArrayList.get(position).getBtn_writer();
                                    String goodNum = list_itemArrayList.get(position).getContent_good();
                                    String visitString = list_itemArrayList.get(position).getBtn_visitnum();
                                    String document_name = list_itemArrayList.get(position).getDocument_name();

                                    int visitInt = Integer.parseInt(visitString);
                                    visitInt = visitInt+1;
                                    String visitnum = Integer.toString(visitInt);


                                    intent2.putExtra("title",title);
                                    intent2.putExtra("content",content);
                                    intent2.putExtra("day",day);
                                    intent2.putExtra("id",conId);
                                    intent2.putExtra("good",goodNum);
                                    intent2.putExtra("visitnum",visitnum);
                                    intent2.putExtra("documentName",document_name);
                                    startActivity(intent2);
                                }
                            });
                        } else {
                            Log.d("태그", "Error getting documents: ", task.getException());
                        }
                    }
                });

          /*한가지 값 불러오기(firestore에서)
        DocumentReference docRef = db.collection("data").document("hello_one");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("태그", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("태그", "No such document");
                    }
                } else {
                    Log.d("태그", "get failed with ", task.getException());
                }
            }
        });*/

        btn_write = (ImageView)findViewById(R.id.btn_write);
        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoardActivity.this,BoardWrite.class);
                startActivity(intent);
            }
        });
        Intent intent1 = getIntent();

        edit_board = findViewById(R.id.edit_board);
        edit_board.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();
                if(search.length() > 0) {
                    listView.setFilterText(search);
                }
                else {
                    listView.clearTextFilter();
                }
            }
        });
    }
    public void onBackPressed() {
        BoardActivity.this.finish();
        Intent intent = new Intent(BoardActivity.this,SubActivity.class);
        startActivity(intent);
    }
}





