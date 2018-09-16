package vn.edu.ut.gts.views.login;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import vn.edu.ut.gts.R;

public class LoginActivity extends AppCompatActivity {

    private RelativeLayout relay_1;
    private Handler handler;
    private Runnable runnable;
    private CircularProgressButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.requestPermission();

        this.init();
        
        this.addControl();

        handler.postDelayed(runnable, 3000);
    }

    /**
     *
     * Initialization all needed for activity
     *
     * @return Void
     */

    private void init() {
        relay_1 = findViewById(R.id.relay_1);
        btnLogin = findViewById(R.id.btn_login);
        this.handler = new Handler();
        this.runnable = new Runnable() {
            @Override
            public void run() {
                relay_1.setVisibility(View.VISIBLE);
            }
        };
    }

    /**
     *
     * Handle all event on activity
     *
     * @return Void
     */
    private void addControl() {
        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.startAnimation();
                AsyncTask<String,Void,String> asyncTask =  new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        btnLogin.startAnimation();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return "asdasdas";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        btnLogin.doneLoadingAnimation(
                                Color.parseColor("#00000000"),
                                BitmapFactory.decodeResource(getResources(),R.drawable.ic_done_white_48dp)
                        );
                    }
                };

                asyncTask.execute("aa");
            }
        });
    }


    /**
     * Request permission need for app on first launch
     *
     * @return Void
     */

    private void requestPermission() {
        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        List<String> listPermissionNeeded = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(permission);
            }
        }
        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]), 1);
        }
    }
}
