package com.tester.Needs.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.tester.Needs.Main.SubActivity;
import com.tester.Needs.R;

import java.util.Timer;
import java.util.TimerTask;

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
    boolean notification = false;
    Timer timer;


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
    private void startForegroundService(){
        uid = user.getUid();
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
        Log.d("서비스실행","서비스실행");
            timer = new Timer(true);
            final Handler handler = new Handler();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override

                        public void run() {
                            try {
                                DocumentReference docRef = db.collection("user").document(uid)
                                        .collection("action").document("write");
                                Log.d("임의의실행","성공일떄의");
                                db.collection(  "user").document(uid).collection("action")
                                        .whereEqualTo("value","data").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            Log.w("에러일때", "listen:error", e);
                                            return;
                                        }
                                        int count = 0;

                                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                            switch (dc.getType()) {
                                                case ADDED: {
                                                        value = dc.getDocument().getData().get("writer").toString();
                                                        count++;
                                                          Log.d("에러가안난다면", String.valueOf(count));
                                                        notification = true;
                                                    break;
                                                }
                                            }
                                        }

                                        if (notification == true && record_count!=count) {
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyService.this, "default")
                                                    .setAutoCancel(true);
                                            builder.setColor(ContextCompat.getColor(MyService.this, R.color.fui_bgFacebook));
                                            builder.setSmallIcon(R.drawable.appicon);
                                            builder.setContentTitle("Needs");
                                            builder.setContentText(value+"님이 게시물에 관심을 가졌습니다");
                                            builder.setDefaults(Notification.DEFAULT_ALL);
                                            builder.setWhen(System.currentTimeMillis());
                                            builder.setAutoCancel(true);

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
                                        else
                                        {
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyService.this, "default")
                                                    .setAutoCancel(true);
                                            builder.setColor(ContextCompat.getColor(MyService.this, R.color.fui_bgFacebook));
                                            builder.setSmallIcon(R.drawable.appicon);
                                            builder.setContentTitle("Needs");
                                            builder.setContentText("Needs Application이 실행중입니다.");
                                            builder.setDefaults(Notification.DEFAULT_ALL);
                                            builder.setWhen(System.currentTimeMillis());
                                            builder.setAutoCancel(true);

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
                                });
                            }
                            catch(Exception e){
                                Log.d("임의의실행","실패일떄의"+uid);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(MyService.this, "default")
                                        .setAutoCancel(true);
                                builder.setColor(ContextCompat.getColor(MyService.this, R.color.fui_bgFacebook));
                                builder.setSmallIcon(R.drawable.appicon);
                                builder.setContentTitle("Needs");
                                builder.setContentText("Needs Application이 실행중입니다.");
                                builder.setDefaults(Notification.DEFAULT_ALL);
                                builder.setWhen(System.currentTimeMillis());
                                builder.setAutoCancel(true);

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
                        }//
                    });
                }
            },0,5000);
    }

}


/*
db.collection("user").document(uid).collection("action").
                                        get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful())
                                        {
                                            int num = 0;

                                            for(QueryDocumentSnapshot documentSnapshot: task.getResult())
                                            {
                                                String record_value = documentSnapshot.getData().get("write").toString();
                                                try {
                                                    Log.d("에러가안난다면", "0번 값");
                                                    recordList.get(num).getContext();
                                                    if (!record_value.equals(recordList.get(num).getContext())) {
                                                        recordList.add(new RecordList(documentSnapshot.getData().get("write").toString()));
                                                        value = record_value;
                                                        Log.d("추가 데이터 rv", "데이터가추가되었습니다"
                                                                + recordList.get(num).getContext());
                                                        notification = true;
                                                    }
                                                } catch (Exception er) {
                                                    Log.d("에러캐치", "0번쨰 대입");
                                                    value = record_value;
                                                    recordList.add(new RecordList(documentSnapshot.getData().get("write").toString()));
                                                    notification = true;
                                                }
                                                num++;
                                            }

                                            if (notification == true) {
                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(MyService.this, "default")
                                                        .setAutoCancel(true);
                                                builder.setColor(ContextCompat.getColor(MyService.this, R.color.fui_bgFacebook));
                                                builder.setSmallIcon(R.drawable.appicon);
                                                builder.setContentTitle("Needs");
                                                builder.setContentText(value+"님이 게시물에 관심을 가졌습니다");
                                                builder.setDefaults(Notification.DEFAULT_ALL);
                                                builder.setWhen(System.currentTimeMillis());
                                                builder.setAutoCancel(true);

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
                                            else
                                            {
                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(MyService.this, "default")
                                                        .setAutoCancel(true);
                                                builder.setColor(ContextCompat.getColor(MyService.this, R.color.fui_bgFacebook));
                                                builder.setSmallIcon(R.drawable.appicon);
                                                builder.setContentTitle("Needs");
                                                builder.setContentText("Needs Application이 실행중입니다.");
                                                builder.setDefaults(Notification.DEFAULT_ALL);
                                                builder.setWhen(System.currentTimeMillis());
                                                builder.setAutoCancel(true);

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
                                    }
                                });
 */