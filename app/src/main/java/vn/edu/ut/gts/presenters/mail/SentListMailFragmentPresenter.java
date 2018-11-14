package vn.edu.ut.gts.presenters.mail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.edu.ut.gts.helpers.Helper;
import vn.edu.ut.gts.helpers.Storage;
import vn.edu.ut.gts.views.mail.fragments.ISentListMailFragment;

public class SentListMailFragmentPresenter implements ISentListMailFragmentPresenter {
    public static int currentStatus = 0;
    private ISentListMailFragment iSentListMailFragment;
    private Storage storage;
    private final String REGEX = "window\\.location='(.*)'\\+this\\.value;";
    private Pattern pattern = null;
    private Matcher matcher = null;


    public SentListMailFragmentPresenter(ISentListMailFragment iSentListMailFragment, Context context) {
        this.iSentListMailFragment = iSentListMailFragment;
        this.storage = new Storage(context);
    }

    public void mail() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                iSentListMailFragment.hideAllComponent();
                iSentListMailFragment.hideNoInternetLayout();
                iSentListMailFragment.showLoadingLayout();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONObject dataMailBox = new JSONObject();
                JSONArray mails = new JSONArray();
                try {
                    Connection.Response res = Jsoup.connect("https://sv.ut.edu.vn/Sso.aspx?MenuID=417")
                            .userAgent(Helper.USER_AGENT)
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .cookie("ASP.NET_SessionId", storage.getCookie())
                            .execute();

                    storage.putString("PHPSESSID", res.cookie("PHPSESSID"));

                    Connection.Response ress = Jsoup.connect("http://tnbsv.ut.edu.vn/tnb_sv/main.php?mod=mes_sent")
                            .userAgent(Helper.USER_AGENT)
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .cookie("PHPSESSID", storage.getString("PHPSESSID"))
                            .execute();

                    Document document = ress.parse();
                    Element form = document.getElementsByTag("form").first();
                    Elements select = document.select("select");

                    pattern = Pattern.compile(REGEX);
                    matcher = pattern.matcher(select.get(1).attr("onchange"));
                    if (matcher.matches()) {
                        dataMailBox.put("page_url", matcher.group(1));
                    }

                    JSONArray page = new JSONArray();
                    for (int i = 0; i < select.get(1).select("option").size(); i++) {
                        Element option = select.get(1).select("option").get(i);
                        page.put(option.attr("value"));
                    }
                    dataMailBox.put("all_page", page);
                    storage.putString("data_mail_sent", dataMailBox.toString());

                    Elements trs = form.getElementsByTag("tr");
                    Element trHeader = trs.get(0);
                    JSONArray header = new JSONArray();

                    for (int i = 1; i < trHeader.select("td").size(); i++) {
                        header.put(Helper.toSlug(trHeader.select("td").get(i).text()));
                    }

                    for (int i = 1; i < trs.size() - 1; i++) {
                        JSONObject mail = new JSONObject();
                        Element tr = trs.get(i);
                        Element tdTitle = tr.select("td").get(1);
                        Element tdReceiver = tr.select("td").get(2);
                        Element tdDaySend = tr.select("td").get(3);
                        if (tdTitle.select("img").size() > 0) {
                            mail.put("with_attack_file", String.valueOf(true));
                        }

                        mail.put((String) header.get(0), tdTitle.select("a").first().text());
                        mail.put("url", tdTitle.select("a").first().attr("href"));
                        mail.put((String) header.get(1), tdReceiver.text());
                        mail.put((String) header.get(2), tdDaySend.text());
                        mails.put(mail);
                    }
                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return mails;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                switch (SentListMailFragmentPresenter.currentStatus) {
                    case 400: {
                        iSentListMailFragment.showNoInternetLayout();
                        iSentListMailFragment.hideLoadingLayout();
                        break;
                    }
                    case 500: {
                        iSentListMailFragment.showNoInternetLayout();
                        iSentListMailFragment.hideLoadingLayout();
                        break;
                    }
                    default: {
                        iSentListMailFragment.setupData(jsonArray);
                        iSentListMailFragment.hideLoadingLayout();
                        iSentListMailFragment.hideNoInternetLayout();
                        iSentListMailFragment.showAllComponent();
                    }
                }

            }
        };
        asyncTask.execute();
    }
}
