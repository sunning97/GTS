package vn.edu.ut.gts.views.home.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import vn.edu.ut.gts.R;
import vn.edu.ut.gts.adapters.StudentInfoViewPagerAdapter;
import vn.edu.ut.gts.views.home.fragments.student_info.StudentFamilyInfoFragment;
import vn.edu.ut.gts.views.home.fragments.student_info.StudentInfoFragment;
import vn.edu.ut.gts.views.home.fragments.student_info.StudentPersonalInfoFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentInfoRootFragment extends Fragment {

    private List<Fragment> fragments;
    private List<String> fragmentTitle;
    private StudentInfoViewPagerAdapter studentInfoViewPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public StudentInfoRootFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_info_root,container,false);
        this.init(view);
        return view;
    }
    private void init(View view){
        tabLayout = view.findViewById(R.id.student_info_tablayout);
        viewPager = view.findViewById(R.id.student_info_view_pager);

        this.fragments = new ArrayList<>();
        fragments.add(new StudentInfoFragment());
        fragments.add(new StudentPersonalInfoFragment());
        //fragments.add(new StudentFamilyInfoFragment());

        this.fragmentTitle = new ArrayList<>();
        fragmentTitle.add("Thông tin sinh viên");
        fragmentTitle.add("Thông tin cá nhân");
        //fragmentTitle.add("Quan hệ gia đình");
        this.studentInfoViewPagerAdapter = new StudentInfoViewPagerAdapter(
                getChildFragmentManager(),
                fragments,
                fragmentTitle
        );
        viewPager.setAdapter(studentInfoViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
