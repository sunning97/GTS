package vn.edu.ut.gts.views.home.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.edu.ut.gts.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentStudyResultFragment extends Fragment {


    public StudentStudyResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_study_result, container, false);
    }

}
