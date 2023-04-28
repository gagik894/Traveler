package com.together.traveler.ui.main.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private RecyclerView rvCards;
    private RecyclerView rvCategories;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HomeViewModel homeViewModel;
    private ProgressBar progressBar;
    private EventCardsAdapter eventCardsAdapter;
    private CategoryAdapter categoryAdapter;
    private final List<Event> eventList = new ArrayList<>();
    private final List<String> categoryList  = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        rvCards = binding.rvHome;
        rvCategories = binding.homeRvCategories;
        swipeRefreshLayout = binding.cardSwipeRefreshLayout;
        progressBar = binding.homePb;

        eventCardsAdapter = new EventCardsAdapter(eventList, item -> {
            if (isAdded()) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("cardData", item);
                bundle.putString("userId", homeViewModel.getUserId());
                NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_eventFragment, bundle);
            }
        });
        categoryAdapter = new CategoryAdapter(categoryList, item -> Toast.makeText(requireContext(), item, Toast.LENGTH_SHORT).show());
        rvCategories.setAdapter(categoryAdapter);
        rvCards.setAdapter(eventCardsAdapter);
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCards.setLayoutManager(new LinearLayoutManager(requireContext()));
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

        homeViewModel.getCategories().observe(getViewLifecycleOwner(), newCategories ->{
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return categoryList.size();
                }

                @Override
                public int getNewListSize() {
                    return newCategories.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    String oldEvent = categoryList.get(oldItemPosition);
                    String newEvent = newCategories.get(newItemPosition);
                    return (Objects.equals(oldEvent, newEvent));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    String oldEvent = categoryList.get(oldItemPosition);
                    String newEvent = newCategories.get(newItemPosition);
                    return oldEvent.equals(newEvent);
                }
            });

            categoryList.clear();
            categoryList.addAll(newCategories);
            diffResult.dispatchUpdatesTo(categoryAdapter);
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
