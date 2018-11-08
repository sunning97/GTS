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
import vn.edu.ut.gts.views.mail.fragments.OnItemClickListener;

public class MailRecyclerViewAdapter extends RecyclerView.Adapter<MailRecyclerViewAdapter.RecyclerViewHolder> {

    private JSONArray data;
    private Random random = new Random();
    private OnItemClickListener mOnItemClickListener;

    public MailRecyclerViewAdapter(JSONArray data, OnItemClickListener mOnItemClickListener) {
        this.data = data;
        this.mOnItemClickListener = mOnItemClickListener;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.mail_item_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        int bgOrder = random.nextInt(6);
        try {
            JSONObject jsonObject = data.getJSONObject(position);
            if(!jsonObject.getBoolean("readed")){
                holder.txtSendTitle.setTypeface(holder.txtSendTitle.getTypeface(), Typeface.BOLD);
                holder.txtSender.setTypeface(holder.txtSender.getTypeface(), Typeface.BOLD);
                holder.txtSendDay.setTypeface(holder.txtSendDay.getTypeface(), Typeface.BOLD);
            }
            holder.txtSendTitle.setText(jsonObject.getString("tieu_de"));
            holder.txtSender.setText(jsonObject.getString("nguoi_gui"));
            holder.txtSendDay.setText(jsonObject.getString("ngay_gui"));
            switch (bgOrder){
                case 0:
                    holder.txtMailCircle.setBackgroundDrawable( holder.itemView.getResources().getDrawable(R.drawable.circle_background_blue) );
                    break;
                case 1:
                    holder.txtMailCircle.setBackgroundDrawable( holder.itemView.getResources().getDrawable(R.drawable.circle_background_blue_dark) );
                    break;
                case 2:
                    holder.txtMailCircle.setBackgroundDrawable( holder.itemView.getResources().getDrawable(R.drawable.circle_background_gray) );
                    break;
                case 3:
                    holder.txtMailCircle.setBackgroundDrawable( holder.itemView.getResources().getDrawable(R.drawable.circle_background_green) );
                    break;
                case 4:
                    holder.txtMailCircle.setBackgroundDrawable( holder.itemView.getResources().getDrawable(R.drawable.circle_background_orange) );
                    break;
                case 5:
                    holder.txtMailCircle.setBackgroundDrawable( holder.itemView.getResources().getDrawable(R.drawable.circle_background_pink) );
                    break;
                case 6:
                    holder.txtMailCircle.setBackgroundDrawable( holder.itemView.getResources().getDrawable(R.drawable.circle_background_violet) );
                    break;
            }
            holder.txtMailCircle.setText(String.valueOf(jsonObject.getString("nguoi_gui").trim().charAt(0)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mOnItemClickListener.onItemClick(v,position,data.getJSONObject(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
