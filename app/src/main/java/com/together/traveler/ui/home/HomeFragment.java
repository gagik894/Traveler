package com.together.traveler.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.together.traveler.R;
import com.together.traveler.adapter.EventCardsAdapter;
import com.together.traveler.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        RecyclerView rvCards = binding.rvHome;

        homeViewModel.getData().observe(getViewLifecycleOwner(), events ->{
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
}
