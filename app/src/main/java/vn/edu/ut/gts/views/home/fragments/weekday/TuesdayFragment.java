package vn.edu.ut.gts.views.home.fragments.weekday;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.edu.ut.gts.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TuesdayFragment extends Fragment {

    @BindView(R.id.morning)
    LinearLayout morningLayout;
    @BindView(R.id.afternoon)
    LinearLayout afternoonLayout;
    @BindView(R.id.evening)
    LinearLayout eveningLayout;

    private JSONObject data;

    public TuesdayFragment() {

    }
    public void setData(JSONObject data){
        this.data = data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_tuesday, container, false);
        ButterKnife.bind(this,view);
        Bundle bundle = getArguments();
        try {
            data = new JSONObject(bundle.getString("data"));
            go();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  view;
    }

    public void go(){
        try {
            JSONObject morning = data.getJSONObject("morning");
            JSONObject afternoon = data.getJSONObject("afternoon");
            JSONObject evening = data.getJSONObject("evening");

            bindData(morning,morningLayout);
            bindData(afternoon,afternoonLayout);
            bindData(evening,eveningLayout);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void bindData(JSONObject jsonObject, LinearLayout layout) {
        TextView subjectID = (TextView) layout.getChildAt(0);
        View line = layout.getChildAt(1);
        TextView subjectName = (TextView) layout.getChildAt(2);
        TextView subjectTime = (TextView) layout.getChildAt(3);
        TextView subjectLecturer = (TextView) layout.getChildAt(4);
        TextView subjectRoom = (TextView) layout.getChildAt(5);

        if (jsonObject.length() > 0) {
            try {
                line.setVisibility(View.VISIBLE);
                subjectID.setText(jsonObject.getString("subjectId"));
                subjectName.setText(jsonObject.getString("subjectName"));
                subjectTime.setText("Tiết: " + jsonObject.getString("subjectTime"));
                subjectLecturer.setText("GV: " + jsonObject.getString("subjectLecturer"));
                subjectRoom.setText("Phòng: " + jsonObject.getString("subjectRoom"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            line.setVisibility(View.INVISIBLE);
            subjectID.setText("");
            subjectName.setText("");
            subjectTime.setText("");
            subjectLecturer.setText("");
            subjectRoom.setText("");
        }
    }
}
