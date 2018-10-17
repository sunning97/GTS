package vn.edu.ut.gts.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import vn.edu.ut.gts.views.home.fragments.weekday.FridayFragment;
import vn.edu.ut.gts.views.home.fragments.weekday.MondayFragment;
import vn.edu.ut.gts.views.home.fragments.weekday.SaturdayFragment;
import vn.edu.ut.gts.views.home.fragments.weekday.SundayFragment;
import vn.edu.ut.gts.views.home.fragments.weekday.ThursdayFragment;
import vn.edu.ut.gts.views.home.fragments.weekday.TuesdayFragment;
import vn.edu.ut.gts.views.home.fragments.weekday.WednesdayFragment;

public class WeekScheduleTablayoutAdapter extends FragmentPagerAdapter {

    private List<String> fragmentTitle;
    private JSONArray data;

    public WeekScheduleTablayoutAdapter(FragmentManager fm, JSONArray data) {
        super(fm);
        this.data = data;
        this.fragmentTitle = new ArrayList<>();
        this.fragmentTitle.add("T2");
        this.fragmentTitle.add("T3");
        this.fragmentTitle.add("T4");
        this.fragmentTitle.add("T5");
        this.fragmentTitle.add("T6");
        this.fragmentTitle.add("T7");
        this.fragmentTitle.add("CN");
    }

    public void setData(JSONArray data) {
        this.data = data;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        Bundle bundle = null;
        try {
            switch (i) {
                case 0:
                    fragment = new MondayFragment();
                    bundle = new Bundle();
                    bundle.putString("data", data.getJSONObject(0).toString());
                    fragment.setArguments(bundle);
                    break;
                case 1:
                    fragment = new TuesdayFragment();
                    bundle = new Bundle();
                    bundle.putString("data", data.getJSONObject(1).toString());
                    fragment.setArguments(bundle);
                    break;
                case 2:
                    fragment = new WednesdayFragment();
                    bundle = new Bundle();
                    bundle.putString("data", data.getJSONObject(2).toString());
                    fragment.setArguments(bundle);
                    break;
                case 3:
                    fragment = new ThursdayFragment();
                    bundle = new Bundle();
                    bundle.putString("data", data.getJSONObject(3).toString());
                    fragment.setArguments(bundle);
                    break;
                case 4:
                    fragment = new FridayFragment();
                    bundle = new Bundle();
                    bundle.putString("data", data.getJSONObject(4).toString());
                    fragment.setArguments(bundle);
                    break;
                case 5:
                    fragment = new SaturdayFragment();
                    bundle = new Bundle();
                    bundle.putString("data", data.getJSONObject(5).toString());
                    fragment.setArguments(bundle);
                    break;
                case 6:
                    fragment = new SundayFragment();
                    bundle = new Bundle();
                    bundle.putString("data", data.getJSONObject(6).toString());
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
