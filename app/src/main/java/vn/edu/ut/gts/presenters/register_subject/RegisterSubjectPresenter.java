package vn.edu.ut.gts.presenters.register_subject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.edu.ut.gts.helpers.Helper;
import vn.edu.ut.gts.helpers.Storage;
import vn.edu.ut.gts.views.register_subject.IRegisterSubjectActivity;

public class RegisterSubjectPresenter implements IRegisterSubjectPresenter{
    private IRegisterSubjectActivity iRegisterSubjectActivity;
    private Storage storage;
    public RegisterSubjectPresenter(IRegisterSubjectActivity iRegisterSubjectActivity, Context context){
        this.iRegisterSubjectActivity = iRegisterSubjectActivity;
        storage = new Storage(context);
    }

    public void getData(){
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,String> getData = new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                iRegisterSubjectActivity.hideInternetErrorLayout();
                iRegisterSubjectActivity.hideAllSubjectLayout();
                iRegisterSubjectActivity.showLoadingLayout();
            }

            @Override
            protected String doInBackground(Void... voids) {
                String id = null;
                try {
                    Document document = Jsoup.connect(Helper.BASE_URL + "DangKy.aspx")
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .get();
                    Element select = document.getElementById("ctl00_ContentPlaceHolder_cboDot");
                    Element option = select.getElementsByTag("option").get(0);
                    storage.putString("current_quater",option.val());
                    return option.val();
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (NullPointerException | IndexOutOfBoundsException | IOException e) {
                    e.printStackTrace();
                }
                return id;
            }

            @Override
            protected void onPostExecute(String s) {
                getRegisterSubject(s);
            }
        };
        getData.execute();
    }

    private void getRegisterSubject(String quarter){
        @SuppressLint("StaticFieldLeak") AsyncTask<String,Void,JSONArray> registerSubject = new AsyncTask<String, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(String... strings) {
                JSONArray jsonArray = null;
                try {
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "ajaxpro/DangKy,PMT.Web.PhongDaoTao.ashx")
                            .method(Connection.Method.POST)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .header("Connection", "keep-alive")
                            .header("X-AjaxPro-Method", "GetMonMoiByHocKyMSSV")
                            .requestBody("{\"HocKy\":\""+strings[0]+"\"}")
                            .execute();

                    Document document = res.parse();
                    final String REGEX_1 = "\\{\"__type\":\"DataSetResult\\,\\sPMT\\.Web\\.PhongDaoTao\\,\\sVersion=1\\.0\\.0\\.0\\,\\sCulture=neutral\\, PublicKeyToken=(.*)\",\"Result\":new Ajax\\.Web\\.DataSet\\(\\[new Ajax\\.Web\\.DataTable\\(\\[\\[\"IDMonHoc\"\\,\"System\\.Int32\"\\]\\,\\[\"LoaiHocPhan\"\\,\"System\\.Boolean\"\\]\\,\\[\"MaHocPhan\"\\,\"System\\.String\"\\]\\,\\[\"SoTinChi\"\\,\"System\\.String\"\\]\\,\\[\"HocPhanTruoc\"\\,\"System\\.String\"\\]\\,\\[\"HocPhanTienQuyet\"\\,\"System\\.String\"\\]\\,\\[\"HocPhanSongHanh\"\\,\"System\\.String\"\\]\\,\\[\"SoTietLyThuyet\"\\,\"System\\.Int32\"\\]\\,\\[\"SoTietThucHanh\"\\,\"System\\.Int32\"\\]\\,\\[\"DVHT\"\\,\"System\\.Byte\"\\]\\,\\[\"MaMonHoc\"\\,\"System\\.String\"\\]\\,\\[\"TenMonHoc\"\\,\\\"System\\.String\"\\]\\,\\[\"IDLoaiMonHoc\"\\,\"System\\.Int32\"\\]\\,\\[\"IDMonHocTuongDuong\"\\,\"System\\.Int32\"\\],\\[\"MaHocPhanTuongDuong\"\\,\"System\\.String\"\\]\\]\\,(.*)\\)\\]\\)\\,\"ErrorCode\":\"E100\"\\,\"Message\":\"\"\\};\\/\\*";
                    Pattern pattern = Pattern.compile(REGEX_1);
                    Matcher matcher = pattern.matcher(document.text());
                    if (matcher.matches()) {
                        jsonArray = new JSONArray(matcher.group(2));
                    }

                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (NullPointerException | IndexOutOfBoundsException | IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonArray;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                iRegisterSubjectActivity.generateTableSubjectContent(jsonArray);
                iRegisterSubjectActivity.loadingToAllSubject();
            }
        };

        registerSubject.execute(quarter);
    }

    public void getClassOfSubject(final String subjectID){
        @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, JSONArray> asyncTask = new AsyncTask<String, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                iRegisterSubjectActivity.allSubjectToLoading();
            }

            @Override
            protected JSONArray doInBackground(String... strings) {
                JSONArray jsonArray = null;
                try {
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "ajaxpro/DangKy,PMT.Web.PhongDaoTao.ashx")
                            .method(Connection.Method.POST)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .header("X-AjaxPro-Method", "GetLopMoiByIDMonHoc")
                            .requestBody("{\"IDMonHoc\":\""+subjectID+"\",\"HocKy\":\""+storage.getString("current_quater")+"\",\"monHocTuongDuong\":\"null\"}")
                            .execute();

                    Document document = res.parse();
                    final String REGEX_1 = "\\{\"__type\":\"DataSetResult\\,\\sPMT\\.Web\\.PhongDaoTao\\,\\sVersion=1\\.0\\.0\\.0\\,\\sCulture=neutral\\,\\ PublicKeyToken=(.*)\"\\,\"Result\":new\\sAjax\\.Web\\.DataSet\\(\\[new Ajax\\.Web\\.DataTable\\(\\[\\[\"Id\"\\,\"System\\.Int32\"\\]\\,\\[\"IDTrangThaiLopHocPhan\"\\,\"System\\.Int32\"\\]\\,\\[\"MaLopHocPhan\"\\,\"System\\.String\"\\]\\,\\[\"LopDuKien\"\\,\"System\\.String\"\\]\\,\\[\"SiSoToiThieu\"\\,\"System\\.Int16\"\\]\\,\\[\"SiSoToiDa\"\\,\"System\\.Int16\"\\]\\,\\[\"NgayHetHanNopHP\"\\,\"System\\.DateTime\"\\]\\,\\[\"NgayHetHanNopHP2\"\\,\"System\\.DateTime\"\\]\\,\\[\"MaHocPhan\"\\,\"System\\.String\"\\]\\,\\[\"MaMonHoc\"\\,\"System\\.String\"\\]\\,\\[\"TenMonHoc\"\\,\"System\\.String\"\\]\\,\\[\"SoTinChi\"\\,\"System\\.Byte\"\\]\\,\\[\"IDDot\"\\,\"System\\.Int32\"\\]\\,\\[\"TenTrangThai\"\\,\"System\\.String\"\\]\\,\\[\"SiSoDangKy\"\\,\"System\\.Int32\"\\]\\,\\[\"MucNop\"\\,\"System\\.Decimal\"\\]\\]\\,(.*)\\)\\]\\)\\,\"ErrorCode\":\"E100\"\\,\"Message\":\"\"\\};\\/\\*";
                    Pattern pattern = Pattern.compile(REGEX_1);
                    Matcher matcher = pattern.matcher(document.text());

                    if (matcher.matches()) {
                        jsonArray = new JSONArray(matcher.group(2));
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (NullPointerException | IndexOutOfBoundsException | IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonArray;
            }

            @Override
            protected void onPostExecute(JSONArray data) {
                iRegisterSubjectActivity.generateTableClassContent(data);
                iRegisterSubjectActivity.loadingToAllClass();
            }
        };
        asyncTask.execute();
    }

    public void getClassSchedule(String classID){
        @SuppressLint("StaticFieldLeak") AsyncTask<String,Void,JSONArray> getClassSchedule = new AsyncTask<String, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONArray doInBackground(String... strings) {
                JSONArray jsonArray = null;
                try {
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "ajaxpro/DangKy,PMT.Web.PhongDaoTao.ashx")
                            .method(Connection.Method.POST)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .header("X-AjaxPro-Method", "GetChiTietLopHocPhan")
                            .requestBody("{\"IDLopHocPhan\":\""+strings[0]+"\"}")
                            .execute();

                    Document document = res.parse();
                    final String REGEX_1 = "\\{\"__type\":\"DataSetResult\\,\\sPMT\\.Web\\.PhongDaoTao\\,\\sVersion=1\\.0\\.0\\.0\\,\\sCulture=neutral\\,\\ PublicKeyToken=(.*)\"\\,\"Result\":new\\sAjax\\.Web\\.DataSet\\(\\[new Ajax\\.Web\\.DataTable\\(\\[\\[\"Id\"\\,\"System\\.Int32\"\\]\\,\\[\"IDTrangThaiLopHocPhan\"\\,\"System\\.Int32\"\\]\\,\\[\"MaLopHocPhan\"\\,\"System\\.String\"\\]\\,\\[\"LopDuKien\"\\,\"System\\.String\"\\]\\,\\[\"SiSoToiThieu\"\\,\"System\\.Int16\"\\]\\,\\[\"SiSoToiDa\"\\,\"System\\.Int16\"\\]\\,\\[\"NgayHetHanNopHP\"\\,\"System\\.DateTime\"\\]\\,\\[\"NgayHetHanNopHP2\"\\,\"System\\.DateTime\"\\]\\,\\[\"MaHocPhan\"\\,\"System\\.String\"\\]\\,\\[\"MaMonHoc\"\\,\"System\\.String\"\\]\\,\\[\"TenMonHoc\"\\,\"System\\.String\"\\]\\,\\[\"SoTinChi\"\\,\"System\\.Byte\"\\]\\,\\[\"IDDot\"\\,\"System\\.Int32\"\\]\\,\\[\"TenTrangThai\"\\,\"System\\.String\"\\]\\,\\[\"SiSoDangKy\"\\,\"System\\.Int32\"\\]\\,\\[\"MucNop\"\\,\"System\\.Decimal\"\\]\\]\\,(.*)\\)\\]\\)\\,\"ErrorCode\":\"E100\"\\,\"Message\":\"\"\\};\\/\\*";
                    Pattern pattern = Pattern.compile(REGEX_1);
                    Matcher matcher = pattern.matcher(document.text());

                    if (matcher.matches()) {
                        jsonArray = new JSONArray(matcher.group(2));
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (NullPointerException | IndexOutOfBoundsException | IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonArray;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                Log.d("AAAAA",jsonArray.toString());
            }
        };
        getClassSchedule.execute(classID);
    }
}
