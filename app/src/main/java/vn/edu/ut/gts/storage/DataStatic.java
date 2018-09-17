package vn.edu.ut.gts.storage;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DataStatic {
    private static final String baseUrl = "https://sv.ut.edu.vn/";
    private static final  String userAgent= "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:56.0) Gecko/20100101 Firefox/56.0";

    public static String getBaseUrl(){
        return baseUrl;
    }

    public static String getUserAgent() {
        return userAgent;
    }
}
