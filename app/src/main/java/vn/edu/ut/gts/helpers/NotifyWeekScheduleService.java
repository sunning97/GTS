package vn.edu.ut.gts.helpers;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.widget.Toast;

@SuppressLint("Registered")
public class NotifyWeekScheduleService extends Service{
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    public void onCreate() {
        networkChangeReceiver = new NetworkChangeReceiver(getApplicationContext());
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(networkChangeReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
