package com.together.traveler.ui.event.parsed.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.together.traveler.R;
import com.together.traveler.databinding.FragmentUserCardBinding;
import com.together.traveler.databinding.FragmentUserCardMiniBinding;
import com.together.traveler.model.User;
import com.together.traveler.ui.event.EventViewModel;
import com.together.traveler.ui.event.parsed.ParsedEventViewModel;

public class UserFragment extends Fragment {
    private FragmentUserCardMiniBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ParsedEventViewModel eventViewModel =
                new ViewModelProvider(requireActivity()).get(ParsedEventViewModel.class);

        binding = FragmentUserCardMiniBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final ImageView userImage = binding.userCardIvImage;
        final TextView username = binding.userCardTvUsername;

        eventViewModel.getData().observe(getViewLifecycleOwner(), data -> {
            Glide.with(requireContext()).load(data.getUserAvatar()).into(userImage);
            username.setText(data.getUsername());
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
