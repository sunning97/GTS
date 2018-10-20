package vn.edu.ut.gts.views.home.fragments;


import android.annotation.SuppressLint;
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
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.Student;
import vn.edu.ut.gts.adapters.WeekScheduleTablayoutAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeekSchedule extends Fragment {

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

    private Student student;
    private SweetAlertDialog loadingDialog;
    private WeekScheduleTablayoutAdapter weekScheduleTablayoutAdapter;

    public WeekSchedule() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_schedule, container, false);
        ButterKnife.bind(this,view);
        init();
        getDataWeekSchedule();

        return view;
    }

    @OnClick(R.id.next_week)
    public void setNextWeek(View view) {
        AsyncTask<Void,Void,JSONArray> voidVoidVoidAsyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                loadingDialog.show();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray jsonArray = student.getNextSchedulesWeek();
                return jsonArray;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                setDateToDate(jsonArray);
                weekScheduleTablayoutAdapter.setData(jsonArray);
                weekScheduleTablayoutAdapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }
        };
        voidVoidVoidAsyncTask.execute();
    }
    @OnClick(R.id.prev_week)
    public void setPrevWeek(View view) {
        AsyncTask<Void,Void,JSONArray> voidVoidVoidAsyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                loadingDialog.show();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray jsonArray = student.getPrevSchedulesWeek();
                return jsonArray;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                setDateToDate(jsonArray);
                weekScheduleTablayoutAdapter.setData(jsonArray);
                weekScheduleTablayoutAdapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }
        };
        voidVoidVoidAsyncTask.execute();
    }
    @OnClick(R.id.current_week)
    public void setCurrentWeek(View view) {
        AsyncTask<Void,Void,JSONArray> voidVoidVoidAsyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                loadingDialog.show();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray jsonArray = student.getCurrentSchedulesWeek();
                return jsonArray;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                setDateToDate(jsonArray);
                weekScheduleTablayoutAdapter.setData(jsonArray);
                weekScheduleTablayoutAdapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }
        };
        voidVoidVoidAsyncTask.execute();
    }

    private void getDataWeekSchedule(){
        AsyncTask<Void, Void, JSONArray> asyncTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {
                loadingDialog.show();
            }

            @Override
            protected JSONArray doInBackground(Void... voids) {
                JSONArray jsonArray = student.getSchedulesGetMethod();
                return jsonArray;
            }

            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                setDateToDate(jsonArray);
                weekScheduleTablayoutAdapter = new WeekScheduleTablayoutAdapter(getFragmentManager(),jsonArray);
                viewPager.setAdapter(weekScheduleTablayoutAdapter);
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
                tabLayout.setupWithViewPager(viewPager);
                tabLayout.setScrollPosition(getCurrentDate(jsonArray),0f,true);
                viewPager.setCurrentItem(getCurrentDate(jsonArray));
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

    @SuppressLint("SetTextI18n")
    private void setDateToDate(JSONArray jsonArray){
        dateToDate.setText("");
        try {
            JSONObject firstDate = jsonArray.getJSONObject(0);
            JSONObject lastDate = jsonArray.getJSONObject(jsonArray.length()-1);
            dateToDate.setText("Tuần: "+firstDate.getString("date")+" - "+lastDate.getString("date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int getCurrentDate(JSONArray jsonArray){
        int position = 0;
        for (int i = 0;i< jsonArray.length();i++){
            try {
                if(jsonArray.getJSONObject(i).getString("current_date").equals("true")){
                    position = i;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  position;
    }
}
