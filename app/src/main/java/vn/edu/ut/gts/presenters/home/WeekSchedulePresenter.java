package vn.edu.ut.gts.presenters.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.views.home.fragments.IWeekSchedule;
import vn.edu.ut.gts.views.home.fragments.StudentDebtFragment;

public class WeekSchedulePresenter implements IWeekSchedulePresenter {
    public static int currentStatus = 0;
    private IWeekSchedule iWeekSchedule;
    private Context context;
    private Storage storage;

    public WeekSchedulePresenter(IWeekSchedule iWeekSchedule, Context context) {
        this.iWeekSchedule = iWeekSchedule;
        this.context = context;
        this.storage = new Storage(this.context);
    }

    private void getDataScedules(Document document) {
        JSONObject dataWeek = new JSONObject();
        try {
            dataWeek.put("eventTarget", document.select("input[name=\"__EVENTTARGET\"]").val());
            dataWeek.put("eventArgument", document.select("input[name=\"__EVENTARGUMENT\"]").val());
            dataWeek.put("lastFocus", document.select("input[name=\"__LASTFOCUS\"]").val());
            dataWeek.put("viewState", document.select("input[name=\"__VIEWSTATE\"]").val());
            dataWeek.put("viewStartGenerator", document.select("input[name=\"__VIEWSTATEGENERATOR\"]").val());
            dataWeek.put("radioBtnListPhieuKhaoSat", document.select("input[name=\"ctl00$ucPhieuKhaoSat1$RadioButtonList1\"][checked=\"checked\"]").val());
            dataWeek.put("listMenu", document.select("select[name=\"ctl00$DdListMenu\"]>option").first().val());
            dataWeek.put("loaiLich", document.select("input[name=\"ctl00$ContentPlaceHolder$rLoaiLich\"][checked=\"checked\"]").val());
            dataWeek.put("txtDate", document.select("input[name=\"ctl00$ContentPlaceHolder$txtDate\"]").val());
            dataWeek.put("tuanSau", document.select("input[name=\"ctl00$ContentPlaceHolder$btnSau\"]").val());
            dataWeek.put("tuanTruoc", document.select("input[name=\"ctl00$ContentPlaceHolder$btnTruoc\"]").val());
            dataWeek.put("hienTai", document.select("input[name=\"ctl00$ContentPlaceHolder$btnHienTai\"]").val());
            dataWeek.put("ngayChon", document.select("input[name=\"ctl00$ContentPlaceHolder$btnNgayChon\"]").val());
            storage.putString("data_week", dataWeek.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private JSONArray parseWeekData(Document document) {

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
                //Get morning
                JSONObject objMorning = new JSONObject();
                JSONObject objAfternoon = new JSONObject();
                JSONObject objEvening = new JSONObject();
                if (trMorning.get(i).select(".div-LichHoc").text().trim().length() > 0) {
                    Elements spanDisplay = trMorning.get(i).children().select(".span-display");
                    objMorning.put("subjectId", spanDisplay.get(0).text().trim().length() > 0 ? spanDisplay.get(0).text().trim() : "");
                    objMorning.put("subjectName", spanDisplay.get(1).text().trim().length() > 0 ? spanDisplay.get(1).text().trim() : "");
                    objMorning.put("subjectTime", spanDisplay.get(2).text().trim().length() > 0 ? spanDisplay.get(2).text().trim() : "");
                    objMorning.put("subjectLecturer", spanDisplay.get(3).text().trim().length() > 0 ? spanDisplay.get(3).text().trim() : "");
                    objMorning.put("subjectRoom", spanDisplay.get(4).text().trim().length() > 0 ? spanDisplay.get(4).text().trim() : "");
                }
                if (trAfternoon.get(i).select(".div-LichHoc").text().trim().length() > 0) {
                    Elements spanDisplay1 = trAfternoon.get(i).children().select(".span-display");
                    objAfternoon.put("subjectId", spanDisplay1.get(0).text().trim().length() > 0 ? spanDisplay1.get(0).text().trim() : "");
                    objAfternoon.put("subjectName", spanDisplay1.get(1).text().trim().length() > 0 ? spanDisplay1.get(1).text().trim() : "");
                    objAfternoon.put("subjectTime", spanDisplay1.get(2).text().trim().length() > 0 ? spanDisplay1.get(2).text().trim() : "");
                    objAfternoon.put("subjectLecturer", spanDisplay1.get(3).text().trim().length() > 0 ? spanDisplay1.get(3).text().trim() : "");
                    objAfternoon.put("subjectRoom", spanDisplay1.get(4).text().trim().length() > 0 ? spanDisplay1.get(4).text().trim() : "");
                }
                if (trEvening.get(i).select(".div-LichHoc").text().trim().length() > 0) {
                    Elements spanDisplay1 = trAfternoon.get(i).children().select(".span-display");
                    objAfternoon.put("subjectId", spanDisplay1.get(0).text().trim().length() > 0 ? spanDisplay1.get(0).text().trim() : "");
                    objAfternoon.put("subjectName", spanDisplay1.get(1).text().trim().length() > 0 ? spanDisplay1.get(1).text().trim() : "");
                    objAfternoon.put("subjectTime", spanDisplay1.get(2).text().trim().length() > 0 ? spanDisplay1.get(2).text().trim() : "");
                    objAfternoon.put("subjectLecturer", spanDisplay1.get(3).text().trim().length() > 0 ? spanDisplay1.get(3).text().trim() : "");
                    objAfternoon.put("subjectRoom", spanDisplay1.get(4).text().trim().length() > 0 ? spanDisplay1.get(4).text().trim() : "");
                }
                schedule.put("morning", objMorning);
                schedule.put("afternoon", objAfternoon);
                schedule.put("evening", objEvening);
                data.put(schedule);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void getSchedulesGetMethod() {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                iWeekSchedule.showLoadingDialog();
            }

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
                    getDataScedules(document);

                    schedules = parseWeekData(document);

                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return schedules;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                switch (currentStatus) {
                    case 400:
                        iWeekSchedule.showNetworkErrorLayout();
                        break;
                    case 500:
                        iWeekSchedule.showNetworkErrorLayout();
                        break;
                    default: {
                        currentStatus = 0;
                        iWeekSchedule.setDateToDate(jsonArray);
                        iWeekSchedule.modifyDataOnfirst(jsonArray);
                        iWeekSchedule.showAllComponent();
                        iWeekSchedule.dismissLoadingDialog();
                    }
                }
            }
        };
        asyncTask.execute();
    }

    @Override
    public void getNextSchedulesWeek() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> voidVoidVoidAsyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                iWeekSchedule.showLoadingDialog();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray data = new JSONArray();
                try {
                    JSONObject dataWeek = new JSONObject(storage.getString("data_week"));

                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "LichHocLichThiTuan.aspx")
                            .method(Connection.Method.POST)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .data("__EVENTTARGET", dataWeek.getString("eventTarget"))
                            .data("__EVENTARGUMENT", dataWeek.getString("eventArgument"))
                            .data("__LASTFOCUS", dataWeek.getString("lastFocus"))
                            .data("__VIEWSTATE", dataWeek.getString("viewState"))
                            .data("__VIEWSTATEGENERATOR", dataWeek.getString("viewStartGenerator"))
                            .data("ctl00$ucPhieuKhaoSat1$RadioButtonList1", dataWeek.getString("radioBtnListPhieuKhaoSat"))
                            .data("ctl00$DdListMenu", dataWeek.getString("listMenu"))
                            .data("ctl00$ContentPlaceHolder$rLoaiLich", dataWeek.getString("loaiLich"))
                            .data("ctl00$ContentPlaceHolder$btnSau", dataWeek.getString("tuanSau"))
                            .data("ctl00$ContentPlaceHolder$txtDate", dataWeek.getString("txtDate"))
                            .execute();
                    Document document = res.parse();
                    getDataScedules(document);

                    data = parseWeekData(document);

                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return data;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                switch (currentStatus) {
                    case 400:
                        iWeekSchedule.showNetworkErrorLayout();
                        break;
                    case 500:
                        iWeekSchedule.showNetworkErrorLayout();
                        break;
                    default: {
                        currentStatus = 0;
                        iWeekSchedule.setDateToDate(jsonArray);
                        iWeekSchedule.modifyDataOnfirst(jsonArray);
                        iWeekSchedule.showAllComponent();
                        iWeekSchedule.dismissLoadingDialog();
                    }
                }
            }
        };
        voidVoidVoidAsyncTask.execute();
    }

    @Override
    public void getPrevSchedulesWeek() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> voidVoidVoidAsyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                iWeekSchedule.showLoadingDialog();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray data = new JSONArray();
                try {
                    JSONObject dataWeek = new JSONObject(storage.getString("data_week"));

                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "LichHocLichThiTuan.aspx")
                            .method(Connection.Method.POST)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .data("__EVENTTARGET", dataWeek.getString("eventTarget"))
                            .data("__EVENTARGUMENT", dataWeek.getString("eventArgument"))
                            .data("__LASTFOCUS", dataWeek.getString("lastFocus"))
                            .data("__VIEWSTATE", dataWeek.getString("viewState"))
                            .data("__VIEWSTATEGENERATOR", dataWeek.getString("viewStartGenerator"))
                            .data("ctl00$ucPhieuKhaoSat1$RadioButtonList1", dataWeek.getString("radioBtnListPhieuKhaoSat"))
                            .data("ctl00$DdListMenu", dataWeek.getString("listMenu"))
                            .data("ctl00$ContentPlaceHolder$rLoaiLich", dataWeek.getString("loaiLich"))
                            .data("ctl00$ContentPlaceHolder$btnTruoc", dataWeek.getString("tuanTruoc"))
                            .data("ctl00$ContentPlaceHolder$txtDate", dataWeek.getString("txtDate"))
                            .execute();
                    Document document = res.parse();
                    getDataScedules(document);

                    data = parseWeekData(document);

                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return data;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                switch (currentStatus) {
                    case 400:
                        iWeekSchedule.showNetworkErrorLayout();
                        break;
                    case 500:
                        iWeekSchedule.showNetworkErrorLayout();
                        break;
                    default: {
                        currentStatus = 0;
                        iWeekSchedule.setDateToDate(jsonArray);
                        iWeekSchedule.modifyDataOnfirst(jsonArray);
                        iWeekSchedule.showAllComponent();
                        iWeekSchedule.dismissLoadingDialog();
                    }
                }
            }
        };
        voidVoidVoidAsyncTask.execute();

    }

    @Override
    public void getCurrentSchedulesWeek() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> voidVoidVoidAsyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                iWeekSchedule.showLoadingDialog();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray data = new JSONArray();
                try {
                    JSONObject dataWeek = new JSONObject(storage.getString("data_week"));

                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "LichHocLichThiTuan.aspx")
                            .method(Connection.Method.POST)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .data("__EVENTTARGET", dataWeek.getString("eventTarget"))
                            .data("__EVENTARGUMENT", dataWeek.getString("eventArgument"))
                            .data("__LASTFOCUS", dataWeek.getString("lastFocus"))
                            .data("__VIEWSTATE", dataWeek.getString("viewState"))
                            .data("__VIEWSTATEGENERATOR", dataWeek.getString("viewStartGenerator"))
                            .data("ctl00$ucPhieuKhaoSat1$RadioButtonList1", dataWeek.getString("radioBtnListPhieuKhaoSat"))
                            .data("ctl00$DdListMenu", dataWeek.getString("listMenu"))
                            .data("ctl00$ContentPlaceHolder$rLoaiLich", dataWeek.getString("loaiLich"))
                            .data("ctl00$ContentPlaceHolder$btnHienTai", dataWeek.getString("hienTai"))
                            .data("ctl00$ContentPlaceHolder$txtDate", dataWeek.getString("txtDate"))
                            .execute();
                    Document document = res.parse();
                    getDataScedules(document);

                    data = parseWeekData(document);

                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return data;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                switch (currentStatus) {
                    case 400:
                        iWeekSchedule.showNetworkErrorLayout();
                        break;
                    case 500:
                        iWeekSchedule.showNetworkErrorLayout();
                        break;
                    default: {
                        currentStatus = 0;
                        iWeekSchedule.setDateToDate(jsonArray);
                        iWeekSchedule.modifyDataOnfirst(jsonArray);
                        iWeekSchedule.showAllComponent();
                        iWeekSchedule.dismissLoadingDialog();
                    }
                }
            }
        };
        voidVoidVoidAsyncTask.execute();

    }

    public void getSchedulesByDate(String date) {
        @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, JSONArray> voidVoidVoidAsyncTask = new AsyncTask<String, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                iWeekSchedule.showLoadingDialog();
            }

            @Override
            protected JSONArray doInBackground(String... strings) {
                String date = strings[0];
                JSONArray data = new JSONArray();
                try {
                    JSONObject dataWeek = new JSONObject(storage.getString("data_week"));

                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "LichHocLichThiTuan.aspx")
                            .method(Connection.Method.POST)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .data("__EVENTTARGET", dataWeek.getString("eventTarget"))
                            .data("__EVENTARGUMENT", dataWeek.getString("eventArgument"))
                            .data("__LASTFOCUS", dataWeek.getString("lastFocus"))
                            .data("__VIEWSTATE", dataWeek.getString("viewState"))
                            .data("__VIEWSTATEGENERATOR", dataWeek.getString("viewStartGenerator"))
                            .data("ctl00$ucPhieuKhaoSat1$RadioButtonList1", dataWeek.getString("radioBtnListPhieuKhaoSat"))
                            .data("ctl00$DdListMenu", dataWeek.getString("listMenu"))
                            .data("ctl00$ContentPlaceHolder$rLoaiLich", dataWeek.getString("loaiLich"))
                            .data("ctl00$ContentPlaceHolder$btnNgayChon", dataWeek.getString("ngayChon"))
                            .data("ctl00$ContentPlaceHolder$txtDate", date)
                            .execute();
                    Document document = res.parse();
                    getDataScedules(document);

                    data = parseWeekData(document);

                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return data;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                switch (currentStatus) {
                    case 400:
                        iWeekSchedule.showNetworkErrorLayout();
                        break;
                    case 500:
                        iWeekSchedule.showNetworkErrorLayout();
                        break;
                    default: {
                        currentStatus = 0;
                        iWeekSchedule.setDateToDate(jsonArray);
                        iWeekSchedule.modifyDataOnfirst(jsonArray);
                        iWeekSchedule.showAllComponent();
                        iWeekSchedule.dismissLoadingDialog();
                    }
                }
            }
        };
        voidVoidVoidAsyncTask.execute(date);

    }

}
