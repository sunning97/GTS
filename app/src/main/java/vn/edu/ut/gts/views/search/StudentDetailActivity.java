package vn.edu.ut.gts.views.search;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.adapters.StudentSearchDetailViewPagerAdpater;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.presenters.search.StudentDetailActivityPresenter;


public class StudentDetailActivity extends AppCompatActivity implements IStudentDetailActivity {
    @BindView(R.id.student_search_tablayout)
    TabLayout studentSearchTablayout;
    @BindView(R.id.student_search_view_pager)
    ViewPager studentSearchViewPager;
    @BindView(R.id.search_toolbar)
    Toolbar searchToolbar;
    @BindView(R.id.tablayout_container)
    RelativeLayout tablayoutContainer;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    @BindView(R.id.spin_kit)
    SpinKitView loadingIcon;
    @BindView(R.id.no_internet_layout)
    LinearLayout noInternetLayout;
    @BindView(R.id.retry_text)
    TextView retryText;

    private StudentSearchDetailViewPagerAdpater studentSearchDetailViewPagerAdpater;
    private StudentDetailActivityPresenter studentDetailActivityPresenter;
    private EpicDialog epicDialog;
    private JSONObject data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        ButterKnife.bind(this);
        epicDialog = new EpicDialog(StudentDetailActivity.this);
        epicDialog.initLoadingDialog();
        studentDetailActivityPresenter = new StudentDetailActivityPresenter(this, this);
        searchToolbar.setTitle("");
        setSupportActionBar(searchToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ThreeBounce threeBounce = new ThreeBounce();
        loadingIcon.setIndeterminateDrawable(threeBounce);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        JSONObject data = null;
        try {
            data = new JSONObject(bundle.getString("data"));
            this.data = data;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        studentDetailActivityPresenter.getStudentDetail(data);
    }

    @OnClick(R.id.retry_text)
    public void retry(View view) {
        StudentDetailActivityPresenter.currentStatus = 0;
        studentDetailActivityPresenter.getStudentDetail(this.data);
    }

    public void setStudentDetailData(JSONArray data) {
        try {
            searchToolbar.setTitle(data.getJSONObject(0).getString("student_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.studentSearchDetailViewPagerAdpater = new StudentSearchDetailViewPagerAdpater(getSupportFragmentManager(), data);
        studentSearchViewPager.setAdapter(studentSearchDetailViewPagerAdpater);
        studentSearchTablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        studentSearchTablayout.setTabMode(TabLayout.MODE_FIXED);
        studentSearchTablayout.setupWithViewPager(studentSearchViewPager);
        this.studentSearchTablayout.setScrollPosition(0, 0f, true);
        this.studentSearchViewPager.setCurrentItem(0);
    }

    @Override
    public void showAllComponent() {
        tablayoutContainer.setVisibility(View.VISIBLE);
        studentSearchViewPager.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAllComponent() {
        tablayoutContainer.setVisibility(View.GONE);
        studentSearchViewPager.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingLayout() {
        loadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingLayout() {
        loadingLayout.setVisibility(View.GONE);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
