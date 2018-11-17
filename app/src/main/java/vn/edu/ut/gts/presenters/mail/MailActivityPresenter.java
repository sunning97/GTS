package vn.edu.ut.gts.presenters.mail;

import android.content.Context;

import vn.edu.ut.gts.helpers.Storage;
import vn.edu.ut.gts.views.mail.IMailActivity;

public class MailActivityPresenter implements IMailActivityPresenter{
    private IMailActivity iMailActivity;
    private Storage storage;

    public MailActivityPresenter(IMailActivity iMailActivity,Context context){
        this.iMailActivity = iMailActivity;
        this.storage = new Storage(context);
    }
}
