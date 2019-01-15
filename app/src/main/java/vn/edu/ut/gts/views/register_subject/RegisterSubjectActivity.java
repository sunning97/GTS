package vn.edu.ut.gts.views.register_subject;

import android.animation.Animator;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.helpers.Helper;
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


    private RegisterSubjectPresenter registerSubjectPresenter;
    private List<String> headerText = new ArrayList<>();
    private List<String> headerTextClass = new ArrayList<>();
    private float d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_subject);
        ButterKnife.bind(this);
        d = getResources().getDisplayMetrics().density;
        headerText.add("Tên môn học");
        headerText.add("Tín chỉ");
        headerText.add("Bắt buộc");
        headerTextClass.add("Lớp dự kiến");
        headerTextClass.add("Sĩ số tối đa");
        headerTextClass.add("Trạng thái");
        FadingCircle fadingCircle = new FadingCircle();
        loadingIcon.setIndeterminateDrawable(fadingCircle);

        setSupportActionBar(registerSubjectToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    public void hideInternetErrorLayout() {

    }

    @Override
    public void showAllSubjectLayout() {
        allSubjectLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAllSubjectLayout() {
        allSubjectLayout.setVisibility(View.GONE);
    }

    @Override
    public void generateTableSubjectContent(JSONArray data) {
        registerSubjectTableHeader.removeAllViews();
        registerSubjectTable.removeAllViews();
        /*add table header*/
        registerSubjectTableHeader.addView(this.generateTableHeader(headerText));
        try {
            for (int i = 0; i < data.length(); i++) {
                /* generate table record & add to table body*/
                registerSubjectTable.addView(generateTableRow(data.getJSONArray(i), ((i + 1) % 2 == 0),1));
            }
            /* show all halt day*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateTableClassContent(JSONArray data) {
        allClassTable.removeAllViews();
        allClassTableHeader.removeAllViews();
        /*add table header*/
        allClassTableHeader.addView(this.generateTableHeader(headerTextClass));
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

    public TableRow generateTableHeader(List<String> data) {
        TableRow header = new TableRow(this);
        header.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT)
        );
        header.setMinimumHeight((int) d * 50);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        for (int i = 0; i < data.size(); i++) {
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
            textView.setText(data.get(i));
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
}
