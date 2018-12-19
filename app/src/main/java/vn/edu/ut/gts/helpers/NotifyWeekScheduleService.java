package vn.edu.ut.gts.helpers;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import vn.edu.ut.gts.views.home.HomeActivity;

@SuppressLint("Registered")
public class NotifyWeekScheduleService extends Service{
    private Storage storage;

    @Override
    public void onCreate() {
        storage = new Storage(getApplicationContext());
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(HomeActivity.isLogin){
            Toast.makeText(getApplicationContext(),"Dang login "+storage.getString("week_schedule_notify_time"),Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),"ko mo ung dung "+storage.getString("week_schedule_notify_time"),Toast.LENGTH_LONG).show();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(),"Huy thong bao",Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}
