package vn.edu.ut.gts.presenters.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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

import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.views.search.IStudentDetailActivity;

public class StudentDetailActivityPresenter {
    public static int currentStatus = 0;
    private IStudentDetailActivity iStudentDetailActivity;
    private Context context;
    private Storage storage;

    public StudentDetailActivityPresenter(IStudentDetailActivity iStudentDetailActivity, Context context) {
        this.iStudentDetailActivity = iStudentDetailActivity;
        this.context = context;
        this.storage = new Storage(this.context);
    }

    public void getStudentPortrait() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                iStudentDetailActivity.showLoadingDialog();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    JSONObject dataSearch = new JSONObject(storage.getString("search_data"));
                    String studentID = storage.getString("search_student_id");
                    Connection.Response resultImageResponse;
                    resultImageResponse = Jsoup.connect(Helper.BASE_URL + "GetImage.aspx?MSSV=" + studentID)
                            .userAgent(Helper.USER_AGENT)
                            .method(Connection.Method.GET)
                            .cookie("ASP.NET_Session_Id", dataSearch.getString("cookie"))
                            .ignoreContentType(true)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .execute();

                    storage.saveImage(resultImageResponse, context,"search_student_portrait.jpg");
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (NullPointerException | IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                iStudentDetailActivity.showStudentPortraitDialog(storage.getString("search_student_id"));
            }
        };
        asyncTask.execute();
    }

    public Bitmap getStudentPortraitFromStorage(){
        Bitmap image = storage.getImageFromStorage(context,"search_student_portrait.jpg");
        return image;
    }

    public void getStudentDetail(final JSONObject jsonObject) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                iStudentDetailActivity.showLoadingDialog();
                iStudentDetailActivity.hideAllComponent();
            }

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
                } catch (NullPointerException | IOException | JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                if (StudentDetailActivityPresenter.currentStatus == Helper.NO_CONNECTION || StudentDetailActivityPresenter.currentStatus == Helper.TIMEOUT) {
                    iStudentDetailActivity.showNoInternetLayout();
                    iStudentDetailActivity.hideLoadingDialog();
                } else {
                    iStudentDetailActivity.setStudentDetailData(jsonArray);
                    iStudentDetailActivity.showAllComponent();
                    iStudentDetailActivity.hideNoInternetLayout();
                    iStudentDetailActivity.hideLoadingDialog();
                }
            }
        };
        asyncTask.execute();
    }

    private JSONObject parseInfo(Document document, String name) {
        JSONObject info = new JSONObject();
        try {
            String[] mssv = document.getElementsByClass("ma-sinhvien").first().text().split(":");
            storage.putString("search_student_id",mssv[1].trim());
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
        } catch (JSONException e) {
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
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
