package vn.edu.ut.gts.views.search;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.codetroopers.betterpickers.OnDialogDismissListener;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.ThreeBounce;
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
import vn.edu.ut.gts.actions.helpers.Storage;
import vn.edu.ut.gts.adapters.StudentSearchDetailViewPagerAdpater;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.presenters.search.StudentSearchPresenter;
import vn.edu.ut.gts.views.search.fragments.StudentSearchStudyResultFragment;

public class StudentSearchActivity extends AppCompatActivity implements IStudentSearchActivity, CalendarDatePickerDialogFragment.OnDateSetListener {

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
    NestedScrollView resultLayoutScroll;
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
    @BindView(R.id.no_internet_layout)
    LinearLayout noInternetLayout;
    @BindView(R.id.retry_text)
    TextView retryText;
    @BindView(R.id.spin_kit)
    SpinKitView loadingIcon;
    @BindView(R.id.student_search_tablayout)
    TabLayout studentSearchTablayout;
    @BindView(R.id.student_search_view_pager)
    ViewPager studentSearchViewPager;
    @BindView(R.id.reset_date)
    ImageButton resetDateBtn;
    @BindView(R.id.layout_container)
    RelativeLayout layoutContainer;

    public static final int SEARCH_LAYOUT = 1;
    public static final int RESULT_LAYOUT = 3;


