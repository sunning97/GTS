package vn.edu.ut.gts.views.home.fragments;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.Student;
import vn.edu.ut.gts.adapters.WeekScheduleTablayoutAdapter;
import vn.edu.ut.gts.views.home.fragments.weekday.MondayFragment;
import vn.edu.ut.gts.views.home.fragments.weekday.TuesdayFragment;
import vn.edu.ut.gts.views.home.fragments.weekday.WednesdayFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeekSchedule extends Fragment {

    @BindView(R.id.week_schedule_tablayout)
    TabLayout tabLayout;
    @BindView(R.id.week_schedule_view_pager)
    ViewPager viewPager;

    private List<Fragment> fragments;
    private List<String> fragmentTitle;
    private TuesdayFragment tuesdayFragment;
    private MondayFragment mondayFragment;
    private WednesdayFragment wednesdayFragment;

    private Student student;
    private SweetAlertDialog loadingDialog;
    private WeekScheduleTablayoutAdapter weekScheduleTablayoutAdapter;




    public WeekSchedule() {
        mondayFragment = new MondayFragment();
        tuesdayFragment = new TuesdayFragment();
        wednesdayFragment = new WednesdayFragment();

        this.fragments = new ArrayList<>();
        this.fragmentTitle = new ArrayList<>();

        this.fragments.add(mondayFragment);
        this.fragments.add(tuesdayFragment);
        this.fragments.add(wednesdayFragment);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_schedule, container, false);
        ButterKnife.bind(this,view);

        init();
        getDataWeekSchedule();

        return view;
    }

    private void getDataWeekSchedule(){
        AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                loadingDialog.show();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray jsonArray = student.getSchedules();
                return jsonArray;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                weekScheduleTablayoutAdapter = new WeekScheduleTablayoutAdapter(getFragmentManager(),jsonArray);
                viewPager.setAdapter(weekScheduleTablayoutAdapter);
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
                tabLayout.setupWithViewPager(viewPager);

                loadingDialog.dismiss();
            }
        };
        asyncTask.execute();
    }


    private void init(){
        student = new Student(getContext());
        loadingDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loadingDialog.setTitleText("Loading");
        loadingDialog.setCancelable(false);
    }
}
