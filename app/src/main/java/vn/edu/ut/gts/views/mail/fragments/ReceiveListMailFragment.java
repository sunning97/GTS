package vn.edu.ut.gts.views.mail.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
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

import com.github.clans.fab.FloatingActionButton;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.FadingCircle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.adapters.MailRecyclerViewAdapter;
import vn.edu.ut.gts.presenters.home.StudentStudyResultFragmentPresenter;
import vn.edu.ut.gts.presenters.mail.MailActivityPresenter;
import vn.edu.ut.gts.presenters.mail.ReceiveListMailFragmentPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiveListMailFragment extends Fragment implements IReceiveListMailFragment,OnItemClickListener{
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
    private FadingCircle fadingCircle;
    @SuppressLint("ValidFragment")

    public ReceiveListMailFragment(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ReceiveListMailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receive_list_mail, container, false);
        ButterKnife.bind(this,view);
        receiveListMailFragmentPresenter = new ReceiveListMailFragmentPresenter(this,getContext());
        fadingCircle = new FadingCircle();
        loadingIcon.setIndeterminateDrawable(fadingCircle);
        if(this.data == null){
            receiveListMailFragmentPresenter.mail();
        } else {
            setupData(data);
            showAllComponent();
        }
        return view;
    }

    @Override
    public void setupData(JSONArray data) {
        this.data = data;
        mRcvAdapter = new MailRecyclerViewAdapter(data,this);
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
    public void onItemClick(View view, int position,JSONObject data) {
        Toolbar toolbar = getActivity().findViewById(R.id.mail_toolbar);
        toolbar.setTitle("");
        this.onItemClickListener.onItemClick(view,position,data);
    }
}
