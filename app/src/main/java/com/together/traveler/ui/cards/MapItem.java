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
import com.together.traveler.databinding.FragmentUserCardBinding;
import com.together.traveler.databinding.ItemHomeLocationBinding;
import com.together.traveler.model.User;
import com.together.traveler.ui.event.EventViewModel;
import com.together.traveler.ui.main.home.HomeViewModel;

public class MapItem extends Fragment {
    private ItemHomeLocationBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = ItemHomeLocationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView name = binding.ItemLocationTv;

        homeViewModel.getLocationName().observe(getViewLifecycleOwner(), name::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}