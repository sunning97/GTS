package vn.edu.ut.gts.presenters.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

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
import vn.edu.ut.gts.views.home.fragments.IStudentStudyResultFragment;

public class StudentStudyResultFragmentPresenter implements IStudentStudyResultFragmentPresenter{
    public static int currentStatus = 0;
    private IStudentStudyResultFragment iStudentStudyResultFragment;
    private Context context;
    private Storage storage;

    public StudentStudyResultFragmentPresenter(IStudentStudyResultFragment iStudentStudyResultFragment,Context context){
        this.iStudentStudyResultFragment = iStudentStudyResultFragment;
        this.context = context;
        this.storage = new Storage(this.context);
    }

    @Override
    public void getStudentStudyResult(final int post) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,JSONObject> getData = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected void onPreExecute() {
                iStudentStudyResultFragment.showLoadingDialog();
            }

            @Override
            protected JSONObject doInBackground(Void... voids) {
                JSONObject result = new JSONObject();
                JSONArray allQuater = new JSONArray();
                try {
                    Document document = Jsoup.connect(Helper.BASE_URL + "Xemdiem.aspx")
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .get();

                    Elements tableResult = document.select("table.grid.grid-color2.tblKetQuaHocTap");
                    Element table1 = tableResult.first();

                    result.put("tong_tin_chi",tableResult.get(1).getElementById("ctl00_ContentPlaceHolder_ucThongTinTotNghiepTinChi1_lblTongTinChi").text());
                    result.put("trung_binh_tich_luy",tableResult.get(1).getElementById("ctl00_ContentPlaceHolder_ucThongTinTotNghiepTinChi1_lblTBCTL").text());
                    result.put("ti_le_no",tableResult.get(1).getElementById("ctl00_ContentPlaceHolder_ucThongTinTotNghiepTinChi1_lblSoTCNo").text());

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

                        if(i == indexs.size() - 1){
                            for (int j = indexs.get(i) + 1; j < trs.size(); j++) {
                                Element tr = trs.get(j);
                                if (!TextUtils.isEmpty(tr.text())) {
                                    JSONObject subject = new JSONObject();
                                    Elements tds = tr.getElementsByTag("td");
                                    subject.put("courseCode",tds.get(1).text());
                                    subject.put("courseName",tds.get(2).text());
                                    subject.put("courseClass",tds.get(3).text());
                                    subject.put("courseCredits",tds.get(4).text());

                                    if(tds.size() == 14){
                                        subject.put("processScore",0);
                                        subject.put("testScores",tds.get(7).text());
                                        subject.put("scoresOf10",tds.get(9).text());
                                        subject.put("scoresOf4",tds.get(10).text());
                                        subject.put("scoresString",tds.get(11).text());
                                        subject.put("classification",tds.get(12).text());
                                        subject.put("note",tds.get(13).text());
                                    } else {
                                        subject.put("processScore",tds.get(6).text());
                                        subject.put("testScores",tds.get(10).text());
                                        subject.put("scoresOf10",tds.get(12).text());
                                        subject.put("scoresOf4",tds.get(13).text());
                                        subject.put("scoresString",tds.get(14).text());
                                        subject.put("classification",tds.get(15).text());
                                        subject.put("note",tds.get(16).text());
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
                                    subject.put("courseCode",tds.get(1).text());
                                    subject.put("courseName",tds.get(2).text());
                                    subject.put("courseClass",tds.get(3).text());
                                    subject.put("courseCredits",tds.get(4).text());

                                    if(tds.size() == 14){
                                        subject.put("processScore",0);
                                        subject.put("testScores",tds.get(7).text());
                                        subject.put("scoresOf10",tds.get(9).text());
                                        subject.put("scoresOf4",tds.get(10).text());
                                        subject.put("scoresString",tds.get(11).text());
                                        subject.put("classification",tds.get(12).text());
                                        subject.put("note",tds.get(13).text());
                                    } else {
                                        subject.put("processScore",tds.get(6).text());
                                        subject.put("testScores",tds.get(10).text());
                                        subject.put("scoresOf10",tds.get(12).text());
                                        subject.put("scoresOf4",tds.get(13).text());
                                        subject.put("scoresString",tds.get(14).text());
                                        subject.put("classification",tds.get(15).text());
                                        subject.put("note",tds.get(16).text());
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
                    result.put("all_semester",allQuater);
                }  catch (SocketTimeoutException e) {
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
                return result;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                switch (currentStatus) {
                    case 400:
                        iStudentStudyResultFragment.showNetworkErrorLayout();
                        break;
                    case 500:
                        iStudentStudyResultFragment.showNetworkErrorLayout();
                        break;
                    default: {
                        currentStatus = 0;
                        iStudentStudyResultFragment.setData(jsonObject);
                        iStudentStudyResultFragment.spinnerInit();
                        iStudentStudyResultFragment.generateTableContent(post);
                        iStudentStudyResultFragment.showAllComponent();
                        iStudentStudyResultFragment.dismissLoadingDialog();
                    }
                }
            }
        };
        getData.execute();
    }
}
