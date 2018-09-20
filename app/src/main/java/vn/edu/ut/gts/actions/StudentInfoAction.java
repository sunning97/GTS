package vn.edu.ut.gts.actions;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import vn.edu.ut.gts.helpers.Helper;
import vn.edu.ut.gts.storage.DataStatic;

public class StudentInfoAction {
    public StudentInfoAction(){

    }


    public JSONArray getStudentProfile(String cookie){
        JSONArray studentData = new JSONArray();
        try {
            Document doc = Jsoup.connect(DataStatic.getBaseUrl() + "HoSoSinhVien.aspx")
                    .userAgent(DataStatic.getUserAgent())
                    .cookie("ASP.NET_SessionId",cookie).get();
            Elements bodyGroups = doc.getElementsByClass("body-group");
            Element bodyFisrt = bodyGroups.first();
            JSONObject studentInfo = new JSONObject();
            for(Element td: bodyFisrt.getElementsByTag("td")){
                String[] tmp = td.text().split(":");
                    if(tmp.length >= 2){
                        studentInfo.put(Helper.toSlug(tmp[0]),tmp[1]);
                    }else{
                        studentInfo.put(Helper.toSlug(tmp[0]),"Không");
                    }
            }
            Log.e("Result", studentInfo.toString());
            return studentData;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
