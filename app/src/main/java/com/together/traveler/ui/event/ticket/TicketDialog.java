package com.together.traveler.ui.event.ticket;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.images.ImageManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.together.traveler.R;
import com.together.traveler.model.Event;

public class TicketDialog extends Dialog {
    private final Event data;
    private final String userId;
    private OnImageLoadedListener listener;

    public interface OnImageLoadedListener {
        void onImageLoaded();
    }

    public void setOnImageLoadedListener(OnImageLoadedListener listener) {
        this.listener = listener;
    }

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
        Glide.with(getContext())
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Handle the failure
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onImageLoaded();
                        }
                        return false;
                    }
                })
                .into(imageView);



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
