package vn.edu.ut.gts.views.home.fragments;


import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.helpers.Helper;
import vn.edu.ut.gts.presenters.home.StudyForImprovementPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudyForImprovementFragment extends Fragment implements IStudyForImprovementFragment {
    private StudyForImprovementPresenter studyForImprovementPresenter;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    @BindView(R.id.loading_icon)
    SpinKitView loadingIcon;
    @BindView(R.id.all_subject_layout)
    LinearLayout allSubjectLayout;
    @BindView(R.id.study_for_improvement_table_header)
    TableLayout studyForImprovementTableHeader;
    @BindView(R.id.study_for_improvement_table)
    TableLayout studyForImprovementTable;
    @BindView(R.id.no_internet_layout)
    LinearLayout noInternetLayout;
    @BindView(R.id.retry_text)
    TextView retryText;
    @BindView(R.id.all_class_of_subject_layout)
    LinearLayout allClassOfSubjectLayout;
    @BindView(R.id.all_class_table_header)
    TableLayout allClassTableHeader;
    @BindView(R.id.all_class_table)
    TableLayout allClassTable;

    private String[] subjectHeaderText, classHeaderText;
    private float d;

    public StudyForImprovementFragment() {
    }

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study_for_improvement, container, false);
        ButterKnife.bind(this, view);
        FadingCircle fadingCircle = new FadingCircle();
        loadingIcon.setIndeterminateDrawable(fadingCircle);
        d = Objects.requireNonNull(getContext()).getResources().getDisplayMetrics().density;
        subjectHeaderText = getResources().getStringArray(R.array.study_for_improvement_subject_header_text);
        classHeaderText = getResources().getStringArray(R.array.study_for_improvement_class_header_text);
        studyForImprovementPresenter = new StudyForImprovementPresenter(getContext(), this);
        studyForImprovementPresenter.getData();
        this.view = view;
        return view;
    }

    @Override
    public void showLoading() {
        loadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void showInternetError() {
        noInternetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideInternetError() {
        noInternetLayout.setVisibility(View.GONE);
    }

    @Override
    public void showNoClassNotify() {
        Snackbar.make(this.view, "Không tìm thấy lớp!", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void generateTableSubjectContent(JSONArray data) {
        studyForImprovementTable.removeAllViews();
        studyForImprovementTableHeader.removeAllViews();
        /*add table header*/
        studyForImprovementTableHeader.addView(this.generateTableHeader(subjectHeaderText));
        try {
            for (int i = 0; i < data.length(); i++) {
                /* generate table record & add to table body*/
                studyForImprovementTable.addView(generateTableRow(data.getJSONArray(i), ((i + 1) % 2 == 0), 1));
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
        allClassTableHeader.addView(this.generateTableHeader(classHeaderText));
        try {
            for (int i = 0; i < data.length(); i++) {
                /* generate table record & add to table body*/
                allClassTable.addView(generateTableRow(data.getJSONArray(i), ((i + 1) % 2 == 0), 2));
            }
            /* show all halt day*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showLoadedLayout() {
        allSubjectLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadedLayout() {
        allSubjectLayout.setVisibility(View.GONE);
    }

    public TableRow generateTableRow(final JSONArray jsonArray, boolean changeBG, int type) {
        TableRow row = new TableRow(getContext());

        int[] attrs = new int[]{R.attr.selectableItemBackground};
        @SuppressLint("Recycle") TypedArray typedArray = Objects.requireNonNull(getActivity()).obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        row.setBackgroundResource(backgroundResource);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT)
        );
        row.setMinimumHeight((int) d * 50);
        if (changeBG) row.setBackgroundColor(getResources().getColor(R.color.gray3));
        if (type == 1) {
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        studyForImprovementPresenter.getClassOfSubject(jsonArray.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            try {
                row.addView(generateTableCell(
                        jsonArray.getString(7),
                        false,
                        (int) (Helper.getScreenWidthInDPs(Objects.requireNonNull(getContext())) * 0.4)
                ));
                row.addView(generateTableCell(
                        jsonArray.getString(8),
                        true,
                        (int) (Helper.getScreenWidthInDPs(getContext()) * 0.3)
                ));
                row.addView(generateTableCell(
                        jsonArray.getString(jsonArray.length() - 1),
                        true,
                        (int) (Helper.getScreenWidthInDPs(getContext()) * 0.3)
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
                        jsonArray.getString(5),
                        false,
                        (int) (Helper.getScreenWidthInDPs(Objects.requireNonNull(getContext())) * 0.4)
                ));
                row.addView(generateTableCell(
                        jsonArray.getString(7),
                        true,
                        (int) (Helper.getScreenWidthInDPs(getContext()) * 0.3)
                ));
                row.addView(generateTableCell(
                        jsonArray.getString(12),
                        true,
                        (int) (Helper.getScreenWidthInDPs(getContext()) * 0.3)
                ));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return row;
    }

    public LinearLayout generateTableCell(String content, Boolean isMarginCenter, int width) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.MATCH_PARENT
        );
        layoutParams.width = width;
        linearLayout.setPadding((int) d * 5, (int) d * 10, (int) d * 15, (int) d * 5);
        if (isMarginCenter) layoutParams.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(layoutParams);

        TextView textView = new TextView(getContext());
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
        TableRow header = new TableRow(getContext());
        header.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT)
        );
        header.setMinimumHeight((int) d * 50);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        for (int i = 0; i < data.length; i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT
            );
            if (i == 0) {
                layoutParams.width = (int) (Helper.getScreenWidthInDPs(Objects.requireNonNull(getContext())) * 0.4);
            } else {
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.width = (int) (Helper.getScreenWidthInDPs(getContext()) * 0.3);
            }
            linearLayout.setPadding((int) d * 5, (int) d * 15, (int) d * 5, 0);
            linearLayout.setLayoutParams(layoutParams);

            TextView textView = new TextView(getContext());
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

    @OnClick(R.id.retry_text)
    public void retry(View view) {
        StudyForImprovementPresenter.currentStatus = 0;
        internetErrorToLoading();
        studyForImprovementPresenter.getData();
    }

    @Override
    public void loadingToAllSubject() {
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
                                        showInternetError();
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
                                        showLoading();
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

}
