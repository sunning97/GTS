package vn.edu.ut.gts.helpers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import vn.edu.ut.gts.R;

public class NotifyWeekScheduleAlert extends BroadcastReceiver {
    public static String title = "";
    public static String message = "";
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.gts_icon)
                .setContentTitle("Lịch học "+title)
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(message))
                .setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setVibrate(new long[] {500,500,500,500});

        Notification notification = builder.build();
        notification.ledARGB=Color.BLUE;
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        notification.ledOnMS = 1000;
        notification.ledOffMS = 2000;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

        this.set(context);
    }

    private void set(Context context){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.add(Calendar.DAY_OF_YEAR,1);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent1 = new Intent(context.getApplicationContext(),CheckWeekSchedule.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),1,intent1,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }
}
