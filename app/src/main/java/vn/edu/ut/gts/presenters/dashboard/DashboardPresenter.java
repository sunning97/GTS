package vn.edu.ut.gts.presenters.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
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

import vn.edu.ut.gts.helpers.Helper;
import vn.edu.ut.gts.helpers.Storage;
import vn.edu.ut.gts.views.dashboard.IDashboardActivity;
import vn.edu.ut.gts.views.home.HomeActivity;

public class DashboardPresenter implements IDashboardPresenter {
    public static boolean isFirst = false;
    public static int currentStatus = 0;
    private IDashboardActivity iDashboardActivity;
    private Storage storage;
    private Context context;

    public DashboardPresenter(IDashboardActivity iDashboardActivity, Context context) {
        this.iDashboardActivity = iDashboardActivity;
        this.context = context;
        this.storage = new Storage(this.context);
    }

    @Override
    public void go() {
        if (HomeActivity.isLogin) {
            @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONObject> asyncTask = new AsyncTask<Void, Void, JSONObject>() {
                @Override
                protected void onPreExecute() {
                    iDashboardActivity.resetLoaderImage();
                    iDashboardActivity.resetLoaderTextView();
                    iDashboardActivity.showLoaderTextView();
                    iDashboardActivity.disableAll();
                    iDashboardActivity.disableSwipeRefresh();
                }

                @Override
                protected JSONObject doInBackground(Void... voids) {
                    JSONObject info = null;
                    try {
                        String studentID = storage.getString("last_student_login");
                        Connection.Response resultImageResponse;
                        resultImageResponse = Jsoup.connect(Helper.BASE_URL + "GetImage.aspx?MSSV=" + studentID)
                                .userAgent(Helper.USER_AGENT)
                                .timeout(Helper.TIMEOUT_VALUE)
                                .method(Connection.Method.GET)
                                .cookie("ASP.NET_Session_Id", storage.getCookie())
                                .ignoreContentType(true)
                                .execute();

                        storage.saveImage(resultImageResponse, context,"student_portrait.jpg");

                        Document document = Jsoup.connect(Helper.BASE_URL + "HoSoSinhVien.aspx")
                                .method(Connection.Method.GET)
                                .timeout(Helper.TIMEOUT_VALUE)
                                .userAgent(Helper.USER_AGENT)
                                .cookie("ASP.NET_SessionId", storage.getCookie())
                                .get();

                        info = parseData(document);
                    } catch (SocketTimeoutException e) {
                        DashboardPresenter.currentStatus = Helper.TIMEOUT;
                    } catch (UnknownHostException e) {
                        DashboardPresenter.currentStatus = Helper.NO_CONNECTION;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return info;
                }

                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    switch (DashboardPresenter.currentStatus) {
                        case 400: {
                            if(!DashboardPresenter.isFirst)
                                iDashboardActivity.showErrorDialog();
                            iDashboardActivity.disableAll();
                            iDashboardActivity.setDefaultPortrait();
                            iDashboardActivity.setDefaultNavigationImage();
                            iDashboardActivity.hideLoaderTextView();
                            iDashboardActivity.setRefreshingSwipe(false);
                            iDashboardActivity.enableSwipeRefresh();
                            break;
                        }
                        case 500: {
                            if(!DashboardPresenter.isFirst)
                                iDashboardActivity.showTimeOutDialog();
                            iDashboardActivity.disableAll();
                            iDashboardActivity.setDefaultPortrait();
                            iDashboardActivity.setDefaultNavigationImage();
                            iDashboardActivity.hideLoaderTextView();
                            iDashboardActivity.setRefreshingSwipe(false);
                            iDashboardActivity.enableSwipeRefresh();
                            break;
                        }
                        default: {
                            storage.putString("student_info", jsonObject.toString());
                            iDashboardActivity.hideLoaderTextView();
                            Bitmap image = storage.getImageFromStorage(context,"student_portrait.jpg");
                            String studentName = null;
                            String studentID = null;
                            try {
                                studentID = storage.getString("last_student_login");
                                studentName = jsonObject.getString("student_name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            iDashboardActivity.setToolbarTitle(studentName);
                            iDashboardActivity.setStudentPortrait(image);
                            iDashboardActivity.setUpNavigationData(image,studentName,studentID);
                            iDashboardActivity.enableAll();
                            iDashboardActivity.disableSwipeRefresh();
                        }
                    }
                }
            };
            asyncTask.execute();
        }
    }

    public Bitmap getStudentPortraitFromStorage() {
        Bitmap image = storage.getImageFromStorage(context,"student_portrait.jpg");
        return image;
    }

    public String getStudentNameFromStorage() {
        JSONObject jsonObject = null;
        String studentName = null;
        try {
            jsonObject = new JSONObject(storage.getString("student_info"));
            studentName = jsonObject.getString("student_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return studentName;
    }

    public String getStudentIDFromStorage(){
        return storage.getString("last_student_login");
    }

    private JSONObject parseData(Document document) {
        JSONObject info = new JSONObject();
        try {
            Element span = document.getElementById("ctl00_ucRight1_Span2");
            info.put("student_name", span.text());
            Element bodyGroup = document.getElementsByClass("body-group").first();
            Elements tds = bodyGroup.select("td");
            JSONArray studentInfo = new JSONArray();
            for (Element td : tds) {
                JSONObject prop = new JSONObject();
                String key = Helper.toSlug(td.text().split(":")[0]);
                String value = td.text().split(":").length > 1 ? td.text().split(":")[1].trim() : "Không";

                prop.put("key", key);

                prop.put("value", value);
                studentInfo.put(prop);
            }
            info.put("studentInfo", studentInfo);
            Element bodyGroup1 = document.getElementsByClass("body-group").get(1);
            Elements tds1 = bodyGroup1.select("td");
            JSONArray studentDetail = new JSONArray();
            for (Element td : tds1) {
                JSONObject prop = new JSONObject();
                String key = Helper.toSlug(td.text().split(":")[0]);
                String value = td.text().split(":").length > 1 ? td.text().split(":")[1].trim() : "Không";
                prop.put("key", key);
                prop.put("value", value);
                studentDetail.put(prop);
            }
            info.put("studentDetail", studentDetail);

            Element bodyGroup2 = document.getElementsByClass("body-group").get(2);
            Elements tds2 = bodyGroup2.select("td");
            JSONArray studentFamily = new JSONArray();
            for (Element td : tds2) {
                JSONObject prop = new JSONObject();
                String key = Helper.toSlug(td.text().split(":")[0]);
                String value = td.text().split(":").length > 1 ? td.text().split(":")[1].trim() : "Không";
                prop.put("key", key);
                prop.put("value", value);
                studentFamily.put(prop);
            }
            info.put("studentFamily", studentFamily);
        } catch (NullPointerException | IndexOutOfBoundsException | JSONException e) {
            e.printStackTrace();
        }
        return info;
    }
}
