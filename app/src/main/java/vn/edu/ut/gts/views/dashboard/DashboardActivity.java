package vn.edu.ut.gts.views.dashboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.elyeproj.loaderviewlibrary.LoaderImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.helpers.OnClearFromRecentService;
import vn.edu.ut.gts.presenters.dashboard.DashboardPresenter;
import vn.edu.ut.gts.views.home.HomeActivity;
import vn.edu.ut.gts.views.login.LoginActivity;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener, IDashboardActivity {
    @BindView(R.id.dashboard_toolbar)
    Toolbar dashboardToolbar;
    @BindView(R.id.dashboard_appbar_layout)
    AppBarLayout dashboardAppbarLayout;
    @BindView(R.id.dashboard_collapsing_toolbar_alyout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.student_info_card)
    CardView studentInfoCard;
    @BindView(R.id.student_study_result_card)
    CardView studentStudyResultCard;
    @BindView(R.id.frame_program_card)
    CardView frameProgramCard;
    @BindView(R.id.student_debt_card)
    CardView studentDebtCard;
    @BindView(R.id.schedule_by_week_card)
    CardView scheduleByWeekCard;
    @BindView(R.id.attendance_card)
    CardView attendanceCard;
    @BindView(R.id.profile_image_loading)
    LoaderImageView profileImageLoading;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.swipe_refresh_dashboard)
    SwipeRefreshLayout swipeRefreshDashboard;

    private Storage storage;
    private DashboardPresenter dashboardPresenter;
    private SweetAlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        storage = new Storage(DashboardActivity.this);
        dashboardPresenter = new DashboardPresenter(this, this);
        swipeRefreshDashboard.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshDashboard.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(DashboardPresenter.currentStatus !=0){
                    swipeRefreshDashboard.setRefreshing(true);
                    dashboardPresenter.go();
                }
            }
        });
        setSupportActionBar(dashboardToolbar);
        if (TextUtils.isEmpty(storage.getString("student_info"))) {
            profileImage.setVisibility(View.INVISIBLE);
            profileImageLoading.resetLoader();
            collapsingToolbarLayout.setTitle("");
            dashboardPresenter.go();
        } else {
            disableSwipeRefresh();
            setStudentPortrait(dashboardPresenter.getStudentPortraitFromStorage());
            setToolbarTitle(dashboardPresenter.getStudentNameFromStorate());
        }

        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout: {
                storage.deleteAllsharedPreferences(DashboardActivity.this);
                storage.putString("is_remember_pass",String.valueOf(false));
                HomeActivity.isLogin = false;
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                break;
            }
        }
        return true;
    }

    @OnClick({
            R.id.student_info_card,
            R.id.student_study_result_card,
            R.id.frame_program_card,
            R.id.student_debt_card,
            R.id.schedule_by_week_card,
            R.id.attendance_card
    })
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.student_info_card:
                startActivity(HomeActivity.STUDENT_INFO);
                break;
            case R.id.student_study_result_card:
                startActivity(HomeActivity.STUDENT_STUDY_RESULT);
                break;
            case R.id.frame_program_card:
                startActivity(HomeActivity.FRAME_PROGRAM);
                break;
            case R.id.student_debt_card:
                startActivity(HomeActivity.STUDENT_DEBT);
                break;
            case R.id.schedule_by_week_card:
                startActivity(HomeActivity.SCHEDULE_BY_WEEK);
                break;
            case R.id.attendance_card:
                startActivity(HomeActivity.ATTENDACE);
                break;
            default:
                return;
        }
    }

    private void startActivity(int order) {
        Intent intent = new Intent(DashboardActivity.this, HomeActivity.class);
        intent.putExtra("order", String.valueOf(order));
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Xác nhận thoát?")
                .setCancelText("Hủy")
                .setConfirmText("Xác nhận")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        storage.deleteAllsharedPreferences(DashboardActivity.this);
                        DashboardActivity.this.finishAffinity();
                    }
                })
                .show();
    }

    @Override
    public void setToolbarTitle(String title) {
        collapsingToolbarLayout.setTitle(title);
    }

    @Override
    public void setStudentPortrait(Bitmap studentPortrait) {
        profileImage.setImageBitmap(studentPortrait);
        profileImageLoading.setVisibility(View.INVISIBLE);
        profileImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadingDialog() {
        loadingDialog.show();
    }

    @Override
    public void dismisLoadingDialog() {
        loadingDialog.dismiss();
    }

    @Override
    public void enableAll() {
        studentInfoCard.setEnabled(true);
        studentStudyResultCard.setEnabled(true);
        frameProgramCard.setEnabled(true);
        studentDebtCard.setEnabled(true);
        scheduleByWeekCard.setEnabled(true);
        attendanceCard.setEnabled(true);
    }

    @Override
    public void disableAll() {
        studentInfoCard.setEnabled(false);
        studentStudyResultCard.setEnabled(false);
        frameProgramCard.setEnabled(false);
        studentDebtCard.setEnabled(false);
        scheduleByWeekCard.setEnabled(false);
        attendanceCard.setEnabled(false);
    }

    @Override
    public void startLoading() {

    }

    @Override
    public void finishLoading() {

    }

    @Override
    public void showErrorDialog() {
        swipeRefreshDashboard.setEnabled(true);
        swipeRefreshDashboard.setRefreshing(false);
        new SweetAlertDialog(this)
                .setTitleText(getResources().getString(R.string.no_internet_access_title))
                .setContentText(getResources().getString(R.string.no_internet_access_content))
                .show();
    }

    @Override
    public void showTimeOutDialog() {
        swipeRefreshDashboard.setEnabled(true);
        swipeRefreshDashboard.setRefreshing(false);
        new SweetAlertDialog(this)
                .setTitleText(getResources().getString(R.string.connect_timeout_dialog_title))
                .setContentText(getResources().getString(R.string.connect_timeout_dialog_content))
                .show();
    }

    @Override
    public void disableSwipeRefresh() {
        swipeRefreshDashboard.setEnabled(false);
    }

    @Override
    public void enableSwipeRefresh() {
        swipeRefreshDashboard.setEnabled(true);
    }

    @Override
    public void resetLoaderImage() {
        profileImageLoading.resetLoader();
    }


}
