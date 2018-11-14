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

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentPersonalInfoFragment extends Fragment {

    @BindView(R.id.ngay_sinh) TextView ngaySinh;
    @BindView(R.id.noi_sinh) TextView noiSinh;
    @BindView(R.id.dan_toc) TextView danToc;
    @BindView(R.id.ton_giao) TextView tonGiao;
    @BindView(R.id.khu_vuc) TextView khuVuc;
    @BindView(R.id.cmnd) TextView cmnd;
    @BindView(R.id.doi_tuong) TextView doiTuong;
    @BindView(R.id.ngay_cap) TextView ngayCap;
    @BindView(R.id.dien_chinh_sach) TextView dienChinhSach;
    @BindView(R.id.noi_cap) TextView noiCap;
    @BindView(R.id.ngay_vao_doan) TextView NgayVaoDoan;
    @BindView(R.id.ngay_vao_dang) TextView NgayVaoDang;
    @BindView(R.id.dien_thoai_di_dong) TextView dienThoaiDiDong;
    @BindView(R.id.email) TextView email;
    @BindView(R.id.ho_khau) TextView hoKhau;
    @BindView(R.id.dia_chi_lien_he) TextView diaChiLienHe;

    private List<TextView> listProp;
    public StudentPersonalInfoFragment() {
        listProp = new ArrayList<>();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_personal_info, container, false);
        ButterKnife.bind(this,view);
        init();

        Bundle bundle = getArguments();
        try {
            JSONArray data = null;
            if (bundle != null) {
                data = new JSONArray(bundle.getString("data"));
                generateStudentInfo(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  view;
    }


    private void generateStudentInfo(JSONArray data){
        for(int i = 0; i< data.length();i++){
            try {
                JSONObject prop = data.getJSONObject(i);
                listProp.get(i).setText(prop.getString("value"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void init(){
        listProp.add(ngaySinh);
        listProp.add(noiSinh);
        listProp.add(danToc);
        listProp.add(tonGiao);
        listProp.add(khuVuc);
        listProp.add(cmnd);
        listProp.add(doiTuong);
        listProp.add(ngayCap);
        listProp.add(dienChinhSach);
        listProp.add(noiCap);
        listProp.add(NgayVaoDoan);
        listProp.add(NgayVaoDang);
        listProp.add(dienThoaiDiDong);
        listProp.add(email);
        listProp.add(hoKhau);
        listProp.add(diaChiLienHe);
    }
}
