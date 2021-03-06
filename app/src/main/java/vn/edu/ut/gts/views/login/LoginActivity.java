package vn.edu.ut.gts.views.login;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.widget.TextView;


import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.Wave;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.helpers.Helper;
import vn.edu.ut.gts.helpers.Storage;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.helpers.OnClearFromRecentService;
import vn.edu.ut.gts.helpers.TextInputValidator;
import vn.edu.ut.gts.presenters.login.LoginProcess;
import vn.edu.ut.gts.views.dashboard.DashboardActivity;
import vn.edu.ut.gts.views.search.StudentSearchActivity;

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
    @BindView(R.id.cb_auto_login)
    CustomCheckBox cbAutoLogin;
    @BindView(R.id.tv_auto_login)
    TextView tvAuthoLogin;
    @BindView(R.id.layout_auto_login)
    LinearLayout layoutAutoLogin;
    @BindView(R.id.layout_login)
    RelativeLayout layoutLogin;
    @BindView(R.id.spin_kit)
    SpinKitView loadingIcon;
    @BindView(R.id.search_student_tv)
    TextView searchStudentTV;


    public static Boolean isAutoLogin = false;
    public static Boolean isLogout = false;
    public static Boolean isOpen = false;
    private LoginProcess loginProcess;
    private Handler handler;
    private Runnable runnable;
    private Runnable runnable2;
    private Storage storage;
    private EpicDialog epicDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Wave wave = new Wave();
        loadingIcon.setIndeterminateDrawable(wave);
        searchStudentTV.setPaintFlags(searchStudentTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        LoginActivity.isOpen = true;
        this.requestPermission();
        this.init();
        this.hideErrorWhileInput();
        /* start service OnClearFromRecentService*/
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
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

    @OnClick(R.id.search_student_tv)
    public void goSearchActivity(){
        startActivity(new Intent(LoginActivity.this,StudentSearchActivity.class));

    }

    @OnClick(R.id.layout_login)
    public void layoutLoginClick(View view){
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
        if(LoginActivity.isAutoLogin){
            inputStudentId.setText(this.storage.getString("last_student_login"));
            inputPassword.setText(this.storage.getString("password"));
            cbAutoLogin.setChecked(true);
        } else {
            inputStudentId.setText(this.storage.getString("last_student_login"));
            cbAutoLogin.setChecked(false);
        }
    }

    @Override
    public void showLoadingDialog() {
        if (!epicDialog.isShowing())
            epicDialog.showLoadingDialog();
    }

    @Override
    public void dismissLoadingDialog() {
        if (epicDialog.isShowing())
            epicDialog.dismisPopup();
    }

    @Override
    public void showTimeoutDialog() {
        if (epicDialog.isShowing()) epicDialog.dismisPopup();
        enableInput();
        new SweetAlertDialog(this)
                .setTitleText(getResources().getString(R.string.connect_timeout_dialog_title))
                .setContentText(getResources().getString(R.string.connect_timeout_dialog_content))
                .show();
    }

    @Override
    public void showNoInternetDialog() {
        if (epicDialog.isShowing()) epicDialog.dismisPopup();
        disableInput();
        new SweetAlertDialog(this)
                .setTitleText(getResources().getString(R.string.no_internet_access_title))
                .setContentText(getResources().getString(R.string.no_internet_access_content))
                .show();
    }

    @Override
    public void transferToRetryBtn() {
        if (epicDialog.isShowing()) epicDialog.dismisPopup();
        disableInput();
        btnLogin.setText(getResources().getString(R.string.retry_btn));
        btnLogin.setEnabled(true);
    }

    @Override
    public void transferToLoginBtn() {
        enableInput();
        btnLogin.setText(getResources().getString(R.string.login_btn));
        btnLogin.setEnabled(true);
    }

    @Override
    public void showLoginLayout() {
        if (Boolean.valueOf(storage.getString("is_auto_login"))) {
            LoginActivity.isAutoLogin = true;
        }
        this.setLastLogin();
        relay_1.setVisibility(View.VISIBLE);
        layoutAutoLogin.setVisibility(View.GONE);
        layoutLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAutoLoginLayout() {
        layoutLogin.setVisibility(View.GONE);
        layoutAutoLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoginAutoErrorDialog() {
        if (epicDialog.isShowing()) epicDialog.dismisPopup();
        enableInput();
        new SweetAlertDialog(this)
                .setTitleText(getResources().getString(R.string.auto_login_error_title))
                .setContentText(getResources().getString(R.string.auto_login_error_content))
                .show();
    }

    @Override
    public void onBackPressed() {
        storage.deleteAllsharedPreferences(LoginActivity.this);
        LoginActivity.this.finishAffinity();
    }

    private void init() {
        storage = new Storage(this);
        loginProcess = new LoginProcess(LoginActivity.this, LoginActivity.this);
        epicDialog = new EpicDialog(LoginActivity.this);
        epicDialog.initLoadingDialog();
        this.handler = new Handler();
        this.runnable = new Runnable() {
            @Override
            public void run() {
                relay_1.setVisibility(View.VISIBLE);
            }
        };
        this.runnable2 = new Runnable() {
            @Override
            public void run() {
                loginProcess.initData(false);
            }
        };
        if (Boolean.valueOf(this.storage.getString("is_auto_login"))) {
            LoginActivity.isAutoLogin = true;
            layoutLogin.setVisibility(View.GONE);
            layoutAutoLogin.setVisibility(View.VISIBLE);
            String id = this.storage.getString("last_student_login");
            String pass = this.storage.getString("password");
            loginProcess.initData(true);
            loginProcess.execute(id, pass, true);
        } else {
            LoginActivity.isAutoLogin = false;
            layoutAutoLogin.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);
            this.setLastLogin();
            handler.postDelayed(runnable, 1500);
            handler.postDelayed(runnable2, 2000);
        }
        storage.deleteString("search_student_id");
        storage.deleteImage(LoginActivity.this, "search_student_portrait.jpg");
    }

    @OnClick(R.id.tv_auto_login)
    public void tvRememberClick(View view) {
        cbAutoLogin.setChecked(!cbAutoLogin.isChecked(), true);
    }

    @OnClick(R.id.btn_login)
    public void submit(View view) {
        /* error in internet connect*/
        if (LoginProcess.currentStatus == Helper.TIMEOUT || LoginProcess.currentStatus == Helper.NO_CONNECTION) {
            LoginProcess.currentStatus = 0;
            disableInput();
            transferToLoadingBtn();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loginProcess.initData(false);
                }
            },1000);
        } else {
            boolean validateStudentId = validateStudentId();
            boolean validatePassword = validatePassword();
            if (validateStudentId && validatePassword) {
                if (cbAutoLogin.isChecked()) {
                    /*Auto login*/
                    LoginActivity.isAutoLogin = true;
                    storage.putString("password", inputPassword.getText().toString().trim());
                    storage.putString("is_auto_login", String.valueOf(true));
                } else {
                    LoginActivity.isAutoLogin = false;
                    storage.putString("is_auto_login", String.valueOf(false));
                }
                storage.putString("w_p", inputPassword.getText().toString().trim());
                unsetInputError(passwordInputErrorShow);
                unsetInputError(studentIdInputErrorShow);
                disableInput();
                loginProcess.execute(getStudentId(), getPassword(), false);
            }
        }
    }

    public void disableInput() {
        inputPassword.setEnabled(false);
        inputStudentId.setEnabled(false);
        searchStudentTV.setEnabled(false);
        tvAuthoLogin.setEnabled(false);
        cbAutoLogin.setEnabled(false);
    }

    public void enableInput() {
        inputPassword.setEnabled(true);
        inputStudentId.setEnabled(true);
        searchStudentTV.setEnabled(true);
        tvAuthoLogin.setEnabled(true);
        cbAutoLogin.setEnabled(true);
    }

    @Override
    public void transferToLoadingBtn() {
        btnLogin.setText(getResources().getString(R.string.loading));
        btnLogin.setEnabled(false);
    }

    /*request permission need for this app if permission is not permit*/
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
            ActivityCompat.requestPermissions(
                    this,
                    listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]),
                    1
            );
        }
    }

    private void hideErrorWhileInput() {
        this.inputStudentId.addTextChangedListener(new TextInputValidator(inputStudentId) {
            @Override
            public void validate(TextView textView, String text) {
                unsetInputError(studentIdInputErrorShow);
            }
        });

        this.inputPassword.addTextChangedListener(new TextInputValidator(inputPassword) {
            @Override
            public void validate(TextView textView, String text) {
                unsetInputError(passwordInputErrorShow);
            }
        });
    }
    /*validate student id input*/
    private boolean validateStudentId() {
        if (TextUtils.isEmpty(this.inputStudentId.getText().toString().trim())) {
            this.setInputError(studentIdInputErrorShow, "Mã số sinh viên không được để trống");
            return false;
        } else if (this.inputStudentId.getText().toString().trim().length() < 10) {
            this.setInputError(studentIdInputErrorShow, "Mã số sinh viên không đúng định dạng");
            return false;
        } else {
            this.unsetInputError(studentIdInputErrorShow);
            return true;
        }
    }

    /*validate password*/
    private boolean validatePassword() {
        if (TextUtils.isEmpty(this.inputPassword.getText().toString().trim())) {
            this.setInputError(passwordInputErrorShow, "Mật khẩu không được để trống");
            return false;
        } else if (this.inputPassword.getText().toString().trim().length() < 1) {
            this.setInputError(passwordInputErrorShow, "Mật khẩu phải có ít nhất 1 ký tự");
            return false;
        } else {
            this.unsetInputError(passwordInputErrorShow);
            return true;
        }
    }

    /*show input error*/
    private void setInputError(TextView textView, String message) {
        textView.setText(message);
    }

    /*hide input error*/
    private void unsetInputError(TextView textView) {
        textView.setText("");
    }

    /*get value of student id input*/
    private String getStudentId() {
        return this.inputStudentId.getText().toString().trim();
    }

    /*get value of password input*/
    private String getPassword() {
        return this.inputPassword.getText().toString().trim();
    }
}
