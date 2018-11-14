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
import vn.edu.ut.gts.helpers.Helper;
import vn.edu.ut.gts.helpers.Storage;
import vn.edu.ut.gts.views.mail.fragments.IMailSentDetailFragment;

public class MailSentDetailFragmentPresenter implements IMailSentDetailFragmentPresenter {
    public static int currentStatus = 0;
    private IMailSentDetailFragment iMailSentDetailFragment;
    private Storage storage;

    public MailSentDetailFragmentPresenter(IMailSentDetailFragment iMailSentDetailFragment, Context context) {
        this.iMailSentDetailFragment = iMailSentDetailFragment;
        this.storage = new Storage(context);
    }


    public void getDetailMail(final JSONObject mail) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONObject> asyncTask = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected void onPreExecute() {
                iMailSentDetailFragment.hideAllComponent();
                iMailSentDetailFragment.hideNoInternetLayout();
                iMailSentDetailFragment.showLoadingDialog();
            }

            @Override
            protected JSONObject doInBackground(Void... voids) {
                JSONObject mailDetail = new JSONObject();
                try {
                    Connection.Response res = Jsoup.connect("http://tnbsv.ut.edu.vn/tnb_sv/main.php" + mail.getString("url"))
                            .userAgent(Helper.USER_AGENT)
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .cookie("PHPSESSID", storage.getString("PHPSESSID"))
                            .execute();

                    Document document = res.parse();

                    Element table = document.getElementsByTag("table").get(2);
                    Elements trs = table.select("tr");
                    mailDetail.put(Helper.toSlug(trs.first().select("td").first().text()), trs.first().select("td").get(1).text());
                    mailDetail.put(Helper.toSlug(trs.get(1).select("td").first().text()), trs.get(1).select("td").get(1).text());
                    mailDetail.put(Helper.toSlug(trs.get(2).select(".page_header").first().text()), trs.get(2).select("td").get(1).text());
                    mailDetail.put(Helper.toSlug(trs.get(3).select(".page_header").first().text()), trs.get(3).select("td").get(1).text());
                    mailDetail.put(Helper.toSlug(trs.get(5).select("td").first().text()), trs.get(5).select("td").get(1).html());
                    if (trs.get(4).select("td").get(1).select("a").size() > 0) {
                        mailDetail.put("has_attach_file", "true");
                        JSONArray attachFiles = new JSONArray();
                        Elements links = trs.get(4).select("td").get(1).select("a");
                        for (int i = 0; i < links.size(); i++) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(Helper.toSlug(trs.get(4).select("td").first().text()), links.get(i).text());
                            jsonObject.put(Helper.toSlug(trs.get(4).select("td").first().text()) + "_url", links.get(i).attr("href"));
                            attachFiles.put(jsonObject);
                        }
                        mailDetail.put(Helper.toSlug(trs.get(4).select("td").first().text()),attachFiles);
                    } else mailDetail.put("has_attach_file", "false");

                } catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return mailDetail;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                switch (currentStatus){
                    case 400:{
                        iMailSentDetailFragment.hideAllComponent();
                        iMailSentDetailFragment.showNoInternetLayout();
                        iMailSentDetailFragment.hideLoadingDialog();
                        break;
                    }
                    case 500:{
                        iMailSentDetailFragment.hideAllComponent();
                        iMailSentDetailFragment.showNoInternetLayout();
                        iMailSentDetailFragment.hideLoadingDialog();
                        break;
                    }
                    default:{
                        iMailSentDetailFragment.setMailDetailContent(jsonObject);
                        iMailSentDetailFragment.hideNoInternetLayout();
                        iMailSentDetailFragment.showAllComponent();
                        iMailSentDetailFragment.hideLoadingDialog();
                    }
                }
            }
        };
        asyncTask.execute();
    }
}
