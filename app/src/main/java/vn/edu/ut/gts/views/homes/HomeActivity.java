package vn.edu.ut.gts.views.homes;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import vn.edu.ut.gts.R;
import vn.edu.ut.gts.StudentProfileActivity;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout appDrawLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        appDrawLayout = findViewById(R.id.app_draw_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    MenuItem menuItem1 = menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.student_profile: {
                                    Intent intent = new Intent(HomeActivity.this, StudentProfileActivity.class);
                                    startActivity(intent);
                                    break;
                                }
                            }
                            return false;
                        }
                    });
                    appDrawLayout.closeDrawers();

                    return true;
                }
            });
    }
}
