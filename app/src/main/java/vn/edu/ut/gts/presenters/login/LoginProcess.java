package vn.edu.ut.gts.presenters.login;

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
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import vn.edu.ut.gts.actions.Login;
import vn.edu.ut.gts.actions.Student;
import vn.edu.ut.gts.actions.helpers.Aes;
import vn.edu.ut.gts.actions.helpers.Curl;
import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.views.home.HomeActivity;
import vn.edu.ut.gts.views.login.ILoginView;

public class LoginProcess implements ILoginProcess {
    public static int TIMEOUT = 1;
    public static int LOGIN_SUCCESS = 2;
    public static int LOGIN_FAILED = 3;
    public static int NO_INTERNET = 4;

    public static int currentStatus = LOGIN_FAILED;

    private ILoginView iLoginView;
    private Context context;
    private Storage storage;

    public LoginProcess(ILoginView iLoginView, Context context) {
        this.iLoginView = iLoginView;
        this.context = context;
        this.storage = new Storage(this.context);
    }

    public void initData(final boolean isAuto) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Integer> asyncTask = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected void onPreExecute() {
                if (!isAuto){
                    iLoginView.disableInput();
                    iLoginView.showLoadingDialog();
                }
            }

            @Override
            protected Integer doInBackground(Void... voids) {
                int result = 0;
                JSONObject data = new JSONObject();
                try {
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL)
                            .userAgent(Helper.USER_AGENT)
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .execute();
                    Document doc = res.parse();
                    storage.setCookie(res.cookie("ASP.NET_SessionId"));
                    data.put("eventTarget", doc.select("input[name=\"__EVENTTARGET\"]").val());
                    data.put("eventArgument", doc.select("input[name=\"__EVENTARGUMENT\"]").val());
                    data.put("lastFocus", doc.select("input[name=\"__LASTFOCUS\"]").val());
                    data.put("viewState", doc.select("input[name=\"__VIEWSTATE\"]").val());
                    data.put("viewStartGenerator", doc.select("input[name=\"__VIEWSTATEGENERATOR\"]").val());
                    data.put("radioBtnList", doc.select("input[name=\"ctl00$ucPhieuKhaoSat1$RadioButtonList1\"][checked=\"checked\"]").val());
                    data.put("listMenu", doc.select("select[name=\"ctl00$DdListMenu\"]>option").first().val());
                    data.put("btnLogin", doc.select("input[name=\"ctl00$ucRight1$btnLogin\"]").val());
                    storage.putString("dataLogin", data.toString());

                } catch (SocketTimeoutException connTimeout) {
                    result = LoginProcess.TIMEOUT;
                    currentStatus = result;
                } catch (UnknownHostException e) {
                    result = LoginProcess.NO_INTERNET;
                    currentStatus = result;
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                switch (integer) {
                    case 1: {
                        iLoginView.dismisLoadingDialog();
                        iLoginView.transferToRetryBtn();
                        iLoginView.showLoginLayout();
                        if (!isAuto)
                            iLoginView.showTimeoutDialog();
                        break;
                    }
                    case 4: {
                        iLoginView.dismisLoadingDialog();
                        iLoginView.transferToRetryBtn();
                        if (!isAuto){
                            iLoginView.showLoginLayout();
                            iLoginView.showNoInternetDialog();
                        } else {
                            iLoginView.setLastLogin();
                        }
                        break;
                    }
                    default: {
                        iLoginView.transferToLoginBtn();
                        iLoginView.dismisLoadingDialog();
                        currentStatus = 0;
                    }
                }
            }
        };
        asyncTask.execute();
    }

    @Override
    public void execute(final String studentId, final String password, final boolean isAuto) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Integer> asyncTask = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected void onPreExecute() {
                if (!isAuto)
                    iLoginView.startLoadingButton();
            }

            @Override
            protected Integer doInBackground(Void... voids) {
                int result = LoginProcess.LOGIN_FAILED;
                try {
                    JSONObject dataLogin = new JSONObject(storage.getString("dataLogin"));
                    String hashPassword = Aes.encrypt(getPrivateKey(studentId), password).toBase64();
                    String securityValue = createConfirmImage();
                    Jsoup.connect(Helper.BASE_URL)
                            .method(Connection.Method.POST)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .data("__EVENTTARGET", dataLogin.getString("eventTarget"))
                            .data("__EVENTARGUMENT", dataLogin.getString("eventArgument"))
                            .data("__LASTFOCUS", dataLogin.getString("lastFocus"))
                            .data("__VIEWSTATE", dataLogin.getString("viewState"))
                            .data("__VIEWSTATEGENERATOR", dataLogin.getString("viewStartGenerator"))
                            .data("ctl00$ucPhieuKhaoSat1$RadioButtonList1", dataLogin.getString("radioBtnList"))
                            .data("ctl00$DdListMenu", dataLogin.getString("listMenu"))
                            .data("ctl00$ucRight1$btnLogin", dataLogin.getString("btnLogin"))
                            .data("ctl00$ucRight1$txtMaSV", studentId)
                            .data("ctl00$ucRight1$txtMatKhau", hashPassword)
                            .data("ctl00$ucRight1$txtSercurityCode", securityValue)
                            .data("txtSecurityCodeValue", Helper.md5(securityValue))
                            .data("ctl00$ucRight1$txtEncodeMatKhau", Helper.md5(password))
                            .execute();

                    if (Helper.checkLogin(storage.getCookie())) result = LoginProcess.LOGIN_SUCCESS;

                } catch (SocketTimeoutException connTimeout) {
                    result = LoginProcess.TIMEOUT;
                } catch (NullPointerException e) {
                    result = LoginProcess.NO_INTERNET;
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(Integer status) {
                switch (status) {
                    case 1: {
                        iLoginView.revertLoadingButton();
                        iLoginView.showLoginLayout();
                        iLoginView.showTimeoutDialog();
                        break;
                    }
                    case 2: {
                        saveLastLoginID(studentId);
                        HomeActivity.isLogin = true;
                        iLoginView.doneLoadingButton();
                        iLoginView.loginSuccess();
                        break;
                    }
                    case 3: {
                        iLoginView.revertLoadingButton();
                        if (isAuto){
                            iLoginView.showLoginLayout();
                            iLoginView.showLoginAutoErrorDialog();
                        }
                        else iLoginView.loginFailed();
                        break;
                    }
                    case 4: {
                        iLoginView.revertLoadingButton();
                        iLoginView.showLoginLayout();
                        iLoginView.showNoInternetDialog();
                        break;
                    }
                }
            }
        };
        asyncTask.execute();
    }
    private void saveLastLoginID(String ID) {
        this.storage.putString("last_student_login", ID);
    }
    private String createConfirmImage() {
        try {
            String res = Curl.connect(Helper.BASE_URL + "ajaxpro/AjaxConfirmImage,PMT.Web.PhongDaoTao.ashx")
                    .method("POST")
                    .setCookie("ASP.NET_SessionId", this.storage.getCookie())
                    .userAgent(Helper.USER_AGENT)
                    .header("X-AjaxPro-Method", "CreateConfirmImage")
                    .dataString("{}")
                    .execute();
            res = res.replace(";/*", "");
            JSONArray ar = new JSONArray(res);
            return Helper.decryptMd5(ar.getString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    private String getPrivateKey(String studentId) {
        String res = Curl.connect(Helper.BASE_URL + "ajaxpro/AjaxCommon,PMT.Web.PhongDaoTao.ashx")
                .method("POST")
                .userAgent(Helper.USER_AGENT)
                .header("X-AjaxPro-Method", "GetPrivateKey")
                .setStringCookie(this.storage.getCookie())
                .dataString("{\"salt\":\"" + studentId + "\"}")
                .execute();
        if (res != null) return res.substring(1, 33);
        return null;
    }
}
