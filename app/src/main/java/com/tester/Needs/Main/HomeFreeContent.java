package com.tester.Needs.Main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tester.Needs.Common.ReplyList;
import com.tester.Needs.Common.ReplyListAdapter;
import com.tester.Needs.R;
import com.tester.Needs.Service.MyService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.tester.Needs.Main.MainActivity.id_nickName;
import static com.tester.Needs.Main.MainActivity.id_uid;
import static com.tester.Needs.Main.MainActivity.id_value;
import static com.tester.Needs.Main.SubActivity.fragmentNumber;
import static com.tester.Needs.Main.SubActivity.point;
import static com.tester.Needs.Main.SubActivity.pointLimit;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeFreeContent extends AppCompatActivity {
    TextView content_title;
    TextView content_content;
    TextView content_good;
    TextView content_Id;
    TextView content_day;
    TextView content_visitnum;

    ImageView content_heart;

    String title;
    String content;
    String day;
    String conId;
    String goodNum;
    String visitNum;
    String documentName;
    String r_documentName;

    ListView list_reply;
    ReplyListAdapter replyListAdapter;
    ArrayList<ReplyList> list_replyArrayList = null;

    EditText edit_reply;
    Button btn_reply;

    Boolean presentBoolean = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();//firestore객체

    Instant nowUtc = Instant.now();
    ZoneId asiaSeoul = ZoneId.of("Asia/Seoul");
    ZonedDateTime nowAsiaSeoul = ZonedDateTime.ofInstant(nowUtc, asiaSeoul);

    String year = String.valueOf(nowAsiaSeoul.getYear());
    String month = String.valueOf(nowAsiaSeoul.getMonthValue());
    String day1 = String.valueOf(nowAsiaSeoul.getDayOfMonth());
    String hour = String.valueOf(nowAsiaSeoul.getHour());
    String minute = String.valueOf(nowAsiaSeoul.getMinute());

    String second = String.valueOf(nowAsiaSeoul.getSecond());

    String fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute+":"+second;


    private ProgressDialog mProgressDialog;
    private BackgroundThread mBackThread;

    String r_writer;
    String r_docName;

    int num = 0;
    int num2 = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_free_content);
        if(month.length() == 1){
            month = "0" + month;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute+":"+second;
        }
        if(day1.length() ==1){
            day1 = "0" + day1;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute+":"+second;
        }


        if (hour.length() == 1) {
            hour = "0" + hour;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute+":"+second;
        }

        if (minute.length() == 1) {
            minute = "0" + minute;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute+":"+second;
        }

        if (second.length() == 1) {
            second = "0" + second;
            fullDay = year + "/" + month + "/" + day1 + " " + hour + ":" + minute+":"+second;
        }

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        day = intent.getStringExtra("day");
        conId = intent.getStringExtra("id");
        goodNum = intent.getStringExtra("good");
        num = Integer.parseInt(goodNum);
        visitNum = intent.getStringExtra("visitnum");
        num2 = Integer.parseInt(visitNum);
        documentName = intent.getStringExtra("documentName");


        content_title = findViewById(R.id.content_free_title);
        content_content = findViewById(R.id.content_free_content);
        content_day = findViewById(R.id.content_free_day);
        content_good = findViewById(R.id.content_free_good);
        content_Id = findViewById(R.id.content_free_id);
        content_visitnum = findViewById(R.id.content_free_visitnum);
        content_heart = findViewById(R.id.content_free_heart);

        content_title.setText(title);
        content_content.setText(content);
        content_good.setText(goodNum);
        content_Id.setText(conId);
        content_day.setText(day);
        content_visitnum.setText(visitNum);


        list_reply = (ListView) findViewById(R.id.list_free_reply);
        list_replyArrayList = new ArrayList<ReplyList>();


        //좋아요 버튼을 누르는것에 대한 data를 boolean을 이용해서 세팅해준다.
        final DocumentReference docRef = db.collection("freeData").document(documentName)
                .collection("like").document(id_value + "like");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        presentBoolean = true;
                        content_heart.setImageResource(R.raw.heart);
                    } else {
                        presentBoolean = false;
                        content_heart.setImageResource(R.raw.bin_heart);
                    }
                } else {
                }
            }
        });

        if (presentBoolean) {
            content_heart.setImageResource(R.raw.heart);
            Log.d("하트의색깔", "빨간색");
        } else if (!presentBoolean) {
            content_heart.setImageResource(R.raw.bin_heart);
            Log.d("하트의색깔", "빈색");
        }

        content_heart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                CollectionReference goodBoolean = db.collection("freeData").
                        document(documentName).collection("like");
                Map<String, Object> user = new HashMap<>();

                if (presentBoolean == true) {
                    int numCompare = Integer.parseInt(pointLimit);
                    if (numCompare > 0) {
                        int number = Integer.parseInt(pointLimit);
                        number = number + 1;
                        pointLimit = Integer.toString(number);

                        int number2 = Integer.parseInt(point);
                        number2 = number2 - 1;
                        point = Integer.toString(number2);

                        db.collection("user").document(id_uid).collection("pointDay")
                                .document(id_value + "pointDay")
                                .update(
                                        "pointLimit", pointLimit
                                );
                        db.collection("user").document(id_uid)
                                .update(
                                        "id_point", point
                                );

                        Map<String, Object> member = new HashMap<>();
                        member.put("day", fullDay);
                        member.put("point", "-1");
                        member.put("type", "취소");

                        db.collection("user").document(id_uid).collection("pointHistory")
                                .add(member)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                    }
                                });
                    }

                    content_heart.setImageResource(R.raw.bin_heart);
                    num = Integer.parseInt(goodNum);
                    num = num - 1;
                    goodNum = Integer.toString(num);
                    content_good.setText(goodNum);
                    presentBoolean = false;
                    DocumentReference documentReference = db.collection("freeData")
                            .document(documentName);

                    documentReference
                            .update("good_num", num)
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
                    //////////////////////////////////data update//////////////////////////////////////////

                    db.collection("user").document(id_uid).collection("like")
                            .document(documentName)
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                    db.collection("freeData").document(documentName)
                            .collection("like").document(id_value + "like")
                            .delete()
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
                    ///////////////////////////////////////data delete by user/////////////////////////////////////
                } else {
                    int numCompare = Integer.parseInt(pointLimit);
                    if (numCompare > 0) {
                        int number = Integer.parseInt(pointLimit);
                        number = number - 1;
                        pointLimit = Integer.toString(number);

                        int number2 = Integer.parseInt(point);
                        number2 = number2 + 1;
                        point = Integer.toString(number2);

                        db.collection("user").document(id_uid).collection("pointDay")
                                .document(id_value + "pointDay")
                                .update(
                                        "pointLimit", pointLimit
                                );
                        db.collection("user").document(id_uid)
                                .update(
                                        "id_point", point
                                );

                        Map<String, Object> member = new HashMap<>();
                        member.put("day", fullDay);
                        member.put("point", "+1");
                        member.put("type", "획득");

                        db.collection("user").document(id_uid).collection("pointHistory")
                                .add(member)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                    }
                                });
                    }


                    user.put("goodBoolean", true);
                    content_heart.setImageResource(R.raw.heart);
                    num = Integer.parseInt(goodNum);
                    num = num + 1;
                    goodNum = Integer.toString(num);
                    content_good.setText(goodNum);
                    presentBoolean = true;
                    DocumentReference documentReference = db.collection("freeData")
                            .document(documentName);

                    documentReference
                            .update("good_num", num)
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

                    goodBoolean.document(id_value + "like").set(user);

                    db.collection("freeData").document(documentName)
                            .collection("like").document(id_value + "like")
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
                    ///////////////////////goodBoolean 값 세팅///////////////////////////

                    CollectionReference userInfo = db.collection("user");
                    Map<String, Object> toUser = new HashMap<>();
                    toUser.put("title", title);
                    toUser.put("content", content);
                    toUser.put("id", conId);
                    toUser.put("day", day);
                    toUser.put("visitnum", visitNum);
                    toUser.put("good", goodNum);
                    toUser.put("documentName", documentName);

                    userInfo.document(id_uid).collection("like").document(documentName)
                            .set(toUser);

                    db.collection("user").document(id_uid).collection("like")
                            .document(documentName)
                            .set(toUser)
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
                    ///////////////user로 데이터 쏴주기/////////////////////////////////////////////


                }

            }

        });


        final DocumentReference documentReference = db.collection("freeData")
                .document(documentName);

        documentReference
                .update("visit_num", num2)
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


        db.collection("freeData").document(documentName)
                .collection("reply").orderBy("timeReply", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list_replyArrayList.add(new ReplyList(document.getData().get("contentReply").toString(),
                                        document.getData().get("writerReply").toString(), document.getData().get("timeReply").toString()
                                        , document.getData().get("data_doc").toString(), document.getData().get("reply_doc").toString()));
                            }
                            replyListAdapter = new ReplyListAdapter(HomeFreeContent.this, list_replyArrayList);
                            list_reply.setAdapter(replyListAdapter);
                            /*
                            list_reply.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent2 = new Intent(BoardContent.this,BoardContent.class);

                                    String comment = list_replyArrayList.get(position).getComment_reply();
                                    String writer = list_replyArrayList.get(position).getWriter_reply();

                                    intent2.putExtra("r_content",comment);
                                    intent2.putExtra("r_writer",writer);

                                    startActivity(intent2);
                                }
                            });*/
                        } else {
                            Log.d("태그", "Error getting documents: ", task.getException());
                        }
                    }
                });

        btn_reply = findViewById(R.id.btn_free_reply);
        edit_reply = findViewById(R.id.edit_free_reply);

        /*
        수정하기 버튼도 만들기 update 기능 만들기
        */


        btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeFreeContent.this);

                builder.setTitle("댓글").setMessage("댓글을 작성하시겠습니까?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String comment_reply = edit_reply.getText().toString().trim();
                        Map<String, Object> user = new HashMap<>();
                        user.put("contentReply", comment_reply);
                        user.put("writerReply", id_nickName);
                        user.put("timeReply", fullDay);
                        user.put("data_doc", documentName);

                        //try {
                        db.collection("freeData").document(documentName).collection("reply")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        r_documentName = documentReference.getId();
                                        execute();
                                    }
                                });
                        mProgressDialog = ProgressDialog.show(HomeFreeContent.this, "Loading"
                                , "댓글작성중입니다..");
                        mBackThread = new BackgroundThread();
                        mBackThread.setRunning(true);
                        mBackThread.start();
                            /*
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/

                        ////////////////////////////////////////////////////////////////////////////////////
                        /////////////////////////////user로 전송////////////////////////////////////////////////
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

        list_reply.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                r_writer = list_replyArrayList.get(position).getWriter_reply();
                r_docName = list_replyArrayList.get(position).getR_doc_reply();

                AlertDialog.Builder builder = new AlertDialog.Builder(HomeFreeContent.this);

                builder.setTitle("댓글 삭제").setMessage("삭제하시겠습니까?");

                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (r_writer.equals(id_nickName)) {
                            db.collection("freeData").document(documentName)
                                    .collection("reply").document(r_docName)
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(HomeFreeContent.this, HomeFreeContent.class);
                                            HomeFreeContent.this.finish();
                                            intent.putExtra("title", title);
                                            intent.putExtra("content", content);
                                            intent.putExtra("day", day);
                                            intent.putExtra("id", conId);
                                            intent.putExtra("visitnum", visitNum);
                                            intent.putExtra("good", goodNum);
                                            intent.putExtra("documentName", documentName);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                            /////////////////////////////////////////////////////////////////////////////////////////////
                            db.collection("user").document(id_uid).collection("reply").document(conId + title + content)
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                            ///////////////////////////////////user쪽에서 reply삭제///////////////////////////////
                        } else {
                            Toast.makeText(getApplicationContext(), "권한이 없습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
    }


    public void OnClickHandler(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("게시물 삭제").setMessage("삭제하시겠습니까?");

        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (id_nickName.equals(conId)) {
                    Log.d("성공 id값", id_value);
                    Log.d("성공 conId값", conId);
                    db.collection("freeData").document(documentName)
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    fragmentNumber = 1;
                                    Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(HomeFreeContent.this, SubActivity.class);
                                    startActivity(intent);
                                }
                            })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                    /////////////////////////////////////////////////////////////////////////////////////////////
                    db.collection("user").document(id_uid).collection("reply").document(documentName)
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                    ///////////////////////////////////user쪽에서 reply삭제///////////////////////////////
                } else {
                    Log.d("실패 id값", id_value);
                    Log.d("실패 conId값", conId);
                    Toast.makeText(getApplicationContext(), "권한이 없습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void execute() {
        CollectionReference userInfo = db.collection("user");
        Map<String, Object> toUser = new HashMap<>();
        toUser.put("title", title);
        toUser.put("content", content);
        toUser.put("id", conId);
        toUser.put("day", day);
        toUser.put("visitnum", visitNum);
        toUser.put("good", goodNum);
        toUser.put("documentName", documentName);

        userInfo.document(id_uid).collection("reply").document(documentName)
                .set(toUser);

        db.collection("user").document(id_uid).collection("reply")
                .document(documentName)
                .set(toUser)
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

        db.collection("freeData").document(documentName).collection("reply")
                .document(r_documentName)
                .update(
                        "reply_doc", r_documentName
                );
        db.collection("user").document(id_uid)
                .collection("reply").document(documentName)
                .update(
                        "reply_doc", r_documentName
                );
    }

    public void onBackPressed() {
        HomeFreeContent.this.finish();
        Intent intent = new Intent(HomeFreeContent.this, SubActivity.class);
        startActivity(intent);
    }

    public class BackgroundThread extends Thread {
        volatile boolean running = false;
        int cnt;

        void setRunning(boolean b) {
            running = b;
            cnt = 7;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    sleep(150);
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
            Toast.makeText(getApplicationContext(), "댓글이 등록되었습니다", Toast.LENGTH_SHORT).show();
            HomeFreeContent.this.finish();
            Intent intent2 = new Intent(HomeFreeContent.this, HomeFreeContent.class);
            intent2.putExtra("title", title);
            intent2.putExtra("content", content);
            intent2.putExtra("day", day);
            intent2.putExtra("id", conId);
            intent2.putExtra("visitnum", visitNum);
            intent2.putExtra("good", goodNum);
            intent2.putExtra("documentName", documentName);
            startActivity(intent2);
        }

    };
}
