package vn.edu.ut.gts.views.mail.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.presenters.home.FrameProgramFragmentPresenter;
import vn.edu.ut.gts.presenters.mail.MailDetailFragmentPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class MailDetailFragment extends Fragment implements IMailDetailFragment,OnMailDeleteClick {
    @BindView(R.id.mail_detail)
    NestedScrollView mailDetail;
    @BindView(R.id.mail_title)
    TextView mailTitle;
    @BindView(R.id.mail_circle)
    TextView mailCircle;
    @BindView(R.id.sender)
    TextView sender;
    @BindView(R.id.sent_time)
    TextView sentTime;
    @BindView(R.id.mail_content)
    TextView mailContent;
    @BindView(R.id.attach_file_tv)
    TextView attachFileTV;
    @BindView(R.id.attach_file_card_view)
    CardView attachFileCardView;
    @BindView(R.id.attach_file_name)
    TextView attachFileName;

    private JSONObject data;
    private JSONObject dataDetail;
    private MailDetailFragmentPresenter mailDetailFragmentPresenter;
    private OnMailDeleteClick onMailDeleteClick;
    private Context context;
    private Drawable drawable;
    private int position;
    private AlertDialog alertDialog;
    @SuppressLint("ValidFragment")

    public MailDetailFragment(OnMailDeleteClick onMailDeleteClick,JSONObject data, Context context, Drawable drawable,int position) {
        this.context = context;
        this.data = data;
        this.drawable = drawable;
        this.position = position;
        this.onMailDeleteClick = onMailDeleteClick;
    }

    public MailDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mail_detail, container, false);
        ButterKnife.bind(this, view);
        mailDetailFragmentPresenter = new MailDetailFragmentPresenter(this, this.context);
        mailDetailFragmentPresenter.getDetailMail(data);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_mail_detail_toolbar_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_mail: {
                onClickDelete(position);
                break;
            }
        }
        return true;
    }

    @OnClick(R.id.attach_file_card_view)
    public void goDownloadFile(View view) {
        try {
            String url = "http://tnbsv.ut.edu.vn/tnb_sv/" + dataDetail.getString("file_dinh_kem_url");
            String fileName = dataDetail.getString("file_dinh_kem");
            mailDetailFragmentPresenter.downLoadFile(url, fileName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMailDetailContent(JSONObject jsonObject) {
        dataDetail = jsonObject;
        try {
            mailTitle.setText(jsonObject.getString("tieu_de"));
            sender.setText(jsonObject.getString("nguoi_gui"));
            sentTime.setText(jsonObject.getString("ngay_gui"));
            mailContent.setText(Html.fromHtml(jsonObject.getString("noi_dung")));
            mailCircle.setBackground(drawable);
            mailCircle.setText(String.valueOf(jsonObject.getString("nguoi_gui").charAt(0)));
            mailContent.setClickable(true);
            mailContent.setMovementMethod(LinkMovementMethod.getInstance());
            if (Boolean.valueOf(jsonObject.getString("has_attach_file"))) {
                attachFileTV.setVisibility(View.VISIBLE);
                attachFileName.setText(jsonObject.getString("file_dinh_kem"));
                attachFileCardView.setVisibility(View.VISIBLE);
            } else {
                attachFileTV.setVisibility(View.GONE);
                attachFileName.setText("");
                attachFileCardView.setVisibility(View.GONE);
            }
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
    public void onClickDelete(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn muốn xóa thư này?");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onMailDeleteClick.onClickDelete(position);
                alertDialog.dismiss();
            }
        });
        builder.setNegativeButton("Hủy",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
    }
}
