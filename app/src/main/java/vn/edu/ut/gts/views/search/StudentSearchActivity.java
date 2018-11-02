package vn.edu.ut.gts.views.search;

import android.animation.Animator;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.jaredrummler.materialspinner.MaterialSpinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.edu.ut.gts.R;

public class StudentSearchActivity extends AppCompatActivity {
    @BindView(R.id.type_search_spinner)
    MaterialSpinner typeSearchSpinner;
    @BindView(R.id.layout_search_by_name)
    LinearLayout layoutSearchByName;
    @BindView(R.id.layout_search_by_id)
    LinearLayout layoutSearchByID;
    @BindView(R.id.layout_search_by_birth_date)
    LinearLayout layoutSearchByBirthDate;
    @BindView(R.id.input_search_layout)
    LinearLayout inputSearchLayout;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.result_layout)
    LinearLayout resultLayout;
    @BindView(R.id.btn_return_search_layout)
    Button btnReturnSearchLayout;

    private String[] spinnerData = {"Mã số sinh viên","Họ tên","Ngày sinh"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_search);
        ButterKnife.bind(this);
        typeSearchSpinner.setItems(spinnerData);
        typeSearchSpinner.setSelectedIndex(0);
        typeSearchSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                typeSearchSpinner.animate();
                switch (position){
                    case 0:{
                        layoutSearchByID.setVisibility(View.VISIBLE);
                        layoutSearchByName.setVisibility(View.GONE);
                        layoutSearchByBirthDate.setVisibility(View.GONE);
                        break;
                    }
                    case 1:{
                        layoutSearchByName.setVisibility(View.VISIBLE);
                        layoutSearchByID.setVisibility(View.GONE);
                        layoutSearchByBirthDate.setVisibility(View.GONE);
                        break;
                    }
                    case 2:{
                        layoutSearchByBirthDate.setVisibility(View.VISIBLE);
                        layoutSearchByID.setVisibility(View.GONE);
                        layoutSearchByName.setVisibility(View.GONE);
                        break;
                    }
                }
            }
        });
    }
    @OnClick(R.id.btn_search)
    public void search(View view){
        YoYo.with(Techniques.SlideOutLeft)
            .duration(250)
            .repeat(0)
            .onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    inputSearchLayout.setVisibility(View.GONE);
                    YoYo.with(Techniques.SlideInRight)
                        .duration(250)
                        .repeat(0)
                        .onStart(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                loadingLayout.setVisibility(View.VISIBLE);
                            }
                        })
                        .playOn(findViewById(R.id.loading_layout));
                }
            })
            .playOn(findViewById(R.id.input_search_layout));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.SlideOutLeft)
                    .duration(250)
                    .repeat(0)
                    .onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            loadingLayout.setVisibility(View.GONE);
                            YoYo.with(Techniques.SlideInRight)
                                    .duration(250)
                                    .repeat(0)
                                    .onStart(new YoYo.AnimatorCallback() {
                                        @Override
                                        public void call(Animator animator) {
                                            resultLayout.setVisibility(View.VISIBLE);
                                        }
                                    })
                                    .playOn(findViewById(R.id.result_layout));
                        }
                    })
                    .playOn(findViewById(R.id.loading_layout));
            }
        },3000);
    }

    @OnClick(R.id.btn_return_search_layout)
    public void returnSearchLayout(){
        YoYo.with(Techniques.SlideOutLeft)
                .duration(250)
                .repeat(0)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        resultLayout.setVisibility(View.GONE);
                        YoYo.with(Techniques.SlideInRight)
                                .duration(250)
                                .repeat(0)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        inputSearchLayout.setVisibility(View.VISIBLE);
                                    }
                                })
                                .playOn(findViewById(R.id.input_search_layout));
                    }
                })
                .playOn(findViewById(R.id.result_layout));

    }
}
