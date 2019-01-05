package vn.edu.ut.gts.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
                    int flag = 0;
                    if (Boolean.valueOf(tmp.getString("current_date"))) {
                        currentDateSchedule = tmp;
                        flag = 1;
                    }
                    if(flag == 1) break;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (currentDateSchedule.getJSONArray("morning").length() > 0 || currentDateSchedule.getJSONArray("afternoon").length() > 0 || currentDateSchedule.getJSONArray("evening").length() > 0) {
                if(!Boolean.valueOf(currentDateSchedule.getJSONArray("morning").getJSONObject(0).getString("is_postpone")) ||
                    !Boolean.valueOf(currentDateSchedule.getJSONArray("afternoon").getJSONObject(0).getString("is_postpone")) ||
                    !Boolean.valueOf(currentDateSchedule.getJSONArray("evening").getJSONObject(0).getString("is_postpone"))){
                    this.setLAlarm(currentDateSchedule,context);
                } else this.set(context);
            } else this.set(context);
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
        int morning = 0;
        int afternoon = 0;
        int evening = 0;

        try {
            if (data.getJSONArray("morning").length() > 0) {
                if(Boolean.valueOf(data.getJSONArray("morning").getJSONObject(0).getString("is_test"))){
                    mess += "Sáng: " + data.getJSONArray("morning").getJSONObject(0).getString("subject_name").replaceAll("\\(LT\\)","").trim() + " (Kiểm tra) \n";
                } else {
                    mess += "Sáng: " + data.getJSONArray("morning").getJSONObject(0).getString("subject_name").replaceAll("\\(LT\\)","").trim() + " \n";
                }
                morning = 1;
            }
            if (data.getJSONArray("afternoon").length() > 0) {
                if(Boolean.valueOf(data.getJSONArray("afternoon").getJSONObject(0).getString("is_test"))){
                    mess += "Chiều: " + data.getJSONArray("afternoon").getJSONObject(0).getString("subject_name").replaceAll("\\(LT\\)","").trim() + " (Kiểm tra) \n";
                } else {
                    mess += "Chiều: " + data.getJSONArray("afternoon").getJSONObject(0).getString("subject_name").replaceAll("\\(LT\\)","").trim() + " \n";
                }
                afternoon = 1;
            }
            if (data.getJSONArray("evening").length() > 0) {
                if(Boolean.valueOf(data.getJSONArray("evening").getJSONObject(0).getString("is_test"))){
                    mess += "Tối: " + data.getJSONArray("evening").getJSONObject(0).getString("subject_name").replaceAll("\\(LT\\)","").trim() + " (Kiểm tra) \n";
                } else {
                    mess += "Tối: " + data.getJSONArray("evening").getJSONObject(0).getString("subject_name").replaceAll("\\(LT\\)","").trim() + " \n";
                }
                evening = 1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NotifyWeekScheduleAlert.message = mess;
        try {
            NotifyWeekScheduleAlert.title = data.getString("date");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (Integer.parseInt(storage.getString("week_schedule_notify_time"))) {
            case 1: {
                if(evening == 1){
                    calendar.set(Calendar.HOUR_OF_DAY, 18);
                }
                if(afternoon == 1){
                    calendar.set(Calendar.HOUR_OF_DAY, 11);
                }
                if(morning == 1){
                    calendar.set(Calendar.HOUR_OF_DAY, 6);
                }
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                break;
            }
            case 2: {
                if(evening == 1){
                    calendar.set(Calendar.HOUR_OF_DAY, 17);
                }
                if(afternoon == 1){
                    calendar.set(Calendar.HOUR_OF_DAY, 10);
                }
                if(morning == 1){
                    calendar.set(Calendar.HOUR_OF_DAY, 5);
                }
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                break;
            }
            case 6: {
                if(evening == 1){
                    calendar.set(Calendar.HOUR_OF_DAY, 13);
                }
                if(afternoon == 1){
                    calendar.set(Calendar.HOUR_OF_DAY, 6);
                }
                if(morning == 1){
                    calendar.set(Calendar.HOUR_OF_DAY, 1);
                }
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                break;
            }
        }

    }

    private void set(Context context){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE,1);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.add(Calendar.DAY_OF_YEAR,1);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent1 = new Intent(context.getApplicationContext(),CheckWeekSchedule.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),1,intent1,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }
}
