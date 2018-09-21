package vn.edu.ut.gts.views.home.fragments.student_info;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.edu.ut.gts.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentInfoFragment extends Fragment {


    public StudentInfoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_info, container, false);
        return view;
    }

}
