package vn.edu.ut.gts.presenters.home;

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
import vn.edu.ut.gts.views.home.fragments.IStudyForImprovementFragment;

public class StudyForImprovementPresenter implements IStudyForImprovementPresenter {
    public static int currentStatus = 0;
    private Storage storage;
    private IStudyForImprovementFragment iStudyForImprovementFragment;

    public StudyForImprovementPresenter(Context context,IStudyForImprovementFragment iStudyForImprovementFragment) {
        storage = new Storage(context);
        this.iStudyForImprovementFragment = iStudyForImprovementFragment;
    }

    public void getData() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                iStudyForImprovementFragment.hideLoadedLayout();
                iStudyForImprovementFragment.hideInternetError();
                iStudyForImprovementFragment.showLoading();
            }

            @Override
            protected String doInBackground(Void... voids) {
                String id = null;
                try {
                    Document document = Jsoup.connect(Helper.BASE_URL + "DangKyHocCaiThien.aspx")
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .get();
                    Element select = document.getElementById("ctl00_ContentPlaceHolder_cboDot");
                    Element option = select.getElementsByTag("option").get(0);
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
            protected void onPostExecute(String string) {
                getAllSubject(string);
            }
        };
        asyncTask.execute();
    }

    public void getAllSubject(final String id) {
        @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, JSONArray> asyncTask = new AsyncTask<String, Void, JSONArray>() {

            @Override
            protected JSONArray doInBackground(String... strings) {
                JSONArray jsonArray = null;
                try {
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "ajaxpro/DangKyHocCaiThien,PMT.Web.PhongDaoTao.ashx")
                            .method(Connection.Method.POST)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .header("Connection", "keep-alive")
                            .header("X-AjaxPro-Method", "GetMonMoiByHocKyMSSV")
                            .requestBody("{}")
                            .execute();

                    Document document = res.parse();
                    final String REGEX_1 = "\\{\"__type\":\"DataSetResult\\,\\sPMT\\.Web\\.PhongDaoTao\\, Version=1\\.0\\.0\\.0,\\sCulture=neutral\\, PublicKeyToken=(.*)\"\\,\"Result\":new Ajax\\.Web\\.DataSet\\(\\[new Ajax\\.Web\\.DataTable\\(\\[\\[\"Id\"\\,\"System\\.Int32\"\\],\\[\"IDMonHoc\"\\,\"System\\.Int32\"\\]\\,\\[\"IDTrangThaiLopHocPhan\"\\,\"System\\.Int32\"\\]\\,\\[\"MaLopHocPhan\"\\,\"System\\.String\"\\]\\,\\[\"ThuTuLopHocPhan\"\\,\"System\\.String\"\\]\\,\\[\"LopDuKien\"\\,\"System\\.String\"\\]\\,\\[\"SiSoToiThieu\"\\,\"System\\.Int16\"\\]\\,\\[\"SiSoToiDa\"\\,\"System\\.Int16\"]\\,\\[\"LoaiLopHocPhan\"\\,\"System\\.Byte\"\\]\\,\\[\"IDKhoaChuQuan\"\\,\"System\\.Int32\"\\]\\,\\[\"NgayHetHanNopHP\"\\,\"System\\.DateTime\"\\]\\,\\[\"NgayHetHanNopHP2\"\\,\"System\\.DateTime\"\\]\\,\\[\"TenTrangThai\"\\,\"System\\.String\"\\]\\,\\[\"MaMonHoc\",\"System\\.String\"\\]\\,\\[\"MaHocPhan\"\\,\"System\\.String\"\\]\\]\\,(.*)\\)\\]\\)\\,\"ErrorCode\":\"E100\"\\,\"Message\":\"\"\\};\\/\\*";
                    final String a = "\\{\"__type\":\"DataSetResult\\,\\sPMT\\.Web\\.PhongDaoTao\\, Version=1\\.0\\.0\\.0,\\sCulture=neutral\\, PublicKeyToken=(.*)\"\\,\"Result\":new Ajax\\.Web\\.DataSet\\(\\[new Ajax\\.Web\\.DataTable\\(\\[\\[\"Id\"\\,\"System\\.Int32\"\\]\\,\\[\"IDLopHoc\"\\,\"System\\.Int32\"\\]\\,\\[\"IDChiTietKhungHocKy\"\\,\"System\\.Int32\"\\]\\,\\[\"IDChiTietPhanMonLopHoc\"\\,\"System\\.Int32\"\\]\\,\\[\"IDLoaiMonHoc\"\\,\"System\\.Int32\"\\]\\,\\[\"MaHocPhan\"\\,\"System\\.String\"\\]\\,\\[\"MaMonHoc\"\\,\"System\\.String\"\\]\\,\\[\"TenMonHoc\"\\,\"System\\.String\"\\]\\,\\[\"SoTinChi\"\\,\"System\\.Byte\"\\]\\,\\[\"SoTietLyThuyet\"\\,\"System\\.Int32\"\\]\\,\\[\"SoTietThucHanh\"\\,\"System\\.Int32\"\\]\\,\\[\"IsLyThuyet\"\\,\"System\\.Boolean\"\\]\\,\\[\"TLBTL\"\\,\"System\\.Boolean\"\\]\\,\\[\"ChiDiemCuoiKy\"\\,\"System\\.Boolean\"\\]\\,\\[\"IDMonHocNgoaiChuongTrinh\"\\,\"System\\.Int32\"\\]\\,\\[\"IDHinhThucThi\"\\,\"System\\.Int32\"\\]\\,\\[\"IDToBoMon\"\\,\"System\\.Int32\"\\]\\,\\[\"NguoiTao\"\\,\"System\\.Int32\"\\]\\,\\[\"NgayTao\"\\,\"System\\.DateTime\"\\]\\,\\[\"NguoiCapNhat\"\\,\"System\\.Int32\"\\]\\,\\[\"NgayCapNhat\"\\,\"System\\.DateTime\"\\]\\,\\[\"SoTinChiLT\"\\,\"System\\.Decimal\"\\]\\,\\[\"SoTinChiTH\"\\,\"System\\.Decimal\"\\]\\,\\[\"IDTinhChatMonHoc\"\\,\"System\\.Int32\"\\]\\,\\[\"IDQuyUocCotDiem\"\\,\"System\\.Int32\"\\]\\,\\[\"TenVietTatMonHoc\"\\,\"System\\.String\"\\]\\,\\[\"IDKhoiKienThuc\"\\,\"System\\.Int32\"\\]\\,\\[\"IDLopHocPhan\"\\,\"System\\.Int32\"\\]\\,\\[\"IDCoSo\"\\,\"System\\.Int32\"\\]\\,\\[\"IDHeDaoTao\"\\,\"System\\.Int32\"\\]\\,\\[\"IDLoaiDaoTao\"\\,\"System\\.Int32\"\\]\\,\\[\"IDKhoaHoc\"\\,\"System\\.Int32\"\\]\\,\\[\"IDDot\"\\,\"System\\.Int32\"\\]\\,\\[\"IDNganh\"\\,\"System\\.Int32\"\\]\\,\\[\"IDNghe\",\"System\\.Int32\"\\]\\,\\[\"IDDangKyHocPhan\"\\,\"System\\.Int32\"\\]\\,\\[\"DiemTongKet\"\\,\"System\\.Decimal\"\\]\\]\\,(.*)\\)\\]\\)\\,\"ErrorCode\":\"E100\"\\,\"Message\":\"\"\\};\\/\\*";
                    Pattern pattern = Pattern.compile(a);
                    Matcher matcher = pattern.matcher(document.text());
                    if (matcher.matches()) {
                        String b = matcher.group(2).replaceAll("new\\sDate\\(Date\\.UTC\\([0-9]{1,}\\,[0-9]{1,}\\,[0-9]{1,}\\,[0-9]{1,}\\,[0-9]{1,}\\,[0-9]{1,}\\,[0-9]{1,}\\)\\)","null");
                        jsonArray = new JSONArray(b);
                    }

                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
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
                switch (currentStatus) {
                    case 400: /* if no connection*/
                        iStudyForImprovementFragment.loadingToInternetError();
                        break;
                    case 500: /* if connect timeout*/
                        iStudyForImprovementFragment.loadingToInternetError();
                        break;
                    default: { /* connect success*/
                        currentStatus = 0;
                        iStudyForImprovementFragment.generateTableContent(data);
                        iStudyForImprovementFragment.loadingToLoaded();
                    }
                }

            }
        };
        asyncTask.execute();
    }
}
