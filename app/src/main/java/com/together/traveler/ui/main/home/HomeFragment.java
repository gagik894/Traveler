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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
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
    private EventCardsAdapter eventCardsAdapter;
    private FragmentContainerView locationFragment;
    private final List<Event> eventList = new ArrayList<>();

    private final ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false))) {
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
        requestLocationAndGetNearbyEvents();
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
        eventCardsAdapter = new EventCardsAdapter(eventList, item -> {
            if (isAdded()) {
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToEventFragment(item, homeViewModel.getUserId());
                NavHostFragment.findNavController(this).navigate(action);
            }
        });

        rvCards.setAdapter(eventCardsAdapter);
        rvCards.setLayoutManager(new LinearLayoutManager(requireContext()));


        swipeRefreshLayout.setOnRefreshListener(() -> {
            Thread thread = new Thread(homeViewModel::fetch);
            thread.start();
        });

        locationFragment.setOnClickListener(v-> showSelectLocation());
        filtersButton.setOnClickListener(v-> homeViewModel.changeCategoriesVisibility());

        homeViewModel.getAllEvents().observe(getViewLifecycleOwner(), newEvents  -> {

            swipeRefreshLayout.setRefreshing(false);
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
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
                diffResult.dispatchUpdatesTo(eventCardsAdapter);
                eventList.clear();
                eventList.addAll(newEvents);
//                eventCardsAdapter.updateData(newEvents);
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
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Permission already granted
            homeViewModel.getLocationByGPS();
        } else {
            // Request permission
            requestMultiplePermissionsLauncher.launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION});
        }
    }
}
