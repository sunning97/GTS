package vn.edu.ut.gts.views.home.fragments;


import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import vn.edu.ut.gts.helpers.Storage;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.presenters.home.TestSchedulePresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestScheduleFragment extends Fragment implements ITestScheduleFragment {
    @BindView(R.id.test_schedule_table)
    TableLayout testScheduleTable;
    @BindView(R.id.test_schedule_spinner)
    MaterialSpinner testScheduleSpinner;
    @BindView(R.id.test_schedule_table_header)
    TableLayout testScheduleTableHeader;
    @BindView(R.id.loaded_layout)
    LinearLayout loadedLayout;
    @BindView(R.id.no_internet_layout)
    LinearLayout noInternetLayout;
    @BindView(R.id.retry_text)
    TextView retryText;


    private TestSchedulePresenter testSchedulePresenter;

    private EpicDialog epicDialog;
    private float d;
    private List<String> headerText = new ArrayList<>();
    private List<String> spinnerData = new ArrayList<>();
    public static int currentPos = 0;
    private Storage storage;

    public TestScheduleFragment() {
        headerText.add("Môn thi");
        headerText.add("Ngày thi");
        headerText.add("Phòng thi");
        headerText.add("Loại thi");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_schedule, container, false);
        ButterKnife.bind(this, view);
        storage = new Storage(Objects.requireNonNull(getContext()));
        epicDialog = new EpicDialog(getContext());
        epicDialog.initLoadingDialog();
        d = getContext().getResources().getDisplayMetrics().density;
        TestSchedulePresenter.currentStatus = 0;
        testSchedulePresenter = new TestSchedulePresenter(this, getContext());
        testSchedulePresenter.getDataTestSchedule();

        return view;
    }


    @OnClick(R.id.retry_text)
    public void retry(View view) {
        TestSchedulePresenter.currentStatus = 0;
        if (TextUtils.isEmpty(storage.getString("data_test_schedule"))) {
            testSchedulePresenter.getDataTestSchedule();
        } else
            testSchedulePresenter.getDataTestSchedule(currentPos);
    }

    @Override
    public void showLoadingDialog() {
        if(!epicDialog.isShowing())
            epicDialog.showLoadingDialog();
    }

    @Override
    public void dismissLoadingDialog() {
        if(epicDialog.isShowing())
            epicDialog.dismisPopup();
    }

    @Override
    public void hideAllComponent() {
        testScheduleSpinner.setVisibility(View.GONE);
        loadedLayout.setVisibility(View.GONE);
    }

    @Override
    public void showAllComponent() {
        testScheduleSpinner.setVisibility(View.VISIBLE);
        loadedLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoInternetLayout() {
        noInternetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoInternetLayout() {
        noInternetLayout.setVisibility(View.GONE);
    }

    public void generateTableContent(JSONArray data) {
        testScheduleTable.removeAllViews();
        testScheduleTableHeader.removeAllViews();
        testScheduleTableHeader.addView(this.generateTableHeader());
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject subject = data.getJSONObject(i);
                testScheduleTable.addView(generateTableRow(subject, (i % 2 == 0)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setupDataSpiner(JSONArray data) {
        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject item = data.getJSONObject(i);
                spinnerData.add(item.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        testScheduleSpinner.setItems(spinnerData);
        testScheduleSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                currentPos = position;
                testSchedulePresenter.getDataTestSchedule(position);
            }
        });
    }

    public TableRow generateTableRow(final JSONObject jsonObject, boolean changeBG) {
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setMinimumHeight((int) d * 60);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTestScheduleDetail(jsonObject);
            }
        });
        if (changeBG) row.setBackgroundColor(getResources().getColor(R.color.gray3));
        try {
            row.addView(generateTableCell(jsonObject.getString("mon_thi"), false, (int) (Helper.getScreenWidthInDPs(Objects.requireNonNull(getContext())) * 0.3)));
            row.addView(generateTableCell(jsonObject.getString("ngay_thi"), false, (int) (Helper.getScreenWidthInDPs(getContext()) * 0.3)));
            row.addView(generateTableCell(jsonObject.getString("phong_thi"), false, (int) (Helper.getScreenWidthInDPs(getContext()) * 0.2)));
            row.addView(generateTableCell(jsonObject.getString("loai_thi"), false, (int) (Helper.getScreenWidthInDPs(getContext()) * 0.2)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return row;
    }

    public TableRow generateTableHeader() {
        TableRow header = new TableRow(getContext());
        header.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header.setMinimumHeight((int) d * 50);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        for (int i = 0; i < headerText.size(); i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            if (i == 0 || i == 1) {
                layoutParams.width = (int) (Helper.getScreenWidthInDPs(Objects.requireNonNull(getContext())) * 0.3);
            } else {
                layoutParams.width = (int) (Helper.getScreenWidthInDPs(getContext()) * 0.2);
            }
            linearLayout.setPadding((int) d * 5, (int) d * 15, (int) d * 5, 0);
            linearLayout.setLayoutParams(layoutParams);

            TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(textViewLayout);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            textView.setText(headerText.get(i));
            linearLayout.addView(textView);
            header.addView(linearLayout);
        }

        return header;
    }

    public LinearLayout generateTableCell(String content, Boolean isGravityCenter, int width) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        layoutParams.width = width;
        linearLayout.setPadding((int) d * 5, (int) d * 15, (int) d * 15, (int) d * 5);
        if (isGravityCenter) layoutParams.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(layoutParams);

        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        if (isGravityCenter) textViewLayout.gravity = Gravity.CENTER;
        textView.setLayoutParams(textViewLayout);
        textView.setTextColor(getResources().getColor(R.color.black));

        textView.setText(content);
        linearLayout.addView(textView);

        return linearLayout;
    }

    public void showTestScheduleDetail(JSONObject jsonObject) {
        LayoutInflater factory = getLayoutInflater();
        View view = factory.inflate(R.layout.test_schedule_detail_dialog, null);
        TextView lopHP = view.findViewById(R.id.lop_hp);
        TextView monThi = view.findViewById(R.id.mon_thi);
        TextView nhom = view.findViewById(R.id.nhom);
        TextView tuSiSo = view.findViewById(R.id.tu_si_so);
        TextView ngayThi = view.findViewById(R.id.ngay_thi);
        TextView tietThi = view.findViewById(R.id.tiet_thi);
        TextView phongThi = view.findViewById(R.id.phong_thi);
        TextView loaiThi = view.findViewById(R.id.loai_thi);
        TextView ghiGhu = view.findViewById(R.id.ghi_chu);
        try {
            lopHP.setText(jsonObject.getString("lop_hp"));
            monThi.setText(jsonObject.getString("mon_thi"));
            nhom.setText(jsonObject.getString("nhom"));
            tuSiSo.setText(jsonObject.getString("tu_si_so"));
            ngayThi.setText(jsonObject.getString("ngay_thi"));
            tietThi.setText(jsonObject.getString("tiet_thi"));
            phongThi.setText(jsonObject.getString("phong_thi"));
            loaiThi.setText(jsonObject.getString("loai_thi"));
            ghiGhu.setText(jsonObject.getString("ghi_chu"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Dialog simpleDialog = DialogUtils.createSimpleDialog(getContext(), view, true);
        if (simpleDialog != null && !simpleDialog.isShowing()) {
            simpleDialog.show();
        }
    }
}
