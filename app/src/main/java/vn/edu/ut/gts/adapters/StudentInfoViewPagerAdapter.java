package vn.edu.ut.gts.adapters;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.edu.ut.gts.views.home.fragments.student_info.StudentFamilyInfoFragment;
import vn.edu.ut.gts.views.home.fragments.student_info.StudentInfoFragment;
import vn.edu.ut.gts.views.home.fragments.student_info.StudentPersonalInfoFragment;
import vn.edu.ut.gts.views.home.fragments.weekday.TuesdayFragment;

public class StudentInfoViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> fragmentTitle;
    private JSONObject data;

    public StudentInfoViewPagerAdapter(FragmentManager fm, JSONObject data) {
        super(fm);
        this.data = data;
        this.fragmentTitle = new ArrayList<>();
        this.fragmentTitle.add("Thông tin sinh viên");
        this.fragmentTitle.add("Thông tin cá nhân");
        this.fragmentTitle.add("Quan hệ gia đình");

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
                    fragment = new StudentInfoFragment();
                    bundle = new Bundle();
                    bundle.putString("data", data.getJSONArray("studentInfo").toString());
                    fragment.setArguments(bundle);
                    break;
                case 1:
                    fragment = new StudentPersonalInfoFragment();
                    bundle = new Bundle();
                    bundle.putString("data", data.getJSONArray("studentDetail").toString());
                    fragment.setArguments(bundle);
                    break;
                case 2:
                    fragment = new StudentFamilyInfoFragment();
                    bundle = new Bundle();
                    bundle.putString("data", data.getJSONArray("studentFamily").toString());
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
