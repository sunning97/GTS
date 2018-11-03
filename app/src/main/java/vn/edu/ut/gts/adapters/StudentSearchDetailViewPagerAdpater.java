package vn.edu.ut.gts.adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.edu.ut.gts.views.search.fragments.StudentSearchDebtFragment;
import vn.edu.ut.gts.views.search.fragments.StudentSearchInfoFragment;
import vn.edu.ut.gts.views.search.fragments.StudentSearchStudyResultFragment;

public class StudentSearchDetailViewPagerAdpater extends FragmentStatePagerAdapter {

    private List<String> fragmentTitle;
    private JSONArray data;

    public StudentSearchDetailViewPagerAdpater(FragmentManager fm, JSONArray data) {
        super(fm);
        this.data = data;
        this.fragmentTitle = new ArrayList<>();
        this.fragmentTitle.add("Thông tin sinh viên");
        this.fragmentTitle.add("Kết quả học tập");
        this.fragmentTitle.add("Công nợ");

    }

    public StudentSearchDetailViewPagerAdpater(FragmentManager fm) {
        super(fm);
        this.fragmentTitle = new ArrayList<>();
        this.fragmentTitle.add("Thông tin sinh viên");
        this.fragmentTitle.add("Kết quả học tập");
//        this.fragmentTitle.add("Công nợ");
    }

    public void setData(JSONArray data) {
        this.data = data;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        Bundle bundle = null;
        try {
            switch (i) {
                case 0:
                    fragment = new StudentSearchInfoFragment();
                    JSONObject info = data.getJSONObject(0);
                    JSONArray studentInfo = info.getJSONArray("studentInfo");
                    bundle = new Bundle();
                    bundle.putString("data", studentInfo.toString());
                    fragment.setArguments(bundle);
                    break;
                case 1:
                    fragment = new StudentSearchStudyResultFragment();
                    JSONObject studyResult = data.getJSONObject(1);
                    bundle = new Bundle();
                    bundle.putString("data", studyResult.toString());
                    fragment.setArguments(bundle);
                    break;
                case 2:
                    fragment = new StudentSearchDebtFragment();
                    bundle = new Bundle();
                    bundle.putString("data", data.getJSONArray(3).toString());
                    fragment.setArguments(bundle);
                    break;
                default:
                    fragment = null;
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return this.fragmentTitle.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return this.fragmentTitle.get(position);
    }
}
