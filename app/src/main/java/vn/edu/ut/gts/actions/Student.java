package vn.edu.ut.gts.actions;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;

public class Student {
    private Storage storage;
    public Student(Context context){
        this.storage = new Storage(context);
    }

    public void getDataTTDiemDanh() {
        JSONObject data = new JSONObject();
        try {
            Document document = Jsoup.connect(Helper.BASE_URL + "ThongTinDiemDanh.aspx")
                    .method(Connection.Method.GET)
                    .userAgent(Helper.USER_AGENT)
                    .cookie("ASP.NET_SessionId", this.storage.getCookie())
                    .get();
            data.put("eventTarget", document.select("input[name=\"__EVENTTARGET\"]").val());
            data.put("eventArgument", document.select("input[name=\"__EVENTARGUMENT\"]").val());
            data.put("lastFocus", document.select("input[name=\"__LASTFOCUS\"]").val());
            data.put("viewState", document.select("input[name=\"__VIEWSTATE\"]").val());
            data.put("viewStartGenerator", document.select("input[name=\"__VIEWSTATEGENERATOR\"]").val());
            data.put("radioBtnList", document.select("input[name=\"ctl00$ucPhieuKhaoSat1$RadioButtonList1\"][checked=\"checked\"]").val());
            data.put("listMenu", document.select("select[name=\"ctl00$DdListMenu\"]>option").first().val());
            data.put("ctl00$ContentPlaceHolder$btnLoc", document.select("input[name=\"ctl00$ContentPlaceHolder$btnLoc\"][type=\"submit\"]").val());

            JSONArray semesters = new JSONArray();
            Elements options = document.select("select[name=\"ctl00$ContentPlaceHolder$cboHocKy\"]>option");
            for(Element option : options) {
                JSONObject tmp = new JSONObject();
                if(option.val().equals("-1")) continue;
                tmp.put("key", option.val());
                tmp.put("text", option.text().trim());
                semesters.put(tmp);
            }
            data.put("semesters", semesters);
        } catch (IOException | JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        storage.putString("dataAttendance", data.toString());
    }
    public JSONArray getTTDiemDanh(int pos) {
        JSONArray data = new JSONArray();
        try {
            JSONObject dataDiemDanh = new JSONObject(this.storage.getString("dataAttendance"));
            Connection.Response res = Jsoup.connect(Helper.BASE_URL + "ThongTinDiemDanh.aspx")
                    .method(Connection.Method.POST)
                    .userAgent(Helper.USER_AGENT)
                    .cookie("ASP.NET_SessionId", this.storage.getCookie())
                    .data("__EVENTTARGET",dataDiemDanh.getString("eventTarget"))
                    .data("__EVENTARGUMENT",dataDiemDanh.getString("eventArgument"))
                    .data("__LASTFOCUS",dataDiemDanh.getString("lastFocus"))
                    .data("__VIEWSTATE",dataDiemDanh.getString("viewState"))
                    .data("__VIEWSTATEGENERATOR",dataDiemDanh.getString("viewStartGenerator"))
                    .data("ctl00$ucPhieuKhaoSat1$RadioButtonList1",dataDiemDanh.getString("radioBtnList"))
                    .data("ctl00$DdListMenu",dataDiemDanh.getString("eventTarget"))
                    .data("ctl00$ContentPlaceHolder$cboHocKy",dataDiemDanh.getJSONArray("semesters").getJSONObject(pos).getString("key"))
                    .data("ctl00$ContentPlaceHolder$btnLoc",dataDiemDanh.getString("ctl00$ContentPlaceHolder$btnLoc"))
                    .execute();
            Document document = res.parse();
            Elements trs = document.select("table.grid.grid-color2>tbody>tr");
            Elements ths= trs.first().select("th");
            JSONArray keys = new JSONArray();
            for(int i = 1; i < ths.size(); i++) {
                String keyTmp = Helper.toSlug(ths.get(i).text().trim());
                keys.put(keyTmp);
            }
            for(int i = 2; i < trs.size() - 2; i++) {
                Elements tds = trs.get(i).select("td");
                JSONObject subject = new JSONObject();
                for(int j = 1; j < tds.size(); j++) {
                    String tmp = tds.get(j).text().trim();
                    subject.put(keys.getString(j-1), tmp);
                }
                data.put(subject);
            }
        } catch (IOException | JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }

    public void getDataStudentDebt(){

        JSONObject data = new JSONObject();
        try {
            Document document = Jsoup.connect(Helper.BASE_URL + "CongNoSinhVien.aspx")
                    .method(Connection.Method.GET)
                    .userAgent(Helper.USER_AGENT)
                    .cookie("ASP.NET_SessionId", this.storage.getCookie())
                    .get();

            data.put("eventTarget", document.select("input[name=\"__EVENTTARGET\"]").val());
            data.put("eventArgument", document.select("input[name=\"__EVENTARGUMENT\"]").val());
            data.put("lastFocus", document.select("input[name=\"__LASTFOCUS\"]").val());
            data.put("viewState", document.select("input[name=\"__VIEWSTATE\"]").val());
            data.put("viewStartGenerator", document.select("input[name=\"__VIEWSTATEGENERATOR\"]").val());
            data.put("radioBtnList", document.select("input[name=\"ctl00$ucPhieuKhaoSat1$RadioButtonList1\"][checked=\"checked\"]").val());
            data.put("listMenu", document.select("select[name=\"ctl00$DdListMenu\"]>option").first().val());
            data.put("ctl00$ContentPlaceHolder$btnLoc", document.select("input[name=\"ctl00$ContentPlaceHolder$btnLoc\"][type=\"submit\"]").val());

            JSONArray semesters = new JSONArray();
            Elements options = document.select("select[name=\"ctl00$ContentPlaceHolder$cboHocKy\"]>option");
            for(Element option : options) {
                JSONObject tmp = new JSONObject();
                if(option.val().equals("-1")) continue;
                tmp.put("key", option.val());
                tmp.put("text", option.text().trim());
                semesters.put(tmp);
            }
            data.put("semesters", semesters);
        } catch (IOException | JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        storage.putString("dataDebt", data.toString());
    }

    public JSONArray getStudentDebt(int pos){

        JSONArray data = new JSONArray();
        try {
            JSONObject dataDiemDanh = new JSONObject(this.storage.getString("dataDebt"));
            Connection.Response res = Jsoup.connect(Helper.BASE_URL + "CongNoSinhVien.aspx")
                    .method(Connection.Method.POST)
                    .userAgent(Helper.USER_AGENT)
                    .cookie("ASP.NET_SessionId", this.storage.getCookie())
                    .data("__EVENTTARGET",dataDiemDanh.getString("eventTarget"))
                    .data("__EVENTARGUMENT",dataDiemDanh.getString("eventArgument"))
                    .data("__LASTFOCUS",dataDiemDanh.getString("lastFocus"))
                    .data("__VIEWSTATE",dataDiemDanh.getString("viewState"))
                    .data("__VIEWSTATEGENERATOR",dataDiemDanh.getString("viewStartGenerator"))
                    .data("ctl00$ucPhieuKhaoSat1$RadioButtonList1",dataDiemDanh.getString("radioBtnList"))
                    .data("ctl00$DdListMenu",dataDiemDanh.getString("eventTarget"))
                    .data("ctl00$ContentPlaceHolder$cboHocKy",dataDiemDanh.getJSONArray("semesters").getJSONObject(pos).getString("key"))
                    .data("ctl00$ContentPlaceHolder$btnLoc",dataDiemDanh.getString("ctl00$ContentPlaceHolder$btnLoc"))
                    .execute();
            Document document = res.parse();
            Elements trs = document.select("table.grid.grid-color2>tbody>tr");
            Elements ths= trs.first().select("th");
            JSONArray keys = new JSONArray();
            for(int i = 2; i < ths.size(); i++) {
                String keyTmp = Helper.toSlug(ths.get(i).text().trim());
                keys.put(keyTmp);
            }
            for(int i = 1; i < trs.size() - 1; i++) {
                Elements tds = trs.get(i).select("td");
                JSONObject subject = new JSONObject();
                for(int j = 2; j < tds.size(); j++) {
                    String tmp = tds.get(j).text().trim();
                    subject.put(keys.getString(j-2), tmp);
                }
                data.put(subject);
            }
        } catch (IOException | JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }


    public JSONObject getStudentInfo() {
        JSONObject info = new JSONObject();
        try {
            Document document = Jsoup.connect(Helper.BASE_URL + "HoSoSinhVien.aspx")
                    .method(Connection.Method.GET)
                    .userAgent(Helper.USER_AGENT)
                    .cookie("ASP.NET_SessionId", storage.getCookie())
                    .get();
            Element bodyGroup = document.getElementsByClass("body-group").first();
            Elements tds = bodyGroup.select("td");
            JSONArray studentInfo = new JSONArray();
            for(Element td:tds) {
                JSONObject prop = new JSONObject();
                String key = Helper.toSlug(td.text().split(":")[0]);
                String value = td.text().split(":").length > 1 ? td.text().split(":")[1].trim():"Không";
                prop.put("key",key);
                prop.put("value",value);
                studentInfo.put(prop);
            }
            info.put("studentInfo", studentInfo);
            Element bodyGroup1 = document.getElementsByClass("body-group").get(1);
            Elements tds1 = bodyGroup1.select("td");
            JSONArray studentDetail = new JSONArray();
            for(Element td: tds1) {
                JSONObject prop = new JSONObject();
                String key = Helper.toSlug(td.text().split(":")[0]);
                String value = td.text().split(":").length > 1 ? td.text().split(":")[1].trim():"Không";
                prop.put("key",key);
                prop.put("value",value);
                studentDetail.put(prop);
            }
            info.put("studentDetail", studentDetail);

            Element bodyGroup2 = document.getElementsByClass("body-group").get(2);
            Elements tds2 = bodyGroup2.select("td");
            JSONArray studentFamily = new JSONArray();
            for(Element td: tds2) {
                JSONObject prop = new JSONObject();
                String key = Helper.toSlug(td.text().split(":")[0]);
                String value = td.text().split(":").length > 1 ? td.text().split(":")[1].trim():"Không";
                prop.put("key",key);
                prop.put("value",value);
                studentFamily.put(prop);
            }
            info.put("studentFamily", studentFamily);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  info;
    }
    public String getStudentName(){
        String name = "";
        try {
            Document document = Jsoup.connect(Helper.BASE_URL + "HoSoSinhVien.aspx")
                    .method(Connection.Method.GET)
                    .userAgent(Helper.USER_AGENT)
                    .cookie("ASP.NET_SessionId", storage.getCookie())
                    .get();

            Element nameHTML = document.getElementById("ctl00_ucRight1_Span2");
            name = nameHTML.text();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  name;
    }

    public JSONArray getSchedules() {
        JSONArray schedules = new JSONArray();
        try {
            Document document = Jsoup.connect(Helper.BASE_URL + "LichHocLichThiTuan.aspx")
                    .method(Connection.Method.GET)
                    .userAgent(Helper.USER_AGENT)
                    .cookie("ASP.NET_SessionId", storage.getCookie())
                    .get();
            Elements table = document.select(".div-ChiTietLich>table");
            Elements trs = table.select("tr");

            Elements trDate = trs.first().select("th");
            Elements trMorning = trs.get(1).select("td");
            Elements trAfternoon = trs.get(2).select("td");
            Elements trEvening= trs.get(3).select("td");
            for(int i = 0; i < trMorning.size(); i++) {
                JSONObject schedule = new JSONObject();
                // Get date
                String dateRegEx="([0-9]{2})/([0-9]{2})/([0-9]{4})";
                Pattern p = Pattern.compile(dateRegEx);
                Matcher m = p.matcher(trDate.get(i+1).text());
                if(m.find()) {
                    schedule.put("date",m.group());
                }
                //Get morning
                JSONObject objMorning = new JSONObject();
                JSONObject objAfternoon = new JSONObject();
                JSONObject objEvening = new JSONObject();
                if(trMorning.get(i).select(".div-LichHoc").text().trim().length() > 0) {
                    Elements spanDisplay = trMorning.get(i).children().select(".span-display");
                    objMorning.put("subjectId", spanDisplay.get(0).text().trim().length() > 0 ? spanDisplay.get(0).text().trim():"");
                    objMorning.put("subjectName", spanDisplay.get(1).text().trim().length() > 0 ? spanDisplay.get(1).text().trim():"");
                    objMorning.put("subjectTime", spanDisplay.get(2).text().trim().length() > 0 ? spanDisplay.get(2).text().trim():"");
                    objMorning.put("subjectLecturer", spanDisplay.get(3).text().trim().length() > 0 ? spanDisplay.get(3).text().trim():"");
                    objMorning.put("subjectRoom", spanDisplay.get(4).text().trim().length() > 0 ? spanDisplay.get(4).text().trim():"");
                }
                if(trAfternoon.get(i).select(".div-LichHoc").text().trim().length() > 0) {
                    Elements spanDisplay1 = trAfternoon.get(i).children().select(".span-display");
                    objAfternoon.put("subjectId", spanDisplay1.get(0).text().trim().length() > 0 ? spanDisplay1.get(0).text().trim():"");
                    objAfternoon.put("subjectName", spanDisplay1.get(1).text().trim().length() > 0 ? spanDisplay1.get(1).text().trim():"");
                    objAfternoon.put("subjectTime", spanDisplay1.get(2).text().trim().length() > 0 ? spanDisplay1.get(2).text().trim():"");
                    objAfternoon.put("subjectLecturer", spanDisplay1.get(3).text().trim().length() > 0 ? spanDisplay1.get(3).text().trim():"");
                    objAfternoon.put("subjectRoom", spanDisplay1.get(4).text().trim().length() > 0 ? spanDisplay1.get(4).text().trim():"");
                }
                if(trEvening.get(i).select(".div-LichHoc").text().trim().length() > 0) {
                    Elements spanDisplay1 = trAfternoon.get(i).children().select(".span-display");
                    objAfternoon.put("subjectId", spanDisplay1.get(0).text().trim().length() > 0 ? spanDisplay1.get(0).text().trim():"");
                    objAfternoon.put("subjectName", spanDisplay1.get(1).text().trim().length() > 0 ? spanDisplay1.get(1).text().trim():"");
                    objAfternoon.put("subjectTime", spanDisplay1.get(2).text().trim().length() > 0 ? spanDisplay1.get(2).text().trim():"");
                    objAfternoon.put("subjectLecturer", spanDisplay1.get(3).text().trim().length() > 0 ? spanDisplay1.get(3).text().trim():"");
                    objAfternoon.put("subjectRoom", spanDisplay1.get(4).text().trim().length() > 0 ? spanDisplay1.get(4).text().trim():"");
                }
                schedule.put("morning", objMorning);
                schedule.put("afternoon", objAfternoon);
                schedule.put("evening", objEvening);
                schedules.put(schedule);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return schedules;
    }
}
