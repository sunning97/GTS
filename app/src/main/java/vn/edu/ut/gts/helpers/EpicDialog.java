package vn.edu.ut.gts.helpers;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import vn.edu.ut.gts.R;

public class EpicDialog {
    public static int POSITIVE = 1;
    public static int NEGATIVE = 0;
    public static int ABOUT_APP = 3;
    private Dialog epicDialog;
    private Context context;
    private ImageView topClosePopup;
    private TextView popupTitle,popupText;
    private Button popupClose;

    public EpicDialog(Context context){
        this.context = context;
        this.epicDialog = new Dialog(this.context);
    }

    public void showPopup(String title,String content,int type){
        switch (type){
            case 0:
                this.epicDialog.setContentView(R.layout.custom_popup_negative);
                break;
            case 1:
                this.epicDialog.setContentView(R.layout.custom_popup_positive);
                break;
            case 3:
                this.epicDialog.setContentView(R.layout.about_app_dialog_layout);
                break;
        }
        this.popupInit();

        popupClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epicDialog.dismiss();
            }
        });
        this.epicDialog.show();
    }

    public void showAboutDialog(){
        this.epicDialog.setContentView(R.layout.about_app_dialog_layout);
        TextView duong = this.epicDialog.findViewById(R.id.dev_duong);
        TextView giang = this.epicDialog.findViewById(R.id.dev_giang);
        duong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = context.getPackageManager();
                Uri uri = Uri.parse("https://www.facebook.com/duongrom.it.305");

                try {
                    ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
                    if (applicationInfo.enabled) {
                        uri = Uri.parse("fb://facewebmodal/f?href=" + "https://www.facebook.com/duongrom.it.305");
                    }
                }

                catch (PackageManager.NameNotFoundException ignored) {
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
        giang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = context.getPackageManager();
                Uri uri = Uri.parse("https://www.facebook.com/kuro.neko.sora.ni.tobu");

                try {
                    ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
                    if (applicationInfo.enabled) {
                        uri = Uri.parse("fb://facewebmodal/f?href=" + "https://www.facebook.com/kuro.neko.sora.ni.tobu");
                    }
                }

                catch (PackageManager.NameNotFoundException ignored) {
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
        this.epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.epicDialog.setCancelable(true);
        this.epicDialog.show();
    }
    public void dismisPopup(){
        this.epicDialog.dismiss();
    }

    public void showFrameProgramInfoDialog(String param1,String param2){
        this.epicDialog.setContentView(R.layout.student_frame_program_info_dialog);
        TextView title1 = this.epicDialog.findViewById(R.id.title_1);
        TextView title2 = this.epicDialog.findViewById(R.id.title_2);
        title1.setText(param1);
        title2.setText(param2);
        this.epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.epicDialog.setCancelable(true);
        this.epicDialog.show();
    }
    private void popupInit(){
        popupClose = this.epicDialog.findViewById(R.id.close_popup_bottom);
        popupTitle = this.epicDialog.findViewById(R.id.popup_title);
        popupText = this.epicDialog.findViewById(R.id.popup_text);
        this.epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.epicDialog.setCancelable(false);
    }
}
