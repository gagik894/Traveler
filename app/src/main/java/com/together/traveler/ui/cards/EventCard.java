package com.together.traveler.ui.cards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.together.traveler.databinding.FragmentEventCardBinding;
import com.together.traveler.model.Event;
import com.together.traveler.model.User;
import com.together.traveler.ui.main.home.HomeViewModel;


public class EventCard extends Fragment {
    private FragmentEventCardBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = FragmentEventCardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ImageView eventImage = binding.eventCardIvImage;
        final TextView title = binding.eventCardTvName;
        final TextView username = binding.eventCardTvUsername;
        final TextView location = binding.eventCardTvLocation;
        final TextView startDate = binding.eventCardTvDate;
        final TextView endDate = binding.eventCardTvTime;
        final ImageView userImage = binding.eventCardIvUserImage;

        homeViewModel.getMapSelectedEvent().observe(getViewLifecycleOwner(), data -> {
            String userImageUrl = String.format("https://drive.google.com/uc?export=wiew&id=%s", data.getUser().getAvatar());
            String eventImageUrl = String.format("https://drive.google.com/uc?export=wiew&id=%s", data.getImgId());
            Glide.with(requireContext()).load(userImageUrl).into(userImage);
            Glide.with(requireContext()).load(eventImageUrl).into(eventImage);
            username.setText(data.getUser().getUsername());
            location.setText(data.getLocation());
            title.setText(data.getTitle());
            startDate.setText(data.getStartDate());
            endDate.setText(data.getEndDate());
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
