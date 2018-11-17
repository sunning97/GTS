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

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import vn.edu.ut.gts.helpers.Aes;
import vn.edu.ut.gts.helpers.Curl;
import vn.edu.ut.gts.helpers.Helper;
import vn.edu.ut.gts.helpers.Storage;
import vn.edu.ut.gts.views.home.HomeActivity;
import vn.edu.ut.gts.views.login.ILoginView;

public class LoginProcess implements ILoginProcess {
    public static int currentStatus = 0;
    private ILoginView iLoginView;
    private Storage storage;

    public LoginProcess(ILoginView iLoginView, Context context) {
        this.iLoginView = iLoginView;
        this.storage = new Storage(context);
    }

    /*get data for login & store to sharedpreference*/
    public void initData(final boolean isAuto) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                if (!isAuto){
                    iLoginView.disableInput();
//                    iLoginView.showLoadingDialog();
                    iLoginView.transferToLoadingBtn();
                }
            }

            @Override
            protected Void doInBackground(Void... voids) {
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

                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    currentStatus = Helper.TIMEOUT;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    currentStatus = Helper.NO_CONNECTION;
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                switch (LoginProcess.currentStatus) {
                    case 500: {
                        iLoginView.dismissLoadingDialog();
                        iLoginView.transferToRetryBtn();
                        iLoginView.showLoginLayout();
                        if (!isAuto)
                            iLoginView.showTimeoutDialog();
                        break;
                    }
                    case 400: {
                        iLoginView.dismissLoadingDialog();
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
//                        iLoginView.dismissLoadingDialog();
                        currentStatus = 0;
                    }
                }
            }
        };
        asyncTask.execute();
    }

    @Override
    public void execute(final String studentId, final String password, final boolean isAuto) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                if (!isAuto)
                    iLoginView.startLoadingButton();
            }

            @Override
            protected Void doInBackground(Void... voids) {
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

                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    LoginProcess.currentStatus = Helper.TIMEOUT;
                } catch (UnknownHostException e){
                    e.printStackTrace();
                    LoginProcess.currentStatus = Helper.NO_CONNECTION;
                } catch (NullPointerException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                switch (LoginProcess.currentStatus) {
                    case 500: { /* if connect timeout*/
                        iLoginView.revertLoadingButton();
                        iLoginView.showLoginLayout();
                        iLoginView.showTimeoutDialog();
                        break;
                    }
                    case 400: { /*if no connection*/
                        iLoginView.revertLoadingButton();
                        iLoginView.showLoginLayout();
                        iLoginView.showNoInternetDialog();
                        break;
                    }
                    default:{
                        checkLogin(studentId,isAuto);
                    }
                }
            }
        };
        asyncTask.execute();
    }

    private void checkLogin(final String studentId, final boolean isAuto){
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "ajaxpro/DangKy,PMT.Web.PhongDaoTao.ashx")
                            .method(Connection.Method.POST)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .header("X-AjaxPro-Method","CheckLogin")
                            .execute();

                    Document document = res.parse();
                    if(Boolean.parseBoolean(document.select("body").text().replace(";/*", "")))
                        currentStatus = Helper.LOGIN_SUCCESS;
                    else currentStatus = Helper.LOGIN_FAILED;

                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    LoginProcess.currentStatus = Helper.TIMEOUT;
                } catch (UnknownHostException e){
                    e.printStackTrace();
                    LoginProcess.currentStatus = Helper.NO_CONNECTION;
                } catch (NullPointerException | IndexOutOfBoundsException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void voids) {
                switch (LoginProcess.currentStatus) {
                    case 500: { /* if connect timeout*/
                        iLoginView.revertLoadingButton();
                        iLoginView.showLoginLayout();
                        iLoginView.showTimeoutDialog();
                        break;
                    }
                    case 200: { /*if login success*/
                        saveLastLoginID(studentId);
                        HomeActivity.isLogin = true;
                        iLoginView.doneLoadingButton();
                        iLoginView.loginSuccess();
                        break;
                    }
                    case 300: { /* if login failed*/
                        iLoginView.revertLoadingButton();
                        if (isAuto){
                            iLoginView.showLoginLayout();
                            iLoginView.showLoginAutoErrorDialog();
                        }
                        else iLoginView.loginFailed();
                        break;
                    }
                    case 400: { /*if no connection*/
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
        } catch (NullPointerException | IndexOutOfBoundsException | JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getPrivateKey(String studentId) {
        String result = null;
        try {
            String res = Curl.connect(Helper.BASE_URL + "ajaxpro/AjaxCommon,PMT.Web.PhongDaoTao.ashx")
                    .method("POST")
                    .userAgent(Helper.USER_AGENT)
                    .header("X-AjaxPro-Method", "GetPrivateKey")
                    .setStringCookie(this.storage.getCookie())
                    .dataString("{\"salt\":\"" + studentId + "\"}")
                    .execute();
            if (res != null) result =  res.substring(1, 33);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return result;
    }
}
