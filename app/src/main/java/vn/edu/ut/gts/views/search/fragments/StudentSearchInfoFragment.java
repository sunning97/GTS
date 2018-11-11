package vn.edu.ut.gts.views.search.fragments;


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
public class StudentSearchInfoFragment extends Fragment {
    @BindView(R.id.trang_thai)
    TextView trangThai;
    @BindView(R.id.gioi_tinh)
    TextView gioiTinh;
    @BindView(R.id.ngay_vao_truong)
    TextView ngayVaoTruong;
    @BindView(R.id.ma_ho_so)
    TextView maHoSo;
    @BindView(R.id.khoa_hoc)
    TextView khoaHoc;
    @BindView(R.id.co_so)
    TextView coSo;
    @BindView(R.id.bac_dao_tao)
    TextView bacDaoTao;
    @BindView(R.id.loai_hinh_dao_tao)
    TextView loaiHinhDaoTao;
    @BindView(R.id.nganh)
    TextView nganh;
    @BindView(R.id.chuyen_nganh)
    TextView chuyenNganh;
    @BindView(R.id.khoa)
    TextView khoa;
    @BindView(R.id.lop)
    TextView lop;
    @BindView(R.id.chuc_vu)
    TextView chucVu;
    @BindView(R.id.cong_tac_doan)
    TextView congTacDoan;
    private List<TextView> listProp = new ArrayList<>();

    public StudentSearchInfoFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_search_info, container, false);
        ButterKnife.bind(this, view);

        this.init();

        Bundle bundle = getArguments();
        try {
            if (bundle != null) {
                JSONArray data = new JSONArray(bundle.getString("data"));
                generateStudentInfo(data);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void init(){
        listProp.add(trangThai);
        listProp.add(gioiTinh);
        listProp.add(ngayVaoTruong);
        listProp.add(maHoSo);
        listProp.add(khoaHoc);
        listProp.add(coSo);
        listProp.add(bacDaoTao);
        listProp.add(loaiHinhDaoTao);
        listProp.add(nganh);
        listProp.add(chuyenNganh);
        listProp.add(khoa);
        listProp.add(lop);
        listProp.add(chucVu);
        listProp.add(congTacDoan);
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
}
