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
import java.util.ArrayList;
import java.util.List;

import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.views.home.fragments.IStudentDebtFragment;

public class StudentDebtFragmentPresenter implements IStudentDebtFragmentPresenter{
    private IStudentDebtFragment iStudentDebtFragment;
    private Context context;
    private Storage storage;

    public StudentDebtFragmentPresenter(IStudentDebtFragment iStudentDebtFragment,Context context){
        this.iStudentDebtFragment = iStudentDebtFragment;
        this.context = context;
        this.storage = new Storage(this.context);
    }
    @Override
    public void initDataStudentDebt() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                JSONObject data = new JSONObject();
                try {
                    Document document = Jsoup.connect(Helper.BASE_URL + "CongNoSinhVien.aspx")
                            .method(Connection.Method.GET)
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

                    JSONArray semesters = new JSONArray();
                    Elements options = document.select("select[name=\"ctl00$ContentPlaceHolder$cboHocKy\"]>option");
                    for (Element option : options) {
                        JSONObject tmp = new JSONObject();
                        if (option.val().equals("-1")) continue;
                        tmp.put("key", option.val());
                        tmp.put("text", option.text().trim());
                        semesters.put(tmp);
                    }
                    data.put("semesters", semesters);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                storage.putString("dataDebt", data.toString());

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                JSONArray semesters;
                List<String> dataSnpinner = new ArrayList<>();
                try {
                    JSONObject dataDebt = new JSONObject(storage.getString("dataDebt"));
                    semesters = new JSONArray(dataDebt.getString("semesters"));
                    for (int i = 0; i < semesters.length(); i++) {
                        JSONObject jsonObject = (JSONObject) semesters.get(i);
                        dataSnpinner.add(jsonObject.getString("text"));
                    }
                } catch (Exception e){}
                iStudentDebtFragment.initAttendanceSpiner(dataSnpinner);
            }
        };
        asyncTask.execute();
    }

    public void getStudentDebt(final int pos) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                iStudentDebtFragment.showLoadingDialog();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray data = new JSONArray();
                try {
                    JSONObject dataDiemDanh = new JSONObject(storage.getString("dataDebt"));
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "CongNoSinhVien.aspx")
                            .method(Connection.Method.POST)
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
                    Document document = res.parse();
                    Elements trs = document.select("table.grid.grid-color2>tbody>tr");
                    Elements ths = trs.first().select("th");
                    JSONArray keys = new JSONArray();
                    for (int i = 2; i < ths.size(); i++) {
                        String keyTmp = Helper.toSlug(ths.get(i).text().trim());
                        keys.put(keyTmp);
                    }
                    for (int i = 1; i < trs.size() - 1; i++) {
                        Elements tds = trs.get(i).select("td");
                        JSONObject subject = new JSONObject();
                        for (int j = 2; j < tds.size(); j++) {
                            String tmp = tds.get(j).text().trim();
                            subject.put(keys.getString(j - 2), tmp);
                        }
                        data.put(subject);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return data;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                iStudentDebtFragment.generateTableContent(jsonArray);
                iStudentDebtFragment.dismissLoadingDialog();
            }
        };
        asyncTask.execute();
    }

}
