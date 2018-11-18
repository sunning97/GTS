package vn.edu.ut.gts.views.home.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.viethoa.DialogUtils;
import com.wang.avi.AVLoadingIndicatorView;

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
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.helpers.Helper;
import vn.edu.ut.gts.presenters.home.AttendanceFragmentPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment implements IAttendanceFragment {

    @BindView(R.id.student_total_halt_date)
    TextView tvStudentTotalDaltDate;
    @BindView(R.id.student_attendance_table)
    TableLayout studentAttendanceTable;
    @BindView(R.id.student_attendance_spinner)
    MaterialSpinner studentAttendanceSpinner;
    @BindView(R.id.student_attendance_table_header)
    TableLayout studentAttendanceTableHeader;
    @BindView(R.id.loaded_layout)
    LinearLayout loadedLayout;
    @BindView(R.id.no_internet_layout)
    LinearLayout noInternetLayout;
    @BindView(R.id.rety_icon)
    AVLoadingIndicatorView retryIcon;
    @BindView(R.id.retry_text)
    TextView retryText;
    @BindView(R.id.total_halt_day_layout)
    LinearLayout totalHaltDayLayout;

    private AttendanceFragmentPresenter attendanceFragmentPresenter;
    private float d;
    public static int currentPos = 0;

    private List<String> headerText = new ArrayList<>();
    private List<String> spinnerData = new ArrayList<>();
    private EpicDialog loadingDialog;

    public AttendanceFragment() {
        headerText.add("Tên môn học");
        headerText.add("ĐVHT");
        headerText.add("Có phép");
        headerText.add("Ko phép");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        /* Bind all view component*/
        ButterKnife.bind(this, view);

        this.attendanceFragmentPresenter = new AttendanceFragmentPresenter(this, getContext());

        this.init();

        d = Objects.requireNonNull(getContext()).getResources().getDisplayMetrics().density;

        /* set default position of spinner & status of connect in AttendanceFragmentPresenter*/
        AttendanceFragment.currentPos = 0;
        AttendanceFragmentPresenter.currentStatus = 0;
        attendanceFragmentPresenter.getDataAttendanceSpinner();
        return view;
    }


    @OnClick(R.id.retry_text)
    public void retry(View view) {
        AttendanceFragmentPresenter.currentStatus = 0;
        retryText.setVisibility(View.VISIBLE);
        attendanceFragmentPresenter.getDataAttendanceSpinner();
    }

    @Override
    public void generateTableContent(JSONArray data) {
        studentAttendanceTable.removeAllViews();
        /*add table header*/
        studentAttendanceTable.addView(this.generateTableHeader());
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject subject = data.getJSONObject(i);
                /* generate table record & add to table body*/
                studentAttendanceTable.addView(generateTableRow(subject, ((i + 1) % 2 == 0)));
            }
            /* show all halt day*/
            tvStudentTotalDaltDate.setText(String.valueOf(getTotalHaltDate(data)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public TableRow generateTableRow(final JSONObject jsonObject, boolean changeBG) {
        TableRow row = new TableRow(getContext());

        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = Objects.requireNonNull(getActivity()).obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);

        row.setBackgroundResource(backgroundResource);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT)
        );
        row.setMinimumHeight((int) d * 50);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* show dialog detail*/
                attendanceDetailShow(jsonObject);
            }
        });
        if (changeBG) row.setBackgroundColor(getResources().getColor(R.color.gray3));
        try {
            row.addView(generateTableCell(
                    jsonObject.getString("ten_mon_hoc"),
                    false,
                    (int) (Helper.getScreenWidthInDPs(Objects.requireNonNull(getContext())) * 0.4)
            ));
            row.addView(generateTableCell(
                    jsonObject.getString("dvht"),
                    true,
                    (int) (Helper.getScreenWidthInDPs(getContext()) * 0.2)
            ));
            row.addView(generateTableCell(
                    jsonObject.getString("nghi_co_phep"),
                    true,
                    (int) (Helper.getScreenWidthInDPs(getContext()) * 0.2)
            ));
            row.addView(generateTableCell(
                    jsonObject.getString("nghi_ko_phep"),
                    true,
                    (int) (Helper.getScreenWidthInDPs(getContext()) * 0.2)
            ));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return row;
    }

    @Override
    public LinearLayout generateTableCell(String content, Boolean isMarginCenter, int width) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.MATCH_PARENT
        );
        layoutParams.width = width;
        linearLayout.setPadding((int) d * 5, (int) d * 15, (int) d * 15, (int) d * 5);
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

    @Override
    public TableRow generateTableHeader() {
        TableRow header = new TableRow(getContext());
        header.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT)
        );
        header.setMinimumHeight((int) d * 50);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        for (int i = 0; i < headerText.size(); i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT
            );
            if (i == 0) {
                layoutParams.width = (int) (Helper.getScreenWidthInDPs(Objects.requireNonNull(getContext())) * 0.4);
            } else {
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.width = (int) (Helper.getScreenWidthInDPs(getContext()) * 0.2);
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
            textView.setText(headerText.get(i));
            linearLayout.addView(textView);
            header.addView(linearLayout);
        }
        return header;
    }

    private void init() {
        loadingDialog = new EpicDialog(getContext());
        loadingDialog.initLoadingDialog();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void attendanceDetailShow(JSONObject jsonObject) {
        LayoutInflater factory = getLayoutInflater();
        /* Bind view component of layout*/
        View view = factory.inflate(R.layout.student_attendance_detail_dialog, null);
        TextView maMonHoc = view.findViewById(R.id.ma_mon_hoc);
        TextView tenMonHoc = view.findViewById(R.id.ten_mon_hoc);
        TextView dvht = view.findViewById(R.id.dvht);
        TextView coPhep = view.findViewById(R.id.co_phep);
        TextView khongPhep = view.findViewById(R.id.khong_phep);
        /* set data for detail*/
        try {
            maMonHoc.setText(jsonObject.getString("ma_mon_hoc"));
            tenMonHoc.setText(jsonObject.getString("ten_mon_hoc"));
            dvht.setText(jsonObject.getString("dvht"));
            coPhep.setText(jsonObject.getString("nghi_co_phep") + " buổi");
            khongPhep.setText(jsonObject.getString("nghi_ko_phep") + " buổi");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /* create dialog & show*/
        Dialog simpleDialog = DialogUtils.createSimpleDialog(getContext(), view, true);
        if (simpleDialog != null && !simpleDialog.isShowing()) {
            simpleDialog.show();
        }
    }

    @Override
    public void initAttendanceSpiner(List<String> dataSnpinner) {
        studentAttendanceSpinner.setItems(dataSnpinner);
        studentAttendanceSpinner.setEnabled(true);
        studentAttendanceSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                currentPos = position;
                /* get data of each item in spinner via position of item*/
                attendanceFragmentPresenter.getDataAttendance(position);
            }
        });
    }

    @Override
    public void showLoadingDialog() {
        if (!loadingDialog.isShowing())
            this.loadingDialog.showLoadingDialog();
    }

    @Override
    public void dismissLoadingDialog() {
        if (loadingDialog.isShowing())
            this.loadingDialog.dismisPopup();
    }

    @Override
    public void showNetworkErrorLayout() {
        if (loadingDialog.isShowing()) loadingDialog.dismisPopup();
        removeAllSpinnerItem();
        retryIcon.hide();
        retryText.setVisibility(View.VISIBLE);
        hideAllComponent();
        noInternetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadedLayout() {
        studentAttendanceSpinner.setVisibility(View.VISIBLE);
        totalHaltDayLayout.setVisibility(View.VISIBLE);
        loadedLayout.setVisibility(View.VISIBLE);
        retryIcon.hide();
        retryText.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);
    }

    public int getTotalHaltDate(JSONArray data) {
        int result = 0;
        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject subject = data.getJSONObject(i);
                if (Integer.parseInt(subject.getString("nghi_co_phep")) > 0)
                    result += Integer.parseInt(subject.getString("nghi_co_phep"));
                if (Integer.parseInt(subject.getString("nghi_ko_phep")) > 0)
                    result += Integer.parseInt(subject.getString("nghi_co_phep"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    private void removeAllSpinnerItem(){
        studentAttendanceSpinner.setItems(spinnerData);
        studentAttendanceSpinner.setEnabled(false);
    }

    @Override
    public void hideAllComponent(){
        loadedLayout.setVisibility(View.GONE);
        studentAttendanceSpinner.setVisibility(View.GONE);
        totalHaltDayLayout.setVisibility(View.GONE);
    }
    @Override
    public void showAllComponent(){
        loadedLayout.setVisibility(View.VISIBLE);
        studentAttendanceSpinner.setVisibility(View.VISIBLE);
        totalHaltDayLayout.setVisibility(View.VISIBLE);
    }
}
