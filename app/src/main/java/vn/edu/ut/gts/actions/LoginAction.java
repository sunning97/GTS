package vn.edu.ut.gts.actions;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import vn.edu.ut.gts.helpers.AES;
import vn.edu.ut.gts.helpers.Curl;
import vn.edu.ut.gts.helpers.Helper;
import vn.edu.ut.gts.storage.DataStatic;

public class LoginAction {
    private String cookie;
    private JSONObject dataLogin;
    public LoginAction(){
        this.dataLogin = new JSONObject();
    }

    public JSONObject getDataLogin(){
        try {
            Connection.Response res = Jsoup.connect(DataStatic.getBaseUrl())
                    .userAgent(DataStatic.getUserAgent())
                    .method(Connection.Method.GET)
                    .execute();
            Document doc = res.parse();
            this.cookie = res.cookie("ASP.NET_SessionId");
            this.dataLogin.put("cookie", this.cookie);
            this.dataLogin.put("eventTarget", doc.select("input[name=\"__EVENTTARGET\"]").val());
            this.dataLogin.put("eventArgument", doc.select("input[name=\"__EVENTARGUMENT\"]").val());
            this.dataLogin.put("lastFocus", doc.select("input[name=\"__LASTFOCUS\"]").val());
            this.dataLogin.put("viewState", doc.select("input[name=\"__VIEWSTATE\"]").val());
            this.dataLogin.put("viewStartGenerator", doc.select("input[name=\"__VIEWSTATEGENERATOR\"]").val());
            this.dataLogin.put("radioBtnList", doc.select("input[name=\"ctl00$ucPhieuKhaoSat1$RadioButtonList1\"][checked=\"checked\"]").val());
            this.dataLogin.put("listMenu", doc.select("select[name=\"ctl00$DdListMenu\"]>option").first().val());
            this.dataLogin.put("btnLogin", doc.select("input[name=\"ctl00$ucRight1$btnLogin\"]").val());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return this.dataLogin;
    }

    private String getPrivateKey(String studentId) {
        String res = Curl.connect(DataStatic.getBaseUrl()+"ajaxpro/AjaxCommon,PMT.Web.PhongDaoTao.ashx")
                .method("POST")
                .userAgent(DataStatic.getUserAgent())
                .header("X-AjaxPro-Method","GetPrivateKey")
                .setStringCookie(this.cookie)
                .dataString("{\"salt\":\""+ studentId +"\"}")
                .execute();
        if(res != null) return res.substring(1,33);
        return null;
    }

    private String getSecurity(String md5){

        try {
            String res = Curl.connect("https://uts.ntuongst.ga/api/getMD5dec/"+md5)
                    .method("GET")
                    .header("Content-Type","application/json")
                    .execute();
            Log.e("Security", res);
            return new JSONObject(res).getString("value");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    private String getSecurityValue(String md5) {
        try {
            Document doc = Jsoup.connect("https://md5.gromweb.com/?md5=" + md5)
                    .userAgent(DataStatic.getUserAgent())
                    .get();
            return doc.select("em[class=\"long-content string\"]").text();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    private String createConfirmImage() {
        try {
            String res = Curl.connect(DataStatic.getBaseUrl() + "ajaxpro/AjaxConfirmImage,PMT.Web.PhongDaoTao.ashx")
                    .method("POST")
                    .setCookie("ASP.NET_SessionId", this.cookie)
                    .userAgent(DataStatic.getUserAgent())
                    .header("X-AjaxPro-Method", "CreateConfirmImage")
                    .dataString("{}")
                    .execute();
            res = res.replace(";/*", "");
            JSONArray ar = new JSONArray(res);
            return getSecurity(ar.getString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public LoginAction doLogin(String studentId, String password, JSONObject dataLogin){
        try {
            String hashPassword = AES.encrypt(getPrivateKey(studentId), password).toBase64();
            String securityValue = createConfirmImage();
            Jsoup.connect(DataStatic.getBaseUrl())
                    .method(Connection.Method.POST)
                    .userAgent(DataStatic.getUserAgent())
                    .cookie("ASP.NET_SessionId", this.cookie)
                    .data("__EVENTTARGET", dataLogin.getString("eventTarget"))
                    .data("__EVENTARGUMENT", dataLogin.getString("eventArgument"))
                    .data("__LASTFOCUS", dataLogin.getString("lastFocus"))
                    .data("__VIEWSTATE", dataLogin.getString("viewState"))
                    .data("__VIEWSTATEGENERATOR", dataLogin.getString("viewStartGenerator"))


                    .data("ctl00$ucPhieuKhaoSat1$RadioButtonList1", dataLogin.getString("radioBtnList"))
                    .data("ctl00$DdListMenu",dataLogin.getString("listMenu"))
                    .data("ctl00$ucRight1$btnLogin", dataLogin.getString("btnLogin"))

                    .data("ctl00$ucRight1$txtMaSV",studentId)
                    .data("ctl00$ucRight1$txtMatKhau", hashPassword)
                    .data("ctl00$ucRight1$txtSercurityCode", securityValue)
                    .data("txtSecurityCodeValue", Helper.md5(securityValue))
                    .data("ctl00$ucRight1$txtEncodeMatKhau", Helper.md5(password))
                    .execute();
        } catch (IOException | JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return this;
    }

    public boolean checkLogin() {
        String res = Curl.connect(DataStatic.getBaseUrl() + "ajaxpro/DangKy,PMT.Web.PhongDaoTao.ashx")
                .method("POST")
                .setCookie("ASP.NET_SessionId", this.cookie)
                .userAgent(DataStatic.getUserAgent())
                .header("X-AjaxPro-Method", "CheckLogin")
                .dataString("{}").execute();
        return Boolean.parseBoolean(res.replace(";/*", ""));
    }
    public boolean checkLogin(String cookie) {

        String res = Curl.connect(DataStatic.getBaseUrl() + "ajaxpro/DangKy,PMT.Web.PhongDaoTao.ashx")
                .method("POST")
                .setCookie("ASP.NET_SessionId", cookie)
                .userAgent(DataStatic.getUserAgent())
                .header("X-AjaxPro-Method", "CheckLogin")
                .dataString("{}").execute();
        return Boolean.parseBoolean(res.replace(";/*", ""));
    }
}
