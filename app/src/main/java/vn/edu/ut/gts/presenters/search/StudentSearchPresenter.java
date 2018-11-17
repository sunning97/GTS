package vn.edu.ut.gts.presenters.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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

import vn.edu.ut.gts.helpers.Curl;
import vn.edu.ut.gts.helpers.Helper;
import vn.edu.ut.gts.helpers.Storage;
import vn.edu.ut.gts.views.search.IStudentSearchActivity;
import vn.edu.ut.gts.views.search.StudentSearchActivity;

public class StudentSearchPresenter implements IStudentSearchPresenter {
    public static int currentStatus = 0;
    private IStudentSearchActivity iStudentSearchActivity;
    private Storage storage;

    public StudentSearchPresenter(IStudentSearchActivity iStudentSearchActivity, Context context) {
        this.iStudentSearchActivity = iStudentSearchActivity;
        storage = new Storage(context);
    }

    public void getDataSearch() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONObject> asyncTask = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected void onPreExecute() {
                iStudentSearchActivity.showLoadingDialog();
            }

            @Override
            protected JSONObject doInBackground(Void... voids) {
                JSONObject result = new JSONObject();
                try {
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "TraCuuThongTin.aspx")
                            .userAgent(Helper.USER_AGENT)
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .execute();

                    Document document = res.parse();

                    result.put("__VIEWSTATE", document.select("input[name=__VIEWSTATE]").val());
                    result.put("__VIEWSTATEGENERATOR", document.select("input[name=__VIEWSTATEGENERATOR]").val());
                    result.put("ctl00$ContentPlaceHolder$btnTraCuuThongTin", document.select("input[name=ctl00$ContentPlaceHolder$btnTraCuuThongTin]").val());
                    result.put("cookie", res.cookie("ASP.NET_SessionId"));
                    result.put("ctl00$ContentPlaceHolder$txtSercurityCode1", createConfirmImage(res.cookie("ASP.NET_SessionId")));
                    currentStatus = 0;
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    currentStatus = Helper.TIMEOUT;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    currentStatus = Helper.NO_CONNECTION;
                } catch (NullPointerException | IndexOutOfBoundsException | IOException | JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                switch (currentStatus) {
                    case 400: {
                        iStudentSearchActivity.showNoInternetDialog();
                        iStudentSearchActivity.searchToRetryBtn();
                        break;
                    }
                    case 500: {
                        iStudentSearchActivity.showTimeoutDialog();
                        iStudentSearchActivity.searchToRetryBtn();
                        break;
                    }
                    default: {
                        storage.putString("search_data", jsonObject.toString());
                        iStudentSearchActivity.retryToSearchBtn();
                        iStudentSearchActivity.dismissLoadingDialog();
                    }
                }
            }
        };
        asyncTask.execute();
    }

    private String createConfirmImage(String cookie) {
        String result = null;
        String res = Curl.connect("https://sv.ut.edu.vn/ajaxpro/AjaxConfirmImage,PMT.Web.PhongDaoTao.ashx")
                .method("POST")
                .setCookie("ASP.NET_SessionId", cookie)
                .header("X-AjaxPro-Method", "CreateConfirmImage")
                .dataString("{}")
                .execute();
        res = res.replace(";/*", "");
        JSONArray ar = null;
        try {
            ar = new JSONArray(res);
            result = Helper.decryptMd5(ar.getString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void searchStudent(final Bundle bundle) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, ArrayList<JSONObject>> asyncTask = new AsyncTask<Void, Void, ArrayList<JSONObject>>() {
            @Override
            protected ArrayList<JSONObject> doInBackground(Void... voids) {
                ArrayList<JSONObject> students = new ArrayList<>();
                try {
                    JSONObject dataSearch = new JSONObject(storage.getString("search_data"));
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "TraCuuThongTin.aspx")
                            .method(Connection.Method.POST)
                            .userAgent(Helper.USER_AGENT)
                            .data("__VIEWSTATE", dataSearch.getString("__VIEWSTATE"))
                            .data("__VIEWSTATEGENERATOR", dataSearch.getString("__VIEWSTATEGENERATOR"))
                            .data("ctl00$ContentPlaceHolder$txtSercurityCode1", dataSearch.getString("ctl00$ContentPlaceHolder$txtSercurityCode1"))
                            .data("ctl00$ContentPlaceHolder$btnTraCuuThongTin", dataSearch.getString("ctl00$ContentPlaceHolder$btnTraCuuThongTin"))
                            .data("ctl00$ContentPlaceHolder$txtMaSoSV", bundle.getString("student_id"))
                            .data("ctl00$ContentPlaceHolder$txtMaLop", bundle.getString("class_name"))
                            .data("ctl00$ContentPlaceHolder$txtHoDem", bundle.getString("first_name"))
                            .data("ctl00$ContentPlaceHolder$txtHoTen", bundle.getString("last_name"))
                            .data("ctl00$ContentPlaceHolder$objNgaySinh", bundle.getString("birthday"))
                            .cookie("ASP.NET_SessionId", dataSearch.getString("cookie"))
                            .timeout(Helper.TIMEOUT_VALUE).execute();

                    Document document = res.parse();
                    Elements trs = document.select("#TblDanhSachSinhVien tr");
                    if (!trs.get(1).text().equals("Không tìm thấy dữ liệu!")) {
                        for (int i = 1; i < trs.size(); i++) {
                            JSONObject student = new JSONObject();
                            Elements tds = trs.get(i).select("td");
                            if (tds.size() >= 8) {
                                student.put("studentCode", tds.get(1).text().trim());
                                student.put("studentName", tds.get(2).text().trim());
                                student.put("birthday", tds.get(3).text().trim());
                                student.put("urlViewMark", tds.get(4).selectFirst("a").attr("href"));
                                student.put("urlViewDebt", tds.get(5).selectFirst("a").attr("href"));
                            }
                            students.add(student);
                        }
                    }
                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (NullPointerException | IndexOutOfBoundsException | JSONException | IOException e) {
                    e.printStackTrace();
                }
                return students;
            }

            @Override
            protected void onPostExecute(ArrayList<JSONObject> jsonObjects) {
                if (currentStatus == Helper.NO_CONNECTION || currentStatus == Helper.TIMEOUT) {
                    iStudentSearchActivity.loadToNoInternetLayout(StudentSearchActivity.SEARCH_LAYOUT);
                } else {
                    if (jsonObjects.size() != 0) {
                        iStudentSearchActivity.generateTableSearchResult(jsonObjects);
                        iStudentSearchActivity.loadToResultLayout(false);
                    } else {
                        iStudentSearchActivity.showNoResultLayout();
                        iStudentSearchActivity.loadToResultLayout(true);
                    }
                }
            }
        };
        asyncTask.execute();
    }

}
