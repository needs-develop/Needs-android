package com.tester.Needs.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tester.Needs.Common.RecordList;
import com.tester.Needs.Main.HomeContent;
import com.tester.Needs.Main.HomeFreeContent;
import com.tester.Needs.Main.SubActivity;
import com.tester.Needs.R;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.tester.Needs.Main.MainActivity.id_value;
import static com.tester.Needs.Main.SubActivity.record_count;
//import static com.tester.Needs.Main.RecordActivity.recordList;


//import static com.tester.Needs.Main.SubActivity.getActivity;

public class MyService extends Service {

    private Thread mThread;
    private int mCount = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid;
    String value;
    String writer_uid;
    String url ;
    Bitmap urlToBitmap;


    boolean notification = false;
    Timer timer;

    int count ;

    String document_name ;
    String data ;
    String address ;


    public MyService() {
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if("startForeground".equals(intent.getAction())){
            startForegroundService();
        }else if(mThread == null){
            mThread = new Thread("My Thread"){
                @Override
                public void run() {

                }
            };
            mThread.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        if(mThread != null){
            mThread.interrupt();
            mThread = null;
            mCount = 0;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startForegroundService() {
        uid = user.getUid();
        Log.d("서비스 실행","서비스 실행");
        /*
        int badgeCount = 1;
        Intent intent_badge = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent_badge.putExtra("badge_count", badgeCount);
        //앱의  패키지 명
        intent_badge.putExtra("badge_count_package_name","com.tester.Needs.Main");
        // AndroidManifest.xml에 정의된 메인 activity 명
        intent_badge.putExtra("badge_count_class_name", "com.tester.Needs.Main.SubActivity");
        sendBroadcast(intent_badge);
        */
       Drawable drawable = getDrawable(R.drawable.appicon);
       BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
       Bitmap bitmap = bitmapDrawable.getBitmap();


            timer = new Timer(true);
        final Handler handler = new Handler();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override

                    public void run() {
                        try {
                            db.collection("user").document(uid).collection("action")
                                    .whereEqualTo("data", "data").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    count = 0;

                                    document_name = null;
                                    data = "NONE";
                                    address = null;
                                    url = "url";
                                    value = null;
                                    try {
                                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                            switch (dc.getType()) {
                                                case ADDED: {
                                                    count++;
                                                    if(count<=record_count) continue;
                                                    else if (count > record_count) {
                                                        value = dc.getDocument().getData().get("writer").toString();
                                                        db.collection("user").whereEqualTo("id_nickName", value)
                                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                        writer_uid = Objects.requireNonNull(document.getData().get("id_uid")).toString();
                                                                        Log.d("writer_uid체크",value+" "+writer_uid);
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        });
                                                        document_name = dc.getDocument().getData().get("document_name").toString();
                                                        data = dc.getDocument().getData().get("value").toString();
                                                        notification = true;
                                                        try {
                                                            address = dc.getDocument().getData().get("address").toString();
                                                        } catch (Exception errorCase) {
                                                        }


                                                        ///////////////////////////////////////////////////////////////////////
                                                        db.collection("user").document(writer_uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                final DocumentSnapshot documentSnapshot = task.getResult();
                                                                try {
                                                                    Thread thread = new Thread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            try{
                                                                                url = documentSnapshot.getData().get("photoUrl").toString();
                                                                                urlToBitmap = GetImageFromURL(url);
                                                                            }catch(Exception e)
                                                                            {
                                                                                url = "url";
                                                                            }
                                                                        }
                                                                    });
                                                                    thread.start();
                                                                }catch (Exception e)
                                                                {
                                                                    url = "url";
                                                                }
                                                            }
                                                        });

                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }catch(Exception error_exception){

                                    }

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            if (notification == true && record_count < count) {
                                                if (data.equals("data")) {
                                                    Log.d("data test","data");
                                                    DocumentReference docR = db.collection("data").document("allData")
                                                            .collection(address).document(document_name);
                                                    docR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                Drawable drawable = getDrawable(R.drawable.appicon);
                                                                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                                                                Bitmap bitmap = bitmapDrawable.getBitmap();

                                                                DocumentSnapshot document = task.getResult();
                                                                String title = document.getData().get("title").toString();
                                                                String content = document.getData().get("content").toString();
                                                                String day = document.getData().get("day").toString();
                                                                String write = document.getData().get("id_nickName").toString();
                                                                String good_num = document.getData().get("good_num").toString();
                                                                String visit_num = document.getData().get("visit_num").toString();
                                                                String documentName = document.getData().get("document_name").toString();

                                                                int visitInt = Integer.parseInt(visit_num);
                                                                visitInt = visitInt + 1;
                                                                visit_num = Integer.toString(visitInt);


                                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(MyService.this, "default")
                                                                        .setAutoCancel(true);
                                                                builder.setColor(ContextCompat.getColor(MyService.this, R.color.fui_bgFacebook));
                                                                builder.setSmallIcon(R.drawable.appicon);
                                                                builder.setContentTitle("Needs");
                                                                builder.setContentText(value + "님이 게시물에 관심을 가졌습니다");
                                                                builder.setDefaults(Notification.DEFAULT_VIBRATE);
                                                                builder.setWhen(System.currentTimeMillis());
                                                                builder.setAutoCancel(true);
                                                                if(!url.equals("url"))
                                                                {
                                                                    builder.setLargeIcon(urlToBitmap);
                                                                }
                                                                else
                                                                {
                                                                    builder.setLargeIcon(bitmap);
                                                                }

                                                                //Class name = getActivity;

                                                                Intent notificationIntent = new Intent(MyService.this, HomeContent.class);

                                                                notificationIntent.putExtra("title", title);
                                                                notificationIntent.putExtra("content", content);
                                                                notificationIntent.putExtra("day", day);
                                                                notificationIntent.putExtra("id", write);
                                                                notificationIntent.putExtra("good", good_num);
                                                                notificationIntent.putExtra("visitnum", visit_num);
                                                                notificationIntent.putExtra("documentName", documentName);

                                                                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, notificationIntent, 0);
                                                                builder.setContentIntent(pendingIntent);

                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                                    manager.createNotificationChannel(new NotificationChannel("default", "기본채널", NotificationManager.IMPORTANCE_DEFAULT));
                                                                }
                                                                startForeground(1, builder.build());
                                                                builder.setAutoCancel(true);


                                                            }
                                                        }
                                                    });
                                                } else if (data.equals("freedata")) {
                                                    Log.d("data test","freedata");
                                                    DocumentReference docR = db.collection("freeData").document(document_name);
                                                    docR.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                Drawable drawable = getDrawable(R.drawable.appicon);
                                                                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                                                                Bitmap bitmap = bitmapDrawable.getBitmap();
                                                                DocumentSnapshot document = task.getResult();
                                                                String title = document.getData().get("title").toString();
                                                                String content = document.getData().get("content").toString();
                                                                String day = document.getData().get("day").toString();
                                                                String write = document.getData().get("id_nickName").toString();
                                                                String good_num = document.getData().get("good_num").toString();
                                                                String visit_num = document.getData().get("visit_num").toString();
                                                                String documentName = document.getData().get("document_name").toString();

                                                                int visitInt = Integer.parseInt(visit_num);
                                                                visitInt = visitInt + 1;
                                                                visit_num = Integer.toString(visitInt);


                                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(MyService.this, "default")
                                                                        .setAutoCancel(true);
                                                                //builder.setColor(ContextCompat.getColor(MyService.this, R.color.fui_bgFacebook));
                                                                builder.setSmallIcon(R.drawable.appicon);
                                                                builder.setContentTitle("Needs");
                                                                builder.setContentText(value + "님이 게시물에 관심을 가졌습니다");
                                                                builder.setDefaults(Notification.DEFAULT_VIBRATE);
                                                                builder.setWhen(System.currentTimeMillis());
                                                                builder.setAutoCancel(true);

                                                                if(!url.equals("url"))
                                                                {
                                                                    builder.setLargeIcon(urlToBitmap);
                                                                }
                                                                else
                                                                {
                                                                    builder.setLargeIcon(bitmap);
                                                                }
                                                                //Class name = getActivity;

                                                                Intent notificationIntent = new Intent(MyService.this, HomeFreeContent.class);

                                                                notificationIntent.putExtra("title", title);
                                                                notificationIntent.putExtra("content", content);
                                                                notificationIntent.putExtra("day", day);
                                                                notificationIntent.putExtra("id", write);
                                                                notificationIntent.putExtra("good", good_num);
                                                                notificationIntent.putExtra("visitnum", visit_num);
                                                                notificationIntent.putExtra("documentName", documentName);

                                                                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, notificationIntent, 0);
                                                                builder.setContentIntent(pendingIntent);

                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                                    manager.createNotificationChannel(new NotificationChannel("default", "기본채널", NotificationManager.IMPORTANCE_DEFAULT));
                                                                }
                                                                startForeground(1, builder.build());
                                                                builder.setAutoCancel(true);

                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                            else
                                            {
                                                Drawable drawable = getDrawable(R.drawable.appicon);
                                                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                                                Bitmap bitmap = bitmapDrawable.getBitmap();

                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(MyService.this, "default")
                                                        .setAutoCancel(true);
                                                builder.setColor(ContextCompat.getColor(MyService.this, R.color.fui_bgFacebook));
                                                builder.setSmallIcon(getNotificationIcon());
                                                builder.setContentTitle("Needs");
                                                builder.setContentText("Needs Application이 실행중입니다.");
                                                builder.setDefaults(Notification.DEFAULT_VIBRATE);
                                                builder.setAutoCancel(true);
                                                builder.setOnlyAlertOnce(true);
                                                if(!url.equals("url"))
                                                {
                                                    builder.setLargeIcon(urlToBitmap);
                                                    Log.d("이미지 url",url);
                                                }
                                                else
                                                {
                                                    builder.setLargeIcon(bitmap);
                                                }

                                                //Class name = getActivity;

                                                Intent notificationIntent = new Intent(MyService.this, SubActivity.class);
                                                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, notificationIntent, 0);
                                                builder.setContentIntent(pendingIntent);

                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                    manager.createNotificationChannel(new NotificationChannel("default", "기본채널", NotificationManager.IMPORTANCE_DEFAULT));
                                                }
                                                startForeground(1, builder.build());
                                                builder.setAutoCancel(true);
                                            }
                                        }
                                    }, 500);
                                }
                            });
                        } catch (Exception e) {
                        }
                    }//
                });
            }
        }, 0, 5000);
    }
    private int getNotificationIcon()
    {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.appicon : R.drawable.appicon;
    }

    private Bitmap GetImageFromURL(String strImageURL) {
        Bitmap imgBitmap = null;

        try {
            URL url = new URL(strImageURL);
            URLConnection conn = url.openConnection();
            conn.connect();

            int nSize = conn.getContentLength();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);

            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgBitmap;
    }
}

                            /*
                            db.collection("user").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    final DocumentSnapshot documentSnapshot = task.getResult();
                                    try {
                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try{
                                                    url = documentSnapshot.getData().get("photoUrl").toString();
                                                    urlToBitmap = GetImageFromURL(url);
                                                }catch(Exception e)
                                                {
                                                    url = "url";
                                                }
                                            }
                                        });
                                        thread.start();
                                    }catch (Exception e)
                                    {
                                        url = "url";
                                    }
                                }
                            });*/