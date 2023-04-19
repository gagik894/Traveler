package com.together.traveler.ui.event;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.together.traveler.R;
import com.together.traveler.model.Event;

public class TicketDialog extends Dialog {
    private final Event data;

    public TicketDialog(Context context, Event data) {
        super(context);
        this.data = data;
    }

    @Override
    public void onStart() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getWindow().getAttributes());
        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.9);
        int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.95);
        layoutParams.width = width;
        layoutParams.height = height;
        getWindow().setAttributes(layoutParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket);

        TextView title = findViewById(R.id.ticketTvName);
        TextView location = findViewById(R.id.ticketTvLocation);
        TextView start = findViewById(R.id.ticketTvDate);
        TextView end = findViewById(R.id.ticketTvTime);
        ImageView imageView = findViewById(R.id.ticketImage);
        ImageView qr  = findViewById(R.id.ticketIvQr);
        ImageButton backBtn = findViewById(R.id.ticketIBtnBack);
        String imageUrl = String.format("https://drive.google.com/uc?export=wiew&id=%s", data.getImgId());

        title.setText(data.getTitle());
        location.setText(data.getLocation());
        start.setText(String.format("From %s, %s", data.getStartDate(), data.getStartTime()));
        end.setText(String.format("To %s, %s", data.getEndDate(), data.getEndTime()));
        Glide.with(getContext()).load(imageUrl).into(imageView);

        backBtn.setOnClickListener(v->dismiss());
    }
}
