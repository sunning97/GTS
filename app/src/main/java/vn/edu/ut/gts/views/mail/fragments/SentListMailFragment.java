package vn.edu.ut.gts.views.mail.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.FadingCircle;

import org.json.JSONArray;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.adapters.MailSentRecyclerViewAdapter;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.presenters.mail.SentListMailFragmentPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SentListMailFragment extends Fragment implements ISentListMailFragment{
    @BindView(R.id.sent_list_mail_swipe_refresh)
    SwipeRefreshLayout sentListMailSwipeRefresh;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.new_mail)
    FloatingActionButton newMail;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    @BindView(R.id.no_internet_layout)
    LinearLayout noInternetLayout;
    @BindView(R.id.retry_text)
    TextView retryText;
    @BindView(R.id.spin_kit)
    SpinKitView loadingIcon;

    private SentListMailFragmentPresenter sentListMailFragmentPresenter;
    private MailSentRecyclerViewAdapter mailSentRecyclerViewAdapter;
    private JSONArray data;
    private EpicDialog epicDialog;
    private AlertDialog alertDialog;

    public SentListMailFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sent_list_mail, container, false);
        ButterKnife.bind(this,view);
        SentListMailFragmentPresenter.currentStatus = 0;
        sentListMailFragmentPresenter = new SentListMailFragmentPresenter(this,getContext());
        FadingCircle fadingCircle = new FadingCircle();
        loadingIcon.setIndeterminateDrawable(fadingCircle);
        epicDialog = new EpicDialog(getContext());
        epicDialog.initLoadingDialog();

        if (this.data == null) {
            sentListMailFragmentPresenter.mail();
        } else {
            setupData(data);
            showAllComponent();
        }
        return view;
    }

    @OnClick(R.id.new_mail)
    public void newMail(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle("Oops...");
        builder.setMessage("Chức năng đang trong quá trình phát triển. Sẽ hoàn thiện sớm trong tương lai :)");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @OnClick(R.id.retry_text)
    public void retry() {
        SentListMailFragmentPresenter.currentStatus = 0;
        sentListMailFragmentPresenter.mail();
    }

    @Override
    public void setupData(JSONArray data) {
        this.data = data;
        mailSentRecyclerViewAdapter = new MailSentRecyclerViewAdapter(data);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mailSentRecyclerViewAdapter);
    }

    @Override
    public void hideAllComponent() {
        recyclerView.setVisibility(View.GONE);
        newMail.setVisibility(View.GONE);
    }

    @Override
    public void showAllComponent() {
        recyclerView.setVisibility(View.VISIBLE);
        newMail.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingLayout() {
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingLayout() {
        loadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadingDialog() {

    }

    @Override
    public void dismissLoadingDialog() {

    }

    @Override
    public void showNoInternetLayout() {
        noInternetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoInternetLayout() {
        noInternetLayout.setVisibility(View.GONE);
    }
}
