package vn.edu.ut.gts.views.search;

import android.animation.Animator;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.presenters.search.StudentSearchPresenter;

public class StudentSearchActivity extends AppCompatActivity implements IStudentSearchActivity,CalendarDatePickerDialogFragment.OnDateSetListener{
    @BindView(R.id.type_search_spinner)
    MaterialSpinner typeSearchSpinner;
    @BindView(R.id.layout_search_by_name)
    LinearLayout layoutSearchByName;
    @BindView(R.id.layout_search_by_id)
    LinearLayout layoutSearchByID;
    @BindView(R.id.layout_search_by_birth_date)
    LinearLayout layoutSearchByBirthDate;
    @BindView(R.id.input_search_layout)
    LinearLayout inputSearchLayout;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.result_layout)
    LinearLayout resultLayout;
    @BindView(R.id.btn_return_search_layout)
    Button btnReturnSearchLayout;
    @BindView(R.id.search_toolbar)
    Toolbar searchToolbar;
    @BindView(R.id.layout_search_by_class)
    LinearLayout layoutSsearchByClass;
    @BindView(R.id.input_student_id)
    EditText inputStudentID;
    @BindView(R.id.birth_date_tv)
    TextView birthDateTV;
    @BindView(R.id.input_class)
    EditText inpuClass;
    @BindView(R.id.input_first_name)
    EditText inputFirstName;
    @BindView(R.id.input_last_name)
    EditText inputLastName;
    @BindView(R.id.pick_date)
    ImageButton pickDate;

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private final String DATE_REGEX = "(.*)-(.*)-(.*)";
    private StudentSearchPresenter studentSearchPresenter;
    private boolean isNoInputFailed = false;
    private int day = 0;
    private int month = 0;
    private int year = 0;
    private String[] spinnerData = {"Mã số sinh viên", "Họ tên", "Ngày sinh", "Lớp Học"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_search);
        ButterKnife.bind(this);
        studentSearchPresenter = new StudentSearchPresenter(this, StudentSearchActivity.this);
        studentSearchPresenter.getDataSearch();
        searchToolbar.setTitle("Tìm kiếm sinh viên");
        setSupportActionBar(searchToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        typeSearchSpinner.setItems(spinnerData);
        typeSearchSpinner.setSelectedIndex(0);
        typeSearchSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                typeSearchSpinner.animate();
                switch (position) {
                    case 0: {
                        layoutSearchByID.setVisibility(View.VISIBLE);
                        layoutSearchByName.setVisibility(View.GONE);
                        layoutSearchByBirthDate.setVisibility(View.GONE);
                        layoutSsearchByClass.setVisibility(View.GONE);
                        break;
                    }
                    case 1: {
                        layoutSearchByName.setVisibility(View.VISIBLE);
                        layoutSearchByID.setVisibility(View.GONE);
                        layoutSearchByBirthDate.setVisibility(View.GONE);
                        layoutSsearchByClass.setVisibility(View.GONE);
                        break;
                    }
                    case 2: {
                        birthDateTV.setText("Chọn ngày:");
                        layoutSearchByBirthDate.setVisibility(View.VISIBLE);
                        layoutSearchByID.setVisibility(View.GONE);
                        layoutSearchByName.setVisibility(View.GONE);
                        layoutSsearchByClass.setVisibility(View.GONE);
                        break;
                    }
                    case 3: {
                        layoutSsearchByClass.setVisibility(View.VISIBLE);
                        layoutSearchByBirthDate.setVisibility(View.GONE);
                        layoutSearchByID.setVisibility(View.GONE);
                        layoutSearchByName.setVisibility(View.GONE);
                        break;
                    }
                }
            }
        });
    }

    @OnClick(R.id.pick_date)
    public void datePicker(){
        CalendarDatePickerDialogFragment datePicker = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(this)
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setCancelText("Hủy")
                .setDoneText("Chọn");
        if (this.day != 0) {
            datePicker.setPreselectedDate(this.year, this.month, this.day);
        }
        datePicker.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }

    @OnClick(R.id.btn_search)
    public void search(View view) {
        this.validateInput();
        if(isNoInputFailed){
            searchToLoadLayout();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadToResultLayout();
                }
            },3000);
        }
    }

    @OnClick(R.id.btn_return_search_layout)
    public void returnSearchLayout() {
        YoYo.with(Techniques.SlideOutLeft)
                .duration(250)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        resultLayout.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideInRight)
                            .duration(250)
                            .repeat(0)
                            .onStart(new YoYo.AnimatorCallback() {
                                @Override
                                public void call(Animator animator) {
                                    inputSearchLayout.setVisibility(View.VISIBLE);
                                }
                            })
                            .playOn(findViewById(R.id.input_search_layout));
                    }
                })
                .playOn(findViewById(R.id.result_layout));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showNetworkErrorLayout() {

    }

    @Override
    public void showInputValidateEmpty(String s) {
        new SweetAlertDialog(this)
                .setTitleText(getResources().getString(R.string.input_validate_empty_title))
                .setContentText(s + " " + getResources().getString(R.string.input_validate_empty_content))
                .show();
    }

    private void validateInput() {
        isNoInputFailed = true;
        int position = typeSearchSpinner.getSelectedIndex();
        switch (position) {
            case 0: {
                if (TextUtils.isEmpty(inputStudentID.getText().toString())) {
                    showInputValidateEmpty("Mã số sinh viên");
                    isNoInputFailed = false;
                }
                break;
            }
            case 1: {
                if (TextUtils.isEmpty(inputLastName.getText().toString().trim())) {
                    showInputValidateEmpty("Tên");
                    isNoInputFailed = false;
                }

                break;
            }
            case 2: {
                String date = birthDateTV.getText().toString().trim();
                Pattern pattern = null;
                Matcher matcher = null;
                pattern = Pattern.compile(DATE_REGEX);
                matcher = pattern.matcher(date);
                if (!matcher.matches()) {
                    showInputValidateEmpty("Ngày sinh");
                    isNoInputFailed = false;
                }
                break;
            }
            case 3: {
                if (TextUtils.isEmpty(inpuClass.getText().toString().trim())) {
                    showInputValidateEmpty("Lớp học");
                    isNoInputFailed = false;
                }
                break;
            }
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        this.day = dayOfMonth;
        this.month = monthOfYear;
        this.year = year;
        String day = (dayOfMonth < 10) ? "0" + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
        String mounth = ((monthOfYear + 1) < 10) ? "0" + String.valueOf(monthOfYear + 1) : String.valueOf(monthOfYear + 1);
        String date = day + "-" + mounth + "-" + String.valueOf(year);
        birthDateTV.setText(date);
    }

    private void searchToLoadLayout(){
        YoYo.with(Techniques.SlideOutLeft)
                .duration(250)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        inputSearchLayout.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideInRight)
                                .duration(250)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        loadingLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(findViewById(R.id.loading_layout));
                    }
                })
                .playOn(findViewById(R.id.input_search_layout));
    }
    private void loadToResultLayout(){
        YoYo.with(Techniques.SlideOutLeft)
                .duration(250)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        loadingLayout.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideInRight)
                                .duration(250)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        resultLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(findViewById(R.id.result_layout));
                    }
                })
                .playOn(findViewById(R.id.loading_layout));
    }
}
