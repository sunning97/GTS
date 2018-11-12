package vn.edu.ut.gts.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vn.edu.ut.gts.R;
import vn.edu.ut.gts.views.mail.fragments.OnItemClickListener;

public class MailSentRecyclerViewAdapter extends RecyclerView.Adapter<MailSentRecyclerViewAdapter.MailSentRecyclerViewHolder>{
    private JSONArray data;
    private OnItemClickListener onItemClickListener;

    public MailSentRecyclerViewAdapter(JSONArray data,OnItemClickListener onItemClickListener){
        this.data  = data;
        this.onItemClickListener = onItemClickListener;
    }

    public MailSentRecyclerViewAdapter(){
    }

    public void setData(JSONArray data) {
        this.data = data;
    }




    @NonNull
    @Override
    public MailSentRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.mail_sent_item_layout, parent, false);
        return new MailSentRecyclerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MailSentRecyclerViewHolder holder, final int position) {
        try {
            JSONObject jsonObject = data.getJSONObject(position);
            holder.txtSendTitle.setText(jsonObject.getString("tieu_de"));
            holder.txtSendDay.setText(jsonObject.getString("ngay_gui"));
            holder.txtMailCircle.setText("T");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onItemClickListener.onSentMailItemCLick(v,position,data.getJSONObject(position));
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

    public class MailSentRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView txtSendTitle;
        TextView txtSendDay;
        TextView txtMailCircle;

        public MailSentRecyclerViewHolder(View itemView) {
            super(itemView);
            txtSendTitle = itemView.findViewById(R.id.send_title);
            txtSendDay = itemView.findViewById(R.id.send_day);
            txtMailCircle = itemView.findViewById(R.id.mail_circle);
        }
    }
}
