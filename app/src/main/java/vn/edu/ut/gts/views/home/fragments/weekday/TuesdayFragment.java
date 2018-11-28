package vn.edu.ut.gts.views.home.fragments.weekday;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
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
    @BindView(R.id.day_tv)
    TextView dayTV;
    private JSONObject data;

    public TuesdayFragment() {

    }
    public void setData(JSONObject data){
        this.data = data;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_tuesday, container, false);
        ButterKnife.bind(this,view);
        Bundle bundle = getArguments();
        try {
            if (bundle != null) {
                data = new JSONObject(bundle.getString("data"));
                go();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  view;
    }

    public void go(){
        try {
            JSONArray morning = data.getJSONArray("morning");
            JSONArray afternoon = data.getJSONArray("afternoon");
            JSONArray evening = data.getJSONArray("evening");

            bindData(morning,morningLayout);
            bindData(afternoon,afternoonLayout);
            bindData(evening,eveningLayout);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void bindData(JSONArray jsonArray,LinearLayout linearLayout) {
        try {
            dayTV.setText("Thứ 3: "+data.getString("date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0;i< jsonArray.length();i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                View view = LayoutInflater.from(getContext()).inflate(R.layout.week_schedule_item_layout, null);
                LinearLayout container = view.findViewById(R.id.container);

                TextView subjectID = view.findViewById(R.id.subject_id);
                TextView subjectName = view.findViewById(R.id.subject_name);
                if (Boolean.valueOf(jsonObject.getString("is_postpone")))
                    subjectID.setText(Html.fromHtml(jsonObject.getString("subject_id") + "<font color=\"#FF0000\">" + " (Tạm ngưng) " + "</font>"));
                else subjectID.setText(jsonObject.getString("subject_id"));
                subjectName.setText(jsonObject.getString("subject_name"));
                JSONArray values = jsonObject.getJSONArray("values");

                for (int j = 0;j< values.length();j++){
                    JSONObject jsonObject1 = values.getJSONObject(j);
                    TextView textView = new TextView(getContext());
                    textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                    textView.setText(jsonObject1.getString("key")+" "+jsonObject1.getString("value"));
                    container.addView(textView);
                }
                linearLayout.addView(view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
