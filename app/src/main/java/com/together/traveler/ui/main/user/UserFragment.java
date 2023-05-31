package com.together.traveler.ui.main.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.together.traveler.R;
import com.together.traveler.SettingsActivity;
import com.together.traveler.adapter.EventCardsAdapter;
import com.together.traveler.databinding.FragmentUserBinding;
import com.together.traveler.model.Event;

import java.util.ArrayList;
import java.util.List;


public class UserFragment extends Fragment {
    private FragmentUserBinding binding;
    private UserViewModel userViewModel;
    private EventCardsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressBar;
    private RecyclerView rvCards;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final List<Event> eventList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        rvCards = binding.rvUser;
        swipeRefreshLayout = binding.userSwipeRefresh;
        progressBar = binding.userPb;

        if (getArguments() != null) {
            if (getArguments().getString("userId") != null) {
                userViewModel.setUserId(getArguments().getString("userId"));
                BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.nvMain);
                MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.homeFragment);
                menuItem.setChecked(true);
            }else{
                userViewModel.setUserId("self");
            }
        }

        adapter = new EventCardsAdapter(eventList, item -> {
            NavDirections action = UserFragmentDirections.actionUserFragmentToEventFragment(item, userViewModel.getUserId());
            NavHostFragment.findNavController(this).navigate(action);
        });
        rvCards.setAdapter(adapter);
        rvCards.setLayoutManager(new LinearLayoutManager(requireActivity()));

        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView username = binding.userTvUsername;
        final TextView rating = binding.userTvRating;
        final Button upcomingButton = binding.userBtnUpcoming;
        final Button savedButton = binding.userBtnSaved;
        final Button myEventsButton = binding.userBtnMyEvents;
        final ImageButton settingsButton = binding.userBtnSettings;
        final LinearLayout eventsLl = binding.userLlEvents;
        final TextView eventsText = binding.userTvEvents;
        final int textColor = savedButton.getCurrentTextColor();

        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(requireActivity());
        }

        userViewModel.isSelfPage().observe(getViewLifecycleOwner(), isSelfPage->{
            if (isSelfPage){
                settingsButton.setVisibility(View.VISIBLE);
                eventsLl.setVisibility(View.VISIBLE);
                eventsText.setVisibility(View.GONE);
            }else{
                settingsButton.setVisibility(View.GONE);
                eventsLl.setVisibility(View.GONE);
                eventsText.setVisibility(View.VISIBLE);
            }
        });

        userViewModel.getUser().observe(getViewLifecycleOwner(), data ->{
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            username.setText(data.getUsername());
            rating.setText(String.valueOf(data.getRating()));
        });

        int orangeColor = ContextCompat.getColor(requireActivity(), R.color.orange);
        userViewModel.getState().observe(getViewLifecycleOwner(), state -> {
            upcomingButton.setTextColor(textColor);
            savedButton.setTextColor(textColor);
            myEventsButton.setTextColor(textColor);

            List<Event> newEvents = new ArrayList<>();
            if (state != null &&  userViewModel.getUser().getValue() != null) {
                switch (state) {
                    case 0:
                        upcomingButton.setTextColor(orangeColor);
                        newEvents = userViewModel.getUser().getValue().getUpcomingEvents();
                        break;
                    case 1:
                        savedButton.setTextColor(orangeColor);
                        newEvents = userViewModel.getUser().getValue().getSavedEvents();
                        break;
                    case 2:
                        myEventsButton.setTextColor(orangeColor);
                        newEvents = userViewModel.getUser().getValue().getUserEvents();
                        break;
                }
            }

            List<Event> finalNewEvents = newEvents;
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return eventList.size();
                }

                @Override
                public int getNewListSize() {
                    return finalNewEvents.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    Event oldEvent = eventList.get(oldItemPosition);
                    Event newEvent = finalNewEvents.get(newItemPosition);
                    return oldEvent.get_id().equals(newEvent.get_id());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Event oldEvent = eventList.get(oldItemPosition);
                    Event newEvent = finalNewEvents.get(newItemPosition);
                    return oldEvent.equals(newEvent);
                }
            });

            eventList.clear();
            eventList.addAll(finalNewEvents);
            diffResult.dispatchUpdatesTo(adapter);
        });


        upcomingButton.setOnClickListener(v -> userViewModel.setState(0));
        savedButton.setOnClickListener(v -> userViewModel.setState(1));
        myEventsButton.setOnClickListener(v -> userViewModel.setState(2));
        settingsButton.setOnClickListener(v-> {
            Intent switchActivityIntent = new Intent(requireActivity(), SettingsActivity.class);
            switchActivityIntent.putExtra("admin", userViewModel.getUser().getValue() != null && userViewModel.getUser().getValue().isAdmin());
            startActivity(switchActivityIntent);
        });
        swipeRefreshLayout.setOnRefreshListener(() -> userViewModel.fetchUser());

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

