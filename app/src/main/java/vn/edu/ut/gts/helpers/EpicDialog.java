package vn.edu.ut.gts.helpers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.FadingCircle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Objects;
import java.util.zip.Inflater;

import vn.edu.ut.gts.R;

public class EpicDialog {
    private Dialog epicDialog;
    private Context context;

    public EpicDialog(Context context) {
        this.context = context;
        this.epicDialog = new Dialog(this.context);
    }

    public void showAboutDialog() {
        this.epicDialog.setContentView(R.layout.about_app_dialog_layout);
        TextView duong = this.epicDialog.findViewById(R.id.dev_duong);
        TextView giang = this.epicDialog.findViewById(R.id.dev_giang);
        TextView close = this.epicDialog.findViewById(R.id.close_dialog);
        duong.setPaintFlags(duong.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        giang.setPaintFlags(giang.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        duong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri webpage = Uri.parse("https://www.facebook.com/duongrom.it.305");
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    context.startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "Không tìm thấy ứng dụng phù hợp để mở", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        giang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri webpage = Uri.parse("https://www.facebook.com/kuro.neko.sora.ni.tobu");
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    context.startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "Không tìm thấy ứng dụng phù hợp để mở", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismisPopup();
            }
        });
        Objects.requireNonNull(this.epicDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.epicDialog.setCancelable(true);
        this.epicDialog.show();
    }

    public void dismisPopup() {
        if (this.epicDialog.isShowing()) {
            this.epicDialog.dismiss();
        }
    }

    @SuppressLint("SetTextI18n")
    public void showFrameProgramInfoDialog(String param1, String param2, JSONArray thongKe) {
        this.epicDialog.setContentView(R.layout.student_frame_program_info_dialog);
        TextView title1 = this.epicDialog.findViewById(R.id.title_1);
        TextView title2 = this.epicDialog.findViewById(R.id.title_2);
        TextView close = this.epicDialog.findViewById(R.id.close);
        TextView thongKe1 = this.epicDialog.findViewById(R.id.thong_ke_1);
        TextView thongKe2 = this.epicDialog.findViewById(R.id.thong_ke_2);
        TextView thongKe3 = this.epicDialog.findViewById(R.id.thong_ke_3);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismisPopup();
            }
        });
        title1.setText(param1);
        title2.setText(param2);
        try {
            thongKe1.setText(Html.fromHtml("<b>"+thongKe.getJSONObject(0).getString("title")+": "+thongKe.getJSONObject(0).getString("value")+"<b/>"));
            thongKe2.setText(Html.fromHtml("<b>"+thongKe.getJSONObject(1).getString("title")+": "+thongKe.getJSONObject(1).getString("value")+"<b/>"));
            thongKe3.setText(Html.fromHtml("<b>"+thongKe.getJSONObject(2).getString("title")+": "+thongKe.getJSONObject(2).getString("value")+"<b/>"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(this.epicDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.epicDialog.setCancelable(true);
        this.epicDialog.show();
    }

    public void showStudyResultInfoDialog(String averageCumulative, String totalCredit, String debtCredits) {
        this.epicDialog.setContentView(R.layout.student_study_result_info_dialog);
        TextView averageCumulativeValue = this.epicDialog.findViewById(R.id.average_cumulative);
        TextView totalCreditValue = this.epicDialog.findViewById(R.id.total_credit);
        TextView debtCreditsValue = this.epicDialog.findViewById(R.id.debt_credits);
        TextView close = this.epicDialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismisPopup();
            }
        });
        averageCumulativeValue.setText(averageCumulative);
        totalCreditValue.setText(totalCredit);
        debtCreditsValue.setText(debtCredits);

        Objects.requireNonNull(this.epicDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.epicDialog.setCancelable(true);
        this.epicDialog.show();
    }

    public void initLoadingDialog() {
        this.epicDialog.setContentView(R.layout.custom_loading_dialog);
        SpinKitView loading = epicDialog.findViewById(R.id.spin_kit);
        FadingCircle fadingCircle = new FadingCircle();
        loading.setIndeterminateDrawable(fadingCircle);
        Objects.requireNonNull(this.epicDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.epicDialog.setCancelable(false);
    }

    public void showLoadingDialog() {
        if (this.epicDialog.isShowing()) {
            this.epicDialog.dismiss();
        }
        this.epicDialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void showSearchStudentPortraitDialog(Bitmap bitmap, String studentId) {
        this.epicDialog.setContentView(R.layout.search_student_portrait_dialog);
        ImageView imageView = this.epicDialog.findViewById(R.id.student_portrait);
        TextView textView = this.epicDialog.findViewById(R.id.student_id);
        imageView.setImageBitmap(bitmap);
        textView.setText("MSSV: " + studentId);
        Objects.requireNonNull(this.epicDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.epicDialog.setCancelable(true);
        this.epicDialog.show();
    }

    public boolean isShowing() {
        return this.epicDialog.isShowing();
    }
}
