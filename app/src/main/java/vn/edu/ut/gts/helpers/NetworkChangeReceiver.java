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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private Storage storage;
    private Context context;


    public NetworkChangeReceiver(Context context){
        storage = new Storage(context);
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try
        {
            if (isOnline(context)) {
                this.iniLogin();
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

    private void getDataWeek(){
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray schedules = new JSONArray();
                try {
                    Document document = Jsoup.connect(Helper.BASE_URL + "LichHocLichThiTuan.aspx")
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getString("w_cookie"))
                            .get();

                    schedules = parseWeekData(document);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return schedules;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                storage.putString("week_notify_data",jsonArray.toString());
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

    private void iniLogin(){

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                JSONObject data = new JSONObject();
                try {
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL)
                            .userAgent(Helper.USER_AGENT)
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .execute();
                    Document doc = res.parse();
                    storage.putString("w_cookie",res.cookie("ASP.NET_SessionId"));
                    data.put("w_eventTarget", doc.select("input[name=\"__EVENTTARGET\"]").val());
                    data.put("w_eventArgument", doc.select("input[name=\"__EVENTARGUMENT\"]").val());
                    data.put("w_lastFocus", doc.select("input[name=\"__LASTFOCUS\"]").val());
                    data.put("w_viewState", doc.select("input[name=\"__VIEWSTATE\"]").val());
                    data.put("w_viewStartGenerator", doc.select("input[name=\"__VIEWSTATEGENERATOR\"]").val());
                    data.put("w_radioBtnList", doc.select("input[name=\"ctl00$ucPhieuKhaoSat1$RadioButtonList1\"][checked=\"checked\"]").val());
                    data.put("w_listMenu", doc.select("select[name=\"ctl00$DdListMenu\"]>option").first().val());
                    data.put("w_btnLogin", doc.select("input[name=\"ctl00$ucRight1$btnLogin\"]").val());
                    storage.putString("w_dataLogin", data.toString());

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                doLogin(storage.getString("last_student_login"),storage.getString("password"));
            }
        };
        asyncTask.execute();
    }

    public void doLogin(final String studentId, final String password) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONObject dataLogin = new JSONObject(storage.getString("w_dataLogin"));
                    String hashPassword = Aes.encrypt(getPrivateKey(studentId), password).toBase64();
                    String securityValue = createConfirmImage();
                    Jsoup.connect(Helper.BASE_URL)
                            .method(Connection.Method.POST)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getString("w_cookie"))
                            .data("__EVENTTARGET", dataLogin.getString("w_eventTarget"))
                            .data("__EVENTARGUMENT", dataLogin.getString("w_eventArgument"))
                            .data("__LASTFOCUS", dataLogin.getString("w_lastFocus"))
                            .data("__VIEWSTATE", dataLogin.getString("w_viewState"))
                            .data("__VIEWSTATEGENERATOR", dataLogin.getString("w_viewStartGenerator"))
                            .data("ctl00$ucPhieuKhaoSat1$RadioButtonList1", dataLogin.getString("w_radioBtnList"))
                            .data("ctl00$DdListMenu", dataLogin.getString("w_listMenu"))
                            .data("ctl00$ucRight1$btnLogin", dataLogin.getString("w_btnLogin"))
                            .data("ctl00$ucRight1$txtMaSV", studentId)
                            .data("ctl00$ucRight1$txtMatKhau", hashPassword)
                            .data("ctl00$ucRight1$txtSercurityCode", securityValue)
                            .data("txtSecurityCodeValue", Helper.md5(securityValue))
                            .data("ctl00$ucRight1$txtEncodeMatKhau", Helper.md5(password))
                            .execute();

                } catch (NullPointerException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                checkLogin();
            }
        };
        asyncTask.execute();
    }

    private void checkLogin(){
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                Boolean a = false;
                try {
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "ajaxpro/DangKy,PMT.Web.PhongDaoTao.ashx")
                            .method(Connection.Method.POST)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getString("w_cookie"))
                            .header("X-AjaxPro-Method","CheckLogin")
                            .execute();

                    Document document = res.parse();
                    if(Boolean.parseBoolean(document.select("body").text().replace(";/*", ""))){
                        a = true;
                    }
                } catch (NullPointerException | IndexOutOfBoundsException | IOException e) {
                    e.printStackTrace();
                }
                return a;
            }

            @Override
            protected void onPostExecute(Boolean b) {
                if(b) getDataWeek();
                else Log.d("AAAAAA","ko login dc");
            }
        };
        asyncTask.execute();
    }

    private String createConfirmImage() {
        try {
            String res = Curl.connect(Helper.BASE_URL + "ajaxpro/AjaxConfirmImage,PMT.Web.PhongDaoTao.ashx")
                    .method("POST")
                    .setCookie("ASP.NET_SessionId", this.storage.getString("w_cookie"))
                    .userAgent(Helper.USER_AGENT)
                    .header("X-AjaxPro-Method", "CreateConfirmImage")
                    .dataString("{}")
                    .execute();
            res = res.replace(";/*", "");
            JSONArray ar = new JSONArray(res);
            return Helper.decryptMd5(ar.getString(1));
        } catch (NullPointerException | IndexOutOfBoundsException | JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getPrivateKey(String studentId) {
        String result = null;
        try {
            String res = Curl.connect(Helper.BASE_URL + "ajaxpro/AjaxCommon,PMT.Web.PhongDaoTao.ashx")
                    .method("POST")
                    .userAgent(Helper.USER_AGENT)
                    .header("X-AjaxPro-Method", "GetPrivateKey")
                    .setStringCookie(this.storage.getString("w_cookie"))
                    .dataString("{\"salt\":\"" + studentId + "\"}")
                    .execute();
            if (res != null) result =  res.substring(1, 33);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return result;
    }
}
