package vn.edu.ut.gts.views.dashboard;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import vn.edu.ut.gts.R;

public class DashboardActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView qqq;
    private AppBarLayout vvv;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private String title = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        vvv = findViewById(R.id.home_appbar_layout);
        qqq = findViewById(R.id.qqq);
        collapsingToolbarLayout = findViewById(R.id.ppp);
        title = collapsingToolbarLayout.getTitle().toString();
        vvv.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    qqq.setVisibility(View.INVISIBLE);
                    collapsingToolbarLayout.setTitle(collapsingToolbarLayout.getTitle() + " - " + qqq.getText());
                } else{
                    qqq.setVisibility(View.VISIBLE);
                    collapsingToolbarLayout.setTitle(title);
                }
            }
        });

        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu, menu);
        return true;
    }
}
