package vn.edu.ut.gts.helpers;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private Storage storage;
    private Context context;
    private final int ALL_DAY = 1;
    private final int MORNING = 2;
    private final int AFTERNOON = 3;
    private final int EVENING = 4;
    private final int MORNING_AFTERNOON = 5;
    private final int MORNING_EVENING = 6;
    private final int AFTERNOON_EVENING = 7;

    public NetworkChangeReceiver(Context context){
        storage = new Storage(context);
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try
        {
            if (isOnline(context)) {
                this.getData();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void getData(){
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray schedules = new JSONArray();
                try {
                    Document document = Jsoup.connect(Helper.BASE_URL + "LichHocLichThiTuan.aspx")
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .get();

                    schedules = parseWeekData(document);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return schedules;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = df.format(c.getTime());

                JSONObject currentDateSchedule = null;

                for (int i = 0;i< jsonArray.length();i++){
                    try {
                        JSONObject tmp = jsonArray.getJSONObject(i);

                        if(tmp.getString("date").equals(formattedDate)){
                            currentDateSchedule = tmp;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                try {
//                    if(currentDateSchedule.getJSONArray("morning").length() > 0 || currentDateSchedule.getJSONArray("afternoon").length() > 0 || currentDateSchedule.getJSONArray("evening").length() > 0){
//                        setLAlarm(currentDateSchedule);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                try {
                    setLAlarm(jsonArray.getJSONObject(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        asyncTask.execute();
    }

    private JSONArray parseWeekData(Document document) {
        /*get data from html*/
        JSONArray data = new JSONArray();
        try {
            Elements table = document.select(".div-ChiTietLich>table");
            Elements trs = table.select("tr");

            Elements trDate = trs.first().select("th");
            Elements trMorning = trs.get(1).select("td");
            Elements trAfternoon = trs.get(2).select("td");
            Elements trEvening = trs.get(3).select("td");
            for (int i = 0; i < trMorning.size(); i++) {
                JSONObject schedule = new JSONObject();
                // Get date
                String dateRegEx = "([0-9]{2})/([0-9]{2})/([0-9]{4})";
                Pattern p = Pattern.compile(dateRegEx);
                Matcher m = p.matcher(trDate.get(i + 1).text());
                if (trDate.get(i + 1).hasClass("current-date"))
                    schedule.put("current_date", "true");
                if (m.find()) {
                    schedule.put("date", m.group());
                }
                //Get
                JSONArray objMorning = new JSONArray();
                JSONArray objAfternoon = new JSONArray();
                JSONArray objEvening = new JSONArray();

                if (trMorning.get(i).select(".div-LichHoc").size() > 0 || trMorning.get(i).select(".div-LichThi").size() > 0) {
                    Elements divLich = null;
                    boolean isTest = false;
                    if(trMorning.get(i).select(".div-LichThi").size() > 0){
                        divLich = trMorning.get(i).select(".div-LichThi");
                        isTest = true;
                    } else {
                        divLich = trMorning.get(i).select(".div-LichHoc");
                    }

                    for (int j = 0; j < divLich.size(); j++) {
                        JSONObject tmp = new JSONObject();
                        if (divLich.get(j).select(".TamNgung").size() > 0)
                            tmp.put("is_postpone", String.valueOf(true));
                        else tmp.put("is_postpone", String.valueOf(false));

                        if(isTest)
                            tmp.put("is_test", String.valueOf(true));
                        else tmp.put("is_test", String.valueOf(false));

                        Elements spanDisplay = divLich.get(j).children().select(".span-display");
                        Elements spanLabel = divLich.get(j).children().select(".span-label");
                        tmp.put("subject_id",spanDisplay.get(0).text().trim());
                        tmp.put("subject_name",spanDisplay.get(1).text().trim());
                        JSONArray jsonArray = new JSONArray();
                        for (int v = 0; v < spanLabel.size(); v++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("key",spanLabel.get(v).text().trim());
                            jsonObject.put("value",spanDisplay.get(v+2).text().trim().length() > 0 ? spanDisplay.get(v+2).text().trim() : "");
                            jsonArray.put(jsonObject);
                            tmp.put("values",jsonArray);
                        }
                        objMorning.put(tmp);
                    }
                }
                if (trAfternoon.get(i).select(".div-LichHoc").size() > 0 || trAfternoon.get(i).select(".div-LichThi").size() > 0) {

                    Elements divLich = null;
                    boolean isTest = false;
                    if(trAfternoon.get(i).select(".div-LichThi").size() > 0){
                        divLich = trAfternoon.get(i).select(".div-LichThi");
                        isTest = true;
                    } else {
                        divLich = trAfternoon.get(i).select(".div-LichHoc");
                    }

                    for (int j = 0; j < divLich.size(); j++) {
                        JSONObject tmp = new JSONObject();
                        if (divLich.get(j).select(".div-TamNgung").size() > 0)
                            tmp.put("is_postpone", String.valueOf(true));
                        else tmp.put("is_postpone", String.valueOf(false));

                        if(isTest)
                            tmp.put("is_test", String.valueOf(true));
                        else tmp.put("is_test", String.valueOf(false));


                        Elements spanDisplay = divLich.get(j).children().select(".span-display");
                        Elements spanLabel = divLich.get(j).children().select(".span-label");
                        tmp.put("subject_id",spanDisplay.get(0).text().trim());
                        tmp.put("subject_name",spanDisplay.get(1).text().trim());
                        JSONArray jsonArray = new JSONArray();

                        for (int v = 0; v < spanLabel.size(); v++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("key",spanLabel.get(v).text().trim());
                            jsonObject.put("value",spanDisplay.get(v+2).text().trim().length() > 0 ? spanDisplay.get(v+2).text().trim() : "");
                            jsonArray.put(jsonObject);
                            tmp.put("values",jsonArray);
                        }

                        objAfternoon.put(tmp);
                    }
                }
                if (trEvening.get(i).select(".div-LichHoc").size() > 0 || trEvening.get(i).select(".div-LichThi").size() > 0) {
                    Elements divLich = null;
                    boolean isTest = false;
                    if(trEvening.get(i).select(".div-LichThi").size() > 0){
                        divLich = trEvening.get(i).select(".div-LichThi");
                        isTest = true;
                    } else {
                        divLich = trEvening.get(i).select(".div-LichHoc");
                    }

                    for (int j = 0; j < divLich.size(); j++) {
                        JSONObject tmp = new JSONObject();
                        if (divLich.get(j).select(".div-TamNgung").size() > 0)
                            tmp.put("is_postpone", String.valueOf(true));
                        else tmp.put("is_postpone", String.valueOf(false));

                        if(isTest)
                            tmp.put("is_test", String.valueOf(true));
                        else tmp.put("is_test", String.valueOf(false));

                        Elements spanDisplay = divLich.get(j).children().select(".span-display");
                        Elements spanLabel = divLich.get(j).children().select(".span-label");
                        tmp.put("subject_id",spanDisplay.get(0).text().trim());
                        tmp.put("subject_name",spanDisplay.get(1).text().trim());
                        JSONArray jsonArray = new JSONArray();

                        for (int v = 0; v < spanLabel.size(); v++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("key",spanLabel.get(v).text().trim());
                            jsonObject.put("value",spanDisplay.get(v+2).text().trim().length() > 0 ? spanDisplay.get(v+2).text().trim() : "");
                            jsonArray.put(jsonObject);
                            tmp.put("values",jsonArray);
                        }
                        objEvening.put(tmp);
                    }
                }
                schedule.put("morning", objMorning);
                schedule.put("afternoon", objAfternoon);
                schedule.put("evening", objEvening);
                data.put(schedule);
            }
        } catch (IndexOutOfBoundsException | NullPointerException | JSONException e) {
            e.printStackTrace();
        }
        return data;
    }


    private void setLAlarm(JSONObject data){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,14);
        calendar.set(Calendar.MINUTE,37);
        calendar.set(Calendar.SECOND,0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,NotifyWeekScheduleAlert.class);

        String mess = "";

        try {
            if(data.getJSONArray("morning").length() > 0) mess+="Sáng: " + data.getJSONArray("morning").getJSONObject(0).getString("subject_name") + " \n";
            if(data.getJSONArray("afternoon").length() > 0) mess+="Chiều: " + data.getJSONArray("afternoon").getJSONObject(0).getString("subject_name") + " \n";
            if(data.getJSONArray("evening").length() > 0) mess+="Tối: " + data.getJSONArray("evening").getJSONObject(0).getString("subject_name") + " \n";

        } catch (JSONException e) {
            e.printStackTrace();
        }

        intent.putExtra("mess",mess);
        try {
            intent.putExtra("title",data.getString("date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,1,intent,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);


//        switch (Integer.parseInt(storage.getString("week_schedule_notify_time"))){
//            case 1:
//            {
//
//                break;
//            }
//            case 2:
//            {
//                break;
//            }
//            case 6:
//            {
//                break;
//            }
//        }
    }
}
