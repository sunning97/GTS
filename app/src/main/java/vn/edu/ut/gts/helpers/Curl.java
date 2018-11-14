package vn.edu.ut.gts.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Curl {
    private static URL url;
    private static HttpsURLConnection urlConnection;
    public static Curl connect(String href){
        try {
            url = new URL(href);
            urlConnection = (HttpsURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Curl();
    }
    public Curl method(String method){
        try {
            urlConnection.setRequestMethod(method);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        return this;
    }
    public Curl userAgent(String userAgent){
        urlConnection.setRequestProperty("User-Agent",userAgent);
        return this;
    }
    public Curl setStringCookie(String cookie){
        urlConnection.setRequestProperty("Cookie",cookie);
        return this;
    }
    public Curl setCookie(String key, String value){
        urlConnection.setRequestProperty("Cookie", key+"="+value);
        return this;
    }

    public Curl header(String key, String value){
        urlConnection.setRequestProperty(key, value);
        return this;
    }

    public Curl dataString(String params){
        urlConnection.setDoOutput(true);
        try {
            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.writeBytes(params);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String execute(){
        String result = null;
        try {
            if(urlConnection.getResponseCode() == 200){
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuffer response = new StringBuffer();
                String tmp;
                while((tmp = reader.readLine()) != null){
                    response.append(tmp);
                }
                reader.close();
                result = response.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
