package com.together.traveler.ui.cards;

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
import com.together.traveler.databinding.FragmentEventCardBinding;
import com.together.traveler.ui.main.map.MapViewModel;


public class EventCard extends Fragment {

    private FragmentEventCardBinding binding;
    private MapViewModel mapViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
        binding = FragmentEventCardBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = binding.eventCardTvName;
        TextView username = binding.eventCardTvUsername;
        TextView location = binding.eventCardTvLocation;
        TextView startDate = binding.eventCardTvDate;
        TextView endDate = binding.eventCardTvTime;
        ImageView userImage = binding.eventCardIvUserImage;
        ImageView eventImage = binding.eventCardIvImage;

        mapViewModel.getMapSelectedEventData().observe(getViewLifecycleOwner(), event -> {
            String userImageUrl = String.format("https://drive.google.com/uc?export=wiew&id=%s", event.getUser().getAvatar());
            String eventImageUrl = String.format("https://drive.google.com/uc?export=wiew&id=%s", event.getImgId());
            Glide.with(requireContext()).load(userImageUrl).into(userImage);
            Glide.with(requireContext()).load(eventImageUrl).into(eventImage);
            username.setText(event.getUser().getUsername());
            location.setText(event.getLocation());
            title.setText(event.getTitle());
            startDate.setText(event.getStartDate());
            endDate.setText(event.getEndDate());
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
