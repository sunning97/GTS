package vn.edu.ut.gts.helpers;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import vn.edu.ut.gts.views.login.LoginActivity;

public class OnClearFromRecentService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Storage storage = new Storage(getApplicationContext());
        storage.deleteAllsharedPreferences(getApplicationContext());
        LoginActivity.isOpen = false;
        stopSelf();
    }
}
