package vn.edu.ut.gts.views.login;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.presenter.login.LoginProcess;
import vn.edu.ut.gts.views.homes.HomeActivity;

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
    private Boolean isValidateNoError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.requestPermission();
        this.init();
        this.validate();
        this.addControl();
        handler.postDelayed(runnable, 3000);

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
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void loginFailed() {
        
    }


    /**
     * Initialization all needed for activity
     *
     * @return Void
     */
    private void init() {
        this.relay_1 = findViewById(R.id.relay_1);
        this.btnLogin = findViewById(R.id.btn_login);
        this.inputStudentId = findViewById(R.id.txtStudentId);
        this.inputPassword = findViewById(R.id.txtPassword);
        this.passwordInputLayout = findViewById(R.id.password_input_layout);
        this.studentIdInputLayout = findViewById(R.id.student_id_input_layout);
        this.isValidateNoError = false;
        this.handler = new Handler();
        this.runnable = new Runnable() {
            @Override
            public void run() {
                relay_1.setVisibility(View.VISIBLE);

            }
        };
        this.loginProcess = new LoginProcess(this, this);
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
                validateStudentId();
                validatePassword();
                if(isValidateNoError) {
                    unsetInputError(passwordInputLayout);
                    unsetInputError(studentIdInputLayout);
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
        this.inputStudentId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    validateStudentId();
                }
            }
        });

        this.inputPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    validatePassword();
                }
            }
        });
    }

    private void validateStudentId() {
        if (this.inputStudentId.getText().toString().trim().isEmpty()) {
            this.setInputError(studentIdInputLayout,"Mã số sinh viên không được để trống");
            this.isValidateNoError = false;
        } else if (this.inputStudentId.getText().toString().trim().length() < 10) {
            this.setInputError(studentIdInputLayout,"Mã số sinh viên không đúng định dạng");
            this.isValidateNoError= false;
        } else {
            this.unsetInputError(studentIdInputLayout);
            this.isValidateNoError = true;
        }
    }

    private void validatePassword(){
        if (TextUtils.isEmpty(this.inputPassword.getText().toString().trim())) {
            this.setInputError(passwordInputLayout,"Mật khẩu không được để trống");
            this.isValidateNoError = false;
        } else if (this.inputPassword.getText().toString().trim().length() < 5) {
            this.setInputError(passwordInputLayout,"Mật khẩu phải lớn hơn 5 kí tự");
            this.isValidateNoError= false;
        } else {
            this.unsetInputError(passwordInputLayout);
            this.isValidateNoError = true;
        }
    }

    private void setInputError(TextInputLayout layout,String message){
        layout.setErrorEnabled(true);
        layout.setError(message);
    }
    private void unsetInputError(TextInputLayout layout){
        layout.setError("");
        layout.setErrorEnabled(false);
    }
}
