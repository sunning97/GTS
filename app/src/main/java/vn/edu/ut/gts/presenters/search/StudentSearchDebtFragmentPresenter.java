package vn.edu.ut.gts.presenters.search;

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
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.views.search.fragments.IStudentSearchDebtFragment;

public class StudentSearchDebtFragmentPresenter implements IStudentSearchDebtFragmentPresenter{
    public static int currentStatus = 0;
    private IStudentSearchDebtFragment iStudentSearchDebtFragment;
    private Context context;
    private Storage storage;
    private JSONObject data;
    public StudentSearchDebtFragmentPresenter(IStudentSearchDebtFragment iStudentSearchDebtFragment,Context context,JSONObject data){
        this.iStudentSearchDebtFragment = iStudentSearchDebtFragment;
        this.context = context;
        this.data = data;
        this.storage = new Storage(this.context);
    }

    public void getStudentDebt(final int pos) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                iStudentSearchDebtFragment.showLoadingDialog();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray result = new JSONArray();
                try {
                    Connection.Response res = Jsoup.connect(Helper.BASE_URL + data.getString("urlViewDebt"))
                            .method(Connection.Method.POST)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .userAgent(Helper.USER_AGENT)
                            .cookie("ASP.NET_SessionId", data.getString("cookie"))
                            .data("__EVENTTARGET",data.getString("eventTarget"))
                            .data("__EVENTARGUMENT",data.getString("eventArgument"))
                            .data("__LASTFOCUS",data.getString("lastFocus"))
                            .data("__VIEWSTATE",data.getString("viewState"))
                            .data("__VIEWSTATEGENERATOR",data.getString("viewStartGenerator"))
                            .data("ctl00$ucPhieuKhaoSat1$RadioButtonList1",data.getString("radioBtnList"))
                            .data("ctl00$DdListMenu",data.getString("eventTarget"))
                            .data("ctl00$ContentPlaceHolder$cboHocKy",data.getJSONArray("semesters").getJSONObject(pos).getString("key"))
                            .execute();
                    Document document = res.parse();
                    Elements table = document.getElementsByClass("grid-color2");
                    Elements trs = table.get(0).select("tr");
                    Elements ths = trs.get(0).select("th");
                    JSONArray keys = new JSONArray();
                    for (int i = 1; i < ths.size(); i++) {
                        String keyTmp = Helper.toSlug(ths.get(i).text().trim());
                        keys.put(keyTmp);
                    }
                    for (int i = 1; i < trs.size() - 1; i++) {
                        Elements tds = trs.get(i).select("td");
                        JSONObject subject = new JSONObject();
                        for (int j = 1; j < tds.size(); j++) {
                            String tmp = tds.get(j).text().trim();
                            subject.put(keys.getString(j - 1), tmp);
                        }
                        result.put(subject);
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    currentStatus = Helper.TIMEOUT;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    currentStatus = Helper.NO_CONNECTION;
                } catch (IndexOutOfBoundsException e){

                }catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                switch (currentStatus) {
                    case 400:
                        iStudentSearchDebtFragment.showNetworkErrorLayout();
                        break;
                    case 500:
                        iStudentSearchDebtFragment.showNetworkErrorLayout();
                        break;
                    default: {
                        iStudentSearchDebtFragment.generateTableContent(jsonArray);
                        iStudentSearchDebtFragment.showAllComponent();
                        iStudentSearchDebtFragment.dismissLoadingDialog();
                        currentStatus = 0;
                    }
                }

            }
        };
        asyncTask.execute();
    }

}
