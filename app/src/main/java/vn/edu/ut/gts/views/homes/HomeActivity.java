package vn.edu.ut.gts.views.homes;

import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import vn.edu.ut.gts.R;
import vn.edu.ut.gts.views.homes.fragments.StudentInfoRootFragment;
import vn.edu.ut.gts.views.homes.fragments.StudentStudyResultFragment;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout appDrawLayout;
    private FragmentTransaction fragmentTransaction;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        appDrawLayout = findViewById(R.id.app_draw_layout);
        toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,appDrawLayout,toolbar,R.string.open,R.string.close);
        appDrawLayout.addDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle.syncState();


        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_frame,new StudentInfoRootFragment());
        fragmentTransaction.commit();

        getSupportActionBar().setTitle("Thông tin sinh viên");



        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    menuItem.setChecked(true);
                    switch (menuItem.getItemId()){
                        case R.id.student_profile:{
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.content_frame,new StudentInfoRootFragment());
                            fragmentTransaction.commit();
                            getSupportActionBar().setTitle("");
                            break;
                        }
                        case R.id.student_study_result:{
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.content_frame,new StudentStudyResultFragment());
                            fragmentTransaction.commit();
                            getSupportActionBar().setTitle("Kết quả học tập");
                            break;
                        }
                    }
                    appDrawLayout.closeDrawers();

                    return true;
                }
            });

    }
}
