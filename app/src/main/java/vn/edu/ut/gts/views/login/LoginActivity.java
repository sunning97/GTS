package vn.edu.ut.gts.views.login;

import android.Manifest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.helpers.TextInputValidator;
import vn.edu.ut.gts.presenter.login.LoginProcess;
import vn.edu.ut.gts.views.dashboard.DashboardActivity;

public class LoginActivity extends AppCompatActivity implements ILoginView {

    @BindView(R.id.relay_1) RelativeLayout relay_1;
    @BindView(R.id.btn_login) CircularProgressButton btnLogin;
    @BindView(R.id.txtStudentId) EditText inputStudentId;
    @BindView(R.id.txtPassword) EditText inputPassword;
    @BindView(R.id.input_student_id_error) TextView studentIdInputErrorShow;
    @BindView(R.id.input_password_error) TextView passwordInputErrorShow;

    private LoginProcess loginProcess;
    private Boolean isValidateNoError;
    private BroadcastReceiver listenToInteret;
    private Handler handler;
    private Runnable runnable;
    private SweetAlertDialog loginAlert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        this.requestPermission();
        this.init();
        this.validate();
        this.addControl();
        handler.postDelayed(runnable, 1500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(listenToInteret, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (listenToInteret != null) {
            unregisterReceiver(listenToInteret);
        }
    }

    @Override
    public void startLoadingButton() {
        this.btnLogin.startAnimation();
    }

    @Override
    public void doneLoadingButton() {
        btnLogin.doneLoadingAnimation(
                Color.parseColor("#00000000"),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp)
        );

    }

    @Override
    public void revertLoadingButton() {
        this.btnLogin.revertAnimation();
    }

    @Override
    public void loginSuccess() {
        this.runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        };
        handler.postDelayed(runnable, 1000);

    }

    @Override
    public void loginFailed() {
        new SweetAlertDialog(this)
                .setTitleText(getResources().getString(R.string.login_failed_dialog_title))
                .setContentText(getResources().getString(R.string.login_failed_dialog_content))
                .show();
    }


    /**
     * Initialization all needed for activity
     *
     * @return Void
     */

    private void init() {
        this.isValidateNoError = false;
        this.handler = new Handler();
        this.runnable = new Runnable() {
            @Override
            public void run() {
                relay_1.setVisibility(View.VISIBLE);
            }
        };

        listenToInteret = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getActiveNetworkInfo() != null) {
                    inputStudentId.setEnabled(true);
                    inputPassword.setEnabled(true);
                    btnLogin.setEnabled(true);
                    if(loginAlert != null) loginAlert.dismissWithAnimation();
                    loginProcess = new LoginProcess(LoginActivity.this, context);
                } else {
                    inputStudentId.setEnabled(false);
                    inputPassword.setEnabled(false);
                    btnLogin.setEnabled(false);
                    loginAlert = new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE);
                    loginAlert.setTitleText(getResources().getString(R.string.no_internet_access_error_dialog_title))
                            .setContentText(getResources().getString(R.string.no_internet_access_error_dialog_content))
                            .show();
                }
            }
        };
    }

    /**
     * Handle all event on activity
     *
     * @return Void
     */
    private void addControl() {
        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateStudentId() && validatePassword()) {
                    unsetInputError(passwordInputErrorShow);
                    unsetInputError(studentIdInputErrorShow);
                    loginProcess.doLogin(getStudentId(),getPassword());
                }
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

    private void validate() {
        this.inputStudentId.addTextChangedListener(new TextInputValidator(inputStudentId) {
            @Override
            public void validate(TextView textView, String text) {
                validateStudentId();
            }
        });

        this.inputPassword.addTextChangedListener(new TextInputValidator(inputPassword) {
            @Override
            public void validate(TextView textView, String text) {
                validatePassword();
            }
        });
    }

    private boolean validateStudentId() {
        if (TextUtils.isEmpty(this.inputStudentId.getText().toString().trim())) {
            this.setInputError(studentIdInputErrorShow, "Mã số sinh viên không được để trống");
            this.isValidateNoError = false;
        } else if (this.inputStudentId.getText().toString().trim().length() < 10) {
            this.setInputError(studentIdInputErrorShow, "Mã số sinh viên không đúng định dạng");
            this.isValidateNoError = false;
        } else {
            this.unsetInputError(studentIdInputErrorShow);
            this.isValidateNoError = true;
        }
        return this.isValidateNoError;
    }

    private boolean validatePassword() {
        if (TextUtils.isEmpty(this.inputPassword.getText().toString().trim())) {
            this.setInputError(passwordInputErrorShow, "Mật khẩu không được để trống");
            this.isValidateNoError = false;
        } else if (this.inputPassword.getText().toString().trim().length() < 5) {
            this.setInputError(passwordInputErrorShow, "Mật khẩu phải lớn hơn 5 kí tự");
            this.isValidateNoError = false;
        } else {
            this.unsetInputError(passwordInputErrorShow);
            this.isValidateNoError = true;
        }
        return this.isValidateNoError;
    }

    private void setInputError(TextView textView, String message) {
        textView.setText(message);
    }

    private void unsetInputError(TextView textView) {
        textView.setText("");
    }

    private String getStudentId(){
        return this.inputStudentId.getText().toString().trim();
    }
    private String getPassword(){
        return this.inputPassword.getText().toString().trim();
    }
}
