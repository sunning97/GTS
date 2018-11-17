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
import vn.edu.ut.gts.views.mail.fragments.IReceiveListMailFragment;
import vn.edu.ut.gts.views.mail.fragments.ReceiveListMailFragment;

public class ReceiveListMailFragmentPresenter implements IReceiveListMailFragmentPresenter {
    public static int currentStatus = 0;
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

    public void getListMail() {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                if(!ReceiveListMailFragment.isReferesh){
                    iReceiveListMailFragment.hideNoInternetLayout();
                    iReceiveListMailFragment.hideAllComponent();
                    iReceiveListMailFragment.showLoadingLayout();
                }
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
                    if (matcher.matches()) {
                        dataMailBox.put("page_url", matcher.group(1));
                    }

                    JSONArray page = new JSONArray();
                    for (int i = 0; i < select.select("option").size(); i++) {
                        Element option = select.select("option").get(i);
                        page.put(option.attr("value"));
                    }
                    dataMailBox.put("all_page", page);
                    storage.putString("data_mail", dataMailBox.toString());

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
                        storage.putString("list_mail", mails.toString());
                    }
                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (NullPointerException | IndexOutOfBoundsException | IOException | JSONException e) {
                    e.printStackTrace();
                }
                return mails;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                switch (ReceiveListMailFragmentPresenter.currentStatus) {
                    case 400: {
                        if(ReceiveListMailFragment.isReferesh) {
                            iReceiveListMailFragment.refreshComplete();
                            ReceiveListMailFragment.isReferesh = false;
                        } else {
                            iReceiveListMailFragment.showNoInternetLayout();
                            iReceiveListMailFragment.hideLoadingLayout();
                            iReceiveListMailFragment.disableRefresh();
                        }
                        break;
                    }
                    case 500: {
                        if(ReceiveListMailFragment.isReferesh) {
                            iReceiveListMailFragment.refreshComplete();
                            ReceiveListMailFragment.isReferesh = false;
                        } else {
                            iReceiveListMailFragment.showNoInternetLayout();
                            iReceiveListMailFragment.hideLoadingLayout();
                            iReceiveListMailFragment.disableRefresh();
                        }
                        break;
                    }
                    default: {
                        if(ReceiveListMailFragment.isReferesh) {
                            iReceiveListMailFragment.refreshComplete();
                            ReceiveListMailFragment.isReferesh = false;
                        }
                        iReceiveListMailFragment.enableRefresh();
                        iReceiveListMailFragment.setupData(jsonArray);
                        iReceiveListMailFragment.hideNoInternetLayout();
                        iReceiveListMailFragment.hideLoadingLayout();
                        iReceiveListMailFragment.showAllComponent();
                    }
                }
            }
        };
        asyncTask.execute();
    }

    public void getListMail(final int page, final JSONArray prevMail) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                ReceiveListMailFragmentPresenter.currentStatus = 0;
                iReceiveListMailFragment.showLoadingDialog();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray mails = new JSONArray();

                try {
                    for (int i = 0; i < prevMail.length(); i++) {
                        mails.put(prevMail.getJSONObject(i));
                    }

                    JSONObject dataMailBox = new JSONObject(storage.getString("data_mail"));

                    Connection.Response ress = Jsoup.connect("http://tnbsv.ut.edu.vn/tnb_sv/main.php" + dataMailBox.getString("page_url") + page)
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
                    if (matcher.matches()) {
                        dataMailBox.put("page_url", matcher.group(1));
                    }

                    JSONArray page = new JSONArray();
                    for (int i = 0; i < select.select("option").size(); i++) {
                        Element option = select.select("option").get(i);
                        page.put(option.attr("value"));
                    }
                    dataMailBox.put("all_page", page);
                    storage.putString("data_mail", dataMailBox.toString());

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
                    }
                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (NullPointerException | IndexOutOfBoundsException | IOException | JSONException e) {
                    e.printStackTrace();
                }
                return mails;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                switch (ReceiveListMailFragmentPresenter.currentStatus) {
                    case 400: {
                        iReceiveListMailFragment.dismissLoadingDialog();
                        break;
                    }
                    case 500: {
                        iReceiveListMailFragment.dismissLoadingDialog();
                        break;
                    }
                    default: {
                        ReceiveListMailFragmentPresenter.currentPage++;
                        iReceiveListMailFragment.updateDataListMail(jsonArray);
                        iReceiveListMailFragment.dismissLoadingDialog();
                    }
                }
            }
        };
        try {
            JSONObject dataMail = new JSONObject(storage.getString("data_mail"));
            JSONArray pages = dataMail.getJSONArray("all_page");
            if (page <= Integer.parseInt(String.valueOf(pages.get(pages.length() - 1)))) {
                asyncTask.execute();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteMail(JSONObject jsonObject, final int position) {
        String urlDelete = "";
        String regex = "\\?mod=mes_view&id=(.*)&t=0";
        Pattern pattern = null;
        Matcher matcher = null;
        pattern = Pattern.compile(regex);
        try {
            matcher = pattern.matcher(jsonObject.getString("url"));
            if (matcher.matches()) {
                urlDelete = "?mod=mes_inbox_del&id=" + matcher.group(1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String finalUrlDelete = urlDelete;
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                iReceiveListMailFragment.showLoadingInMailActivity();
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                Boolean result = false;
                try {
                    Connection.Response ress = Jsoup.connect("http://tnbsv.ut.edu.vn/tnb_sv/main.php" + finalUrlDelete)
                            .userAgent(Helper.USER_AGENT)
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .cookie("PHPSESSID", storage.getString("PHPSESSID"))
                            .execute();

                    Document document = ress.parse();
                    Elements success = document.getElementsByClass("style1");
                    if (success.size() > 0) {
                        result = true;
                    }
                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (NullPointerException | IndexOutOfBoundsException | IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                iReceiveListMailFragment.dismissLoadingInMailActivity();
                if (result) {
                    iReceiveListMailFragment.updateDataAfterDelete(position);
                } else
                    iReceiveListMailFragment.showDeleteFailedInMainActivity();
            }
        };
        asyncTask.execute();
    }
}
