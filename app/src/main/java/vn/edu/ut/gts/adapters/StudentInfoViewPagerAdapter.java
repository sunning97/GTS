package vn.edu.ut.gts.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class StudentInfoViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragment;
    private List<String> fragmentTitle;

    public StudentInfoViewPagerAdapter(FragmentManager fm,List<Fragment> fragment,List<String> fragmentTitle) {
        super(fm);
        this.fragment = fragment;
        this.fragmentTitle = fragmentTitle;
    }

    @Override
    public Fragment getItem(int i) {
        return this.fragment.get(i);
    }

    @Override
    public int getCount() {
        return this.fragment.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return this.fragmentTitle.get(position);
    }
}
