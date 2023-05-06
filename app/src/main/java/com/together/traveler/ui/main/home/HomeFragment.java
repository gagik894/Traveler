package com.together.traveler.ui.main.home;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

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

import com.together.traveler.R;
import com.together.traveler.adapter.CategoryAdapter;
import com.together.traveler.adapter.EventCardsAdapter;
import com.together.traveler.databinding.FragmentHomeBinding;
import com.together.traveler.model.Event;
import com.together.traveler.ui.event.EventFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private RecyclerView rvCards;
    private Spinner spCategories;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HomeViewModel homeViewModel;
    private ProgressBar progressBar;

    private SearchView searchView;
    private EventCardsAdapter eventCardsAdapter;
    private final List<Event> eventList = new ArrayList<>();
    private final List<String> categoryList  = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        rvCards = binding.rvHome;
        spCategories = binding.homeLvCategories;
        swipeRefreshLayout = binding.cardSwipeRefreshLayout;
        progressBar = binding.homePb;
        searchView = binding.searchView;

        eventCardsAdapter = new EventCardsAdapter(eventList, item -> {
            if (isAdded()) {
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToEventFragment(item, homeViewModel.getUserId());
                NavHostFragment.findNavController(this).navigate(action);
            }
        });


        String[] fruits = {"Apple", "Banana", "Cherry", "Date", "Elderberry", "Fig", "Grape"};

        rvCards.setAdapter(eventCardsAdapter);
        rvCards.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ArrayAdapter<>(
                requireActivity(), android.R.layout.simple_spinner_item,
                new ArrayList<>());

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spCategories.setAdapter(adapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            searchView.setIconifiedByDefault(false);
        }
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Thread thread = new Thread(homeViewModel::fetchEvents);
            thread.start();
        });

        homeViewModel.getData().observe(getViewLifecycleOwner(), newEvents  -> {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
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

            eventList.clear();
            eventList.addAll(newEvents);
            diffResult.dispatchUpdatesTo(eventCardsAdapter);
        });

        homeViewModel.getCategories().observe(getViewLifecycleOwner(), categories ->{
            ArrayList<String> categoriesList = new ArrayList<>(categories);
            adapter.clear();
            adapter.addAll(categoriesList);
            adapter.notifyDataSetChanged();
//            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
//                @Override
//                public int getOldListSize() {
//                    return categoryList.size();
//                }
//
//                @Override
//                public int getNewListSize() {
//                    return newCategories.size();
//                }
//
//                @Override
//                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//                    String oldEvent = categoryList.get(oldItemPosition);
//                    String newEvent = newCategories.get(newItemPosition);
//                    return (Objects.equals(oldEvent, newEvent));
//                }
//
//                @Override
//                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//                    String oldEvent = categoryList.get(oldItemPosition);
//                    String newEvent = newCategories.get(newItemPosition);
//                    return oldEvent.equals(newEvent);
//                }
//            });
//
//            categoryList.clear();
//            categoryList.addAll(newCategories);
//            diffResult.dispatchUpdatesTo(categoryAdapter);
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
}
