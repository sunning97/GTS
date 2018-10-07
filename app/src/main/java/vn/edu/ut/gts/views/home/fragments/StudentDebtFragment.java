package vn.edu.ut.gts.views.home.fragments;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaredrummler.materialspinner.MaterialSpinner;

import vn.edu.ut.gts.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentDebtFragment extends Fragment {


    public StudentDebtFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_debt, container, false);
        MaterialSpinner spinner = (MaterialSpinner) view.findViewById(R.id.spinner);
        String[] aa = {"Học kỳ 1 năm học 2018-2019","Học kỳ hè năm học 2017-2018","Học kỳ 2 năm học 2017-2018","Học kỳ 1 năm học 2017-2018","Học kỳ hè năm học 2016-2017","Học kỳ 2 năm học 2016-2017","Học kỳ 1 năm học 2016-2017","Học kỳ hè năm học 2015-2016","Học kỳ 2 năm học 2015-2016","Học kỳ 1 năm học 2015-2016"};
        spinner.setItems(aa);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }

}
