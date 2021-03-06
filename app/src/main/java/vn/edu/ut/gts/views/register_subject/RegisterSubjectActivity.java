package vn.edu.ut.gts.views.register_subject;

import android.animation.Animator;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.helpers.Helper;
import vn.edu.ut.gts.helpers.Storage;
import vn.edu.ut.gts.presenters.home.FrameProgramFragmentPresenter;
import vn.edu.ut.gts.presenters.register_subject.RegisterSubjectPresenter;

public class RegisterSubjectActivity extends AppCompatActivity implements IRegisterSubjectActivity{
    @BindView(R.id.all_subject_layout)
    LinearLayout allSubjectLayout;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    @BindView(R.id.no_internet_layout)
    LinearLayout noInternetLayout;
    @BindView(R.id.all_class_of_subject_layout)
    LinearLayout allClassOfSubjectLayout;
    @BindView(R.id.register_subject_table_header)
    TableLayout registerSubjectTableHeader;
    @BindView(R.id.register_subject_table)
    TableLayout registerSubjectTable;
    @BindView(R.id.loading_icon)
    SpinKitView loadingIcon;
    @BindView(R.id.register_subject_toolbar)
    Toolbar registerSubjectToolbar;
    @BindView(R.id.all_class_table_header)
    TableLayout allClassTableHeader;
    @BindView(R.id.all_class_table)
    TableLayout allClassTable;
    @BindView(R.id.quarter_text)
    TextView quarterText;

