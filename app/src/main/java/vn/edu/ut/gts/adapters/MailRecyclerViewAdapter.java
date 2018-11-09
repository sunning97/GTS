package vn.edu.ut.gts.adapters;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import vn.edu.ut.gts.R;
import vn.edu.ut.gts.actions.helpers.Helper;
import vn.edu.ut.gts.views.mail.fragments.OnBottomReachedListener;
import vn.edu.ut.gts.views.mail.fragments.OnItemClickListener;

public class MailRecyclerViewAdapter extends RecyclerView.Adapter<MailRecyclerViewAdapter.RecyclerViewHolder> {

    private JSONArray data;
    private Random random = new Random();
    private OnItemClickListener mOnItemClickListener;
    private OnBottomReachedListener onBottomReachedListener;
    private final String charBlue = "abc";
    private final String charBlueDark = "def";
    private final String charGray = "ghi";
    private final String charOrange = "jkl";
    private final String charGreen = "mno";
    private final String charPink = "pqrs";
    private final String charViolet = "tuvwxyz";

    public MailRecyclerViewAdapter(JSONArray data, OnItemClickListener mOnItemClickListener, OnBottomReachedListener onBottomReachedListener) {
        this.data = data;
        this.mOnItemClickListener = mOnItemClickListener;
        this.onBottomReachedListener = onBottomReachedListener;

    }

    public void setData(JSONArray data) {
        this.data = data;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.mail_item_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        try {
            JSONObject jsonObject = data.getJSONObject(position);
            if (!jsonObject.getBoolean("readed")) {
                Log.d("AAAA", "asdasddasdas");
                holder.txtSendTitle.setTypeface(holder.txtSendTitle.getTypeface(), Typeface.BOLD);
                holder.txtSender.setTypeface(holder.txtSender.getTypeface(), Typeface.BOLD);
                holder.txtSendDay.setTypeface(holder.txtSendDay.getTypeface(), Typeface.BOLD);
            }
            holder.txtSendTitle.setText(jsonObject.getString("tieu_de"));
            holder.txtSender.setText(jsonObject.getString("nguoi_gui"));
            holder.txtSendDay.setText(jsonObject.getString("ngay_gui"));
            String firstChar = Helper.toSlug(String.valueOf(jsonObject.getString("nguoi_gui").trim().charAt(0)).toUpperCase());

            if (charBlue.contains(firstChar))
                holder.txtMailCircle.setBackgroundDrawable(holder.itemView.getResources().getDrawable(R.drawable.circle_background_blue));
            if (charBlueDark.contains(firstChar))
                holder.txtMailCircle.setBackgroundDrawable(holder.itemView.getResources().getDrawable(R.drawable.circle_background_blue_dark));
            if (charGreen.contains(firstChar))
                holder.txtMailCircle.setBackgroundDrawable(holder.itemView.getResources().getDrawable(R.drawable.circle_background_green));
            if (charOrange.contains(firstChar))
                holder.txtMailCircle.setBackgroundDrawable(holder.itemView.getResources().getDrawable(R.drawable.circle_background_orange));
            if (charPink.contains(firstChar))
                holder.txtMailCircle.setBackgroundDrawable(holder.itemView.getResources().getDrawable(R.drawable.circle_background_pink));
            if (charViolet.contains(firstChar))
                holder.txtMailCircle.setBackgroundDrawable(holder.itemView.getResources().getDrawable(R.drawable.circle_background_violet));
            if (charGray.contains(firstChar))
                holder.txtMailCircle.setBackgroundDrawable(holder.itemView.getResources().getDrawable(R.drawable.circle_background_gray));
            holder.txtMailCircle.setText(String.valueOf(jsonObject.getString("nguoi_gui").trim().charAt(0)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                try {
                    data.getJSONObject(position).put("readed","true");
                    mOnItemClickListener.onItemClick(v, position, data.getJSONObject(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if (position == data.length() - 1)

        {
            onBottomReachedListener.onBottomReached(position);
        }
    }

    @Override
    public int getItemCount() {
        return data.length();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView txtSendTitle;
        TextView txtSender;
        TextView txtSendDay;
        TextView txtMailCircle;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            txtSendTitle = itemView.findViewById(R.id.send_title);
            txtSender = itemView.findViewById(R.id.sender);
            txtSendDay = itemView.findViewById(R.id.send_day);
            txtMailCircle = itemView.findViewById(R.id.mail_circle);
        }
    }
}
