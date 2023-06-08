package com.together.traveler.ui.event.parsed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.together.traveler.R;
import com.together.traveler.databinding.FragmentEventBinding;
import com.together.traveler.ui.event.parsed.map.MapFragment;
import com.together.traveler.ui.event.parsed.user.UserFragment;

public class ParsedEvent extends Fragment {
    private final String TAG = "EventFragment";

    private FragmentEventBinding binding;
    private ParsedEventViewModel eventViewModel;

    private NestedScrollView scrollView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventViewModel = new ViewModelProvider(requireActivity()).get(ParsedEventViewModel.class);

        binding = FragmentEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollView = binding.eventNsv;
        final View childView = view.findViewById(R.id.eventMap);
        final ImageView image = binding.eventIvImage;
        final TextView name = binding.ticketTvName;
        final TextView location = binding.eventTvLocation;
        final TextView startDate = binding.eventTvDate;
        final TextView endDate = binding.eventTvTime;
        final TextView description = binding.eventTvDescription;
        final TextView moreButton = binding.eventTvMore;
        final TextView category = binding.eventTvCategory;
        final TextView tags = binding.eventTvTags;
        final Button bottomButton = binding.eventBtnBottom;
        final ImageButton backButton = binding.eventIBtnBack;
        final ImageButton saveButton = binding.eventIBtnSave;
        final ChipGroup chipGroup = binding.eventChgTags;
        final FragmentContainerView userCard = binding.eventUser;
        final FragmentContainerView mapCard = binding.eventMap;
        final Group userGroup = binding.eventGrpUser;
        if (userCard.getChildCount() > 0) {
            userCard.removeAllViews();
        }
        UserFragment userFragment = new UserFragment();
        MapFragment mapFragment = new MapFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(userCard.getId(), userFragment)
                .commit();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(mapCard.getId(), mapFragment)
                .commit();

        int maxLines = description.getMaxLines();

        childView.setClickable(false);
        if (getArguments() != null) {
            Log.d("asd", "onCreateView: " + getArguments());
            eventViewModel.setData(getArguments().getParcelable("cardData"));
        }


        saveButton.setVisibility(View.GONE);
        userGroup.setVisibility(View.VISIBLE);
        bottomButton.setText(R.string.event_get_tickets);

        eventViewModel.getData().observe(getViewLifecycleOwner(), data -> {

            Glide.with(requireContext()).load(data.getImg_url()).into(image);
            name.setText(data.getTitle());
            location.setText(data.getLocation());
            startDate.setText(String.format("From %s", data.getStartDate()));
            endDate.setText(String.format("To %s", data.getEndDate()));
            category.setText(data.getGenre());
            description.setText(data.getDescription());


            if (data.getTags().size() > 0){
                tags.setVisibility(View.VISIBLE);
                chipGroup.removeAllViews();
                for (int i = 0; i < data.getTags().size(); i++) {
                    Chip chip = new Chip(requireContext());
                    chip.setText(data.getTags().get(i));
                    chip.setClickable(false);
                    chip.setCheckable(false);
                    chipGroup.addView(chip);
                }
            }else{
                tags.setVisibility(View.GONE);
            }

            ViewTreeObserver vto = description.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int lineCount = description.getLineCount();
                    if (description.getMaxLines() == lineCount) {
                        moreButton.setVisibility(View.VISIBLE);
                    } else {
                        moreButton.setVisibility(View.GONE);
                    }
                    ViewTreeObserver vto = description.getViewTreeObserver();
                    vto.removeOnGlobalLayoutListener(this);
                }
            });

            bottomButton.setOnClickListener(v -> openUrl(data.getLink()));
        });


        backButton.setOnClickListener(v->backPress());
        moreButton.setOnClickListener(v -> {
            if (description.getMaxLines() == maxLines) {
                description.setMaxLines(Integer.MAX_VALUE);
                moreButton.setText(R.string.event_read_less);
            } else {
                description.setMaxLines(maxLines);
                moreButton.setText(R.string.event_read_more);
            }
        });


    }


    @Override
    public void onResume() {
        BottomNavigationView bottomNavigationView =requireActivity().findViewById(R.id.nvMain);
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.homeFragment);
        menuItem.setChecked(true);
        super.onResume();
    }


    public void scrollUp(){
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_UP));
    }
    public void scrollDown(){
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
    public void backPress(){
        NavHostFragment.findNavController(this).navigateUp();
    }

    public void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}