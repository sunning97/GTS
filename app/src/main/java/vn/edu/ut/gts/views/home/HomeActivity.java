package vn.edu.ut.gts.views.home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import vn.edu.ut.gts.views.dashboard.DashboardActivity;
import vn.edu.ut.gts.views.home.fragments.AttendanceFragment;
import vn.edu.ut.gts.views.home.fragments.FrameProgramFragment;
import vn.edu.ut.gts.views.home.fragments.StudentDebtFragment;
import vn.edu.ut.gts.views.home.fragments.StudentInfoRootFragment;
import vn.edu.ut.gts.views.home.fragments.StudentStudyResultFragment;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.views.home.fragments.WeekSchedule;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static final int STUDENT_INFO = 1;
    public static final int STUDENT_STUDY_RESULT = 2;
    public static final int FRAME_PROGRAM = 3;
    public static final int STUDENT_DEBT = 4;
    public static final int SCHEDULE_BY_WEEK = 5;
    public static final int ATTENDACE = 6;

    @BindView(R.id.home_toolbar)
    Toolbar toolbar;
    @BindView(R.id.home_drawerlayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.home_navigation_view)
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHomeFragment(intent);

        navigationView.setNavigationItemSelectedListener(this);
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
            case R.id.student_profile:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new StudentInfoRootFragment()
                ).commit();
                setTitle(item.getTitle());
                break;
            case R.id.student_study_result:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new StudentStudyResultFragment()
                ).commit();
                setTitle(item.getTitle());
                break;
            case R.id.schedule_by_week:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new WeekSchedule()
                ).commit();
                setTitle(item.getTitle());
                break;
            case R.id.home_dashboard:
                Intent dashboardIntent = new Intent(HomeActivity.this, DashboardActivity.class);
                startActivity(dashboardIntent);
                break;
            case  R.id.frame_program:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new FrameProgramFragment()
                ).commit();
                setTitle(item.getTitle());
            case R.id.student_debt:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new StudentDebtFragment()
                ).commit();
                setTitle(item.getTitle());
            case R.id.attendance:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new AttendanceFragment()
                ).commit();
                setTitle(item.getTitle());
        }
        return true;
    }

    @Override
    public void onBackPressed() {
    }

    private void setHomeFragment(Intent intent){
        switch (Integer.parseInt(intent.getStringExtra("order"))){
            case HomeActivity.STUDENT_INFO:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new StudentInfoRootFragment()
                ).commit();
                setTitle("Thông tin sinh viên");
                break;
            case HomeActivity.STUDENT_STUDY_RESULT:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new StudentStudyResultFragment()
                ).commit();
                setTitle("Kết quả học tập");
                break;
            case HomeActivity.SCHEDULE_BY_WEEK:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new WeekSchedule()
                ).commit();
                setTitle("Xem lịch theo tuần");
                break;
            case HomeActivity.FRAME_PROGRAM:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new FrameProgramFragment()
                ).commit();
                setTitle("Chương trình khung");
            case HomeActivity.STUDENT_DEBT:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new StudentDebtFragment()
                ).commit();
                setTitle("Công nợ Sinh viên");
            case HomeActivity.ATTENDACE:
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.home_fragment_container,
                        new AttendanceFragment()
                ).commit();
                setTitle("Thông tin điểm danh");
        }
    }
}
