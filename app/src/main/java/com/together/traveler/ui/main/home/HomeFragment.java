package com.together.traveler.ui.main.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.together.traveler.R;
import com.together.traveler.adapter.EventCardsAdapter;
import com.together.traveler.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private RecyclerView rvCards;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        rvCards = binding.rvHome;
        swipeRefreshLayout = binding.cardSwipeRefreshLayout;

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Thread thread = new Thread(homeViewModel::getEvents);
            thread.start();
        });

        homeViewModel.getData().observe(getViewLifecycleOwner(), events ->{
            swipeRefreshLayout.setRefreshing(false);
            EventCardsAdapter adapter = new EventCardsAdapter(events, item ->{
                Bundle bundle = new Bundle();
                bundle.putParcelable("cardData", item);
                NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_eventFragment, bundle);
            });
            rvCards.setAdapter(adapter);
            rvCards.setLayoutManager(new LinearLayoutManager(requireContext()));
        } );
        return root;
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
