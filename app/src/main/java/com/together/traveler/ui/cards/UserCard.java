package com.together.traveler.ui.cards;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.together.traveler.R;
import com.together.traveler.databinding.FragmentUserCardBinding;
import com.together.traveler.model.User;
import com.together.traveler.ui.event.EventViewModel;

import java.util.Date;

public class UserCard extends Fragment {
    private FragmentUserCardBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventViewModel eventViewModel =
                new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        binding = FragmentUserCardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final ImageView userImage = binding.userCardIvImage;
        final TextView username = binding.userCardTvUsername;
        final TextView rating = binding.userCardTvRating;
        final Button followButton = binding.userCardBtnFollow;

        followButton.setOnClickListener(v -> Toast.makeText(requireContext(), "Followed", Toast.LENGTH_SHORT).show());

        eventViewModel.getData().observe(getViewLifecycleOwner(), data -> {
            User userData = data.getUser();
//            userImage.setImageResource(userData.getProfileImage());
            username.setText(userData.getUsername());
            rating.setText(String.valueOf(userData.getRating()));
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}