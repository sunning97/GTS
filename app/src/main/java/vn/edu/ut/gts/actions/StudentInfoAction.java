package vn.edu.ut.gts.actions;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import vn.edu.ut.gts.storage.DataStatic;

public class StudentInfoAction {
    public StudentInfoAction(){

    }


    public JSONObject getStudentProfile(String cookie){
        try {
            Document doc = Jsoup.connect(DataStatic.getBaseUrl() + "HoSoSinhVien.aspx")
                    .userAgent(DataStatic.getUserAgent())
                    .cookie("ASP.NET_SessionId",cookie).get();
            System.out.println(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
