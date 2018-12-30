package vn.edu.ut.gts.helpers;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import java.util.Calendar;

@SuppressLint("Registered")
public class NotifyWeekScheduleService extends Service{
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    public void onCreate() {
        networkChangeReceiver = new NetworkChangeReceiver(getApplicationContext());
        registerReceiver(networkChangeReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        invokeCheckScheduleForNotify();
        return START_STICKY;
    }


    private void invokeCheckScheduleForNotify(){

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE,1);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.add(Calendar.DAY_OF_YEAR,1);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent1 = new Intent(getApplicationContext(),CheckWeekSchedule.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),1,intent1,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
