package vn.edu.ut.gts.views.search;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.presenters.search.StudentSearchPresenter;

public class StudentSearchActivity extends AppCompatActivity implements IStudentSearchActivity, CalendarDatePickerDialogFragment.OnDateSetListener {
    @BindView(R.id.type_search_spinner)
    MaterialSpinner typeSearchSpinner;
    @BindView(R.id.layout_search_by_name)
    LinearLayout layoutSearchByName;
    @BindView(R.id.layout_search_by_id)
    LinearLayout layoutSearchByID;
    @BindView(R.id.layout_search_by_birth_date)
    LinearLayout layoutSearchByBirthDate;
    @BindView(R.id.input_search_layout)
    LinearLayout searchLayout;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.result_layout)
    LinearLayout resultLayout;
    @BindView(R.id.search_toolbar)
    Toolbar searchToolbar;
    @BindView(R.id.layout_search_by_class)
    LinearLayout layoutSearchByClass;
    @BindView(R.id.input_student_id)
    EditText inputStudentID;
    @BindView(R.id.birth_date_tv)
    TextView birthDateTV;
    @BindView(R.id.input_class)
    EditText inputClass;
    @BindView(R.id.input_first_name)
    EditText inputFirstName;
    @BindView(R.id.input_last_name)
    EditText inputLastName;
    @BindView(R.id.pick_date)
    ImageButton pickDate;
    @BindView(R.id.search_result_table_header)
    TableLayout searchResultTableHeader;
    @BindView(R.id.search_result_table_body)
    TableLayout searchResultTableBody;
    @BindView(R.id.result_layout_scroll)
    ScrollView resultLayoutScroll;
    @BindView(R.id.gts_logo)
    ImageView gtsLogo;
    @BindView(R.id.floating_container)
    FloatingActionMenu floatingContainer;
    @BindView(R.id.to_search_layout_float_btn)
    FloatingActionButton toSearchLayoutFloatBTN;
    @BindView(R.id.to_result_layout_float_btn)
    FloatingActionButton toResultLayoutFloatBTN;
    @BindView(R.id.no_result_layout)
    LinearLayout noResultLayout;
    @BindView(R.id.detail_layout)
    LinearLayout detailLayout;

    private static final int SEARCH_LAYOUT = 1;
    private static final int RESULT_LAYOUT = 2;
    private static final int DETAIL_LAYOUT = 3;
    private int currentLayout = StudentSearchActivity.SEARCH_LAYOUT;
    private int nextLayout = 0;

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private final String DATE_REGEX = "(.*)-(.*)-(.*)";
    private StudentSearchPresenter studentSearchPresenter;
    private boolean isNoInputFailed = false;
    private int day = 0;
    private int month = 0;
    private int year = 0;
    private String[] spinnerData = {"Mã số sinh viên", "Họ tên", "Ngày sinh", "Lớp Học"};
    private String[] searchResultHeaderData = {"MSSV", "Họ tên", "Ngày sinh"};
    private float d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_search);
        ButterKnife.bind(this);
        d = getResources().getDisplayMetrics().density;
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
                        inputStudentID.setText("");
                        layoutSearchByID.setVisibility(View.VISIBLE);
                        layoutSearchByName.setVisibility(View.GONE);
                        layoutSearchByBirthDate.setVisibility(View.GONE);
                        layoutSearchByClass.setVisibility(View.GONE);
                        break;
                    }
                    case 1: {
                        inputFirstName.setText("");
                        inputLastName.setText("");
                        layoutSearchByName.setVisibility(View.VISIBLE);
                        layoutSearchByID.setVisibility(View.GONE);
                        layoutSearchByBirthDate.setVisibility(View.GONE);
                        layoutSearchByClass.setVisibility(View.GONE);
                        break;
                    }
                    case 2: {
                        birthDateTV.setText("Chọn ngày:");
                        layoutSearchByBirthDate.setVisibility(View.VISIBLE);
                        layoutSearchByID.setVisibility(View.GONE);
                        layoutSearchByName.setVisibility(View.GONE);
                        layoutSearchByClass.setVisibility(View.GONE);
                        break;
                    }
                    case 3: {
                        inputClass.setText("");
                        layoutSearchByClass.setVisibility(View.VISIBLE);
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
    public void datePicker() {
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
        if (isNoInputFailed) {
            searchToLoadLayout();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int position = typeSearchSpinner.getSelectedIndex();
                    Bundle bundle;
                    switch (position) {
                        case 0:
                            bundle = new Bundle();
                            bundle.putString("mssv",inputStudentID.getText().toString().trim());
                            studentSearchPresenter.searchStudent(0,bundle);
                            break;
                        case 1:
                            bundle = new Bundle();
                            bundle.putString("first_name",(TextUtils.isEmpty(inputFirstName.getText().toString().trim())) ? "" : inputFirstName.getText().toString().trim());
                            bundle.putString("last_name",inputLastName.getText().toString().trim());
                            studentSearchPresenter.searchStudent(1,bundle);
                            break;
                        case 2:
                            bundle = new Bundle();
                            bundle.putString("birth_date",birthDateTV.getText().toString());
                            studentSearchPresenter.searchStudent(2,bundle);
                            break;
                        case 3:
                            bundle = new Bundle();
                            bundle.putString("class",inputClass.getText().toString().trim());
                            studentSearchPresenter.searchStudent(3,bundle);
                            break;
                    }
                }
            }, 1000);
        }
    }

    @OnClick(R.id.to_search_layout_float_btn)
    public void returnSearchLayout() {
        floatingContainer.close(true);
        resultToSearchLayout();
    }

    @OnClick(R.id.to_result_layout_float_btn)
    public void returnResultLayout(){
        floatingContainer.close(true);
        detailToResultLayout();
    }

    public void viewStudentDetail(JSONObject jsonObject){
        resultToLoadLayout();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadToDetailLayout();
            }
        }, 1000);
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

    @Override
    public void generateTableSearchResult(ArrayList<JSONObject> jsonObjects) {
        searchResultTableHeader.removeAllViews();
        searchResultTableBody.removeAllViews();

        //header
        TableRow header = new TableRow(StudentSearchActivity.this);
        header.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header.setMinimumHeight((int) d * 50);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        for (int i = 0; i < searchResultHeaderData.length; i++) {
            LinearLayout linearLayout = new LinearLayout(StudentSearchActivity.this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            switch (i) {
                case 0:
                    layoutParams.gravity = Gravity.CENTER;
                    layoutParams.width = (int) (getScreenWidthInDPs() * 0.3);
                    break;
                case 1:
                    layoutParams.width = (int) (getScreenWidthInDPs() * 0.4);
                    break;
                case 2:
                    layoutParams.gravity = Gravity.CENTER;
                    layoutParams.width = (int) (getScreenWidthInDPs() * 0.3);
                    break;
            }

            linearLayout.setPadding((int) d * 5, (int) d * 15, (int) d * 5, 0);
            linearLayout.setLayoutParams(layoutParams);

            TextView textView = new TextView(StudentSearchActivity.this);
            LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(textViewLayout);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            textView.setText(searchResultHeaderData[i]);
            linearLayout.addView(textView);
            header.addView(linearLayout);
        }
        searchResultTableHeader.addView(header);

        //body
        try {
            for (int i = 0; i < jsonObjects.size(); i++) {
                final JSONObject subject = jsonObjects.get(i);
                TableRow row = new TableRow(StudentSearchActivity.this);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                row.setMinimumHeight((int) d * 50);
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewStudentDetail(subject);
                    }
                });
                if (i % 2 != 0) row.setBackgroundColor(getResources().getColor(R.color.gray));
                row.addView(generateTableCell(subject.getString("studentCode"), true, (int) (getScreenWidthInDPs() * 0.3)));
                row.addView(generateTableCell(subject.getString("studentName"), false, (int) (getScreenWidthInDPs() * 0.4)));
                row.addView(generateTableCell(subject.getString("birthday"), true, (int) (getScreenWidthInDPs() * 0.3)));
                searchResultTableBody.addView(row);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LinearLayout generateTableCell(String content, Boolean isGravityCenter, int width) {
        LinearLayout linearLayout = new LinearLayout(StudentSearchActivity.this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        layoutParams.width = width;
        linearLayout.setPadding((int) d * 5, (int) d * 15, (int) d * 15, (int) d * 5);
        if (isGravityCenter) layoutParams.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(layoutParams);

        TextView textView = new TextView(StudentSearchActivity.this);
        LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        if (isGravityCenter) textViewLayout.gravity = Gravity.CENTER;
        textView.setLayoutParams(textViewLayout);
        textView.setTextColor(getResources().getColor(R.color.black));

        textView.setText(content);
        linearLayout.addView(textView);

        return linearLayout;
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
                if (TextUtils.isEmpty(inputClass.getText().toString().trim())) {
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

    public void searchToLoadLayout() {
        YoYo.with(Techniques.SlideOutLeft)
                .duration(250)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        searchLayout.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideInRight)
                                .duration(250)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        loadingLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(loadingLayout);
                    }
                })
                .playOn(searchLayout);
        YoYo.with(Techniques.SlideOutLeft)
                .duration(250)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        gtsLogo.setVisibility(View.GONE);
                    }
                })
                .playOn(gtsLogo);
    }



    @Override
    public void showNoResultLayout() {
        searchResultTableHeader.setVisibility(View.GONE);
        resultLayoutScroll.setVisibility(View.GONE);
        noResultLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void loadToResultLayout(final Boolean isNoResult) {
        YoYo.with(Techniques.SlideOutLeft)
                .duration(150)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        loadingLayout.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideInRight)
                                .duration(150)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        if(isNoResult) resultLayout.setGravity(Gravity.CENTER); else resultLayout.setGravity(Gravity.TOP);
                                        searchResultTableHeader.setVisibility(View.VISIBLE);
                                        resultLayoutScroll.setVisibility(View.VISIBLE);
                                        gtsLogo.setVisibility(View.GONE);
                                        floatingContainer.setVisibility(View.VISIBLE);
                                        toSearchLayoutFloatBTN.setVisibility(View.VISIBLE);
                                        floatingContainer.animate();
                                        resultLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(findViewById(R.id.result_layout));
                    }
                })
                .playOn(findViewById(R.id.loading_layout));
    }

    public void resultToSearchLayout() {
        YoYo.with(Techniques.SlideOutRight)
                .duration(150)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        floatingContainer.setVisibility(View.GONE);
                        toSearchLayoutFloatBTN.setVisibility(View.GONE);
                        floatingContainer.animate();
                        resultLayout.setVisibility(View.GONE);
                        noResultLayout.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideInLeft)
                                .duration(150)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        searchLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(searchLayout);
                        YoYo.with(Techniques.SlideInLeft)
                                .duration(250)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        gtsLogo.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(gtsLogo);
                    }
                })
                .playOn(resultLayout);
    }

    public void resultToLoadLayout() {
        YoYo.with(Techniques.SlideOutLeft)
                .duration(150)
                .repeat(0)
                .onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        floatingContainer.setVisibility(View.GONE);
                    }
                })
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        resultLayout.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideInRight)
                                .duration(150)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        loadingLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(loadingLayout);
                    }
                })
                .playOn(resultLayout);
    }

    public void loadToDetailLayout(){
        YoYo.with(Techniques.SlideOutLeft)
                .duration(150)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        loadingLayout.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideInRight)
                                .duration(150)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        floatingContainer.setVisibility(View.VISIBLE);
                                        toSearchLayoutFloatBTN.setVisibility(View.VISIBLE);
                                        toResultLayoutFloatBTN.setVisibility(View.VISIBLE);
                                        detailLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(detailLayout);
                    }
                })
                .playOn(loadingLayout);
    }

    public void detailToResultLayout(){
        YoYo.with(Techniques.SlideOutRight)
                .duration(150)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        floatingContainer.setVisibility(View.VISIBLE);
                        toSearchLayoutFloatBTN.setVisibility(View.VISIBLE);
                        toResultLayoutFloatBTN.setVisibility(View.GONE);
                        floatingContainer.animate();
                        detailLayout.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideInLeft)
                                .duration(150)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        resultLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(resultLayout);
                    }
                })
                .playOn(detailLayout);
    }

    public int getScreenWidthInDPs() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }
}
