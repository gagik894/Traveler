package com.together.traveler.ui.main.ticket;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.together.traveler.databinding.FragmentUniversalTicketBinding;
import com.together.traveler.ui.main.user.UserViewModel;

public class UniversalTicketFragment extends Fragment {

    private FragmentUniversalTicketBinding binding;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUniversalTicketBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setUserId("self");
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView username = binding.utkTvUsername;
        final ImageView qr = binding.utkIvQr;
        final ImageView userImage = binding.utkIvUser;
        final QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int qrCodeSize = 1000;
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        userViewModel.getUser().observe(getViewLifecycleOwner(), data->{
            Log.d("Ticket", "onViewCreated: " + userId);
            String imageUrl = String.format("https://drive.google.com/uc?export=wiew&id=%s", data.getAvatar());
            username.setText(data.getUsername());
            Glide.with(requireContext()).load(imageUrl).into(userImage);

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
        });
    }
}