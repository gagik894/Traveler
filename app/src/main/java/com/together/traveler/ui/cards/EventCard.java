package com.together.traveler.ui.cards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.together.traveler.databinding.FragmentEventCardBinding;


public class EventCard extends Fragment {
    private FragmentEventCardBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEventCardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
