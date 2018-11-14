package vn.edu.ut.gts.helpers;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class Helper {
    public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:56.0) Gecko/20100101 Firefox/56.0";
    public static String BASE_URL = "https://sv.ut.edu.vn/";
    public static final int TIMEOUT = 500;
    public static final int NO_CONNECTION = 400;
    public static final int TIMEOUT_VALUE = 20000;

    public static String md5(String str) {
        String md5 = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] thedigest = digest.digest(str.getBytes());
            md5 = new String(Hex.encodeHex(thedigest));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;

    }

    public static String decryptMd5(String md5){
        try {
            Document doc = Jsoup.connect("https://md5.gromweb.com/?md5=" + md5)
                    .userAgent(USER_AGENT)
                    .get();
            return doc.select("em[class=\"long-content string\"]").text().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean checkLogin(String cookie) {

        String res = Curl.connect(BASE_URL + "ajaxpro/DangKy,PMT.Web.PhongDaoTao.ashx")
                .method("POST")
                .setCookie("ASP.NET_SessionId", cookie)
                .userAgent(USER_AGENT)
                .header("X-AjaxPro-Method", "CheckLogin")
                .dataString("{}").execute();
        return Boolean.parseBoolean(res.replace(";/*", ""));
    }
    public static String base64Encode(String str) {
        byte[] base = Base64.decodeBase64(str);
        return new String(base);
    }

    public static String toSlug(String str){
        str = str.trim();
        String tmp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern p = Pattern.compile("\\p{InCOMBINING_DIACRITICAL_MARKS}+");
        return p.matcher(tmp).replaceAll("").toLowerCase().replace("đ","d").replaceAll("\\s","_").replaceAll("\\(","").replaceAll("\\)","").replaceAll(",","");
    }
}
