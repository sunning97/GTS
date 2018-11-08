package vn.edu.ut.gts.presenters.mail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.views.mail.fragments.IMailDetailFragment;

public class MailDetailFragmentPresenter implements IMailDetailFragmentPresenter {
    private IMailDetailFragment iMailDetailFragment;
    private Context context;
    private Storage storage;

    public MailDetailFragmentPresenter(IMailDetailFragment iMailDetailFragment, Context context) {
        this.iMailDetailFragment = iMailDetailFragment;
        this.context = context;
        this.storage = new Storage(this.context);
    }

    public void getDetailMail(final JSONObject mail) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, JSONObject> asyncTask = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected void onPreExecute() {
                iMailDetailFragment.hideAllComponent();
            }

            @Override
            protected JSONObject doInBackground(Void... voids) {
                JSONObject mailDetail = new JSONObject();
                try {
                    Connection.Response ress = Jsoup.connect("http://tnbsv.ut.edu.vn/tnb_sv/main.php" + mail.getString("url"))
                            .userAgent(Helper.USER_AGENT)
                            .method(Connection.Method.GET)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .cookie("PHPSESSID", storage.getString("PHPSESSID"))
                            .execute();

                    Document document = ress.parse();

                    Element table = document.getElementsByTag("table").get(2);
                    Elements trs = table.select("tr");
                    mailDetail.put(Helper.toSlug(trs.first().select("td").first().text()), trs.first().select("td").get(1).text());
                    mailDetail.put(Helper.toSlug(trs.get(1).select("td").first().text()), trs.get(1).select("td").get(1).text());
                    mailDetail.put(Helper.toSlug(trs.get(3).select("td").first().text()), trs.get(3).select("td").get(1).text());
                    String content = trs.get(5).select("td").get(1).html();
                    String REGEX_1 = "(.*)<p><a\\shref=\"(.*)\">(.*)<\\/a><\\/p>(.*)";
                    String REGEX_2 = "(.*)<p>(.*)(https:\\/\\/|http:\\/\\/)(.*)<\\/p>(.*)";
                    Pattern pattern = null;
                    Matcher matcher = null;
                    pattern = Pattern.compile(REGEX_2);
                    matcher = pattern.matcher(content);
                    if (matcher.matches()) {
                        String a = matcher.group(3) + matcher.group(4);
                        String[] b = a.split("</p>");
                        if (!b[0].contains("</a>")) {
                            String link = b[0].replace("</a>", "");
                            link = "<a href=\"" + link + "\">" + link + "</a>";
                            content = content.replace(b[0].replace("</a>", ""), link);
                        }
                    }
                    mailDetail.put(Helper.toSlug(trs.get(5).select("td").first().text()), trs.get(5).select("td").get(1).html());
                    if (trs.get(4).select("td").get(1).select("a").size() > 0) {
                        mailDetail.put("has_attach_file", "true");
                        mailDetail.put(Helper.toSlug(trs.get(4).select("td").first().text()), trs.get(4).select("td").get(1).select("a").text());
                        mailDetail.put(Helper.toSlug(trs.get(4).select("td").first().text()) + "_url", trs.get(4).select("td").get(1).select("a").attr("href"));
                    } else mailDetail.put("has_attach_file", "false");


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return mailDetail;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                iMailDetailFragment.setMailDetailContent(jsonObject);
                iMailDetailFragment.showAllComponent();
            }
        };
        asyncTask.execute();
    }

    public void downLoadFile(final String url,final String fileName){
        @SuppressLint("StaticFieldLeak") AsyncTask<Void,Void,Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Connection.Response resultImageResponse;
                    resultImageResponse = Jsoup.connect(url)
                            .userAgent(Helper.USER_AGENT)
                            .method(Connection.Method.GET)
                            .cookie("PHPSESSID", storage.getString("PHPSESSID"))
                            .ignoreContentType(true)
                            .timeout(Helper.TIMEOUT_VALUE)
                            .execute();

                    File file;
                    FileOutputStream outputStream;
                    try {
                        file = new File(Environment.getExternalStorageDirectory()+"/Download", fileName);
                        Log.d("MainActivity", Environment.getExternalStorageDirectory().getAbsolutePath());
                        outputStream = new FileOutputStream(file);
                        outputStream.write(resultImageResponse.bodyAsBytes());
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

            }
        };
        asyncTask.execute();
    }


}
