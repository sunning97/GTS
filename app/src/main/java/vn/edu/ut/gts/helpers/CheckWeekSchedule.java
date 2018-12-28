package vn.edu.ut.gts.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CheckWeekSchedule extends BroadcastReceiver {
    private Storage storage;


    @Override
    public void onReceive(Context context, Intent intent) {
        storage = new Storage(context);
        JSONArray dataWeek = null;
        try {
            if(storage.getString("week_notify_data") == null){

            } else {
                dataWeek = new JSONArray(storage.getString("week_notify_data"));
            }

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = df.format(c.getTime());

            JSONObject currentDateSchedule = null;

            for (int i = 0; i < dataWeek.length(); i++) {
                try {
                    JSONObject tmp = dataWeek.getJSONObject(i);

                    if (tmp.getString("date").equals(formattedDate)) {
                        currentDateSchedule = tmp;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (currentDateSchedule.getJSONArray("morning").length() > 0 || currentDateSchedule.getJSONArray("afternoon").length() > 0 || currentDateSchedule.getJSONArray("evening").length() > 0) {
                setLAlarm(currentDateSchedule, context);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setLAlarm(JSONObject data, Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotifyWeekScheduleAlert.class);
        String mess = "";

        try {
            if (data.getJSONArray("morning").length() > 0)
                mess += "Sáng: " + data.getJSONArray("morning").getJSONObject(0).getString("subject_name") + " \n";
            if (data.getJSONArray("afternoon").length() > 0)
                mess += "Chiều: " + data.getJSONArray("afternoon").getJSONObject(0).getString("subject_name") + " \n";
            if (data.getJSONArray("evening").length() > 0)
                mess += "Tối: " + data.getJSONArray("evening").getJSONObject(0).getString("subject_name") + " \n";

        } catch (JSONException e) {
            e.printStackTrace();
        }

        intent.putExtra("mess", mess);

        try {
            intent.putExtra("title", data.getString("date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (Integer.parseInt(storage.getString("week_schedule_notify_time"))) {
            case 1: {
                calendar.set(Calendar.HOUR_OF_DAY, 6);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                break;
            }
            case 2: {
                calendar.set(Calendar.HOUR_OF_DAY, 5);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                break;
            }
            case 6: {
                calendar.set(Calendar.HOUR_OF_DAY, 1);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                break;
            }
        }

    }
}
