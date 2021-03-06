package vn.edu.ut.gts.presenters.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import vn.edu.ut.gts.helpers.Helper;
import vn.edu.ut.gts.helpers.Storage;
import vn.edu.ut.gts.views.home.fragments.AttendanceFragment;
import vn.edu.ut.gts.views.home.fragments.IAttendanceFragment;

public class AttendanceFragmentPresenter implements IAttendanceFragmentPresenter {
    public static int currentStatus = 0;
    private IAttendanceFragment iAttendanceFragment;
    private Storage storage;

    public AttendanceFragmentPresenter(IAttendanceFragment iAttendanceFragment, Context context) {
        this.iAttendanceFragment = iAttendanceFragment;
        this.storage = new Storage(context);
    }

    @Override
    public void getDataAttendanceSpinner() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                iAttendanceFragment.showLoadingDialog();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONObject data = new JSONObject();
                JSONArray semesters = new JSONArray();
                try {
                    Document document = Jsoup.connect(Helper.BASE_URL + "ThongTinDiemDanh.aspx")
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .get();
                    data.put("eventTarget", document.select("input[name=\"__EVENTTARGET\"]").val());
                    data.put("eventArgument", document.select("input[name=\"__EVENTARGUMENT\"]").val());
                    data.put("lastFocus", document.select("input[name=\"__LASTFOCUS\"]").val());
                    data.put("viewState", document.select("input[name=\"__VIEWSTATE\"]").val());
                    data.put("viewStartGenerator", document.select("input[name=\"__VIEWSTATEGENERATOR\"]").val());
                    data.put("radioBtnList", document.select("input[name=\"ctl00$ucPhieuKhaoSat1$RadioButtonList1\"][checked=\"checked\"]").val());
                    data.put("listMenu", document.select("select[name=\"ctl00$DdListMenu\"]>option").first().val());
                    data.put("ctl00$ContentPlaceHolder$btnLoc", document.select("input[name=\"ctl00$ContentPlaceHolder$btnLoc\"][type=\"submit\"]").val());

                    /* Crawl data from html */
                    Elements options = document.select("select[name=\"ctl00$ContentPlaceHolder$cboHocKy\"]>option");
                    for (Element option : options) {
                        JSONObject tmp = new JSONObject();
                        if (option.val().equals("-1")) continue;
                        tmp.put("key", option.val());
                        tmp.put("text", option.text().trim());
                        semesters.put(tmp);
                    }
                    data.put("semesters", semesters);
                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                /* store in sharedpreference*/
                storage.putString("dataAttendance", data.toString());
                return semesters;
            }

            @Override
            protected void onPostExecute(JSONArray semesters) {
                switch (currentStatus) {
                    case 400: /* if no connection*/
                        iAttendanceFragment.showNetworkErrorLayout();
                        break;
                    case 500: /* if connect timeout*/
                        iAttendanceFragment.showNetworkErrorLayout();
                        break;
                    default: { /* connect success*/
                        currentStatus = 0;
                        List<String> dataSnpinner = new ArrayList<>();
                        try {
                            for (int i = 0; i < semesters.length(); i++) {
                                JSONObject jsonObject = (JSONObject) semesters.get(i);
                                dataSnpinner.add(jsonObject.getString("text"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        iAttendanceFragment.initAttendanceSpiner(dataSnpinner);
                        getDataAttendance(AttendanceFragment.currentPos);
                    }
                }
            }
        };
        asyncTask.execute();
    }

    @Override
    public void getDataAttendance(final int pos) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                iAttendanceFragment.showLoadingDialog();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray data = new JSONArray();
                try {
                    JSONObject dataDiemDanh = new JSONObject(storage.getString("dataAttendance"));
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "ThongTinDiemDanh.aspx")
                        .method(Connection.Method.POST)
                        .timeout(Helper.TIMEOUT_VALUE)
                        .userAgent(Helper.USER_AGENT)
                        .cookie("ASP.NET_SessionId", storage.getCookie())
                        .data("__EVENTTARGET", dataDiemDanh.getString("eventTarget"))
                        .data("__EVENTARGUMENT", dataDiemDanh.getString("eventArgument"))
                        .data("__LASTFOCUS", dataDiemDanh.getString("lastFocus"))
                        .data("__VIEWSTATE", dataDiemDanh.getString("viewState"))
                        .data("__VIEWSTATEGENERATOR", dataDiemDanh.getString("viewStartGenerator"))
                        .data("ctl00$ucPhieuKhaoSat1$RadioButtonList1", dataDiemDanh.getString("radioBtnList"))
                        .data("ctl00$DdListMenu", dataDiemDanh.getString("eventTarget"))
                        .data("ctl00$ContentPlaceHolder$cboHocKy", dataDiemDanh.getJSONArray("semesters").getJSONObject(pos).getString("key"))
                        .data("ctl00$ContentPlaceHolder$btnLoc", dataDiemDanh.getString("ctl00$ContentPlaceHolder$btnLoc"))
                        .execute();

                    /* crawl data from html*/
                    Document document = res.parse();
                    Elements table = document.getElementsByClass("grid-color2");
                    Elements trs = table.get(0).select("tr");
                    Elements ths = trs.get(0).select("th");
                    JSONArray keys = new JSONArray();
                    for (int i = 1; i < ths.size(); i++) {
                        String keyTmp = Helper.toSlug(ths.get(i).text().trim());
                        keys.put(keyTmp);
                    }
                    for (int i = 2; i < trs.size() - 2; i++) {
                        Elements tds = trs.get(i).select("td");
                        JSONObject subject = new JSONObject();
                        for (int j = 1; j < tds.size(); j++) {
                            String tmp = tds.get(j).text().trim();
                            subject.put(keys.getString(j - 1), tmp);
                        }
                        data.put(subject);
                    }
                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    e.printStackTrace();
                } catch (IOException | JSONException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                }
                return data;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                switch (currentStatus) {
                    case 400: /* if no connection*/
                        iAttendanceFragment.showNetworkErrorLayout();
                        break;
                    case 500: /* if connect timeout*/
                        iAttendanceFragment.showNetworkErrorLayout();
                        break;
                    default: { /* connect success*/
                        currentStatus = 0;
                        iAttendanceFragment.generateTableContent(jsonArray);
                        iAttendanceFragment.showLoadedLayout();
                        iAttendanceFragment.dismissLoadingDialog();
                    }
                }
            }
        };
        asyncTask.execute();
    }
}
