package vn.edu.ut.gts.views.home.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.Student;

/**
 * A simple {@link Fragment} subclass.
 */
public class FrameProgramFragment extends Fragment {

    private Student student;

    public FrameProgramFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        student = new Student(getContext());
        View view = inflater.inflate(R.layout.fragment_frame_program, container, false);


        AsyncTask<Void,Void,Void> voidVoidVoidAsyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                student.Test();
                return null;
            }
        };
        voidVoidVoidAsyncTask.execute();
        return  view;
    }

}
