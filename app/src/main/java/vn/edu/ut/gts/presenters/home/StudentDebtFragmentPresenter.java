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
import vn.edu.ut.gts.views.home.fragments.IStudentDebtFragment;
import vn.edu.ut.gts.views.home.fragments.StudentDebtFragment;

public class StudentDebtFragmentPresenter implements IStudentDebtFragmentPresenter{
    public static int currentStatus = 0;
    private IStudentDebtFragment iStudentDebtFragment;
    private Storage storage;

    public StudentDebtFragmentPresenter(IStudentDebtFragment iStudentDebtFragment,Context context){
        this.iStudentDebtFragment = iStudentDebtFragment;
        this.storage = new Storage(context);
    }
    @Override
    public void getDataDebtSpinner() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                iStudentDebtFragment.showLoadingDialog();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONObject data = new JSONObject();
                JSONArray semesters = new JSONArray();
                try {
                    Document document = Jsoup.connect(Helper.BASE_URL + "CongNoSinhVien.aspx")
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

                    Elements options = document.select("select[name=\"ctl00$ContentPlaceHolder$cboHocKy\"]>option");
                    for (Element option : options) {
                        JSONObject tmp = new JSONObject();
                        if (option.val().equals("-1")) continue;
                        tmp.put("key", option.val());
                        tmp.put("text", option.text().trim());
                        semesters.put(tmp);
                    }
                    data.put("semesters", semesters);
                }catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                storage.putString("dataDebt", data.toString());
                return semesters;
            }

            @Override
            protected void onPostExecute(JSONArray semesters) {
                switch (currentStatus) {
                    case 400:
                        iStudentDebtFragment.showNetworkErrorLayout();
                        break;
                    case 500:
                        iStudentDebtFragment.showNetworkErrorLayout();
                        break;
                    default: {
                        currentStatus = 0;
                        List<String> dataSpinner = new ArrayList<>();
                        try {
                            for (int i = 0; i < semesters.length(); i++) {
                                JSONObject jsonObject = (JSONObject) semesters.get(i);
                                dataSpinner.add(jsonObject.getString("text"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        iStudentDebtFragment.initDebtSpinner(dataSpinner);
                        getStudentDebt(StudentDebtFragment.currentPos);
                    }
                }
            }
        };
        asyncTask.execute();
    }
    @Override
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
                    JSONObject dataDiemDebt = new JSONObject(storage.getString("dataDebt"));
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "CongNoSinhVien.aspx")
                            .method(Connection.Method.POST)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .data("__EVENTTARGET", dataDiemDebt.getString("eventTarget"))
                            .data("__EVENTARGUMENT", dataDiemDebt.getString("eventArgument"))
                            .data("__LASTFOCUS", dataDiemDebt.getString("lastFocus"))
                            .data("__VIEWSTATE", dataDiemDebt.getString("viewState"))
                            .data("__VIEWSTATEGENERATOR", dataDiemDebt.getString("viewStartGenerator"))
                            .data("ctl00$ucPhieuKhaoSat1$RadioButtonList1", dataDiemDebt.getString("radioBtnList"))
                            .data("ctl00$DdListMenu", dataDiemDebt.getString("eventTarget"))
                            .data("ctl00$ContentPlaceHolder$cboHocKy", dataDiemDebt.getJSONArray("semesters").getJSONObject(pos).getString("key"))
                            .data("ctl00$ContentPlaceHolder$btnLoc", dataDiemDebt.getString("ctl00$ContentPlaceHolder$btnLoc"))
                            .execute();
                    Document document = res.parse();
                    Elements table = document.getElementsByClass("grid-color2");
                    Elements trs = table.get(0).select("tr");
                    Elements ths = trs.get(0).select("th");
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
                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException | IOException | JSONException e){
                    e.printStackTrace();
                }
                return data;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                switch (currentStatus) {
                    case 400:
                        iStudentDebtFragment.showNetworkErrorLayout();
                        break;
                    case 500:
                        iStudentDebtFragment.showNetworkErrorLayout();
                        break;
                    default: {
                        currentStatus = 0;
                        iStudentDebtFragment.generateTableContent(jsonArray);
                        iStudentDebtFragment.showAllComponent();
                        iStudentDebtFragment.dismissLoadingDialog();
                    }
                }

            }
        };
        asyncTask.execute();
    }

}
