package vn.edu.ut.gts.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import vn.edu.ut.gts.R;

public class NotifyWeekScheduleAlert extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.gts_icon)
                .setContentTitle("Lịch học "+intent.getStringExtra("title"))
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(intent.getStringExtra("mess")))
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
    }
}
