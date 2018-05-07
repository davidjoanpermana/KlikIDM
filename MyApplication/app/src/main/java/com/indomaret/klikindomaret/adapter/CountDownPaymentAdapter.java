package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.helper.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by USER on 4/26/2016.
 */
public class CountDownPaymentAdapter extends RecyclerView.Adapter<CountDownPaymentAdapter.ViewHolder> {
    private Activity activity;
    private SessionManager sessionManager;
    private String expiredDate = "";
    private Handler handler = new Handler();
    private Date eventDate = new Date();
    private Date currentDate = new Date();

    public CountDownPaymentAdapter(Activity activity, String expiredDate){
        this.activity = activity;
        this.expiredDate = expiredDate;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView messageErrorPromo;

        public ViewHolder(View view) {
            super(view);
            messageErrorPromo = (TextView) view.findViewById(R.id.message_error_promo);
        }
    }

    @Override
    public CountDownPaymentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.count_down_payment, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            sessionManager = new SessionManager(activity);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            if (expiredDate != null && expiredDate.length() > 0){
                eventDate = dateFormat.parse(expiredDate.replace("T", " "));
            } else if (sessionManager.getKeyExpiredDate() != null && sessionManager.getKeyExpiredDate().length() > 0){
                eventDate = dateFormat.parse(sessionManager.getKeyExpiredDate().replace("T", " "));
            }

            currentDate = new Date();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        long diff = eventDate.getTime() - currentDate.getTime();
                        long minutes = ((diff / (1000*60)) % 60);
                        long seconds = (diff / 1000) % 60;
                        int hours   = (int) ((diff / (1000*60*60)) % 24);

                        if (diff > 0){
                            holder.messageErrorPromo.setText("Anda hanya dapat menggunakan pembayaran ini dalam : "
                                    + String.format("%02d", hours) + "Jam "
                                    + String.format("%02d", minutes) + "Menit "
                                    + String.format("%02d", seconds) + "Detik ");
                        }else{
                            holder.messageErrorPromo.setText("Anda hanya dapat menggunakan pembayaran ini dalam : 00Jam 00Menit 00Detik ");
                        }

                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
