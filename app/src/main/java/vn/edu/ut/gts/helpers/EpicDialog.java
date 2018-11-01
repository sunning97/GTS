package vn.edu.ut.gts.helpers;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import vn.edu.ut.gts.R;

public class EpicDialog {
    public static int POSITIVE = 1;
    public static int NEGATIVE = 0;
    public static int ABOUT_APP = 3;
    private Dialog epicDialog;
    private Context context;

    public EpicDialog(Context context){
        this.context = context;
        this.epicDialog = new Dialog(this.context);
    }

    public void showAboutDialog(){
        this.epicDialog.setContentView(R.layout.about_app_dialog_layout);
        TextView duong = this.epicDialog.findViewById(R.id.dev_duong);
        TextView giang = this.epicDialog.findViewById(R.id.dev_giang);
        TextView close = this.epicDialog.findViewById(R.id.close_dialog);
        duong.setPaintFlags(duong.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        giang.setPaintFlags(giang.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        duong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PackageManager pm = context.getPackageManager();
//                Uri uri = null;
//
//                try {
//                    ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
//                    if (applicationInfo.enabled) {
//                        uri = Uri.parse("fb://facewebmodal/f?href=" + "https://www.facebook.com/duongrom.it.305");
//                    }
//                }
//
//                catch (PackageManager.NameNotFoundException ignored) {
//                    Toast.makeText(context, "Không tìm thấy ứng dụng phù hợp để mở", Toast.LENGTH_SHORT).show();
//                }
//
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                context.startActivity(intent);

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
//                PackageManager pm = context.getPackageManager();
//                Uri uri = null;
//
//                try {
//                    ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
//                    if (applicationInfo.enabled) {
//                        uri = Uri.parse("fb://facewebmodal/f?href=" + "https://www.facebook.com/kuro.neko.sora.ni.tobu");
//                    }
//                }
//
//                catch (PackageManager.NameNotFoundException ignored) {
//                    Toast.makeText(context, "Không tìm thấy ứng dụng phù hợp để mở", Toast.LENGTH_SHORT).show();
//                }
//
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                context.startActivity(intent);
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
        this.epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.epicDialog.setCancelable(true);
        this.epicDialog.show();
    }
    public void dismisPopup(){
        if(this.epicDialog.isShowing()){
            this.epicDialog.dismiss();
        }
    }
    public void showFrameProgramInfoDialog(String param1,String param2){
        this.epicDialog.setContentView(R.layout.student_frame_program_info_dialog);
        TextView title1 = this.epicDialog.findViewById(R.id.title_1);
        TextView title2 = this.epicDialog.findViewById(R.id.title_2);
        TextView close = this.epicDialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismisPopup();
            }
        });
        title1.setText(param1);
        title2.setText(param2);
        this.epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.epicDialog.setCancelable(true);
        this.epicDialog.show();
    }
    public void showStudyResultInfoDialog(String averageCumulative,String totalCredit,String debtCredits){
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

        this.epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.epicDialog.setCancelable(true);
        this.epicDialog.show();
    }
    public void initLoadingDialog(){
        this.epicDialog.setContentView(R.layout.custom_loading_dialog);
        this.epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.epicDialog.setCancelable(false);
    }
    public void showLoadingDialog(){
        if(this.epicDialog.isShowing()){
            this.epicDialog.dismiss();
        }
        this.epicDialog.show();
    }
}
