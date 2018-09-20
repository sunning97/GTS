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

public class Login {
    private static String getPrivateKey(String studentId) {
        String res = Curl.connect(DataStatic.getBaseUrl()+"ajaxpro/AjaxCommon,PMT.Web.PhongDaoTao.ashx")
                .method("POST")
                .userAgent(DataStatic.getUserAgent())
                .header("X-AjaxPro-Method","GetPrivateKey")
                .dataString("{\"salt\":\""+ studentId +"\"}")
                .execute();
        if(res != null) return res.substring(1,33);
        return null;
    }
    private static String getSecurityValue(String md5) {
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
    private static String createConfirmImage() {
        String result = null;
        try {
            Connection.Response res = Jsoup.connect(DataStatic.getBaseUrl() + "ajaxpro/AjaxConfirmImage,PMT.Web.PhongDaoTao.ashx")
                    .method(Connection.Method.POST)
                    .userAgent(DataStatic.getUserAgent())
                    .header("X-AjaxPro-Method", "CreateConfirmImage")
                    .execute();
            String str = res.body();
            str = str.replace(";/*", "");
            JSONArray ar = new JSONArray(str);
            result  = getSecurityValue(ar.getString(1));
        } catch (IOException | JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    public static JSONObject getDataLogin(){
        JSONObject data = new JSONObject();
        try {
            Connection.Response res = Jsoup.connect(DataStatic.getBaseUrl())
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:56.0) Gecko/20100101 Firefox/56.0")
                    .method(Connection.Method.GET)
                    .execute();
            Document doc = res.parse();
            data.put("cookie", res.cookies());
            data.put("eventTarget", doc.select("input[name=\"__EVENTTARGET\"]").val());
            data.put("eventArgument", doc.select("input[name=\"__EVENTARGUMENT\"]").val());
            data.put("lastFocus", doc.select("input[name=\"__LASTFOCUS\"]").val());
            data.put("viewState", doc.select("input[name=\"__VIEWSTATE\"]").val());
            data.put("viewStartGenerator", doc.select("input[name=\"__VIEWSTATEGENERATOR\"]").val());
            data.put("radioBtnList", doc.select("input[name=\"ctl00$ucPhieuKhaoSat1$RadioButtonList1\"][checked=\"checked\"]").val());
            data.put("listMenu", doc.select("select[name=\"ctl00$DdListMenu\"]>option").first().val());
            data.put("btnLogin", doc.select("input[name=\"ctl00$ucRight1$btnLogin\"]").val());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
    public static boolean doLogin(String studentId, String password, JSONObject dataLogin){
        String hashPassword = AES.encrypt(getPrivateKey(studentId), password).toBase64();
        String securityValue = createConfirmImage();
        try {
            Connection.Response res = Jsoup.connect(DataStatic.getBaseUrl())
                    .method(Connection.Method.POST)
                    .userAgent(DataStatic.getUserAgent())
                    .header("Cookie",dataLogin.getString("cookie"))
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
            Log.d("CCC",res.parse().toString());
            return (!res.parse().select("#ctl00_ucRight1_Span2").isEmpty());
        } catch (IOException | JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}
