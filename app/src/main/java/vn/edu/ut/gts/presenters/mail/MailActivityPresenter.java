package vn.edu.ut.gts.presenters.mail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;

import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.views.mail.IMailActivity;

public class MailActivityPresenter implements IMailActivityPresenter{
    private IMailActivity iMailActivity;
    private Context context;
    private Storage storage;

    public MailActivityPresenter(IMailActivity iMailActivity,Context context){
        this.iMailActivity = iMailActivity;
        this.context = context;
        this.storage = new Storage(this.context);
    }

}
