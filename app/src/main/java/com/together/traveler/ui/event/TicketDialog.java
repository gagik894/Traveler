package com.together.traveler.ui.event;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.together.traveler.R;
import com.together.traveler.model.Event;

public class TicketDialog extends Dialog {
    private final Event data;
    private String userId;

    public TicketDialog(Context context, Event data, String userId) {
        super(context);
        this.data = data;
        this.userId = userId;
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
        start.setText(String.format("From %s", data.getStartDate()));
        end.setText(String.format("To %s", data.getEndDate()));
        Glide.with(getContext()).load(imageUrl).into(imageView);

        int qrCodeSize = 1000;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(userId, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize);
            int bitmapWidth = bitMatrix.getWidth();
            int bitmapHeight = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < bitmapWidth; x++) {
                for (int y = 0; y < bitmapHeight; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            qr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }


        backBtn.setOnClickListener(v->dismiss());
    }
}
