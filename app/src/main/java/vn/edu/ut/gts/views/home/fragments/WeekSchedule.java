package vn.edu.ut.gts.views.home.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.adapters.WeekScheduleTablayoutAdapter;
import vn.edu.ut.gts.helpers.EpicDialog;
import vn.edu.ut.gts.presenters.home.WeekSchedulePresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeekSchedule extends Fragment implements CalendarDatePickerDialogFragment.OnDateSetListener, IWeekSchedule {
    @BindView(R.id.week_schedule_tablayout)
    TabLayout tabLayout;
    @BindView(R.id.week_schedule_view_pager)
    ViewPager viewPager;
    @BindView(R.id.date_to_date)
    TextView dateToDate;
    @BindView(R.id.next_week)
    FloatingActionButton nextWeek;
    @BindView(R.id.prev_week)
    FloatingActionButton prevWeek;
    @BindView(R.id.current_week)
    FloatingActionButton currentWeek;
    @BindView(R.id.date_picker)
    ImageButton datePicker;
    @BindView(R.id.tablayout_container)
    RelativeLayout tablayoutContainer;
    @BindView(R.id.floating_container)
    FloatingActionMenu floatingContainer;
    @BindView(R.id.no_internet_layout)
    LinearLayout noInternetLayout;
    @BindView(R.id.rety_icon)
    AVLoadingIndicatorView retyIcon;
    @BindView(R.id.retry_text)
    TextView retryText;

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private WeekSchedulePresenter weekSchedulePresenter;
    private int day = 0;
    private int month = 0;
    private int year = 0;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;

    private EpicDialog loadingDialog;
    private WeekScheduleTablayoutAdapter weekScheduleTablayoutAdapter;

    public WeekSchedule() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_schedule, container, false);
        ButterKnife.bind(this, view);
        init();
        WeekSchedulePresenter.currentStatus = 0;
        weekSchedulePresenter = new WeekSchedulePresenter(this, getContext());
        weekSchedulePresenter.getSchedulesGetMethod();
        return view;
    }

    @OnClick(R.id.next_week)
    public void setNextWeek(View view) {
        floatingContainer.close(true);
        weekSchedulePresenter.getNextSchedulesWeek();
    }

    @OnClick(R.id.prev_week)
    public void setPrevWeek(View view) {
        floatingContainer.close(true);
        weekSchedulePresenter.getPrevSchedulesWeek();
    }

    @OnClick(R.id.current_week)
    public void setCurrentWeek(View view) {
        floatingContainer.close(true);
        weekSchedulePresenter.getCurrentSchedulesWeek();
    }

    @OnClick(R.id.date_picker)
    public void showDatePickerDialog(View view) {
        CalendarDatePickerDialogFragment datePicker = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(this)
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setCancelText("Hủy")
                .setDoneText("Chọn");
        if (day != 0) {
            datePicker.setPreselectedDate(year, month, day);
        }
        datePicker.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);
    }


    private void init() {
        loadingDialog = new EpicDialog(getContext());
        loadingDialog.initLoadingDialog();
    }

    @OnClick(R.id.retry_text)
    public void retry(View view) {
        WeekSchedulePresenter.currentStatus = 0;
        weekSchedulePresenter.getSchedulesGetMethod();
    }

    @SuppressLint("SetTextI18n")
    public void setDateToDate(JSONArray jsonArray) {
        dateToDate.setText("");
        try {
            JSONObject firstDate = jsonArray.getJSONObject(0);
            JSONObject lastDate = jsonArray.getJSONObject(jsonArray.length() - 1);
            dateToDate.setText("Tuần: " + firstDate.getString("date") + " - " + lastDate.getString("date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifyDataOnfirst(JSONArray jsonArray) {
        weekScheduleTablayoutAdapter = new WeekScheduleTablayoutAdapter(getFragmentManager(), jsonArray);
        viewPager.setAdapter(weekScheduleTablayoutAdapter);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setScrollPosition(getCurrentDate(jsonArray), 0f, true);
        viewPager.setCurrentItem(getCurrentDate(jsonArray));
    }

    @Override
    public void modifyDataChange(JSONArray jsonArray) {
        weekScheduleTablayoutAdapter.setData(jsonArray);
        weekScheduleTablayoutAdapter.notifyDataSetChanged();
    }

    @Override
    public void hideAllComponent() {
        tablayoutContainer.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        floatingContainer.setVisibility(View.GONE);
    }

    @Override
    public void showAllComponent() {
        noInternetLayout.setVisibility(View.GONE);
        tablayoutContainer.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        floatingContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNetworkErrorLayout() {
        if (loadingDialog.isShowing()) loadingDialog.dismisPopup();
        floatingContainer.close(true);
        hideAllComponent();
        retyIcon.hide();
        retryText.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.VISIBLE);
    }

    private int getCurrentDate(JSONArray jsonArray) {
        int position = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                if (jsonArray.getJSONObject(i).getString("current_date").equals("true")) {
                    position = i;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return position;
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        this.day = dayOfMonth;
        this.month = monthOfYear;
        this.year = year;

        String day = (dayOfMonth < 10) ? "0" + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
        String mounth = ((monthOfYear + 1) < 10) ? "0" + String.valueOf(monthOfYear + 1) : String.valueOf(monthOfYear + 1);
        String date = day + "-" + mounth + "-" + String.valueOf(year);

        weekSchedulePresenter.getSchedulesByDate(date);
    }

    @Override
    public void showLoadingDialog() {
        if (!loadingDialog.isShowing())
            loadingDialog.showLoadingDialog();
    }

    @Override
    public void dismissLoadingDialog() {
        if (loadingDialog.isShowing())
            loadingDialog.dismisPopup();
    }
}
