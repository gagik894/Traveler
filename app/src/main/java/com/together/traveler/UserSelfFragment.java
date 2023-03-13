package com.together.traveler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.together.traveler.adapter.CardsAdapter;
import com.together.traveler.model.Card;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserSelfFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSelfFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rvCards;
    private Button upcomingButton;
    private Button savedButton;
    private Button myEventsButton;

    public UserSelfFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserSelfFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserSelfFragment newInstance(String param1, String param2) {
        UserSelfFragment fragment = new UserSelfFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    ArrayList<Card> cards = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View RootView = inflater.inflate(R.layout.fragment_user_self, container, false);
        rvCards = RootView.findViewById(R.id.rvUser);
        upcomingButton = RootView.findViewById(R.id.btnUserUpcoming);
        savedButton = RootView.findViewById(R.id.btnUserSaved);
        myEventsButton = RootView.findViewById(R.id.btnUserMyEvents);
        int textColor = savedButton.getCurrentTextColor();
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
        return RootView;
    }

    private void showCard(int n) {
        cards = Card.createCardList(n);
        CardsAdapter adapter = new CardsAdapter(cards);
        rvCards.setAdapter(adapter);
        rvCards.setLayoutManager(new LinearLayoutManager(getContext()));

    }

}