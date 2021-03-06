package vn.edu.ut.gts.views.home.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.helpers.Storage;
import vn.edu.ut.gts.adapters.StudentInfoViewPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentInfoRootFragment extends Fragment {

    @BindView(R.id.student_info_tablayout)
    TabLayout tabLayout;
    @BindView(R.id.student_info_view_pager)
    ViewPager viewPager;
    private Storage storage;
    private StudentInfoViewPagerAdapter studentInfoViewPagerAdapter;

    public StudentInfoRootFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_info_root,container,false);
        ButterKnife.bind(this,view);
        init();
        return view;
    }
    private void init(){
        storage = new Storage(Objects.requireNonNull(getContext()));
        try {
            JSONObject data = new JSONObject(storage.getString("student_info"));
            this.studentInfoViewPagerAdapter = new StudentInfoViewPagerAdapter(getChildFragmentManager(),data);
            viewPager.setAdapter(studentInfoViewPagerAdapter);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            tabLayout.setupWithViewPager(viewPager);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
