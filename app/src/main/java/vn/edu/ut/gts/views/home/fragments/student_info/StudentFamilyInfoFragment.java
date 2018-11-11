package vn.edu.ut.gts.views.home.fragments.student_info;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.helpers.Storage;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentFamilyInfoFragment extends Fragment {

    @BindView(R.id.ho_va_ten_1)
    TextView hoVaTen1;
    @BindView(R.id.ho_va_ten_2)
    TextView hoVaTen2;
    @BindView(R.id.quan_he_1)
    TextView quanHe1;
    @BindView(R.id.quan_he_2)
    TextView quanHe2;
    @BindView(R.id.quoc_tich_1)
    TextView quocTich1;
    @BindView(R.id.quoc_tich_2)
    TextView quocTich2;
    @BindView(R.id.nghe_nghiep_1)
    TextView ngheNghiep1;
    @BindView(R.id.nghe_nghiep_2)
    TextView ngheNghiep2;
    @BindView(R.id.dien_thoai_1)
    TextView dienThoai1;
    @BindView(R.id.dien_thoai_2)
    TextView dienThoai2;
    @BindView(R.id.nam_sinh_1)
    TextView namSinh1;
    @BindView(R.id.nam_sinh_2)
    TextView namSinh2;

    private List<TextView> listProp = new ArrayList<>();

    public StudentFamilyInfoFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_family_info, container, false);
        ButterKnife.bind(this,view);
        init();
        Bundle bundle = getArguments();
        try {
            JSONArray data = null;
            if (bundle != null) {
                data = new JSONArray(bundle.getString("data"));
                generateStudentFamilyInfo(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  view;
    }

    private void generateStudentFamilyInfo(JSONArray jsonArray) {
        for(int i = 0; i< jsonArray.length();i++){
            try {
                JSONObject prop = jsonArray.getJSONObject(i);
                listProp.get(i).setText(prop.getString("value"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void init(){
        listProp.add(hoVaTen1);
        listProp.add(quanHe1);
        listProp.add(namSinh1);
        listProp.add(quocTich1);
        listProp.add(ngheNghiep1);
        listProp.add(dienThoai1);
        listProp.add(hoVaTen2);
        listProp.add(quanHe2);
        listProp.add(namSinh2);
        listProp.add(quocTich2);
        listProp.add(ngheNghiep2);
        listProp.add(dienThoai2);
    }
}
