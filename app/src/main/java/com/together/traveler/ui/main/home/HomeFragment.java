package com.together.traveler.ui.main.home;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.together.traveler.R;
import com.together.traveler.adapter.EventCardsAdapter;
import com.together.traveler.databinding.FragmentHomeBinding;
import com.together.traveler.model.Event;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private final String TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    private RecyclerView rvCards;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HomeViewModel homeViewModel;
    private ProgressBar progressBar;
    private ImageButton filtersButton;
    private ChipGroup chipGroup;
    private EventCardsAdapter eventCardsAdapter;
    private final List<Event> eventList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        rvCards = binding.rvHome;
        swipeRefreshLayout = binding.cardSwipeRefreshLayout;
        progressBar = binding.homePb;
        filtersButton = binding.homeBtnFilters;
        chipGroup = binding.homeChgCategories;

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchView searchView = binding.searchView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            searchView.setIconifiedByDefault(false);
        }

        eventCardsAdapter = new EventCardsAdapter(eventList, item -> {
            if (isAdded()) {
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToEventFragment(item, homeViewModel.getUserId());
                NavHostFragment.findNavController(this).navigate(action);
            }
        });

        rvCards.setAdapter(eventCardsAdapter);
        rvCards.setLayoutManager(new LinearLayoutManager(requireContext()));


        swipeRefreshLayout.setOnRefreshListener(() -> {
            Thread thread = new Thread(homeViewModel::fetchEvents);
            thread.start();
        });

        filtersButton.setOnClickListener(v-> homeViewModel.changeCategoriesVisibility());

        homeViewModel.getAllEvents().observe(getViewLifecycleOwner(), newEvents -> {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);

            new CalculateDiffTask(eventList, newEvents, eventCardsAdapter).execute();
        });

        homeViewModel.getCategoriesVisibility().observe(getViewLifecycleOwner(), visible->{
            chipGroup.setVisibility(visible? View.VISIBLE: View.GONE);
            filtersButton.setImageResource(visible? R.drawable.ic_baseline_filter_list_off: R.drawable.ic_baseline_filter_list);
        });

        homeViewModel.getCategories().observe(getViewLifecycleOwner(), categories ->{
            chipGroup.removeAllViews();
            for (int i = 0; i < categories.size(); i++) {
                Chip chip = new Chip(requireContext());
                chip.setText(categories.get(i));
                chip.setClickable(true);
                chip.setCheckable(true);
                chip.setOnClickListener(v -> homeViewModel.addOrRemoveSelectedCategories((String) chip.getText()));
                chipGroup.addView(chip);
            }
        });

        homeViewModel.getSelectedCategories().observe(getViewLifecycleOwner(), selectedCategories->{
            for (int i = 0; i < selectedCategories.size(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(selectedCategories.get(i));
                chip.setChecked(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void scrollUp(){
        Log.i("asd", "scrollUp: " + rvCards);
        rvCards.post(() -> rvCards.smoothScrollBy(0, -1000));
    }
    public void scrollDown(){
        Log.i("asd", "scrollDown: " + rvCards);
        rvCards.post(() -> rvCards.smoothScrollBy(0, 1000));
    }

    private static class CalculateDiffTask extends AsyncTask<Void, Void, DiffUtil.DiffResult> {
        private final List<Event> eventList;
        private final List<Event> newEvents;
        private final EventCardsAdapter eventCardsAdapter;

        public CalculateDiffTask(List<Event> eventList, List<Event> newEvents, EventCardsAdapter eventCardsAdapter) {
            this.eventList = eventList;
            this.newEvents = newEvents;
            this.eventCardsAdapter = eventCardsAdapter;
        }

        @Override
        protected DiffUtil.DiffResult doInBackground(Void... voids) {
            return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return eventList.size();
                }

                @Override
                public int getNewListSize() {
                    return newEvents.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    Event oldEvent = eventList.get(oldItemPosition);
                    Event newEvent = newEvents.get(newItemPosition);
                    return oldEvent.get_id().equals(newEvent.get_id());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Event oldEvent = eventList.get(oldItemPosition);
                    Event newEvent = newEvents.get(newItemPosition);
                    return oldEvent.equals(newEvent);
                }
            });
        }

        @Override
        protected void onPostExecute(DiffUtil.DiffResult diffResult) {
            eventList.clear();
            eventList.addAll(newEvents);
            diffResult.dispatchUpdatesTo(eventCardsAdapter);
        }
    }
}
