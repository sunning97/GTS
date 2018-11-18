package vn.edu.ut.gts.views.home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.ut.gts.helpers.Storage;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.helpers.OnClearFromRecentService;
import vn.edu.ut.gts.views.dashboard.DashboardActivity;
import vn.edu.ut.gts.views.home.fragments.AttendanceFragment;
import vn.edu.ut.gts.views.home.fragments.FrameProgramFragment;
import vn.edu.ut.gts.views.home.fragments.StudentDebtFragment;
import vn.edu.ut.gts.views.home.fragments.StudentInfoRootFragment;
import vn.edu.ut.gts.views.home.fragments.StudentStudyResultFragment;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.views.home.fragments.TestScheduleFragment;
import vn.edu.ut.gts.views.home.fragments.WeekSchedule;
import vn.edu.ut.gts.views.login.LoginActivity;
import vn.edu.ut.gts.views.mail.MailActivity;
import vn.edu.ut.gts.views.search.StudentSearchActivity;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int STUDENT_INFO = 1;
    public static final int STUDENT_STUDY_RESULT = 2;
    public static final int FRAME_PROGRAM = 3;
    public static final int STUDENT_DEBT = 4;
    public static final int SCHEDULE_BY_WEEK = 5;
    public static final int ATTENDANCE = 6;
    public static final int TEST_SCHEDULE = 7;
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
    private ActionBarDrawerToggle actionBarDrawerToggle;

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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setHomeFragment(intent);
        profileImage.setImageBitmap(storage.getImageFromStorage(HomeActivity.this,"student_portrait.jpg"));
        try {
            /* get student name from sharedpreference & set to drawlayout header*/
            studentFullName.setText(studentInfo.getString("student_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /* ge student Id from sharedpreference & set to drawlayout header*/
        studentID.setText(storage.getString("last_student_login"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawers();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.home_fragment_container);
        switch (item.getItemId()) {
            case R.id.student_profile: {
                item.setChecked(true);
                if(!(currentFragment instanceof StudentInfoRootFragment)){
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.home_fragment_container,
                            new StudentInfoRootFragment()
                    ).commit();
                }
                setTitle(item.getTitle());
                break;
            }
            case R.id.student_study_result: {
                item.setChecked(true);
                if(!(currentFragment instanceof  StudentStudyResultFragment))
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new StudentStudyResultFragment()
                ).commit();
                setTitle(item.getTitle());
                break;
            }
            case R.id.schedule_by_week: {
                item.setChecked(true);
                if(!(currentFragment instanceof  WeekSchedule)){
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.home_fragment_container,
                            new WeekSchedule()
                    ).commit();
                }
                setTitle(item.getTitle());
                break;
            }
            case R.id.test_schedule: {
                item.setChecked(true);
                if(!(currentFragment instanceof TestScheduleFragment)){
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.home_fragment_container,
                            new TestScheduleFragment()
                    ).commit();
                }
                setTitle(item.getTitle());
                break;
            }
            case R.id.home_dashboard: {
                Intent dashboardIntent = new Intent(HomeActivity.this, DashboardActivity.class);
                startActivity(dashboardIntent);
                break;
            }
            case R.id.frame_program: {
                item.setChecked(true);
                if(!(currentFragment instanceof FrameProgramFragment)){
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.home_fragment_container,
                            new FrameProgramFragment()
                    ).commit();
                }
                setTitle(item.getTitle());
                break;
            }
            case R.id.student_debt: {
                item.setChecked(true);
                if(!(currentFragment instanceof StudentDebtFragment)){
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.home_fragment_container,
                            new StudentDebtFragment()
                    ).commit();
                }
                setTitle(item.getTitle());
                break;
            }
            case R.id.attendance: {
                item.setChecked(true);
                if(!(currentFragment instanceof AttendanceFragment)) {
                    getSupportFragmentManager().beginTransaction().replace(
                            R.id.home_fragment_container,
                            new AttendanceFragment()
                    ).commit();
                }
                setTitle(item.getTitle());
                break;
            }
            case R.id.mail_box:{
                item.setChecked(false);
                startActivity(new Intent(HomeActivity.this, MailActivity.class));
                break;
            }
            case R.id.student_search: {
                item.setChecked(false);
                startActivity(new Intent(HomeActivity.this,StudentSearchActivity.class));
                break;
            }
            case R.id.about_app:{
                item.setChecked(false);
                EpicDialog epicDialog = new EpicDialog(HomeActivity.this);
                epicDialog.showAboutDialog();
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
            case HomeActivity.ATTENDANCE: {
                menuItem = menu.findItem(R.id.attendance);
                menuItem.setChecked(true);
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new AttendanceFragment()
                ).commit();
                setTitle("Thông tin điểm danh");
                break;
            }
            case HomeActivity.TEST_SCHEDULE: {
                menuItem = menu.findItem(R.id.test_schedule);
                menuItem.setChecked(true);
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new TestScheduleFragment()
                ).commit();
                setTitle("Lịch thi");
                break;
            }
        }
    }
}
