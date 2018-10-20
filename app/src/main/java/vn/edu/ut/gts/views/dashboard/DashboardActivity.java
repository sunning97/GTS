package vn.edu.ut.gts.views.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.Student;
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.views.home.HomeActivity;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.dashboard_toolbar)
    Toolbar dashboardToolbar;
    @BindView(R.id.dashboard_appbar_layout)
    AppBarLayout dashboardAppbarLayout;
    @BindView(R.id.dashboard_collapsing_toolbar_alyout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.student_info_card)
    CardView studentInfoCard;
    @BindView(R.id.student_study_result_card)
    CardView studentStudyResultCard;
    @BindView(R.id.frame_program_card)
    CardView frameProgramCard;
    @BindView(R.id.student_debt_card)
    CardView studentDebtCard;
    @BindView(R.id.schedule_by_week_card)
    CardView scheduleByWeekCard;
    @BindView(R.id.attendance_card)
    CardView attendanceCard;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;

    private Storage storage;
    private Student student;
    private SweetAlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);
        this.init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar_menu, menu);
        return true;
    }

    @OnClick({
            R.id.student_info_card,
            R.id.student_study_result_card,
            R.id.frame_program_card,
            R.id.student_debt_card,
            R.id.schedule_by_week_card,
            R.id.attendance_card
    })
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.student_info_card:
                startActivity(HomeActivity.STUDENT_INFO);
                break;
            case R.id.student_study_result_card:
                startActivity(HomeActivity.STUDENT_STUDY_RESULT);
                break;
            case R.id.frame_program_card:
                startActivity(HomeActivity.FRAME_PROGRAM);
                break;
            case R.id.student_debt_card:
                startActivity(HomeActivity.STUDENT_DEBT);
                break;
            case R.id.schedule_by_week_card:
                startActivity(HomeActivity.SCHEDULE_BY_WEEK);
                break;
            case R.id.attendance_card:
                startActivity(HomeActivity.ATTENDACE);
                break;
            default:
                return;
        }
    }

    private void startActivity(int order) {
        Intent intent = new Intent(DashboardActivity.this, HomeActivity.class);
        intent.putExtra("order", String.valueOf(order));
        startActivity(intent);
    }

    private void init() {
        collapsingToolbarLayout.setTitle("loading...");
        this.storage = new Storage(this);
        this.student = new Student(this);

        loadingDialog = new SweetAlertDialog(DashboardActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loadingDialog.setTitleText("Loading");
        loadingDialog.setCancelable(false);

        if(!HomeActivity.isLogin){
            getStudentData();
        } else {
            Bitmap image = storage.getImageFromStorage(DashboardActivity.this);
            profileImage.setImageBitmap(image);
        }

        setSupportActionBar(dashboardToolbar);
        String studentName = this.storage.getString("student_name");
        String studentID = this.storage.getString("last_student_login");
        collapsingToolbarLayout.setTitle(studentName+" - "+studentID);
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

    private void getStudentData(){
        AsyncTask<Void, Void, JSONObject> asyncTask = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected void onPreExecute() {
                loadingDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... voids) {
                JSONObject studentData = student.getStudentInfo();
                student.saveStudentImage(DashboardActivity.this);
                return studentData;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                storage.putString("student_info",jsonObject.toString());
                Bitmap image = storage.getImageFromStorage(DashboardActivity.this);
                profileImage.setImageBitmap(image);
                loadingDialog.dismiss();
            }
        };
        asyncTask.execute();
    }

}
