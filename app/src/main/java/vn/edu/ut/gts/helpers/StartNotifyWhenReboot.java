package vn.edu.ut.gts.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartNotifyWhenReboot extends BroadcastReceiver {
    Storage storage;
    @Override
    public void onReceive(Context context, Intent intent) {
        storage = new Storage(context);
        Boolean isNotify = Boolean.valueOf(storage.getString("week_schedule_notify"));
        if(isNotify){
            Intent intentStart = new Intent(context,NotifyWeekScheduleService.class);
            context.startService(intentStart);
        }
    }
}
