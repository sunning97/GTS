package vn.edu.ut.gts.actions;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import vn.edu.ut.gts.actions.helpers.Aes;
import vn.edu.ut.gts.actions.helpers.Curl;
import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;

public class Login {
    private Storage storage;
    public Login(Context context){
        this.storage = new Storage(context);
    }
    /* Get data login */
    public void getDataLogin(){
        JSONObject data = new JSONObject();
        try {
            Connection.Response res = Jsoup.connect(Helper.BASE_URL)
                    .userAgent(Helper.USER_AGENT)
                    .method(Connection.Method.GET)
                    .execute();
            Document doc = res.parse();
            this.storage.setCookie(res.cookie("ASP.NET_SessionId"));
            data.put("eventTarget", doc.select("input[name=\"__EVENTTARGET\"]").val());
            data.put("eventArgument", doc.select("input[name=\"__EVENTARGUMENT\"]").val());
            data.put("lastFocus", doc.select("input[name=\"__LASTFOCUS\"]").val());
            data.put("viewState", doc.select("input[name=\"__VIEWSTATE\"]").val());
            data.put("viewStartGenerator", doc.select("input[name=\"__VIEWSTATEGENERATOR\"]").val());
            data.put("radioBtnList", doc.select("input[name=\"ctl00$ucPhieuKhaoSat1$RadioButtonList1\"][checked=\"checked\"]").val());
            data.put("listMenu", doc.select("select[name=\"ctl00$DdListMenu\"]>option").first().val());
            data.put("btnLogin", doc.select("input[name=\"ctl00$ucRight1$btnLogin\"]").val());
            this.storage.putString("dataLogin", data.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    /* Get private key */
    private String getPrivateKey(String studentId) {
        String res = Curl.connect(Helper.BASE_URL+"ajaxpro/AjaxCommon,PMT.Web.PhongDaoTao.ashx")
                .method("POST")
                .userAgent(Helper.USER_AGENT)
                .header("X-AjaxPro-Method","GetPrivateKey")
                .setStringCookie(this.storage.getCookie())
                .dataString("{\"salt\":\""+ studentId +"\"}")
                .execute();
        if(res != null) return res.substring(1,33);
        return null;
    }
    /* Create new confirm image */
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

    /* Execute login */
    public void login(String studentId, String password){
        try {
            JSONObject dataLogin = new JSONObject(this.storage.getString("dataLogin"));
            String hashPassword = Aes.encrypt(this.getPrivateKey(studentId), password).toBase64();
            String securityValue = this.createConfirmImage();
            Jsoup.connect(Helper.BASE_URL)
                    .method(Connection.Method.POST)
                    .userAgent(Helper.USER_AGENT)
                    .cookie("ASP.NET_SessionId", this.storage.getCookie())
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
    }
}
