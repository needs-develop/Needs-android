package com.tester.Needs.Common;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

public class MyService extends Service {

    private IBinder IBinder = new MyBinder();

    public int var = 1;

    class MyBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("로그", "바인드");
        return IBinder;
    }

    @Override
    public void onCreate() {
        Log.e("로그", "크리에이트");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("로그", "스타트커맨드");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("로그", "디스트로이");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("로그", "언 바인드");
        return super.onUnbind(intent);
    }

}