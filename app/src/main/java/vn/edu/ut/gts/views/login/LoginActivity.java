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
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.helpers.TextInputValidator;
import vn.edu.ut.gts.presenter.login.LoginProcess;
import vn.edu.ut.gts.views.dashboard.DashboardActivity;

public class LoginActivity extends AppCompatActivity implements ILoginView {

    private RelativeLayout relay_1;
    private Handler handler;
    private Runnable runnable;
    private CircularProgressButton btnLogin;
    private EditText inputStudentId;
    private EditText inputPassword;
    private LoginProcess loginProcess;
    private TextInputLayout studentIdInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextView studentIdInputErrorShow, passwordInputErrorShow;
    private Boolean isValidateNoError;
    private BroadcastReceiver listenToInteret;
    private EpicDialog epicDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.requestPermission();
        this.init(this);
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
        this.btnLogin.doneLoadingAnimation(
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
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public void loginFailed() {
        epicDialog.showPopup(
                "Đăng nhập thất bại",
                "Mã số sinh viên / mật khẩu không đúng",
                EpicDialog.NEGATIVE
        );
    }


    /**
     * Initialization all needed for activity
     *
     * @return Void
     */
    private void init(Context context) {
        this.relay_1 = findViewById(R.id.relay_1);
        this.btnLogin = findViewById(R.id.btn_login);
        this.inputStudentId = findViewById(R.id.txtStudentId);
        this.inputPassword = findViewById(R.id.txtPassword);
        this.passwordInputLayout = findViewById(R.id.password_input_layout);
        this.studentIdInputLayout = findViewById(R.id.student_id_input_layout);
        this.studentIdInputErrorShow = findViewById(R.id.input_student_id_error);
        this.passwordInputErrorShow = findViewById(R.id.input_password_error);
        this.isValidateNoError = false;
        this.handler = new Handler();
        this.epicDialog = new EpicDialog(context);
        this.runnable = new Runnable() {
            @Override
            public void run() {
                relay_1.setVisibility(View.VISIBLE);
            }
        };
        this.loginProcess = new LoginProcess(this, this);

        listenToInteret = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getActiveNetworkInfo() != null) {
                    inputStudentId.setEnabled(true);
                    inputPassword.setEnabled(true);
                    btnLogin.setEnabled(true);
                    epicDialog.dismisPopup();
                } else {
                    inputStudentId.setEnabled(false);
                    inputPassword.setEnabled(false);
                    btnLogin.setEnabled(false);
                    epicDialog.showPopup("Không có Internet", "Thiết bị của bạn đang không kết nối mạng, vui lòng mở kết nối trước khi sử dụng", EpicDialog.NEGATIVE);
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
                if (validateStudentId() || validatePassword()) {
                    unsetInputError(passwordInputErrorShow);
                    unsetInputError(studentIdInputErrorShow);
                    loginProcess.doLogin(inputStudentId.getText().toString(), inputPassword.getText().toString());
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

}
