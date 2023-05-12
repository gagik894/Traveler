package com.together.traveler.ui.cards;

import android.graphics.Color;
import android.os.Bundle;
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
import com.together.traveler.R;
import com.together.traveler.databinding.FragmentPlaceCardBinding;
import com.together.traveler.ui.main.map.MapViewModel;
import com.together.traveler.ui.place.PlaceViewModel;


public class PlaceCard extends Fragment {
    private FragmentPlaceCardBinding binding;
    private MapViewModel mapViewModel;
    private PlaceViewModel placeViewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
        placeViewModel = new ViewModelProvider(requireActivity()).get(PlaceViewModel.class);
        binding = FragmentPlaceCardBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ImageView eventImage = binding.placeCardIvMain;
        final TextView category = binding.placeCardTvCategory;
        final TextView name = binding.placeCardTvName;
        final TextView phone = binding.placeCardTvPhone;
        final TextView currentStatus = binding.placeCardTvOpenStatus;
        final TextView nextStatus = binding.placeCardTvNextStatus;
        final TextView nextTime = binding.placeCardTvNextTime;


        mapViewModel.getMapSelectedPlaceData().observe(getViewLifecycleOwner(), data -> placeViewModel.setPlaceData(data));
        placeViewModel.getPlaceData().observe(getViewLifecycleOwner(), data->{
            boolean isOpen = placeViewModel.isOpen();
            String eventImageUrl = String.format("https://drive.google.com/uc?export=wiew&id=%s", data.getImgId());
            Glide.with(requireContext()).load(eventImageUrl).into(eventImage);
            name.setText(data.getName());
            phone.setText(data.getPhone());
            category.setText(data.getCategory());
            currentStatus.setText(isOpen? R.string.place_open: R.string.place_closed);
            currentStatus.setTextColor(isOpen? Color.GREEN : Color.RED);
            nextStatus.setText(isOpen? R.string.place_closes : R.string.place_opens);
            nextTime.setText(placeViewModel.getNextTime());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
