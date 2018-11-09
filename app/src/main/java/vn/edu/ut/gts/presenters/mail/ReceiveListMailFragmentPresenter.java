package vn.edu.ut.gts.presenters.mail;

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
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.views.mail.fragments.IReceiveListMailFragment;

public class ReceiveListMailFragmentPresenter implements IReceiveListMailFragmentPresenter {
    public static int currentPage = 2;
    private IReceiveListMailFragment iReceiveListMailFragment;
    private Context context;
    private Storage storage;
    private final String REGEX = "window\\.location='(.*)'\\+this\\.value;";
    private Pattern pattern = null;
    private Matcher matcher = null;

    public ReceiveListMailFragmentPresenter(IReceiveListMailFragment iReceiveListMailFragment, Context context) {
        this.iReceiveListMailFragment = iReceiveListMailFragment;
        this.context = context;
        this.storage = new Storage(this.context);
    }

    public void mail() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                iReceiveListMailFragment.hideAllComponent();
                iReceiveListMailFragment.showLoadingLayout();
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

                    Connection.Response ress = Jsoup.connect("http://tnbsv.ut.edu.vn/tnb_sv/main.php?mod=mes_inbox")
                            .userAgent(Helper.USER_AGENT)
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .cookie("PHPSESSID", storage.getString("PHPSESSID"))
                            .execute();
                    Document document = ress.parse();
                    Element form = document.getElementsByTag("form").first();
                    Elements select = document.select("select[name=\"select\"]");
                    pattern = Pattern.compile(REGEX);
                    matcher = pattern.matcher(select.first().attr("onchange"));
                    if(matcher.matches()) {
                        dataMailBox.put("page_url",matcher.group(1));
                    }

                    JSONArray page = new JSONArray();
                    for (int i = 0;i< select.select("option").size();i++){
                        Element option = select.select("option").get(i);
                        page.put(option.attr("value"));
                    }
                    dataMailBox.put("all_page",page);
                    storage.putString("data_mail",dataMailBox.toString());

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
                        Element tdSender = tr.select("td").get(2);
                        Element tdDaySend = tr.select("td").get(3);
                        if (tdTitle.select("img").size() > 0) {
                            mail.put("with_attack_file", String.valueOf(true));
                        }
                        if (tdTitle.hasClass("mes_inbox_read")) {
                            mail.put("readed", String.valueOf(true));
                        } else mail.put("readed", String.valueOf(false));

                        mail.put((String) header.get(0), tdTitle.select("a").first().text());
                        mail.put("url", tdTitle.select("a").first().attr("href"));
                        mail.put((String) header.get(1), tdSender.text());
                        mail.put((String) header.get(2), tdDaySend.text());
                        mails.put(mail);
                        storage.putString("list_mail",mails.toString());
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return mails;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                iReceiveListMailFragment.setupData(jsonArray);
                iReceiveListMailFragment.hideLoadingLayout();
                iReceiveListMailFragment.showAllComponent();
            }
        };
        asyncTask.execute();
    }

    public void getMailByPage(final int page,final JSONArray prevMail){
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                iReceiveListMailFragment.showLoadingDialog();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray mails = new JSONArray();

                try {
                    for (int i = 0;i< prevMail.length();i++){
                        mails.put(prevMail.getJSONObject(i));
                    }

                    JSONObject dataMailBox = new JSONObject(storage.getString("data_mail"));

                    Connection.Response ress = Jsoup.connect("http://tnbsv.ut.edu.vn/tnb_sv/main.php"+dataMailBox.getString("page_url")+page)
                            .userAgent(Helper.USER_AGENT)
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .cookie("PHPSESSID", storage.getString("PHPSESSID"))
                            .execute();
                    Document document = ress.parse();
                    Element form = document.getElementsByTag("form").first();
                    Elements select = document.select("select[name=\"select\"]");
                    pattern = Pattern.compile(REGEX);
                    matcher = pattern.matcher(select.first().attr("onchange"));
                    if(matcher.matches()) {
                        dataMailBox.put("page_url",matcher.group(1));
                    }

                    JSONArray page = new JSONArray();
                    for (int i = 0;i< select.select("option").size();i++){
                        Element option = select.select("option").get(i);
                        page.put(option.attr("value"));
                    }
                    dataMailBox.put("all_page",page);
                    storage.putString("data_mail",dataMailBox.toString());

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
                        Element tdSender = tr.select("td").get(2);
                        Element tdDaySend = tr.select("td").get(3);
                        if (tdTitle.select("img").size() > 0) {
                            mail.put("with_attack_file", String.valueOf(true));
                        }
                        if (tdTitle.hasClass("mes_inbox_read")) {
                            mail.put("readed", String.valueOf(true));
                        } else mail.put("readed", String.valueOf(false));

                        mail.put((String) header.get(0), tdTitle.select("a").first().text());
                        mail.put("url", tdTitle.select("a").first().attr("href"));
                        mail.put((String) header.get(1), tdSender.text());
                        mail.put((String) header.get(2), tdDaySend.text());
                        mails.put(mail);
                        storage.putString("list_mail",mails.toString());
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return mails;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                ReceiveListMailFragmentPresenter.currentPage++;
                iReceiveListMailFragment.updateDataListMail(jsonArray);
                iReceiveListMailFragment.dismissLoadingDialog();
            }
        };
        try {
            JSONObject dataMail = new JSONObject(storage.getString("data_mail"));
            JSONArray pages = dataMail.getJSONArray("all_page");
            if(page <= Integer.parseInt(String.valueOf(pages.get(pages.length()-1)))){
                asyncTask.execute();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteMail(JSONObject jsonObject, final int position){
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                iReceiveListMailFragment.showLoadingInMailActivity();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                iReceiveListMailFragment.dismissLoadingInMailActivity();
                iReceiveListMailFragment.updateDataAfterDelete(position);
            }
        };
        asyncTask.execute();
    }
}
