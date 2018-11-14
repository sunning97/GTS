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

    public void getStudentDetail(final JSONObject jsonObject) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray result = new JSONArray();
                try {
                    JSONObject dataSearch = new JSONObject(storage.getString("search_data"));
                    Document document = null;
                    Connection.Response res = null;
                    res = Jsoup.connect(Helper.BASE_URL + jsonObject.getString("urlViewMark"))
                            .userAgent(Helper.USER_AGENT)
                            .method(Connection.Method.GET)
                            .cookie("ASP.NET_SessionId", dataSearch.getString("cookie"))
                            .timeout(Helper.TIMEOUT_VALUE)
                            .execute();

                    document = res.parse();
                    JSONObject info = parseInfo(document, jsonObject.getString("studentName"));
                    JSONObject studyResult = parseStudyResult(document);
                    result.put(info);
                    result.put(studyResult);


                    res = Jsoup.connect(Helper.BASE_URL + jsonObject.getString("urlViewDebt"))
                            .method(Connection.Method.GET)
                            .userAgent(Helper.USER_AGENT)
                            .execute();
                    document = res.parse();
                    JSONObject dataSearchDebt = new JSONObject();
                    dataSearchDebt.put("cookie", dataSearch.getString("cookie"));
                    dataSearchDebt.put("eventTarget", document.select("input[name=\"__EVENTTARGET\"]").val());
                    dataSearchDebt.put("eventArgument", document.select("input[name=\"__EVENTARGUMENT\"]").val());
                    dataSearchDebt.put("lastFocus", document.select("input[name=\"__LASTFOCUS\"]").val());
                    dataSearchDebt.put("viewState", document.select("input[name=\"__VIEWSTATE\"]").val());
                    dataSearchDebt.put("viewStartGenerator", document.select("input[name=\"__VIEWSTATEGENERATOR\"]").val());
                    dataSearchDebt.put("radioBtnList", document.select("input[name=\"ctl00$ucPhieuKhaoSat1$RadioButtonList1\"][checked=\"checked\"]").val());
                    dataSearchDebt.put("listMenu", document.select("select[name=\"ctl00$DdListMenu\"]>option").first().val());
                    JSONArray semesters = new JSONArray();
                    Elements options = document.select("select[name=\"ctl00$ContentPlaceHolder$cboHocKy\"]>option");
                    for (Element option : options) {
                        if (option.val().equals("-1")) continue;
                        JSONObject tmp = new JSONObject();
                        tmp.put("key", option.val());
                        tmp.put("text", option.text().trim());
                        semesters.put(tmp);
                    }

                    dataSearchDebt.put("semesters", semesters);
                    storage.putString("data_searchdebt", dataSearchDebt.toString());

                    JSONArray data = new JSONArray();
                    Element table = document.getElementById("tblDetail");
                    JSONArray keys = new JSONArray();
                    Elements ths = table.select("tr").first().select("th");
                    for (int i = 1; i < ths.size(); i++) {
                        String keyTmp = Helper.toSlug(ths.get(i).text());
                        keys.put(keyTmp);
                    }
                    Elements trs = table.select("tr");
                    for (int i = 1; i < trs.size() - 1; i++) {
                        JSONObject tmp = new JSONObject();
                        Elements tds = trs.get(i).select("td");
                        for (int j = 1; j < tds.size(); j++) {
                            tmp.put(keys.getString(j - 1), tds.get(j).text().trim());
                        }
                        data.put(tmp);
                    }
                    dataSearchDebt.put("init_semester", data.toString());
                    dataSearchDebt.put("urlViewDebt", jsonObject.getString("urlViewDebt"));
                    result.put(dataSearchDebt);

                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException | NullPointerException | IOException | JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                if (currentStatus == Helper.NO_CONNECTION || currentStatus == Helper.TIMEOUT) {
                    iStudentSearchActivity.loadToNoInternetLayout(StudentSearchActivity.RESULT_LAYOUT);
                } else {
                    try {
                        JSONObject info = jsonArray.getJSONObject(0);
                        iStudentSearchActivity.setStudentDetailData(jsonArray);
                        iStudentSearchActivity.loadToDetailLayout(info.getString("student_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        asyncTask.execute();
    }

    private JSONObject parseInfo(Document document, String name) {
        JSONObject info = new JSONObject();
        try {
            info.put("student_name", name);
            Elements tables = document.getElementsByTag("table");
            Element infoTable = tables.get(7);
            Elements tds = infoTable.select("td");
            JSONArray studentInfo = new JSONArray();
            for (Element td : tds) {
                JSONObject prop = new JSONObject();
                String key = Helper.toSlug(td.text().split(":")[0]);
                String value = td.text().split(":").length > 1 ? td.text().split(":")[1].trim() : "Không";
                prop.put("key", key);
                prop.put("value", value);
                studentInfo.put(prop);
                info.put("studentInfo", studentInfo);
            }
            Element learnTable = tables.get(8);
            Elements tds1 = learnTable.select("td");
            JSONArray studentDetail = new JSONArray();
            for (Element td : tds1) {
                JSONObject prop = new JSONObject();
                String key = Helper.toSlug(td.text().split(":")[0]);
                String value = td.text().split(":").length > 1 ? td.text().split(":")[1].trim() : "Không";
                prop.put("key", key);
                prop.put("value", value);
                studentDetail.put(prop);
            }
            info.put("learnDetail", studentDetail);
        } catch (NullPointerException | IndexOutOfBoundsException | JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    private JSONObject parseStudyResult(Document document) {
        JSONObject result = new JSONObject();
        JSONArray allQuater = new JSONArray();
        try {
            Elements tableResult = document.select("table.grid.grid-color2.tblKetQuaHocTap");
            Element table1 = tableResult.get(0);

            result.put("tong_tin_chi", tableResult.get(1).getElementById("ctl00_ContentPlaceHolder_ucThongTinTotNghiepTinChi1_lblTongTinChi").text());
            result.put("trung_binh_tich_luy", tableResult.get(1).getElementById("ctl00_ContentPlaceHolder_ucThongTinTotNghiepTinChi1_lblTBCTL").text());
            result.put("ti_le_no", tableResult.get(1).getElementById("ctl00_ContentPlaceHolder_ucThongTinTotNghiepTinChi1_lblSoTCNo").text());


            //header
            Elements trs = table1.select("tr");
            Elements headerTh = table1.select("tr").first().select("th");
            List<String> strings = new ArrayList<>();
            for (int i = 1; i < headerTh.size(); i++) {
                strings.add(Helper.toSlug(headerTh.get(i).text()));
            }

            List<Integer> indexs = new ArrayList<>();
            List<String> quaterText = new ArrayList<>();

            for (int i = 0; i < trs.size(); i++) {
                if (trs.get(i).hasClass("quater")) {
                    indexs.add(i);
                    quaterText.add(trs.get(i).text());
                }
            }

            for (int i = 0; i <= indexs.size() - 1; i++) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("quater", quaterText.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray quater = new JSONArray();

                if (i == indexs.size() - 1) {
                    for (int j = indexs.get(i) + 1; j < trs.size(); j++) {
                        Element tr = trs.get(j);
                        if (!TextUtils.isEmpty(tr.text())) {
                            JSONObject subject = new JSONObject();
                            Elements tds = tr.getElementsByTag("td");
                            subject.put("courseCode", tds.get(1).text());
                            subject.put("courseName", tds.get(2).text());
                            subject.put("courseClass", tds.get(3).text());
                            subject.put("courseCredits", tds.get(4).text());

                            if (tds.size() == 14) {
                                subject.put("processScore", 0);
                                subject.put("testScores", tds.get(7).text());
                                subject.put("scoresOf10", tds.get(9).text());
                                subject.put("scoresOf4", tds.get(10).text());
                                subject.put("scoresString", tds.get(11).text());
                                subject.put("classification", tds.get(12).text());
                                subject.put("note", tds.get(13).text());
                            } else {
                                subject.put("processScore", tds.get(6).text());
                                subject.put("testScores", tds.get(10).text());
                                subject.put("scoresOf10", tds.get(12).text());
                                subject.put("scoresOf4", tds.get(13).text());
                                subject.put("scoresString", tds.get(14).text());
                                subject.put("classification", tds.get(15).text());
                                subject.put("note", tds.get(16).text());
                            }
                            quater.put(subject);
                        }
                        try {
                            jsonObject.put("subjects", quater);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (int j = indexs.get(i) + 1; j < indexs.get((i + 1)); j++) {
                        Element tr = trs.get(j);
                        if (!TextUtils.isEmpty(tr.text())) {
                            JSONObject subject = new JSONObject();
                            Elements tds = tr.getElementsByTag("td");
                            subject.put("courseCode", tds.get(1).text());
                            subject.put("courseName", tds.get(2).text());
                            subject.put("courseClass", tds.get(3).text());
                            subject.put("courseCredits", tds.get(4).text());

                            if (tds.size() == 14) {
                                subject.put("processScore", 0);
                                subject.put("testScores", tds.get(7).text());
                                subject.put("scoresOf10", tds.get(9).text());
                                subject.put("scoresOf4", tds.get(10).text());
                                subject.put("scoresString", tds.get(11).text());
                                subject.put("classification", tds.get(12).text());
                                subject.put("note", tds.get(13).text());
                            } else {
                                subject.put("processScore", tds.get(6).text());
                                subject.put("testScores", tds.get(10).text());
                                subject.put("scoresOf10", tds.get(12).text());
                                subject.put("scoresOf4", tds.get(13).text());
                                subject.put("scoresString", tds.get(14).text());
                                subject.put("classification", tds.get(15).text());
                                subject.put("note", tds.get(16).text());
                            }
                            quater.put(subject);
                        }
                        try {
                            jsonObject.put("subjects", quater);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                allQuater.put(jsonObject);
            }
            result.put("all_semester", allQuater);
        } catch (IndexOutOfBoundsException | NullPointerException | JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
