package vn.edu.ut.gts.views.login;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import vn.edu.ut.gts.R;

public class LoginActivity extends AppCompatActivity {

    RelativeLayout relay_1;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            relay_1.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        relay_1 = findViewById(R.id.relay_1);

        handler.postDelayed(runnable,2000);
    }
}
