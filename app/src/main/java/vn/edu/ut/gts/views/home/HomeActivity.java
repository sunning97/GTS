package vn.edu.ut.gts.views.home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.helpers.OnClearFromRecentService;
import vn.edu.ut.gts.views.dashboard.DashboardActivity;
import vn.edu.ut.gts.views.home.fragments.AttendanceFragment;
import vn.edu.ut.gts.views.home.fragments.FrameProgramFragment;
import vn.edu.ut.gts.views.home.fragments.StudentDebtFragment;
import vn.edu.ut.gts.views.home.fragments.StudentInfoRootFragment;
import vn.edu.ut.gts.views.home.fragments.StudentStudyResultFragment;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.views.home.fragments.WeekSchedule;
import vn.edu.ut.gts.views.login.LoginActivity;
import vn.edu.ut.gts.views.search.StudentSearchActivity;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int STUDENT_INFO = 1;
    public static final int STUDENT_STUDY_RESULT = 2;
    public static final int FRAME_PROGRAM = 3;
    public static final int STUDENT_DEBT = 4;
    public static final int SCHEDULE_BY_WEEK = 5;
    public static final int ATTENDACE = 6;
    public static Boolean isLogin = false;

    @BindView(R.id.home_toolbar)
    Toolbar toolbar;
    @BindView(R.id.home_drawerlayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.home_navigation_view)
    NavigationView navigationView;
    @BindView(R.id.student_name)
    TextView studentFullName;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.student_id)
    TextView studentID;

    private Storage storage;
    private JSONObject studentInfo = null;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.storage = new Storage(this);
        try {
            studentInfo = new JSONObject(storage.getString("student_info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHomeFragment(intent);
        navigationView.setNavigationItemSelectedListener(this);
        profileImage.setImageBitmap(storage.getImageFromStorage(HomeActivity.this,"student_portrait.jpg"));
        try {
            studentFullName.setText(studentInfo.getString("student_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        studentID.setText(storage.getString("last_student_login"));

        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        drawerLayout.closeDrawers();
        switch (item.getItemId()) {
            case R.id.student_profile: {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new StudentInfoRootFragment()
                ).commit();
                setTitle(item.getTitle());
                break;
            }
            case R.id.student_study_result: {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new StudentStudyResultFragment()
                ).commit();
                setTitle(item.getTitle());
                break;
            }
            case R.id.schedule_by_week: {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new WeekSchedule()
                ).commit();
                setTitle(item.getTitle());
                break;
            }
            case R.id.home_dashboard: {
                Intent dashboardIntent = new Intent(HomeActivity.this, DashboardActivity.class);
                startActivity(dashboardIntent);
                break;
            }
            case R.id.frame_program: {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new FrameProgramFragment()
                ).commit();
                setTitle(item.getTitle());
                break;
            }
            case R.id.student_debt: {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new StudentDebtFragment()
                ).commit();
                setTitle(item.getTitle());
                break;
            }
            case R.id.attendance: {
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new AttendanceFragment()
                ).commit();
                setTitle(item.getTitle());
                break;
            }
            case R.id.student_search: {
                item.setChecked(false);
                startActivity(new Intent(HomeActivity.this,StudentSearchActivity.class));
                break;
            }
            case R.id.about_app:{
                EpicDialog epicDialog = new EpicDialog(HomeActivity.this);
                epicDialog.showAboutDialog();
                item.setChecked(false);
                break;
            }
            case R.id.logout: {
                LoginActivity.isLogout = true;
                HomeActivity.isLogin = false;
                LoginActivity.isAutoLogin = false;
                storage.deleteAllsharedPreferences(HomeActivity.this);
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                break;
            }
            case R.id.exit: {
                LoginActivity.isLogout = false;
                storage.deleteAllsharedPreferences(HomeActivity.this);
                HomeActivity.this.finishAffinity();
                break;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent dashboardIntent = new Intent(HomeActivity.this, DashboardActivity.class);
        startActivity(dashboardIntent);
    }

    private void setHomeFragment(Intent intent) {
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = null;
        switch (Integer.parseInt(intent.getStringExtra("order"))) {
            case HomeActivity.STUDENT_INFO: {
                menuItem = menu.findItem(R.id.student_profile);
                menuItem.setChecked(true);
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new StudentInfoRootFragment()
                ).commit();
                setTitle("Thông tin sinh viên");
                break;
            }
            case HomeActivity.STUDENT_STUDY_RESULT: {
                menuItem = menu.findItem(R.id.student_study_result);
                menuItem.setChecked(true);
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new StudentStudyResultFragment()
                ).commit();
                setTitle("Kết quả học tập");
                break;
            }
            case HomeActivity.SCHEDULE_BY_WEEK: {
                menuItem = menu.findItem(R.id.schedule_by_week);
                menuItem.setChecked(true);
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new WeekSchedule()
                ).commit();
                setTitle("Xem lịch theo tuần");
                break;
            }
            case HomeActivity.FRAME_PROGRAM: {
                menuItem = menu.findItem(R.id.frame_program);
                menuItem.setChecked(true);
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new FrameProgramFragment()
                ).commit();
                setTitle("Chương trình khung");
                break;
            }
            case HomeActivity.STUDENT_DEBT: {
                menuItem = menu.findItem(R.id.student_debt);
                menuItem.setChecked(true);
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new StudentDebtFragment()
                ).commit();
                setTitle("Công nợ Sinh viên");
                break;
            }
            case HomeActivity.ATTENDACE: {
                menuItem = menu.findItem(R.id.attendance);
                menuItem.setChecked(true);
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new AttendanceFragment()
                ).commit();
                setTitle("Thông tin điểm danh");
                break;
            }
        }
    }
}
