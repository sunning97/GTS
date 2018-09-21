package vn.edu.ut.gts.views.homes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import vn.edu.ut.gts.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu,menu);
        return true;
        
    public void onBackPressed() {
        builder.setTitle("Xác nhận thoát");
        builder.setMessage("Bạn có muốn thoát không?");
        builder.setCancelable(true);
        builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void init(){
        appDrawLayout = findViewById(R.id.app_draw_layout);
        toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,appDrawLayout,toolbar,R.string.open,R.string.close);
        appDrawLayout.addDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle.syncState();

        studentInfoProcess = new StudentInfoProcess(this);

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
                        studentInfoProcess.loadStudentData();
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
