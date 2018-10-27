package vn.edu.ut.gts.views.login;

import android.Manifest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.helpers.TextInputValidator;
import vn.edu.ut.gts.presenters.login.LoginProcess;
import vn.edu.ut.gts.views.dashboard.DashboardActivity;

public class LoginActivity extends AppCompatActivity implements ILoginView {

    @BindView(R.id.relay_1)
    RelativeLayout relay_1;
    @BindView(R.id.btn_login)
    CircularProgressButton btnLogin;
    @BindView(R.id.txtStudentId)
    EditText inputStudentId;
    @BindView(R.id.txtPassword)
    EditText inputPassword;
    @BindView(R.id.input_student_id_error)
    TextView studentIdInputErrorShow;
    @BindView(R.id.input_password_error)
    TextView passwordInputErrorShow;

    private LoginProcess loginProcess;
    private Boolean isValidateNoError;
    private BroadcastReceiver listenToInteret;
    private Handler handler;
    private Runnable runnable;
    private SweetAlertDialog loginAlert;
    private Storage storage;
    private EpicDialog epicDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        this.requestPermission();
        this.init();
        this.setLastLogin();
        this.validate();
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
        handler.postDelayed(runnable, 500);
    }

    @Override
    public void loginFailed() {
        enableInput();
        new SweetAlertDialog(this)
                .setTitleText(getResources().getString(R.string.login_failed_dialog_title))
                .setContentText(getResources().getString(R.string.login_failed_dialog_content))
                .show();
    }

    @Override
    public void setLastLogin() {
        if (this.storage.getString("last_student_login") != null)
            inputStudentId.setText(this.storage.getString("last_student_login"));
    }

    @Override
    public void showLoadingDialog() {
        epicDialog.showLoadingDialog();
    }

    @Override
    public void dismisLoadingDialog() {
        epicDialog.dismisPopup();
    }

    @Override
    public void showError() {
        enableInput();
        new SweetAlertDialog(this)
                .setTitleText(getResources().getString(R.string.login_error_dialog_title))
                .setContentText(getResources().getString(R.string.login_error_dialog_content))
                .show();
    }

    @Override
    public void transferToRetryBtn() {
        disableInput();
        btnLogin.setText("Thử lại");
    }

    @Override
    public void transferToLoginBtn() {
        enableInput();
        btnLogin.setText("Đăng nhập");
    }

    @Override
    public void onBackPressed() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Xác nhận thoát?")
                .setCancelText("Ok")
                .setConfirmText("Hủy")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                })
                .show();
    }

    private void init() {
        storage = new Storage(this);
        this.isValidateNoError = false;
        epicDialog = new EpicDialog(LoginActivity.this);
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
                    enableInput();
                    btnLogin.setEnabled(true);
                    if (loginAlert != null) loginAlert.dismissWithAnimation();
                    loginProcess = new LoginProcess(LoginActivity.this, context);
                    loginProcess.initData();
                } else {
                    disableInput();
                    btnLogin.setEnabled(false);
                    loginAlert = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                    loginAlert.setTitleText(getResources().getString(R.string.no_internet_access_error_dialog_title))
                            .setContentText(getResources().getString(R.string.no_internet_access_error_dialog_content))
                            .show();
                }
            }
        };
    }

    @OnClick(R.id.btn_login)
    public void submit(View view) {
        if(LoginProcess.currentStatus == LoginProcess.TIMEOUT){
            loginProcess.initData();
        } else {
            if (validateStudentId() && validatePassword()) {
                unsetInputError(passwordInputErrorShow);
                unsetInputError(studentIdInputErrorShow);
                disableInput();
                loginProcess.execute(getStudentId(), getPassword());
            }
        }

    }

    private void disableInput() {
        inputPassword.setEnabled(false);
        inputStudentId.setEnabled(false);
    }

    private void enableInput(){
        inputPassword.setEnabled(true);
        inputStudentId.setEnabled(true);
    }

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
            this.setInputError(passwordInputErrorShow, "Mật khẩu phải có ít nhất 6 ký tự");
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

    private String getStudentId() {
        return this.inputStudentId.getText().toString().trim();
    }

    private String getPassword() {
        return this.inputPassword.getText().toString().trim();
    }
}
