package vn.edu.ut.gts.views.home.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.Student;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentStudyResultFragment extends Fragment {

    @BindView(R.id.image)
    ImageView imageView;

    public StudentStudyResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_student_study_result, container, false);
        ButterKnife.bind(this,view);

        final Student student = new Student(getContext());

//        AsyncTask<Void,Void,Void> aa = new AsyncTask<Void, Void, Bitmap>() {
//            @Override
//            protected Bitmap doInBackground(Void... voids) {
//                student.getStudentImage();
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Bitmap bitmap) {
//
//            }
//        };
//        aa.execute();

        return  view;
    }

}
