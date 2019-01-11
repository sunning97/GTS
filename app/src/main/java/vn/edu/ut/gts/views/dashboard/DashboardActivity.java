package vn.edu.ut.gts.views.dashboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.elyeproj.loaderviewlibrary.LoaderImageView;
import com.elyeproj.loaderviewlibrary.LoaderTextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.helpers.Storage;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.helpers.OnClearFromRecentService;
import vn.edu.ut.gts.presenters.dashboard.DashboardPresenter;
import vn.edu.ut.gts.views.home.HomeActivity;
import vn.edu.ut.gts.views.login.LoginActivity;
import vn.edu.ut.gts.views.mail.MailActivity;
import vn.edu.ut.gts.views.search.StudentSearchActivity;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener, IDashboardActivity,NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.dashboard_toolbar)
    Toolbar dashboardToolbar;
    @BindView(R.id.dashboard_drawerlayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.dashboard_navigation_view)
    NavigationView navigationView;
    @BindView(R.id.dashboard_appbar_layout)
    AppBarLayout dashboardAppbarLayout;
    @BindView(R.id.dashboard_collapsing_toolbar_layout)
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
    @BindView(R.id.dashboard_profile_image)
    CircleImageView dashboardProfileImage;
    @BindView(R.id.student_name_text_loading)
    LoaderTextView studentNameTextLoading;
    @BindView(R.id.swipe_refresh_dashboard)
    SwipeRefreshLayout swipeRefreshDashboard;
    @BindView(R.id.dashboard_scroll)
    NestedScrollView dashboardScroll;
    @BindView(R.id.student_name)
    TextView navigationStudentFullName;
    @BindView(R.id.profile_image)
    CircleImageView navigationProfileImage;
    @BindView(R.id.student_id)
    TextView navigationStudentID;


    private Storage storage;
    private DashboardPresenter dashboardPresenter;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private EpicDialog epicDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        DashboardPresenter.isFirst = true;
        storage = new Storage(DashboardActivity.this);
        epicDialog = new EpicDialog(this);
        dashboardToolbar.setTitle("");
        collapsingToolbarLayout.setTitle("");
        dashboardPresenter = new DashboardPresenter(this, this);

        setSupportActionBar(dashboardToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,dashboardToolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        swipeRefreshDashboard.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshDashboard.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DashboardPresenter.isFirst = false;
                swipeRefreshDashboard.setRefreshing(true);
                DashboardPresenter.currentStatus = 0;
                dashboardPresenter.go();
            }
        });

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.home_dashboard);
        menuItem.setChecked(true);

        dashboardScroll.setSmoothScrollingEnabled(true);
        dashboardScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0 && DashboardPresenter.currentStatus != 0) {
                    swipeRefreshDashboard.setEnabled(true);
                } else swipeRefreshDashboard.setEnabled(false);
            }
        });
        if (!storage.isImageExist(getApplicationContext(), "student_portrait.jpg") || TextUtils.isEmpty(storage.getString("student_info"))) {
            dashboardProfileImage.setVisibility(View.INVISIBLE);
            dashboardPresenter.go();
        } else {
            hideLoaderTextView();
            disableSwipeRefresh();
            setStudentPortrait(dashboardPresenter.getStudentPortraitFromStorage());
            setToolbarTitle(dashboardPresenter.getStudentNameFromStorage());
            setUpNavigationData(dashboardPresenter.getStudentPortraitFromStorage(),dashboardPresenter.getStudentNameFromStorage(),dashboardPresenter.getStudentIDFromStorage());
        }
    }

    @OnClick({
            R.id.student_info_card,
            R.id.student_study_result_card,
            R.id.frame_program_card,
            R.id.student_debt_card,
            R.id.schedule_by_week_card,
            R.id.attendance_card,
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
                startActivity(HomeActivity.ATTENDANCE);
                break;
            default:
        }
    }

    private void startActivity(int order) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("order", String.valueOf(order));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getResources().getString(R.string.confirm_exit_app_title))
                .setCancelText(getResources().getString(R.string.cancel_text))
                .setConfirmText(getResources().getString(R.string.confirm_text))
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        LoginActivity.isLogout = false;
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
        dashboardProfileImage.setImageBitmap(studentPortrait);
        profileImageLoading.setVisibility(View.INVISIBLE);
        dashboardProfileImage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadingDialog() {

    }

    @Override
    public void dismissLoadingDialog() {

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

    @Override
    public void resetLoaderTextView() {
        studentNameTextLoading.resetLoader();
    }

    @Override
    public void hideLoaderTextView() {
        studentNameTextLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showLoaderTextView() {
        studentNameTextLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void setDefaultPortrait() {
        dashboardProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_deafult_100));
        dashboardProfileImage.setVisibility(View.VISIBLE);
        profileImageLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setRefreshingSwipe(boolean value) {
        swipeRefreshDashboard.setRefreshing(value);
    }

    @Override
    public void setUpNavigationData(Bitmap image,String name,String ID) {
        navigationProfileImage.setImageBitmap(image);
        navigationStudentFullName.setText(name);
        navigationStudentID.setText(ID);
    }

    @Override
    public void setDefaultNavigationImage() {
        navigationProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_deafult_100));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(DashboardPresenter.currentStatus == 0){
            drawerLayout.closeDrawers();
            item.setChecked(false);
            switch (item.getItemId()) {
                case R.id.student_profile: {
                    startActivity(HomeActivity.STUDENT_INFO);
                    break;
                }
                case R.id.student_study_result: {
                    startActivity(HomeActivity.STUDENT_STUDY_RESULT);
                    break;
                }
                case R.id.schedule_by_week: {
                    startActivity(HomeActivity.SCHEDULE_BY_WEEK);
                    break;
                }
                case R.id.test_schedule: {
                    startActivity(HomeActivity.TEST_SCHEDULE);
                    break;
                }
                case R.id.frame_program: {
                    startActivity(HomeActivity.FRAME_PROGRAM);
                    break;
                }
                case R.id.student_debt: {
                    startActivity(HomeActivity.STUDENT_DEBT);
                    break;
                }
                case R.id.attendance: {
                    startActivity(HomeActivity.ATTENDANCE);
                    break;
                }
                case R.id.mail_box:{
                    startActivity(new Intent(this, MailActivity.class));
                    break;
                }
                case R.id.student_search: {
                    startActivity(new Intent(this,StudentSearchActivity.class));
                    break;
                }
                case R.id.about_app:{
                    epicDialog.showAboutDialog();
                    break;
                }
                case R.id.logout: {
                    LoginActivity.isLogout = true;
                    LoginActivity.isAutoLogin = false;
                    HomeActivity.isLogin = false;
                    storage.deleteAllsharedPreferences(this);
                    startActivity(new Intent(this, LoginActivity.class));
                    break;
                }
                case R.id.exit: {
                    LoginActivity.isLogout = false;
                    storage.deleteAllsharedPreferences(this);
                    this.finishAffinity();
                    break;
                }
                case R.id.home_dashboard:{
                    item.setChecked(true);
                    break;
                }
                case R.id.study_for_improvement:{
                    startActivity(HomeActivity.STUDY_FOR_IMPROVEMENT);
                    break;
                }
            }
            return true;
        }
         return false;
    }
}
