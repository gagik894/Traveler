package com.together.traveler.ui.main.home;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
import com.together.traveler.adapter.ParsedEventCardsAdapter;
import com.together.traveler.databinding.FragmentHomeBinding;
import com.together.traveler.model.ParsedEvent;
import com.together.traveler.model.Event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class HomeFragment extends Fragment{
    private final String TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    private RecyclerView rvCards;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HomeViewModel homeViewModel;
    private ProgressBar progressBar;
    private ImageButton filtersButton;
    private ChipGroup chipGroup;
    private ParsedEventCardsAdapter parsedEventCardsAdapter;
    private CardView locationFragment;
    private final List<Event> eventList = new ArrayList<>();
    private final List<ParsedEvent> parsedEventList = new ArrayList<>();

    private final ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false))) {
                    homeViewModel.getLocationByGPS();
                }else{
                    homeViewModel.getLocationByIP();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvCards = binding.rvHome;
        swipeRefreshLayout = binding.cardSwipeRefreshLayout;
        progressBar = binding.homePb;
        filtersButton = binding.homeBtnFilters;
        chipGroup = binding.homeChgCategories;
        locationFragment = binding.fragmentContainerView;
        SearchView searchView = binding.searchView;
        final TextView locationNameTv = binding.ItemLocationTv;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            searchView.setIconifiedByDefault(false);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                homeViewModel.filterBySearchAndTags(newText);
//                eventCardsAdapter.getFilter().filter(newText);
                return false;
            }
        });

        rvCards.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    // Scrolling up
                    locationFragment.animate().translationY(locationFragment.getHeight()).setDuration(300);
                    locationFragment.animate().alpha(0.0f).setDuration(300);
                } else {
                    // Scrolling down
                    locationFragment.animate().translationY(0).setDuration(300);
                    locationFragment.animate().alpha(1.0f).setDuration(300);
                }
            }
        });

        parsedEventCardsAdapter = new ParsedEventCardsAdapter(eventList,parsedEventList, item -> {
            if (isAdded()) {
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToEventFragment(item, homeViewModel.getUserId());
                NavHostFragment.findNavController(this).navigate(action);
            }
        }, item -> {
            NavDirections action = HomeFragmentDirections.actionHomeFragmentToParsedEvent(item);
            NavHostFragment.findNavController(this).navigate(action);
        });


        rvCards.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCards.setAdapter(parsedEventCardsAdapter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Thread thread = new Thread(homeViewModel::fetch);
            thread.start();
        });

        locationFragment.setOnClickListener(v-> showSelectLocation());
        filtersButton.setOnClickListener(v-> homeViewModel.changeCategoriesVisibility());

        homeViewModel.getAllEvents().observe(getViewLifecycleOwner(), newEvents -> {
            swipeRefreshLayout.setRefreshing(false);
            DiffUtil.DiffResult parsedEventDiffResult = DiffUtil.calculateDiff(new CombinedDiffCallback(eventList, newEvents, parsedEventList, parsedEventList));
            eventList.clear();
            eventList.addAll(newEvents);
            parsedEventDiffResult.dispatchUpdatesTo(parsedEventCardsAdapter);

        });

        homeViewModel.getParsedEvents().observe(getViewLifecycleOwner(), newEvents -> {
            swipeRefreshLayout.setRefreshing(false);
            DiffUtil.DiffResult parsedEventDiffResult = DiffUtil.calculateDiff(new CombinedDiffCallback(eventList, eventList, parsedEventList, newEvents ));
            parsedEventList.clear();
            parsedEventList.addAll(newEvents);
            parsedEventDiffResult.dispatchUpdatesTo(parsedEventCardsAdapter);
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

        homeViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> progressBar.setVisibility(isLoading? View.VISIBLE : View.GONE));
        homeViewModel.getLocationName().observe(getViewLifecycleOwner(), locationNameTv::setText);
        if (homeViewModel.getLocationName().getValue() == null){
            requestLocationAndGetNearbyEvents();
        }

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

    private void showSelectLocation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_home_location, null);
        builder.setView(dialogLayout);
        EditText locationSearch = dialogLayout.findViewById(R.id.DHomeLocationEtLocation);
        Button allBtn = dialogLayout.findViewById(R.id.DHomeLocationBtnAll);
        Button nearbyBtn = dialogLayout.findViewById(R.id.DHomeLocationBtnNearby);

        builder.setPositiveButton("OK", (dialog, which) -> homeViewModel.setLocationName(locationSearch.getText().toString()));

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());


        AlertDialog dialog = builder.create();
        dialog.show();
        allBtn.setOnClickListener(v->{
            homeViewModel.setLocationName("");
            dialog.dismiss();
        });
        nearbyBtn.setOnClickListener(v->{
            requestLocationAndGetNearbyEvents();
            dialog.dismiss();
        });
    }

    private void requestLocationAndGetNearbyEvents() {
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Permission already granted
            homeViewModel.getLocationByGPS();
        } else {
            // Request permission
            requestMultiplePermissionsLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }
}


