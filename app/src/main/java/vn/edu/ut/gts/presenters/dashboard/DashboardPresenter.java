package vn.edu.ut.gts.presenters.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
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

import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.presenters.login.LoginProcess;
import vn.edu.ut.gts.views.dashboard.DashboardActivity;
import vn.edu.ut.gts.views.dashboard.IDashboardActivity;
import vn.edu.ut.gts.views.home.HomeActivity;

public class DashboardPresenter implements IDashboardPresenter {
    public static int TIMEOUT = 1;
    public static int NO_INTERNET = 2;
    public static int currentStatus = 0;

    private IDashboardActivity iDashboardActivity;
    private Storage storage;
    private Context context;

    public DashboardPresenter(IDashboardActivity iDashboardActivity, Context context) {
        this.iDashboardActivity = iDashboardActivity;
        this.context = context;
        this.storage = new Storage(this.context);
    }

    public void go() {
        if (HomeActivity.isLogin) {
            @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONObject> asyncTask = new AsyncTask<Void, Void, JSONObject>() {
                @Override
                protected void onPreExecute() {
                    iDashboardActivity.resetLoaderImage();
                    iDashboardActivity.disableAll();
//                    iDashboardActivity.showLoadingDialog();
                }

                @Override
                protected JSONObject doInBackground(Void... voids) {
                    JSONObject info = null;
                    try {
                        String studentID = storage.getString("last_student_login");
                        Connection.Response resultImageResponse;
                        resultImageResponse = Jsoup.connect(Helper.BASE_URL + "GetImage.aspx?MSSV=" + studentID)
                                .userAgent(Helper.USER_AGENT)
                                .method(Connection.Method.GET)
                                .cookie("ASP.NET_Session_Id", storage.getCookie())
                                .ignoreContentType(true)
                                .timeout(10000)
                                .execute();

                        storage.saveImage(resultImageResponse, context);

                        Document document = Jsoup.connect(Helper.BASE_URL + "HoSoSinhVien.aspx")
                                .method(Connection.Method.GET)
                                .userAgent(Helper.USER_AGENT)
                                .cookie("ASP.NET_SessionId", storage.getCookie())
                                .timeout(10000)
                                .get();
                        info = parseData(document);
//                        Element bodyGroup = document.getElementsByClass("body-group").first();
//                        Elements tds = bodyGroup.select("td");
//                        JSONArray studentInfo = new JSONArray();
//                        for (Element td : tds) {
//                            JSONObject prop = new JSONObject();
//                            String key = Helper.toSlug(td.text().split(":")[0]);
//                            String value = td.text().split(":").length > 1 ? td.text().split(":")[1].trim() : "Không";
//                            prop.put("key", key);
//                            prop.put("value", value);
//                            studentInfo.put(prop);
//                        }
//                        info.put("studentInfo", studentInfo);
//                        Element bodyGroup1 = document.getElementsByClass("body-group").get(1);
//                        Elements tds1 = bodyGroup1.select("td");
//                        JSONArray studentDetail = new JSONArray();
//                        for (Element td : tds1) {
//                            JSONObject prop = new JSONObject();
//                            String key = Helper.toSlug(td.text().split(":")[0]);
//                            String value = td.text().split(":").length > 1 ? td.text().split(":")[1].trim() : "Không";
//                            prop.put("key", key);
//                            prop.put("value", value);
//                            studentDetail.put(prop);
//                        }
//                        info.put("studentDetail", studentDetail);
//
//                        Element bodyGroup2 = document.getElementsByClass("body-group").get(2);
//                        Elements tds2 = bodyGroup2.select("td");
//                        JSONArray studentFamily = new JSONArray();
//                        for (Element td : tds2) {
//                            JSONObject prop = new JSONObject();
//                            String key = Helper.toSlug(td.text().split(":")[0]);
//                            String value = td.text().split(":").length > 1 ? td.text().split(":")[1].trim() : "Không";
//                            prop.put("key", key);
//                            prop.put("value", value);
//                            studentFamily.put(prop);
//                        }
//                        info.put("studentFamily", studentFamily);
                    } catch (SocketTimeoutException e) {
                        DashboardPresenter.currentStatus = DashboardPresenter.TIMEOUT;
                    } catch (UnknownHostException e) {
                        currentStatus = DashboardPresenter.NO_INTERNET;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return info;
                }

                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    switch (DashboardPresenter.currentStatus) {
                        case 2: {
                            iDashboardActivity.disableAll();
                            iDashboardActivity.showErrorDialog();
                            break;
                        }
                        case 1: {
                            iDashboardActivity.disableAll();
                            iDashboardActivity.showTimeOutDialog();
                            break;
                        }
                        default: {
                            storage.putString("student_info", jsonObject.toString());
                            Bitmap image = storage.getImageFromStorage(context);
                            String studentName = storage.getString("student_name");
                            iDashboardActivity.setToolbarTitle(studentName);
                            iDashboardActivity.setStudentPortrait(image);
                            iDashboardActivity.enableAll();
                            iDashboardActivity.disableSwipeRefresh();
                        }
                    }

//                iDashboardActivity.dismisLoadingDialog();
                }
            };
            asyncTask.execute();
        }
    }

    @Override
    public JSONObject getStudentInfoData() {
        JSONObject info = new JSONObject();
        try {
            Document document = Jsoup.connect(Helper.BASE_URL + "HoSoSinhVien.aspx")
                    .method(Connection.Method.GET)
                    .userAgent(Helper.USER_AGENT)
                    .cookie("ASP.NET_SessionId", storage.getCookie())
                    .get();
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    @Override
    public void getStudentPortrait() {
        try {
            String studentID = storage.getString("last_student_login");
            Connection.Response resultImageResponse;
            resultImageResponse = Jsoup.connect(Helper.BASE_URL + "GetImage.aspx?MSSV=" + studentID)
                    .userAgent(Helper.USER_AGENT)
                    .method(Connection.Method.GET)
                    .cookie("ASP.NET_Session_Id", storage.getCookie())
                    .ignoreContentType(true)
                    .timeout(500)
                    .execute();

            storage.saveImage(resultImageResponse, context);
            Log.d("AAA", "save image");
        } catch (SocketTimeoutException e) {
            Log.d("AAA", e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getStudentPortraitFromStorage() {
        Bitmap image = storage.getImageFromStorage(context);
        return image;
    }

    public String getStudentNameFromStorate() {
        String studentName = storage.getString("student_name");
        return studentName;
    }

    private JSONObject parseData(Document document) {
        JSONObject info = new JSONObject();
        try {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  info;
    }
}
