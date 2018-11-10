package vn.edu.ut.gts.presenters.mail;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

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
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.views.mail.fragments.IMailDetailFragment;

public class MailDetailFragmentPresenter implements IMailDetailFragmentPresenter {
    public static int currentStatus = 0;
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
                iMailDetailFragment.showLoadingDialog();
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
                switch (MailDetailFragmentPresenter.currentStatus) {
                    case 400: {
                        iMailDetailFragment.showNoInternetLayout();
                        break;
                    }
                    case 500: {
                        iMailDetailFragment.showNoInternetLayout();
                        break;
                    }
                    default: {
                        iMailDetailFragment.setMailDetailContent(jsonObject);
                        iMailDetailFragment.hideNoInternetLayout();
                        iMailDetailFragment.showAllComponent();
                    }
                }
                iMailDetailFragment.hideLoadingDialog();
            }
        };
        asyncTask.execute();
    }

    public void downLoadFile(final String url, final String fileName) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setSmallIcon(R.drawable.ic_file_download_white_24dp)
                        .setContentTitle("Đang tải xuống")
                        .setContentText("")
                        .setProgress(0, 0, true)
                        .setAutoCancel(false);
                Notification notification = builder.build();
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification);
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
                        file = new File(Environment.getExternalStorageDirectory() + "/Download", fileName);
                        Log.d("MainActivity", Environment.getExternalStorageDirectory().getAbsolutePath());
                        outputStream = new FileOutputStream(file);
                        outputStream.write(resultImageResponse.bodyAsBytes());
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }catch (SocketTimeoutException e) {
                    currentStatus = Helper.TIMEOUT;
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    currentStatus = Helper.NO_CONNECTION;
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(currentStatus == Helper.TIMEOUT || currentStatus == Helper.NO_CONNECTION){
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                    builder.setSmallIcon(R.drawable.ic_close_white_24dp)
                            .setContentTitle("Tải xuống không thành công :(")
                            .setContentText("Kiểm tra kết nối Internet")
                            .setAutoCancel(true);
                    Notification notification = builder.build();
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                    notificationManager.notify(2, notification);
                } else {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                    builder.setSmallIcon(R.drawable.ic_check_white_24dp)
                            .setContentTitle("Đã tải xong")
                            .setContentText(fileName)
                            .setAutoCancel(true);
                    Notification notification = builder.build();
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                    notificationManager.notify(2, notification);
                    try {
                        String a = fileName;
                        String[] b = a.split("\\.(?=[^\\.]+$)");
                        File file = new File(Environment.getExternalStorageDirectory() + "/Download/" + fileName);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        MimeTypeMap myMime = MimeTypeMap.getSingleton();
                        String mimeType = myMime.getMimeTypeFromExtension(b[1]);
                        intent.setDataAndType(Uri.fromFile(file), mimeType);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "Không tìm thấy ứng dụng phù hợp để mở", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        asyncTask.execute();
    }
}