    private int fromLayout = 0;
    private JSONObject currentStudentClick = null;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private final String DATE_REGEX = "(.*)-(.*)-(.*)";
    private StudentSearchPresenter studentSearchPresenter;
    private boolean isNoInputFailed = false;
    private int day = 0;
    private int month = 0;
    private int year = 0;
    private String[] searchResultHeaderData = {"MSSV", "Họ tên", "Ngày sinh"};
    private float d;
    private StudentSearchDetailViewPagerAdpater studentSearchDetailViewPagerAdpater;
    private EpicDialog epicDialog;
    private Pattern pattern = Pattern.compile(DATE_REGEX);
    private Matcher matcher = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_search);
        ButterKnife.bind(this);
        d = getResources().getDisplayMetrics().density;
        epicDialog = new EpicDialog(StudentSearchActivity.this);
        epicDialog.initLoadingDialog();
        studentSearchPresenter = new StudentSearchPresenter(this, StudentSearchActivity.this);
        studentSearchPresenter.getDataSearch();
        searchToolbar.setTitle("Tìm kiếm sinh viên");
        ThreeBounce threeBounce = new ThreeBounce();
        loadingIcon.setIndeterminateDrawable(threeBounce);
        setSupportActionBar(searchToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.studentSearchDetailViewPagerAdpater = new StudentSearchDetailViewPagerAdpater(getSupportFragmentManager());
        studentSearchViewPager.setAdapter(studentSearchDetailViewPagerAdpater);
        studentSearchTablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        studentSearchTablayout.setTabMode(TabLayout.MODE_FIXED);
        studentSearchTablayout.setupWithViewPager(studentSearchViewPager);
        resultLayoutScroll.setSmoothScrollingEnabled(true);
        resultLayoutScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY - oldScrollY > 50) {
                    floatingContainer.hideMenu(true);
                }
                if (oldScrollY - scrollY > 50) {
                    floatingContainer.showMenu(true);
                }
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    floatingContainer.hideMenu(true);
                }
            }
        });
    }

    @OnClick(R.id.layout_container)
    public void containerLayoutClick(View view){
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
        if (StudentSearchPresenter.currentStatus != 0) {
            studentSearchPresenter.getDataSearch();
        } else {
            this.validateInput();
            if (isNoInputFailed) {
                searchToLoadLayout();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Bundle bundle = new Bundle();
                        String studentId = (TextUtils.isEmpty(inputStudentID.getText().toString().trim())) ? "" : inputStudentID.getText().toString().trim();
                        String firstname = (TextUtils.isEmpty(inputFirstName.getText().toString().trim())) ? "" : inputFirstName.getText().toString().trim();
                        String lastname = (TextUtils.isEmpty(inputLastName.getText().toString().trim())) ? "" : inputLastName.getText().toString().trim();
                        matcher = pattern.matcher(birthDateTV.getText().toString());
                        String birthday = (matcher.matches()) ? birthDateTV.getText().toString() : "";
                        String classname = (TextUtils.isEmpty(inputClass.getText().toString().trim())) ? "" : inputClass.getText().toString().trim();

                        bundle.putString("student_id", studentId);
                        bundle.putString("first_name", firstname);
                        bundle.putString("last_name", lastname);
                        bundle.putString("birthday", birthday);
                        bundle.putString("class_name", classname);

                        studentSearchPresenter.searchStudent(bundle);
                    }
                }, 1000);
            }
        }
    }

    @OnClick(R.id.to_search_layout_float_btn)
    public void returnSearchLayout() {
        floatingContainer.close(true);
        if (fromLayout != 0 && StudentSearchPresenter.currentStatus != 0)
            noInternetToSearchLayout();
        else {
            if (detailLayout.isShown())
                detailReturnSearchLayout();
            else resultReturnSearchLayout();
        }
        StudentSearchPresenter.currentStatus = 0;
    }

    @OnClick(R.id.to_result_layout_float_btn)
    public void returnResultLayout() {
        floatingContainer.close(true);
        if (fromLayout != 0 && StudentSearchPresenter.currentStatus != 0)
            noInternetToResultLayout();
        else detailReturnResultLayout();
        StudentSearchPresenter.currentStatus = 0;
    }

    public void viewStudentDetail(final JSONObject jsonObject) {
//        resultToLoadLayout();
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                studentSearchPresenter.getStudentDetail(jsonObject);
//            }
//        }, 1000);
        Bundle bundle = new Bundle();
        bundle.putString("data",jsonObject.toString());
        Intent intent = new Intent(StudentSearchActivity.this,StudentDetailActivity.class);
        intent.putExtra("data",bundle);
        startActivity(intent);
    }

    @OnClick(R.id.retry_text)
    public void retry(View view) {
        StudentSearchPresenter.currentStatus = 0;
        noInternetToLoadLayout();
        if (fromLayout == StudentSearchActivity.SEARCH_LAYOUT) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Bundle bundle = new Bundle();
                    bundle.putString("mssv", TextUtils.isEmpty(inputStudentID.getText().toString().trim()) ? "" : inputStudentID.getText().toString().trim());
                    bundle.putString("first_name", (TextUtils.isEmpty(inputFirstName.getText().toString().trim())) ? "" : inputFirstName.getText().toString().trim());
                    bundle.putString("last_name", TextUtils.isEmpty(inputLastName.getText().toString().trim()) ? "" : inputLastName.getText().toString().trim());
                    matcher = pattern.matcher(birthDateTV.getText().toString());
                    bundle.putString("birth_date", (matcher.matches()) ? birthDateTV.getText().toString() : "");
                    bundle.putString("class", TextUtils.isEmpty(inputClass.getText().toString().trim()) ? "" : inputClass.getText().toString().trim());
                }
            }, 1000);
        }
        if (fromLayout == StudentSearchActivity.RESULT_LAYOUT) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            studentSearchPresenter.getStudentDetail(currentStudentClick);
                        }
                    }, 1000);
                }
            }, 1000);
        }
    }

    @OnClick(R.id.reset_date)
    public void resetDateTV(View view){
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.rotate_360_1ts);
        view.startAnimation(animation);
        birthDateTV.setText(getResources().getString(R.string.search_birthday));
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
    public void onBackPressed() {
        if (resultLayout.isShown() || noResultLayout.isShown()) {
            resultReturnSearchLayout();
        } else if (detailLayout.isShown()) {
            detailReturnResultLayout();
        } else if (noInternetLayout.isShown()) {
            if (fromLayout == StudentSearchActivity.SEARCH_LAYOUT) {
                noInternetToSearchLayout();
            } else if (fromLayout == StudentSearchActivity.RESULT_LAYOUT) {
                noInternetToResultLayout();
            }
        }
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
                        currentStudentClick = subject;
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
        String studentId = inputStudentID.getText().toString().trim();
        String lastname = inputLastName.getText().toString().trim();
        String birthday = birthDateTV.getText().toString().trim();
        String classname = inputClass.getText().toString().trim();

        matcher = pattern.matcher(birthday);

        if (!studentId.isEmpty() || !lastname.isEmpty() || matcher.matches() || !classname.isEmpty()) {
            isNoInputFailed = true;
        } else {
            showInputValidateEmpty("Thông tin tìm kiếm");
            isNoInputFailed = false;
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
                .duration(150)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        searchLayout.setVisibility(View.GONE);
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
                .playOn(searchLayout);
        YoYo.with(Techniques.SlideOutLeft)
                .duration(150)
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
                                        if (isNoResult) {
                                            resultLayout.setGravity(Gravity.CENTER);
                                        } else {
                                            resultLayout.setGravity(Gravity.TOP);
                                            searchResultTableHeader.setVisibility(View.VISIBLE);
                                            resultLayoutScroll.setVisibility(View.VISIBLE);
                                        }
                                        gtsLogo.setVisibility(View.GONE);
                                        floatingContainer.close(true);
                                        floatingContainer.setVisibility(View.VISIBLE);
                                        toSearchLayoutFloatBTN.setVisibility(View.VISIBLE);
                                        floatingContainer.animate();
                                        resultLayout.setVisibility(View.VISIBLE);
                                        searchToolbar.setTitle("Kết quả tìm kiếm");
                                    }
                                })
                                .playOn(findViewById(R.id.result_layout));
                    }
                })
                .playOn(findViewById(R.id.loading_layout));
    }

    @Override
    public void loadToNoInternetLayout(final int from) {
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
                                        fromLayout = from;
                                        if (fromLayout == StudentSearchActivity.RESULT_LAYOUT) {
                                            toResultLayoutFloatBTN.setVisibility(View.VISIBLE);
                                        } else toResultLayoutFloatBTN.setVisibility(View.GONE);
                                        toSearchLayoutFloatBTN.setVisibility(View.VISIBLE);
                                        floatingContainer.setVisibility(View.VISIBLE);
                                        noInternetLayout.setVisibility(View.VISIBLE);
                                        searchToolbar.setTitle("");
                                    }
                                })
                                .playOn(noInternetLayout);
                    }
                })
                .playOn(loadingLayout);
    }

    public void noInternetToSearchLayout() {
        YoYo.with(Techniques.SlideOutRight)
                .duration(150)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        floatingContainer.setVisibility(View.GONE);
                        floatingContainer.animate();
                        noInternetLayout.setVisibility(View.GONE);

                        YoYo.with(Techniques.SlideInLeft)
                                .duration(150)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        searchToolbar.setTitle("Tìm kiếm sinh viên");
                                        searchLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(searchLayout);
                        YoYo.with(Techniques.SlideInLeft)
                                .duration(150)
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
                .playOn(noInternetLayout);
    }

    public void noInternetToResultLayout() {
        YoYo.with(Techniques.SlideOutRight)
                .duration(150)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        floatingContainer.setVisibility(View.GONE);
                        floatingContainer.animate();
                        noInternetLayout.setVisibility(View.GONE);

                        YoYo.with(Techniques.SlideInLeft)
                                .duration(150)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        searchToolbar.setTitle("Kết quả tìm kiếm:");
                                        resultLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(resultLayout);
                    }
                })
                .playOn(noInternetLayout);
    }

    public void noInternetToLoadLayout() {
        YoYo.with(Techniques.SlideOutLeft)
                .duration(150)
                .repeat(0)
                .onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        floatingContainer.setVisibility(View.GONE);
                        toSearchLayoutFloatBTN.setVisibility(View.GONE);
                        toResultLayoutFloatBTN.setVisibility(View.GONE);
                    }
                })
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        noInternetLayout.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideInRight)
                                .duration(150)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        loadingLayout.setVisibility(View.VISIBLE);
                                        searchToolbar.setTitle("");
                                    }
                                })
                                .playOn(loadingLayout);
                    }
                })
                .playOn(noInternetLayout);
    }

    public void resultReturnSearchLayout() {
        YoYo.with(Techniques.SlideOutRight)
                .duration(150)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        floatingContainer.setVisibility(View.GONE);
                        toSearchLayoutFloatBTN.setVisibility(View.GONE);
                        toResultLayoutFloatBTN.setVisibility(View.GONE);
                        floatingContainer.animate();
                        resultLayout.setVisibility(View.GONE);
                        noResultLayout.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideInLeft)
                                .duration(150)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        searchToolbar.setTitle("Tìm kiếm sinh viên");
                                        searchLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(searchLayout);
                        YoYo.with(Techniques.SlideInLeft)
                                .duration(150)
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
                        searchToolbar.setTitle("");
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

    @Override
    public void loadToDetailLayout(final String name) {
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
                                        floatingContainer.close(true);
                                        floatingContainer.setVisibility(View.VISIBLE);
                                        toSearchLayoutFloatBTN.setVisibility(View.VISIBLE);
                                        toResultLayoutFloatBTN.setVisibility(View.VISIBLE);
                                        detailLayout.setVisibility(View.VISIBLE);
                                        searchToolbar.setTitle(name);
                                    }
                                })
                                .playOn(detailLayout);
                    }
                })
                .playOn(loadingLayout);
    }

    public void detailReturnResultLayout() {
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
                                        searchToolbar.setTitle("Kết quả tìm kiếm");
                                        resultLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(resultLayout);
                    }
                })
                .playOn(detailLayout);
    }

    public void detailReturnSearchLayout() {
        YoYo.with(Techniques.SlideOutRight)
                .duration(150)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        floatingContainer.setVisibility(View.GONE);
                        toResultLayoutFloatBTN.setVisibility(View.GONE);
                        toSearchLayoutFloatBTN.setVisibility(View.GONE);
                        floatingContainer.animate();
                        detailLayout.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideInLeft)
                                .duration(150)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        searchToolbar.setTitle("Tìm kiếm sinh viên");
                                        searchLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(searchLayout);
                        YoYo.with(Techniques.SlideInLeft)
                                .duration(150)
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
                .playOn(detailLayout);
    }

    public int getScreenWidthInDPs() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }

    public void setStudentDetailData(JSONArray data) {
        this.studentSearchDetailViewPagerAdpater.setData(data);
        this.studentSearchDetailViewPagerAdpater.notifyDataSetChanged();
        this.studentSearchTablayout.setScrollPosition(0, 0f, true);
        this.studentSearchViewPager.setCurrentItem(0);
    }

    @Override
    public void showLoadingDialog() {
        if (!epicDialog.isShowing()) epicDialog.showLoadingDialog();
    }

    @Override
    public void dismissLoadingDialog() {
        if (epicDialog.isShowing()) epicDialog.dismisPopup();
    }

    @Override
    public void showNoResultLayout() {
        noResultLayout.setVisibility(View.VISIBLE);
        searchResultTableHeader.setVisibility(View.GONE);
        resultLayoutScroll.setVisibility(View.GONE);
    }

    @Override
    public void showTimeoutDialog() {
        if (epicDialog.isShowing()) epicDialog.dismisPopup();
        disableInput();
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
    public void searchToRetryBtn() {
        btnSearch.setText(getResources().getString(R.string.retry_btn));
    }

    @Override
    public void retryToSearchBtn() {
        btnSearch.setText(getResources().getString(R.string.search_btn));
        enableInput();
    }

    private void enableInput() {
        inputStudentID.setEnabled(true);
        inputClass.setEnabled(true);
        inputFirstName.setEnabled(true);
        inputLastName.setEnabled(true);
        pickDate.setEnabled(true);
        resetDateBtn.setEnabled(true);
    }

    private void disableInput() {
        inputStudentID.setEnabled(false);
        inputClass.setEnabled(false);
        inputFirstName.setEnabled(false);
        inputLastName.setEnabled(false);
        pickDate.setEnabled(false);
        resetDateBtn.setEnabled(false);
    }


}
