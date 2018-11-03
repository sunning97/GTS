package vn.edu.ut.gts.presenters.search;

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
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import vn.edu.ut.gts.actions.helpers.Curl;
import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.views.search.IStudentSearchActivity;

public class StudentSearchPresenter implements IStudentSearchPresenter{
    private IStudentSearchActivity iStudentSearchActivity;
    private Context context;
    private Storage storage;
    public StudentSearchPresenter(IStudentSearchActivity iStudentSearchActivity,Context context){
        this.iStudentSearchActivity = iStudentSearchActivity;
        this.context = context;
        storage = new Storage(this.context);
    }

    public void getDataSearch() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,JSONObject> asyncTask = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... voids) {
                JSONObject result = new JSONObject();
                try {
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL+"TraCuuThongTin.aspx")
                            .userAgent(Helper.USER_AGENT)
                            .method(Connection.Method.GET)
                            .execute();

                    Document document = res.parse();
                    try {
                        result.put("__VIEWSTATE", document.select("input[name=__VIEWSTATE]").val());
                        result.put("__VIEWSTATEGENERATOR", document.select("input[name=__VIEWSTATEGENERATOR]").val());
                        result.put("ctl00$ContentPlaceHolder$btnTraCuuThongTin", document.select("input[name=ctl00$ContentPlaceHolder$btnTraCuuThongTin]").val());
                        result.put("cookie",res.cookie("ASP.NET_SessionId"));
                        result.put("ctl00$ContentPlaceHolder$txtSercurityCode1", createConfirmImage(res.cookie("ASP.NET_SessionId")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                storage.putString("search_data",jsonObject.toString());
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
    private void searchStudent() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,ArrayList<JSONObject>> asyncTask = new AsyncTask<Void, Void, ArrayList<JSONObject>>() {
            @Override
            protected ArrayList<JSONObject> doInBackground(Void... voids) {
                ArrayList<JSONObject> students = new ArrayList<>();
                try {
                    JSONObject dataSearch = new JSONObject(storage.getString("search_data"));
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL+"TraCuuThongTin.aspx")
                        .method(Connection.Method.POST)
                        .userAgent(Helper.USER_AGENT)
                        .data("ctl00$ContentPlaceHolder$txtMaSoSV","")
                        .data("ctl00$ContentPlaceHolder$txtMaLop", "")
                        .data("ctl00$ContentPlaceHolder$txtHoDem", "")
                        .data("ctl00$ContentPlaceHolder$txtHoTen", "Giang")
                        .data("ctl00$ContentPlaceHolder$objNgaySinh", "")
                        .data("__VIEWSTATE", dataSearch.getString("__VIEWSTATE"))
                        .data("__VIEWSTATEGENERATOR", dataSearch.getString("__VIEWSTATEGENERATOR"))
                        .data("ctl00$ContentPlaceHolder$txtSercurityCode1", dataSearch.getString("ctl00$ContentPlaceHolder$txtSercurityCode1"))
                        .data("ctl00$ContentPlaceHolder$btnTraCuuThongTin", dataSearch.getString("ctl00$ContentPlaceHolder$btnTraCuuThongTin"))
                        .cookie("ASP.NET_SessionId",dataSearch.getString("cookie"))
                        .execute();
                    Document document = res.parse();
                    Elements trs = document.select("#TblDanhSachSinhVien tr");
                    for(int i = 1; i < trs.size(); i++) {
                        JSONObject student = new JSONObject();
                        Elements tds = trs.get(i).select("td");
                        if(tds.size() >= 8) {
                            student.put("studentCode", tds.get(1).text().trim());
                            student.put("studentName", tds.get(2).text().trim());
                            student.put("birthday", tds.get(3).text().trim());
                            student.put("urlViewMark", tds.get(4).selectFirst("a").attr("href"));
                            student.put("urlViewDebt", tds.get(5).selectFirst("a").attr("href"));
                        }
                        students.add(student);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return students;
            }

            @Override
            protected void onPostExecute(ArrayList<JSONObject> jsonObjects) {

            }
        };
        asyncTask.execute();
    }
}