class CombinedDiffCallback extends DiffUtil.Callback {
    private List<Event> oldEvents;
    private List<Event> newEvents;
    private List<ParsedEvent> oldParsedEvents;
    private List<ParsedEvent> newParsedEvents;

    public CombinedDiffCallback(List<Event> oldEvents, List<Event> newEvents, List<ParsedEvent> oldParsedEvents, List<ParsedEvent> newParsedEvents) {
        this.oldEvents = oldEvents;
        this.newEvents = newEvents;
        this.oldParsedEvents = oldParsedEvents;
        this.newParsedEvents = newParsedEvents;
    }

    @Override
    public int getOldListSize() {
        return oldEvents.size() + oldParsedEvents.size() + 1; // Add 1 for the divider
    }

    @Override
    public int getNewListSize() {
        return newEvents.size() + newParsedEvents.size() + 1; // Add 1 for the divider
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        if (isDividerPosition(oldItemPosition) && isDividerPosition(newItemPosition)) {
            return true; // Both are dividers
        } else if (isDividerPosition(oldItemPosition) || isDividerPosition(newItemPosition)) {
            return false; // One is a divider, the other is an item
        }

        int oldPosition = adjustPosition(oldItemPosition);
        int newPosition = adjustPosition(newItemPosition);

        if (oldPosition < oldEvents.size() && newPosition < newEvents.size()) {
            Event oldEvent = oldEvents.get(oldPosition);
            Event newEvent = newEvents.get(newPosition);
            return oldEvent.get_id().equals(newEvent.get_id());
        } else if (oldPosition >= oldEvents.size() && newPosition >= newEvents.size()) {
            int oldParsedPosition = oldPosition - oldEvents.size();
            int newParsedPosition = newPosition - newEvents.size();
            ParsedEvent oldParsedEvent = oldParsedEvents.get(oldParsedPosition);
            ParsedEvent newParsedEvent = newParsedEvents.get(newParsedPosition);
            return oldParsedEvent.getTitle().equals(newParsedEvent.getTitle());
        }

        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        if (isDividerPosition(oldItemPosition) && isDividerPosition(newItemPosition)) {
            return true; // Both are dividers
        } else if (isDividerPosition(oldItemPosition) || isDividerPosition(newItemPosition)) {
            return false; // One is a divider, the other is an item
        }

        int oldPosition = adjustPosition(oldItemPosition);
        int newPosition = adjustPosition(newItemPosition);

        if (oldPosition < oldEvents.size() && newPosition < newEvents.size()) {
            Event oldEvent = oldEvents.get(oldPosition);
            Event newEvent = newEvents.get(newPosition);
            return oldEvent.equals(newEvent);
        } else if (oldPosition >= oldEvents.size() && newPosition >= newEvents.size()) {
            int oldParsedPosition = oldPosition - oldEvents.size();
            int newParsedPosition = newPosition - newEvents.size();
            ParsedEvent oldParsedEvent = oldParsedEvents.get(oldParsedPosition);
            ParsedEvent newParsedEvent = newParsedEvents.get(newParsedPosition);
            return oldParsedEvent.equals(newParsedEvent);
        }

        return false;
    }

    private boolean isDividerPosition(int position) {
        return position == oldEvents.size() || position == oldEvents.size() + oldParsedEvents.size() + 1;
    }

    private int adjustPosition(int position) {
        if (position >= oldEvents.size()) {
            return position - oldEvents.size() - 1;
        }
        return position;
    }
}

