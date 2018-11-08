package vn.edu.ut.gts.views.mail.fragments;

import android.view.View;

import org.json.JSONObject;

public interface OnItemClickListener {
    void onItemClick(View view, int position,JSONObject data);
}
