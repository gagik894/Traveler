package com.together.traveler.ui.place;

import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.together.traveler.R;
import com.together.traveler.databinding.FragmentPlaceBinding;

public class PlaceFragment extends Fragment {

    private FragmentPlaceBinding binding;
    private PlaceViewModel placeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaceBinding.inflate(inflater, container, false);
        placeViewModel = new ViewModelProvider(requireActivity()).get(PlaceViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ImageView image = binding.placeIvImage;
        final TextView name = binding.placeTvName;
        final TextView location = binding.placeTvLocation;
        final TextView openStatus = binding.placeTvOpenStatus;
        final TextView nextStatus = binding.placeTvNextStatus;
        final TextView nextTime = binding.placeTvNextTime;
        final TextView phone = binding.placeTvPhone;
        final TextView link = binding.placeTvLink;
        final TextView description = binding.placeTvDescription;
        final TextView moreButton = binding.placeTvMore;
        final TextView category = binding.placeTvCategory;
        final ImageButton backButton = binding.placeIBtnBack;
        final ImageButton saveButton = binding.placeIBtnSave;
        final FragmentContainerView mapCard = binding.placeMap;
        int maxLines = description.getMaxLines();


        if (getArguments() != null) {
            Log.d("asd", "onCreateView: " + getArguments());
            placeViewModel.setPlaceData(getArguments().getParcelable("placeData"));
            placeViewModel.setUserId(getArguments().getString("userId"));
        }

        backButton.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
        moreButton.setOnClickListener(v -> {
            if (description.getMaxLines() == maxLines) {
                description.setMaxLines(Integer.MAX_VALUE);
                moreButton.setText(R.string.event_read_less);
            } else {
                description.setMaxLines(maxLines);
                moreButton.setText(R.string.event_read_more);
            }
        });

        placeViewModel.getPlaceData().observe(getViewLifecycleOwner(), place -> {

            boolean isOpen = placeViewModel.isOpen();
            String imageUrl = String.format("https://drive.google.com/uc?export=wiew&id=%s", place.getImgId());
            Log.i("asd", "onCreateView: " + imageUrl);
            Glide.with(requireContext()).load(imageUrl).into(image);
            Toast.makeText(requireContext(), place.getName() + "" + isOpen, Toast.LENGTH_SHORT).show();
            name.setText(place.getName());
            location.setText(place.getLocation());
            category.setText(place.getCategory());
            description.setText(place.getDescription());
            openStatus.setText(isOpen? R.string.place_open: R.string.place_closed);
            openStatus.setTextColor(isOpen? Color.GREEN : Color.RED);
            nextStatus.setText(isOpen? R.string.place_closes : R.string.place_opens);
            nextTime.setText(placeViewModel.getNextTime());
            phone.setText(place.getPhone());
            link.setText(place.getUrl());

            ViewTreeObserver vto = description.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int lineCount = description.getLineCount();
                    if (description.getMaxLines() == lineCount) {
                        moreButton.setVisibility(View.VISIBLE);
                    } else {
                        moreButton.setVisibility(View.GONE);
                    }
                    ViewTreeObserver vto = description.getViewTreeObserver();
                    vto.removeOnGlobalLayoutListener(this);
                }
            });
        });
    }
}