    private RegisterSubjectPresenter registerSubjectPresenter;
    private String[] subjectHeaderText, classHeaderText;
    private float d;
    private Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_subject);
        ButterKnife.bind(this);
        d = getResources().getDisplayMetrics().density;
        storage = new Storage(this);
        Resources res = getResources();
        subjectHeaderText = res.getStringArray(R.array.register_subject_header_text);
        classHeaderText = res.getStringArray(R.array.register_class_header_text);
        FadingCircle fadingCircle = new FadingCircle();
        loadingIcon.setIndeterminateDrawable(fadingCircle);
        registerSubjectToolbar.setTitle(res.getString(R.string.register_subject));
        setSupportActionBar(registerSubjectToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        registerSubjectPresenter = new RegisterSubjectPresenter(this,this);
        registerSubjectPresenter.getData();
    }

    @Override
    public void showLoadingLayout() {
        loadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingLayout() {
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void showInternetErrorLayout() {

    }

    @Override
    public IRegisterSubjectActivity hideInternetErrorLayout() {
        noInternetLayout.setVisibility(View.GONE);
        return  this;
    }

    @Override
    public void showAllSubjectLayout() {
        allSubjectLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public IRegisterSubjectActivity setTextQuarter() {
        String quarter = this.storage.getString("register_subject_current_quarter_text");
        String batch = getResources().getString(R.string.register_quarter);
        quarterText.setText(batch.concat(": ").concat(quarter));
        return this;
    }

    @Override
    public IRegisterSubjectActivity hideAllSubjectLayout() {
        allSubjectLayout.setVisibility(View.GONE);
        return this;
    }

    @Override
    public IRegisterSubjectActivity generateTableSubjectContent(JSONArray data) {
        registerSubjectTableHeader.removeAllViews();
        registerSubjectTable.removeAllViews();
        /*add table header*/
        registerSubjectTableHeader.addView(this.generateTableHeader(subjectHeaderText));
        try {
            for (int i = 0; i < data.length(); i++) {
                /* generate table record & add to table body*/
                registerSubjectTable.addView(generateTableRow(data.getJSONArray(i), ((i + 1) % 2 == 0),1));
            }
            /* show all halt day*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void generateTableClassContent(JSONArray data) {
        allClassTable.removeAllViews();
        allClassTableHeader.removeAllViews();
        /*add table header*/
        allClassTableHeader.addView(this.generateTableHeader(classHeaderText));
        try {
            for (int i = 0; i < data.length(); i++) {
                /* generate table record & add to table body*/
                allClassTable.addView(generateTableRow(data.getJSONArray(i), ((i + 1) % 2 == 0),2));
            }
            /* show all halt day*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public TableRow generateTableRow(final JSONArray jsonArray, boolean changeBG, int type) {
        TableRow row = new TableRow(this);

        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);

        row.setBackgroundResource(backgroundResource);

        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT)
        );
        row.setMinimumHeight((int) d * 50);
        if (changeBG) row.setBackgroundColor(getResources().getColor(R.color.gray3));

        if(type == 1){
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        registerSubjectToolbar.setTitle(jsonArray.getString(11));
                        registerSubjectPresenter.getClassOfSubject(jsonArray.getString(2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            try {
                row.addView(generateTableCell(
                        jsonArray.getString(11),
                        false,
                        (int) (Helper.getScreenWidthInDPs(this) * 0.4)
                ));
                row.addView(generateTableCell(
                        jsonArray.getString(9),
                        true,
                        (int) (Helper.getScreenWidthInDPs(this) * 0.3)
                ));
                row.addView(generateTableCell(
                        (Boolean.valueOf(jsonArray.getString(1)) ? "Có": ""),
                        true,
                        (int) (Helper.getScreenWidthInDPs(this) * 0.3)
                ));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        registerSubjectPresenter.getClassSchedule(jsonArray.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            try {
                row.addView(generateTableCell(
                        jsonArray.getString(3),
                        false,
                        (int) (Helper.getScreenWidthInDPs(this) * 0.4)
                ));
                row.addView(generateTableCell(
                        jsonArray.getString(5),
                        true,
                        (int) (Helper.getScreenWidthInDPs(this) * 0.3)
                ));
                row.addView(generateTableCell(
                        jsonArray.getString(13),
                        true,
                        (int) (Helper.getScreenWidthInDPs(this) * 0.3)
                ));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return row;
    }

    public LinearLayout generateTableCell(String content, Boolean isMarginCenter, int width) {
        LinearLayout linearLayout = new LinearLayout(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.MATCH_PARENT
        );
        layoutParams.width = width;
        linearLayout.setPadding((int) d * 5, (int) d * 10, (int) d * 15, (int) d * 5);
        if (isMarginCenter) layoutParams.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(layoutParams);

        TextView textView = new TextView(this);
        LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        if (isMarginCenter) textViewLayout.gravity = Gravity.CENTER;
        textView.setLayoutParams(textViewLayout);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(content);
        linearLayout.addView(textView);

        return linearLayout;
    }

    public TableRow generateTableHeader(String[] data) {
        TableRow header = new TableRow(this);
        header.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT)
        );
        header.setMinimumHeight((int) d * 50);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        for (int i = 0; i < data.length; i++) {
            LinearLayout linearLayout = new LinearLayout(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT
            );
            if (i == 0) {
                layoutParams.width = (int) (Helper.getScreenWidthInDPs(this) * 0.4);
            } else {
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.width = (int) (Helper.getScreenWidthInDPs(this) * 0.3);
            }
            linearLayout.setPadding((int) d * 5, (int) d * 15, (int) d * 5, 0);
            linearLayout.setLayoutParams(layoutParams);

            TextView textView = new TextView(this);
            LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            textView.setLayoutParams(textViewLayout);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            textView.setText(data[i]);
            linearLayout.addView(textView);
            header.addView(linearLayout);
        }
        return header;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadingToAllSubject(){
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
                                        allSubjectLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(allSubjectLayout);
                    }
                })
                .playOn(loadingLayout);
    }

    @Override
    public void loadingToInternetError() {
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
                                        noInternetLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(noInternetLayout);
                    }
                })
                .playOn(loadingLayout);
    }

    @Override
    public void internetErrorToLoading() {
        YoYo.with(Techniques.SlideOutLeft)
                .duration(150)
                .repeat(0)
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
                                    }
                                })
                                .playOn(loadingLayout);
                    }
                })
                .playOn(noInternetLayout);
    }

    @Override
    public void allSubjectToLoading() {
        YoYo.with(Techniques.SlideOutLeft)
                .duration(150)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        allSubjectLayout.setVisibility(View.GONE);
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
                .playOn(allSubjectLayout);
    }

    @Override
    public void loadingToAllClass() {
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
                                        allClassOfSubjectLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(allClassOfSubjectLayout);
                    }
                })
                .playOn(loadingLayout);
    }

    @Override
    public void showNoClassNotify() {
        Snackbar.make(findViewById(R.id.register_subject_layout), "Không tìm thấy dữ liệu!", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void allClassReturnAllSubject() {
        YoYo.with(Techniques.SlideOutRight)
                .duration(150)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        allClassOfSubjectLayout.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideInLeft)
                                .duration(150)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        registerSubjectToolbar.setTitle("Đăng kí học phần");
                                        allSubjectLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(allSubjectLayout);
                    }
                })
                .playOn(allClassOfSubjectLayout);
    }

    @Override
    public void onBackPressed() {
        if(allClassOfSubjectLayout.isShown()){
            allClassReturnAllSubject();
        } else if(allSubjectLayout.isShown()) finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register_subject_menu, menu);
        return true;
    }

    @OnClick(R.id.retry_text)
    public void retry(View view){
        RegisterSubjectPresenter.currentStatus = 0;
        registerSubjectPresenter.getData();
    }
}