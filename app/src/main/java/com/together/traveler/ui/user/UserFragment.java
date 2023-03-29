package com.together.traveler.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.together.traveler.R;
import com.together.traveler.adapter.CardsAdapter;
import com.together.traveler.databinding.FragmentUserBinding;
import com.together.traveler.model.Card;

import java.util.ArrayList;


public class UserFragment extends Fragment {
    private FragmentUserBinding binding;
    private RecyclerView rvCards;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UserViewModel userViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView username = binding.userTvUsername;
        final Button upcomingButton = binding.userBtnUpcoming;
        final Button savedButton = binding.userBtnSaved;
        final Button myEventsButton = binding.userBtnMyEvents;
        final int textColor = savedButton.getCurrentTextColor();

        rvCards = binding.rvUser;
        userViewModel.getData().observe(getViewLifecycleOwner(), username::setText);
        showCard(100);

        upcomingButton.setOnClickListener(v -> {
            upcomingButton.setTextColor(ContextCompat.getColor(requireActivity().getApplicationContext(),(R.color.orange)));
            savedButton.setTextColor(textColor);
            myEventsButton.setTextColor(textColor);
            showCard(100);
        });
        savedButton.setOnClickListener(v -> {
            savedButton.setTextColor(ContextCompat.getColor(requireActivity().getApplicationContext(),(R.color.orange)));
            upcomingButton.setTextColor(textColor);
            myEventsButton.setTextColor(textColor);
            showCard(5);
        });
        myEventsButton.setOnClickListener(v -> {
            myEventsButton.setTextColor(ContextCompat.getColor(requireActivity().getApplicationContext(),(R.color.orange)));
            upcomingButton.setTextColor(textColor);
            savedButton.setTextColor(textColor);
            showCard(2);
        });

        return root;
    }

    private void showCard(int n) {
        ArrayList<Card> cards = Card.createCardList(n);
        CardsAdapter adapter = new CardsAdapter(cards, item -> Toast.makeText(requireContext(), "Item Clicked", Toast.LENGTH_LONG).show());
        rvCards.setAdapter(adapter);
        rvCards.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

