package vn.edu.ut.gts.views.home.fragments;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import vn.edu.ut.gts.presenters.home.FrameProgramFragmentPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FrameProgramFragment extends Fragment implements IFrameProgramFragment {
    @BindView(R.id.frame_program_table)
    TableLayout frameProgramTable;
    @BindView(R.id.frame_program_table_header)
    TableLayout frameprogramTableHeader;
    @BindView(R.id.frame_program_spinner)
    MaterialSpinner frameProgramSpinner;
    @BindView(R.id.retry_text)
    TextView retryText;
    @BindView(R.id.loaded_layout)
    LinearLayout loadedLayout;
    @BindView(R.id.no_internet_layout)
    LinearLayout noInternetLayout;
    @BindView(R.id.rety_icon)
    AVLoadingIndicatorView retyIcon;



    public static int currentPos = 0;
    private FrameProgramFragmentPresenter frameProgramFragmentPresenter;
    private JSONObject data;
    private EpicDialog epicDialog;
    private float d;
    private List<String> headerText = new ArrayList<>();
    private List<String> dataSpinner = new ArrayList<>();

    public FrameProgramFragment() {
        headerText.add("Tên môn học");
        headerText.add("Số tín chỉ");
        headerText.add("Số tiết");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_frame_program_toolbar_menu, menu);
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    @Override
    public void showAllComponent() {
        frameProgramSpinner.setVisibility(View.VISIBLE);
        loadedLayout.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);
    }

    @Override
    public void hideAllComponent() {
        frameProgramSpinner.setVisibility(View.GONE);
        loadedLayout.setVisibility(View.GONE);
    }

    @Override
    public void showNetworkErrorLayout() {
        if (epicDialog.isShowing()) epicDialog.dismisPopup();
        hideAllComponent();
        retyIcon.hide();
        retryText.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frame_program, container, false);
        ButterKnife.bind(this, view);
        frameProgramSpinner.canScrollVertically(MaterialSpinner.LAYOUT_DIRECTION_INHERIT);
        init();
        d = Objects.requireNonNull(getContext()).getResources().getDisplayMetrics().density;
        FrameProgramFragmentPresenter.currentStatus = 0;
        frameProgramFragmentPresenter = new FrameProgramFragmentPresenter(this, getContext());
        setHasOptionsMenu(true);
        frameProgramFragmentPresenter.getDataFrameProgram();
        return view;
    }

    @OnClick(R.id.retry_text)
    public void retry(View view){
        FrameProgramFragmentPresenter.currentStatus = 0;
        frameProgramFragmentPresenter.getDataFrameProgram();
    }

    private void init() {
        epicDialog = new EpicDialog(getContext());
        epicDialog.initLoadingDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.infor: {
                try {
                    if(FrameProgramFragmentPresenter.currentStatus == 0){
                        String titleAll = data.getString("info");
                        String[] parts = titleAll.split("-");
                        StringBuilder title1 = new StringBuilder();
                        for (int i = 0; i <= parts.length - 3; i++) {
                            if (i == parts.length - 3) {
                                title1.append(parts[i].trim());
                                break;
                            }
                            title1.append(parts[i].trim()).append(" - ");
                        }
                        epicDialog.showFrameProgramInfoDialog(title1.toString().trim(), parts[parts.length - 2].trim() + " - " + parts[parts.length - 1].trim());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return true;
    }

    @Override
    public void generateTableContent(int position) {
        frameProgramTable.removeAllViews();
        try {
            JSONArray allQuater = data.getJSONArray("all_quater");
            JSONObject quater = (JSONObject) allQuater.get(position);
            frameProgramTable.addView(generateSubjectGroup("Học phần bắt buộc (" + quater.getString("so_chi_bat_buoc") + " tín chỉ)"));
            JSONArray batBuoc = quater.getJSONArray("bat_buoc");
            JSONArray khongBatBuoc = quater.getJSONArray("khong_bat_buoc");

            for (int i = 0; i < batBuoc.length(); i++) {
                JSONArray subject = (JSONArray) batBuoc.get(i);
                try {
                    if ((i + 1) % 2 == 0) {
                        frameProgramTable.addView(generateTableRow(subject, true));
                    } else frameProgramTable.addView(generateTableRow(subject, false));

                } catch (Exception e) {

                }
            }

            if (khongBatBuoc.length() > 0) {
                frameProgramTable.addView(generateSubjectGroup("Học phần tự chọn (" + quater.getString("so_chi_khong_bat_buoc") + " tín chỉ)"));
                for (int i = 0; i < khongBatBuoc.length(); i++) {
                    JSONArray subject = (JSONArray) khongBatBuoc.get(i);
                    try {
                        if ((i + 1) % 2 == 0) {
                            frameProgramTable.addView(generateTableRow(subject, true));
                        } else frameProgramTable.addView(generateTableRow(subject, false));

                    } catch (Exception e) {

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showLoadingDialog() {
        if (!epicDialog.isShowing())
            epicDialog.showLoadingDialog();
    }

    @Override
    public void dismissLoadingDialog() {
        if (epicDialog.isShowing())
            epicDialog.dismisPopup();
    }

    @Override
    public TableRow generateTableHeader() {
        TableRow header = new TableRow(getContext());
        header.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        header.setMinimumHeight((int) d * 50);
        header.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        for (int i = 0; i < headerText.size(); i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            if (i == 0) {
                layoutParams.width = (int) (getScreenWidthInDPs(getContext()) * 0.6);
            } else {
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.width = (int) (getScreenWidthInDPs(getContext()) * 0.2);
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

    @Override
    public TableRow generateTableRow(final JSONArray jsonArray, boolean changeBG) {
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        row.setMinimumHeight((int) d * 50);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameDetailShow(jsonArray);
            }
        });
        if (changeBG) row.setBackgroundColor(getResources().getColor(R.color.gray3));
        try {
            row.addView(generateTableCell(jsonArray.get(1).toString(), false, (int) (getScreenWidthInDPs(getContext()) * 0.6)));
            row.addView(generateTableCell(jsonArray.get(4).toString(), true, (int) (getScreenWidthInDPs(getContext()) * 0.2)));
            row.addView(generateTableCell(jsonArray.get(5).toString(), true, (int) (getScreenWidthInDPs(getContext()) * 0.2)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return row;
    }

    @Override
    public LinearLayout generateTableCell(String data, boolean center, int width) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        layoutParams.width = width;
        linearLayout.setPadding((int) d * 5, (int) d * 15, (int) d * 15, (int) d * 5);
        if (center) layoutParams.gravity = Gravity.CENTER;
        linearLayout.setLayoutParams(layoutParams);

        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams textViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        if (center) textViewLayout.gravity = Gravity.CENTER;
        textView.setLayoutParams(textViewLayout);
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setText(data);
        linearLayout.addView(textView);

        return linearLayout;
    }

    public TableRow generateSubjectGroup(String content) {
        TableRow tableRow = new TableRow(getContext());
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setMinimumHeight((int) d * 40);
        tableRow.setBackgroundColor(getResources().getColor(R.color.violet));

        // generate cell container
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setGravity(Gravity.CENTER);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);

        // generate cell's text view
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams a = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        a.setMargins((int) d * 10, 0, (int) d, 0);
        textView.setLayoutParams(a);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setText(content);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size));
        linearLayout.addView(textView);
        tableRow.addView(linearLayout);
        return tableRow;
    }

    @Override
    public void spinnerInit() {
        try {
            JSONArray allQuater = data.getJSONArray("all_quater");
            for (int i = 0; i < allQuater.length(); i++) {
                JSONObject jsonObject = (JSONObject) allQuater.get(i);
                dataSpinner.add(jsonObject.getString("quater_name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        frameProgramSpinner.setItems(dataSpinner);
        frameprogramTableHeader.addView(this.generateTableHeader());
        frameProgramSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                FrameProgramFragment.currentPos = position;
                generateTableContent(position);
            }
        });
    }

    public int getScreenWidthInDPs(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    @Override
    public void frameDetailShow(JSONArray jsonArray) {

        LayoutInflater factory = getLayoutInflater();
        View view = factory.inflate(R.layout.student_frame_program_detail_dialog, null);
        /*Bind view component*/
        TextView maMonHoc = view.findViewById(R.id.ma_mon_hoc);
        TextView tenMonHoc = view.findViewById(R.id.ten_mon_hoc);
        TextView maHocPhan = view.findViewById(R.id.ma_hoc_phan);
        TextView hocPhan = view.findViewById(R.id.hoc_phan);
        TextView soTC = view.findViewById(R.id.so_tc_dvht);
        TextView soTietLT = view.findViewById(R.id.so_tiet_lt);
        TextView soTietTH = view.findViewById(R.id.so_tiet_th);
        /* set data detail*/
        try {
            maMonHoc.setText(jsonArray.get(0).toString());
            tenMonHoc.setText(jsonArray.get(1).toString());
            maHocPhan.setText(jsonArray.get(2).toString());
            hocPhan.setText(jsonArray.get(3).toString());
            soTC.setText(jsonArray.get(4).toString());
            soTietLT.setText(jsonArray.get(5).toString());
            soTietTH.setText(jsonArray.get(6).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*create & show dialog*/
        Dialog simpleDialog = DialogUtils.createSimpleDialog(getContext(), view, true);
        if (simpleDialog != null && !simpleDialog.isShowing()) {
            simpleDialog.show();
        }
    }
}
