package vn.edu.ut.gts.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import vn.edu.ut.gts.R;

public class EpicDialog {
    public static int POSITIVE = 1;
    public static int NEGATIVE = 0;
    private Dialog epicDialog;
    private Context context;
    private ImageView topClosePopup;
    private TextView popupTitle,popupText;
    private Button popupClose;

    public EpicDialog(Context context){
        this.context = context;
        this.epicDialog = new Dialog(this.context);
    }

    public void showDialog(String title,String content,int type){
        if(type == EpicDialog.POSITIVE){
            this.epicDialog.setContentView(R.layout.custom_popup_negative);
        } else {
            this.epicDialog.setContentView(R.layout.custom_popup_positive);
        }
        this.popupInit();

        popupTitle.setText(title);
        popupText.setText(content);

        popupClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epicDialog.dismiss();
            }
        });
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
