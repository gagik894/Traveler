package com.together.traveler.ui.event;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.together.traveler.R;
import com.together.traveler.databinding.FragmentEventBinding;

public class EventFragment extends Fragment {
    private FragmentEventBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventViewModel eventViewModel =
                new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final ImageView image = binding.eventIvImage;
        final TextView name = binding.eventTvName;
        final TextView location = binding.eventTvLocation;
        final TextView date = binding.eventTvDate;
        final TextView time = binding.eventTvTime;
        final TextView description = binding.eventTvDescription;
        final TextView buttonMore = binding.eventTvMore;
        final Button enrollButton = binding.eventBtnEnroll;
        final ImageButton backButton = binding.eventIBtnBack;
        final ImageButton saveButton = binding.eventIBtnSave;
        int maxLines = description.getMaxLines();

        if (getArguments() != null) {
            eventViewModel.setData(getArguments().getParcelable("cardData"));
        }
        eventViewModel.getData().observe(getViewLifecycleOwner(), data -> {
            image.setImageResource(data.getImage());
            name.setText(data.getName());
            location.setText(data.getLocation());
            date.setText(String.valueOf(data.getDate()));
            time.setText(String.valueOf(data.getTime()));
            description.setText(data.getDescription());

            if (data.isEnrolled()){
                enrollButton.setEnabled(false);
                enrollButton.setText(R.string.event_enroll_button_enrolled);
                enrollButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray));
                enrollButton.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));

            }else{
                enrollButton.setEnabled(true);
                enrollButton.setText(R.string.event_enroll_button);
                enrollButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.secondary_color));
            }
            if(data.isSaved()){
                saveButton.setImageResource(R.drawable.favorite);
            }else{
                saveButton.setImageResource(R.drawable.favorite_border);
            }
            ViewTreeObserver vto = description.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int lineCount = description.getLineCount();
                    Log.d("asd", "Line count: " + lineCount + description.getMaxLines());
                    if (description.getMaxLines()==lineCount){
                        buttonMore.setVisibility(View.VISIBLE);
                    }else{
                        buttonMore.setVisibility(View.GONE);
                    }
                    ViewTreeObserver vto = description.getViewTreeObserver();
                    vto.removeOnGlobalLayoutListener(this);
                }
            });
        });

        saveButton.setOnClickListener(v -> eventViewModel.save());
        enrollButton.setOnClickListener(v -> eventViewModel.enroll());
        backButton.setOnClickListener(v-> NavHostFragment.findNavController(this).navigateUp());

        buttonMore.setOnClickListener(v -> {
            if (description.getMaxLines() == maxLines) {
                description.setMaxLines(Integer.MAX_VALUE);
                buttonMore.setText(R.string.event_read_less);
            } else {
                description.setMaxLines(maxLines);
                buttonMore.setText(R.string.event_read_more);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
