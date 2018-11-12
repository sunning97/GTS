package vn.edu.ut.gts.views.mail.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.presenters.mail.MailSentDetailFragmentPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class MailSentDetailFragment extends Fragment implements IMailSentDetailFragment{
    @BindView(R.id.mail_detail)
    NestedScrollView mailDetail;
    @BindView(R.id.mail_title)
    TextView mailTitle;
    @BindView(R.id.mail_circle)
    TextView mailCircle;
    @BindView(R.id.sender)
    TextView sender;
    @BindView(R.id.sent_to)
    TextView sentTo;
    @BindView(R.id.sent_time)
    TextView sentTime;
    @BindView(R.id.mail_content)
    TextView mailContent;
    @BindView(R.id.attach_file_tv)
    TextView attachFileTV;
    @BindView(R.id.attach_file_card_view)
    CardView attachFileCardView;
    @BindView(R.id.no_internet_layout)
    LinearLayout noInternetLayout;
    @BindView(R.id.retry_text)
    TextView retryText;


    private Context context;
    private Drawable drawable;
    private int position;
    private AlertDialog alertDialog;
    EpicDialog epicDialog;
    private JSONObject data;
    private JSONObject dataDetail;
    private MailSentDetailFragmentPresenter mailSentDetailFragmentPresenter;

    public MailSentDetailFragment() {
    }

    @SuppressLint("ValidFragment")
    public MailSentDetailFragment(JSONObject data, Context context, Drawable drawable, int position) {
        this.data = data;
        this.context = context;
        this.drawable = drawable;
        this.position = position;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_mail_sent_detail, container, false);
        ButterKnife.bind(this,view);
        epicDialog = new EpicDialog(context);
        epicDialog.initLoadingDialog();
        mailSentDetailFragmentPresenter = new MailSentDetailFragmentPresenter(this,this.context);
        mailSentDetailFragmentPresenter.getDetailMail(this.data);
        return  view;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void setMailDetailContent(JSONObject jsonObject) {
        dataDetail = jsonObject;
        try {
            mailContent.setText(Html.fromHtml(jsonObject.getString("noi_dung")));
            mailCircle.setBackground(drawable);
            mailCircle.setText(String.valueOf(jsonObject.getString("nguoi_gui").charAt(0)));
            mailContent.setClickable(true);
            mailContent.setMovementMethod(LinkMovementMethod.getInstance());
            sender.setText(jsonObject.getString("nguoi_gui"));
            sentTime.setText(jsonObject.getString("ngay_gui"));
            sentTo.setText("tới "+jsonObject.getString("nguoi_nhan"));
            mailTitle.setText(jsonObject.getString("tieu_de"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hideAllComponent() {
        mailDetail.setVisibility(View.GONE);
    }

    @Override
    public void showAllComponent() {
        mailDetail.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoInternetLayout() {
        noInternetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoInternetLayout() {
        noInternetLayout.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingDialog() {
        epicDialog.showLoadingDialog();
    }

    @Override
    public void hideLoadingDialog() {
        epicDialog.dismisPopup();
    }
}
