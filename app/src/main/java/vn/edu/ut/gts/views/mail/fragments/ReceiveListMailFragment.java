package vn.edu.ut.gts.views.mail.fragments;


import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.clans.fab.FloatingActionButton;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.FadingCircle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.adapters.MailRecyclerViewAdapter;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.presenters.home.StudentStudyResultFragmentPresenter;
import vn.edu.ut.gts.presenters.mail.MailActivityPresenter;
import vn.edu.ut.gts.presenters.mail.ReceiveListMailFragmentPresenter;
import vn.edu.ut.gts.views.mail.IMailActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiveListMailFragment extends Fragment implements IReceiveListMailFragment, OnItemClickListener, OnBottomReachedListener {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.new_mail)
    FloatingActionButton newMailBtn;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    @BindView(R.id.spin_kit)
    SpinKitView loadingIcon;

    private MailRecyclerViewAdapter mRcvAdapter;
    private ReceiveListMailFragmentPresenter receiveListMailFragmentPresenter;
    private JSONArray data;
    private OnItemClickListener onItemClickListener;
    private OnDeleteSuccess onDeleteSuccess;
    private IMailActivity iMailActivity;
    private FadingCircle fadingCircle;
    private EpicDialog epicDialog;
    private AlertDialog alertDialog;

    @SuppressLint("ValidFragment")

    public ReceiveListMailFragment(OnItemClickListener onItemClickListener,OnDeleteSuccess onDeleteSuccess,IMailActivity iMailActivity) {
        this.onItemClickListener = onItemClickListener;
        this.onDeleteSuccess = onDeleteSuccess;
        this.iMailActivity = iMailActivity;
        ReceiveListMailFragmentPresenter.currentPage = 2;
    }

    public ReceiveListMailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receive_list_mail, container, false);
        ButterKnife.bind(this, view);
        receiveListMailFragmentPresenter = new ReceiveListMailFragmentPresenter(this, getContext());
        fadingCircle = new FadingCircle();
        loadingIcon.setIndeterminateDrawable(fadingCircle);
        epicDialog = new EpicDialog(getContext());
        epicDialog.initLoadingDialog();

        if (this.data == null) {
            receiveListMailFragmentPresenter.mail();
        } else {
            setupData(data);
            showAllComponent();
        }

        return view;
    }

    @OnClick(R.id.new_mail)
    public void newMail(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    @Override
    public void setupData(JSONArray data) {
        this.data = data;
        mRcvAdapter = new MailRecyclerViewAdapter(data, this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mRcvAdapter);
    }

    @Override
    public void hideAllComponent() {
        mRecyclerView.setVisibility(View.GONE);
        newMailBtn.setVisibility(View.GONE);
    }

    @Override
    public void showAllComponent() {
        mRecyclerView.setVisibility(View.VISIBLE);
        newMailBtn.setVisibility(View.VISIBLE);
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
        if(!epicDialog.isShowing()) epicDialog.showLoadingDialog();
    }

    @Override
    public void dismissLoadingDialog() {
        if(epicDialog.isShowing()) epicDialog.dismisPopup();
    }

    @Override
    public void updateDataListMail(JSONArray data) {
        this.data = data;
        mRcvAdapter.setData(data);
        mRcvAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateDataAfterDelete(int position) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0;i< this.data.length();i++){
            if(i == position) continue;
            try {
                jsonArray.put(this.data.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        updateDataListMail(jsonArray);
        onDeleteSuccess.onDeleteSuccess();
    }

    @Override
    public void showLoadingInMailActivity() {
        iMailActivity.showLoadingDialog();
    }

    @Override
    public void dismissLoadingInMailActivity() {
        iMailActivity.dismissLoadingDialog();
    }

    @Override
    public void showDeleteFailedInMainActivity() {
        iMailActivity.showDeleteFailDialog();
    }

    @Override
    public void onItemClick(View view, int position, JSONObject data) {
        Toolbar toolbar = getActivity().findViewById(R.id.mail_toolbar);
        toolbar.setTitle("");
        this.onItemClickListener.onItemClick(view, position, data);
    }

    @Override
    public void onBottomReached(int position) {
        receiveListMailFragmentPresenter.getMailByPage(ReceiveListMailFragmentPresenter.currentPage,data);
    }

    public void deleteAt(int position){
        try {
            receiveListMailFragmentPresenter.deleteMail(this.data.getJSONObject(position),position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}