package com.together.traveler.ui.main.bottomSheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.together.traveler.databinding.DialogBottomSheetMainAddBinding;
import com.together.traveler.ui.add.event.AddEventActivity;
import com.together.traveler.ui.add.place.AddPlaceActivity;

public class AddBottomSheet extends BottomSheetDialogFragment {
    private DialogBottomSheetMainAddBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DialogBottomSheetMainAddBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button eventsBtn = binding.mainBtnEvent;
        Button placesBtn = binding.mainBtnPlace;

        eventsBtn.setOnClickListener(v->{
            Intent intent = new Intent(requireActivity(), AddEventActivity.class);
            startActivity(intent);
        });

        placesBtn.setOnClickListener(v->{
            Intent intent = new Intent(requireActivity(), AddPlaceActivity.class);
            startActivity(intent);
        });
    }
}
