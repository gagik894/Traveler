package com.together.traveler.ui.event.scan;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.together.traveler.R;
import com.together.traveler.model.CheckTicketResponse;


public class TicketCheckResponseDialog extends Dialog {
    private final CheckTicketResponse data;
    private TicketCheckResponseDialogListener mListener;

    public interface TicketCheckResponseDialogListener {
        void onTicketCheckResponseDialogDismissed();
    }

    public TicketCheckResponseDialog(Context context, CheckTicketResponse data) {
        super(context);
        this.data = data;
    }

    public void setListener(TicketCheckResponseDialogListener listener) {
        mListener = listener;
    }

    @Override
    public void dismiss() {
        Log.i("asd", "dismiss: ");
        if (mListener != null) {
            mListener.onTicketCheckResponseDialogDismissed();
        }
        super.dismiss();
    }

    @Override
    public void onStart() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getWindow().getAttributes());
        layoutParams.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.9);
        getWindow().setAttributes(layoutParams);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ticket_check_response);

        TextView username = findViewById(R.id.checkTicketTvUsername);
        ImageView successImage = findViewById(R.id.checkTicketIvSuccess);
        ImageView userAvatar  = findViewById(R.id.checkTicketIvImage);
        String imageUrl = String.format("https://drive.google.com/uc?export=wiew&id=%s", data.getUser().getAvatar());

        username.setText(data.getUser().getUsername());
        Glide.with(getContext()).load(imageUrl).into(userAvatar);
        ColorStateList colorStateList;
        if (data.isEnrolled()){
            successImage.setImageResource(R.drawable.done);
            colorStateList = ColorStateList.valueOf(getContext().getResources().getColor(R.color.green));
        }else{
            successImage.setImageResource(R.drawable.close);
            colorStateList = ColorStateList.valueOf(getContext().getResources().getColor(R.color.red));
        }
        successImage.setImageTintList(colorStateList);
        username.setTextColor(colorStateList);
    }
}