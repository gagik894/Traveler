package com.together.traveler.ui.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.together.traveler.R;
import com.together.traveler.databinding.FragmentEventBinding;

public class EventFragment extends Fragment {
    private FragmentEventBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventViewModel eventViewModel =
                new ViewModelProvider(this).get(EventViewModel.class);

        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView name = binding.eventTvName;
        final TextView location = binding.eventTvLocation;
        final TextView date = binding.eventTvDate;
        final TextView time = binding.eventTvTime;
        final TextView description = binding.eventTvDescription;
        final TextView buttonMore = binding.eventTvMore;
        int maxLines = description.getMaxLines();

        buttonMore.setOnClickListener(v -> {
            if (description.getMaxLines() == maxLines) {
                description.setMaxLines(Integer.MAX_VALUE);
                buttonMore.setText(R.string.read_less);
            } else {
                description.setMaxLines(maxLines);
                buttonMore.setText(R.string.event_read_more);
            }
        });
        eventViewModel.setData(getArguments().getParcelable("cardData"));
        eventViewModel.getData().observe(getViewLifecycleOwner(), data -> {
            name.setText(data.getName());
            location.setText(data.getLocation());
            date.setText(String.valueOf(data.getDate()));
            time.setText(String.valueOf(data.getTime()));
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
