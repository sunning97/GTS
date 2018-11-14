package vn.edu.ut.gts.presenters.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
import vn.edu.ut.gts.views.home.fragments.ITestScheduleFragment;
import vn.edu.ut.gts.views.home.fragments.TestScheduleFragment;

public class TestSchedulePresenter implements ITestSchedulePresenter {
    public static int currentStatus = 0;
    private ITestScheduleFragment iTestScheduleFragment;
    private Storage storage;

    public TestSchedulePresenter(ITestScheduleFragment iTestScheduleFragment, Context context) {
        this.iTestScheduleFragment = iTestScheduleFragment;
        this.storage = new Storage(context);
    }

    public void getDataTestSchedule() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONObject> asyncTask = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected void onPreExecute() {
                iTestScheduleFragment.showLoadingDialog();
            }

            @Override
            protected JSONObject doInBackground(Void... voids) {
                JSONObject dataInit = new JSONObject();
                try {
                    Document document = Jsoup.connect(Helper.BASE_URL + "XemLichThi.aspx")
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .get();

                    dataInit.put("__EVENTTARGET", document.select("input[name=\"__EVENTTARGET\"]").val());
                    dataInit.put("__EVENTARGUMENT", document.select("input[name=\"__EVENTARGUMENT\"]").val());
                    dataInit.put("__LASTFOCUS", document.select("input[name=\"__LASTFOCUS\"]").val());
                    dataInit.put("__VIEWSTATE", document.select("input[name=\"__VIEWSTATE\"]").val());
                    dataInit.put("__VIEWSTATEGENERATOR", document.select("input[name=\"__VIEWSTATEGENERATOR\"]").val());
                    dataInit.put("ctl00$ucPhieuKhaoSat1$RadioButtonList1", document.select("input[name=\"ctl00$ucPhieuKhaoSat1$RadioButtonList1\"][checked=\"checked\"]").val());
                    dataInit.put("ctl00$DdListMenu", document.select("select[name=\"ctl00$DdListMenu\"]>option").first().val());
                    dataInit.put("ctl00$ContentPlaceHolder$SearchType", document.select("input[name=\"ctl00$ContentPlaceHolder$SearchType\"][checked=\"checked\"]").val());
                    dataInit.put("ctl00$ContentPlaceHolder$TestType", document.select("input[name=\"ctl00$ContentPlaceHolder$TestType\"][checked=\"checked\"]").val());
                    dataInit.put("ctl00$ContentPlaceHolder$btnSearch", document.select("input[name=\"ctl00$ContentPlaceHolder$btnSearch\"][type=\"submit\"]").val());

                    JSONArray semester = new JSONArray();
                    Element select = document.select("select[name=\"ctl00$ContentPlaceHolder$cboHocKy3\"]").first();

                    for (int i = 1; i < select.select("option").size(); i++) {
                        JSONObject jsonObject = new JSONObject();
                        Element option = select.select("option").get(i);
                        jsonObject.put("name", option.text());
                        jsonObject.put("value", option.attr("value"));
                        semester.put(jsonObject);
                    }
                    dataInit.put("semester", semester);
                    storage.putString("data_test_schedule",dataInit.toString());
                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (IOException | JSONException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                }
                return dataInit;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                switch (TestSchedulePresenter.currentStatus) {
                    case 400: {
                        iTestScheduleFragment.hideAllComponent();
                        iTestScheduleFragment.showNoInternetLayout();
                        iTestScheduleFragment.dismissLoadingDialog();
                        break;
                    }
                    case 500: {
                        iTestScheduleFragment.hideAllComponent();
                        iTestScheduleFragment.showNoInternetLayout();
                        iTestScheduleFragment.dismissLoadingDialog();
                        break;
                    }
                    default: {
                        try {
                            JSONArray semester = jsonObject.getJSONArray("semester");
                            iTestScheduleFragment.setupDataSpiner(semester);
                            getDataTestSchedule(TestScheduleFragment.currentPos);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        asyncTask.execute();
    }

    public void getDataTestSchedule(final int pos) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONObject> asyncTask = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected void onPreExecute() {
                iTestScheduleFragment.showLoadingDialog();
            }

            @Override
            protected JSONObject doInBackground(Void... voids) {
                JSONObject result = new JSONObject();
                try {
                    JSONObject dataTestScheDule = new JSONObject(storage.getString("data_test_schedule"));
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "XemLichThi.aspx")
                            .method(Connection.Method.POST)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .data("__EVENTTARGET", dataTestScheDule.getString("__EVENTTARGET"))
                            .data("__EVENTARGUMENT", dataTestScheDule.getString("__EVENTARGUMENT"))
                            .data("__LASTFOCUS", dataTestScheDule.getString("__LASTFOCUS"))
                            .data("__VIEWSTATE", dataTestScheDule.getString("__VIEWSTATE"))
                            .data("__VIEWSTATEGENERATOR", dataTestScheDule.getString("__VIEWSTATEGENERATOR"))
                            .data("ctl00$ucPhieuKhaoSat1$RadioButtonList1", dataTestScheDule.getString("ctl00$ucPhieuKhaoSat1$RadioButtonList1"))
                            .data("ctl00$DdListMenu", dataTestScheDule.getString("ctl00$DdListMenu"))
                            .data("ctl00$ContentPlaceHolder$cboHocKy3", dataTestScheDule.getJSONArray("semester").getJSONObject(pos).getString("value"))
                            .data("ctl00$ContentPlaceHolder$SearchType", dataTestScheDule.getString("ctl00$ContentPlaceHolder$SearchType"))
                            .data("ctl00$ContentPlaceHolder$TestType", dataTestScheDule.getString("ctl00$ContentPlaceHolder$TestType"))
                            .data("ctl00$ContentPlaceHolder$btnSearch", dataTestScheDule.getString("ctl00$ContentPlaceHolder$SearchType"))
                            .execute();

                    Document document = res.parse();
                    JSONArray data = parseData(document);
                    result.put("data", data);
                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (IOException | JSONException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                switch (TestSchedulePresenter.currentStatus) {
                    case 400: {
                        iTestScheduleFragment.hideAllComponent();
                        iTestScheduleFragment.showNoInternetLayout();
                        iTestScheduleFragment.dismissLoadingDialog();
                        break;
                    }
                    case 500: {
                        iTestScheduleFragment.hideAllComponent();
                        iTestScheduleFragment.showNoInternetLayout();
                        iTestScheduleFragment.dismissLoadingDialog();
                        break;
                    }
                    default: {
                        try {
                            JSONArray data = jsonObject.getJSONArray("data");
                            iTestScheduleFragment.generateTableContent(data);
                            iTestScheduleFragment.hideNoInternetLayout();
                            iTestScheduleFragment.showAllComponent();
                            iTestScheduleFragment.dismissLoadingDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        asyncTask.execute();
    }

    private JSONArray parseData(Document document) {
        JSONArray result = new JSONArray();
        try {
            Element table = document.getElementById("detailTbl");
            Elements trs = table.select("tr");
            Element header = table.selectFirst("tr");
            List<String> dataHeader = new ArrayList<>();
            for (int i = 1; i < header.select("th").size(); i++) {
                Element th = header.select("th").get(i);
                dataHeader.add(Helper.toSlug(th.text()));
            }

            for (int i = 1; i < trs.size(); i++) {
                Elements tds = trs.get(i).select("td");
                JSONObject subject = new JSONObject();
                for (int j = 1; j < tds.size(); j++) {
                    Element td = tds.get(j);
                    subject.put(dataHeader.get(j - 1), td.text());
                }
                result.put(subject);
            }

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
