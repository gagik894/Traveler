package com.together.traveler.ui.main.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.together.traveler.R;
import com.together.traveler.adapter.EventCardsAdapter;
import com.together.traveler.databinding.FragmentUserBinding;
import com.together.traveler.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.Objects;


public class UserFragment extends Fragment {
    private FragmentUserBinding binding;
    private UserViewModel userViewModel;
    private EventCardsAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView username = binding.userTvUsername;
        final TextView rating = binding.userTvRating;
        final TextView location = binding.userTvLocation;
        final Button upcomingButton = binding.userBtnUpcoming;
        final Button savedButton = binding.userBtnSaved;
        final Button myEventsButton = binding.userBtnMyEvents;
        final ImageButton settingsButton = binding.userBtnSettings;
        final RecyclerView rvCards = binding.rvUser;
        final int textColor = savedButton.getCurrentTextColor();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getData().observe(getViewLifecycleOwner(), data ->{
            username.setText(data.getUsername());
            rating.setText(String.valueOf(data.getRating()));
            location.setText(data.getLocation());
        });

        adapter = new EventCardsAdapter(new ArrayList<>(), item -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("cardData", item);
            NavHostFragment.findNavController(this).navigate(R.id.action_userFragment_to_eventFragment, bundle);
        });
        rvCards.setAdapter(adapter);
        rvCards.setLayoutManager(new LinearLayoutManager(getContext()));

        userViewModel.getState().observe(getViewLifecycleOwner(), data ->{
            upcomingButton.setTextColor(textColor);
            savedButton.setTextColor(textColor);
            myEventsButton.setTextColor(textColor);
            switch (data){
                case 0: {
                    upcomingButton.setTextColor(ContextCompat.getColor(requireActivity().getApplicationContext(), R.color.orange));
                    adapter.updateData(Objects.requireNonNull(userViewModel.getData().getValue()).getUpcomingEvents());
                    break;
                }
                case 1: {
                    savedButton.setTextColor(ContextCompat.getColor(requireActivity().getApplicationContext(), R.color.orange));
                    adapter.updateData(Objects.requireNonNull(userViewModel.getData().getValue()).getSavedEvents());
                    break;
                }
                case 2: {
                    myEventsButton.setTextColor(ContextCompat.getColor(requireActivity().getApplicationContext(), R.color.orange));
                    adapter.updateData(Objects.requireNonNull(userViewModel.getData().getValue()).getUserEvents());
                    break;
                }
            }
        });

        upcomingButton.setOnClickListener(v -> userViewModel.setState(0));
        savedButton.setOnClickListener(v -> userViewModel.setState(1));
        myEventsButton.setOnClickListener(v -> userViewModel.setState(2));
        settingsButton.setOnClickListener(v-> {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(requireActivity(), LoginActivity.class));
            requireActivity().finish();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

