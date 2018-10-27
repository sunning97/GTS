package vn.edu.ut.gts.presenters.home;

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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.views.home.fragments.IFrameProgramFragment;

public class FrameProgramFragmentPresenter implements IFrameProgramFragmentPresenter{
    private IFrameProgramFragment iFrameProgramFragment;
    private Context context;
    private Storage storage;

    public FrameProgramFragmentPresenter(IFrameProgramFragment iFrameProgramFragment,Context context){
        this.iFrameProgramFragment = iFrameProgramFragment;
        this.context = context;
        this.storage = new Storage(this.context);
    }

    public void getDataFrameProgram() {
        JSONObject dataFrame = new JSONObject();
        try {
            Document document = Jsoup.connect(Helper.BASE_URL + "XemChuongTrinhKhung.aspx")
                    .method(Connection.Method.GET)
                    .userAgent(Helper.USER_AGENT)
                    .cookie("ASP.NET_SessionId", storage.getCookie())
                    .get();

            dataFrame.put("eventTarget", document.select("input[name=\"__EVENTTARGET\"]").val());
            dataFrame.put("eventArgument", document.select("input[name=\"__EVENTARGUMENT\"]").val());
            dataFrame.put("lastFocus", document.select("input[name=\"__LASTFOCUS\"]").val());
            dataFrame.put("viewState", document.select("input[name=\"__VIEWSTATE\"]").val());
            dataFrame.put("viewStartGenerator", document.select("input[name=\"__VIEWSTATEGENERATOR\"]").val());
            dataFrame.put("radioBtnListPhieuKhaoSat", document.select("input[name=\"ctl00$ucPhieuKhaoSat1$RadioButtonList1\"][checked=\"checked\"]").val());
            dataFrame.put("listMenu", document.select("select[name=\"ctl00$DdListMenu\"]>option").first().val());
            dataFrame.put("btnXem", document.select("input[name=\"ctl00$ContentPlaceHolder$btnXem\"]").val());
            storage.putString("data_frame", dataFrame.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getFrameProgram() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,JSONObject> getData = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected void onPreExecute() {
                iFrameProgramFragment.showLoadingDialog();
            }

            @Override
            protected JSONObject doInBackground(Void... voids) {

                final String REGEX_1 = "CHƯƠNG\\sTRÌNH\\sKHUNG\\s(.*)";
                final String REGEX_2 = "<span\\sonmouseover=\"tooltip\\.show\\('<div>(.*)<\\/div>'\\)\"\\sonmouseout=\"tooltip\\.hide\\(\\)\">(.*)\\s\\((.*)\\)<\\/span>";
                Pattern pattern = null;
                Matcher matcher = null;
                JSONObject returnData = new JSONObject();

                getDataFrameProgram();

                try {
                    JSONObject dataFrame = new JSONObject(storage.getString("data_frame"));
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + "XemChuongTrinhKhung.aspx")
                            .method(Connection.Method.POST)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .data("__EVENTTARGET", dataFrame.getString("eventTarget"))
                            .data("__EVENTARGUMENT", dataFrame.getString("eventArgument"))
                            .data("__LASTFOCUS", dataFrame.getString("lastFocus"))
                            .data("__VIEWSTATE", dataFrame.getString("viewState"))
                            .data("__VIEWSTATEGENERATOR", dataFrame.getString("viewStartGenerator"))
                            .data("ctl00$ucPhieuKhaoSat1$RadioButtonList1", dataFrame.getString("radioBtnListPhieuKhaoSat"))
                            .data("ctl00$DdListMenu", dataFrame.getString("listMenu"))
                            .data("ctl00$ContentPlaceHolder$btnXem", dataFrame.getString("btnXem"))
                            .execute();
                    Document document = res.parse();
                    Elements trs = document.select("table.grid.grid-color2>tbody>tr");

                    Elements elements = document.getElementsByClass("title-group");
                    pattern = Pattern.compile(REGEX_1);
                    matcher = pattern.matcher(elements.get(1).text());
                    if(matcher.matches())
                        returnData.put("info",matcher.group(1));

                    List<Integer> quaterTrPosition = new ArrayList<>();
                    JSONArray quatersName = new JSONArray();
                    for (int i = 0; i < trs.size(); i++) {
                        if (trs.get(i).hasAttr("style")) {
                            Elements tds = trs.get(i).getElementsByTag("td");
                            if (tds.size() == 4) {
                                quatersName.put(tds.get(0).text());
                                quaterTrPosition.add(i);
                            }
                        }
                    }


                    pattern = Pattern.compile(REGEX_2);
                    JSONArray allQuater = new JSONArray();
                    for (int i = 0; i <= quaterTrPosition.size() - 1; i++) {

                        JSONObject quater = new JSONObject();
                        JSONArray hocPhanbatBuoc = new JSONArray();
                        JSONArray hocPhanTuChon = new JSONArray();
                        int check = 0;

                        if (i == quaterTrPosition.size() - 1) {
                            Element trBatBuoc = null;
                            Element trKhongBatBuoc = null;
                            for (int j = quaterTrPosition.get(i) + 1; j < trs.size() - 1; j++) {
                                if (trs.get(j).hasAttr("style")) {
                                    check++;
                                    if(check == 1) trBatBuoc = trs.get(j); else trKhongBatBuoc = trs.get(j);
                                    continue;
                                }
                                Element tr = trs.get(j);
                                if (!tr.hasClass("thongke")) {
                                    Elements tds = tr.getElementsByTag("td");
                                    JSONArray subject = new JSONArray();
                                    for (int v = 1; v < tds.size(); v++) {
                                        if (v == 4 && tds.get(v).getElementsByTag("span").size() > 0) {
                                            matcher = pattern.matcher(tds.get(v).getElementsByTag("span").get(0).toString());
                                            if (matcher.matches()) {
                                                subject.put(matcher.group(1) + " - " + tds.get(v).text());
                                            }
                                        } else
                                            subject.put(tds.get(v).text());
                                    }
                                    if (check == 1) {
                                        hocPhanbatBuoc.put(subject);
                                    } else {
                                        hocPhanTuChon.put(subject);
                                    }
                                }
                            }
                            quater.put("so_chi_bat_buoc",trBatBuoc.getElementsByTag("td").get(2).text());
                            if(trKhongBatBuoc != null)
                                quater.put("so_chi_khong_bat_buoc",trKhongBatBuoc.getElementsByTag("td").get(2).text());
                            else quater.put("so_chi_khong_bat_buoc","0");
                        } else {
                            Element trBatBuoc = null;
                            Element trKhongBatBuoc = null;
                            for (int j = quaterTrPosition.get(i) + 1; j < quaterTrPosition.get((i + 1)); j++) {
                                if (trs.get(j).hasAttr("style")) {
                                    check++;
                                    if(check == 1) trBatBuoc = trs.get(j); else trKhongBatBuoc = trs.get(j);
                                    continue;
                                }
                                Element tr = trs.get(j);
                                Elements tds = tr.getElementsByTag("td");
                                JSONArray subject = new JSONArray();
                                for (int v = 1; v < tds.size(); v++) {
                                    if (v == 4 && tds.get(v).getElementsByTag("span").size() > 0) {
                                        matcher = pattern.matcher(tds.get(v).getElementsByTag("span").get(0).toString());
                                        if (matcher.matches()) {
                                            subject.put(matcher.group(1) + " - " + tds.get(v).text());
                                        }
                                    } else
                                        subject.put(tds.get(v).text());
                                }
                                if (check == 1) {
                                    hocPhanbatBuoc.put(subject);
                                } else {
                                    hocPhanTuChon.put(subject);
                                }
                            }
                            quater.put("so_chi_bat_buoc",trBatBuoc.getElementsByTag("td").get(2).text());
                            if(trKhongBatBuoc != null)
                                quater.put("so_chi_khong_bat_buoc",trKhongBatBuoc.getElementsByTag("td").get(2).text());
                            else quater.put("so_chi_khong_bat_buoc","0");
                        }
                        quater.put("quater_name", quatersName.get(i));
                        quater.put("bat_buoc", hocPhanbatBuoc);
                        quater.put("khong_bat_buoc", hocPhanTuChon);
                        allQuater.put(quater);
                    }
                    returnData.put("all_quater",allQuater);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return returnData;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                iFrameProgramFragment.setData(jsonObject);
                iFrameProgramFragment.spinnerInit();
                iFrameProgramFragment.generateTableContent(0);
                iFrameProgramFragment.dismissLoadingDialog();
            }
        };
        getData.execute();
    }
}
