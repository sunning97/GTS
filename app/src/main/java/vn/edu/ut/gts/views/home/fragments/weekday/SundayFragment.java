package vn.edu.ut.gts.views.home.fragments.weekday;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
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
public class SundayFragment extends Fragment {

    @BindView(R.id.morning)
    LinearLayout morningLayout;
    @BindView(R.id.afternoon)
    LinearLayout afternoonLayout;
    @BindView(R.id.evening)
    LinearLayout eveningLayout;
    @BindView(R.id.day_tv)
    TextView dayTV;
    private JSONObject data;
    public SundayFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sunday, container, false);
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

    @SuppressLint("SetTextI18n")
    private void bindData(JSONObject jsonObject, LinearLayout layout) {
        try {
            dayTV.setText(data.getString("date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView subjectID = (TextView) layout.getChildAt(0);
        View line = layout.getChildAt(1);
        TextView subjectName = (TextView) layout.getChildAt(2);
        TextView subjectTime = (TextView) layout.getChildAt(3);
        TextView subjectLecturer = (TextView) layout.getChildAt(4);
        TextView subjectRoom = (TextView) layout.getChildAt(5);

        if (jsonObject.length() > 0) {
            try {
                if (Boolean.valueOf(jsonObject.getString("is_postpone")))
                    subjectID.setText(Html.fromHtml(jsonObject.getString("subjectId") + "<font color=\"#FF0000\">" + " (Tạm ngưng) " + "</font>"));
                else subjectID.setText(jsonObject.getString("subjectId"));
                line.setVisibility(View.VISIBLE);
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
