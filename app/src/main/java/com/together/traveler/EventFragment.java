package com.together.traveler;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.together.traveler.model.Card;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventFragment newInstance(String param1, String param2) {
        EventFragment fragment = new EventFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView description = requireView().findViewById(R.id.eventTvDescription);
        TextView name = requireView().findViewById(R.id.eventTvName);
        TextView location = requireView().findViewById(R.id.eventTvLocation);
        TextView date = requireView().findViewById(R.id.eventTvDate);
        TextView time = requireView().findViewById(R.id.eventTvTime);
        TextView buttonMore = requireView().findViewById(R.id.eventTvMore);
        int maxLines = description.getMaxLines();
        Card data = getArguments().getParcelable("cardData");

        name.setText(data.getName());
        location.setText(String.valueOf(data.getLocation()));
        date.setText(String.valueOf(data.getDate()));
        time.setText(String.valueOf(data.getTime()));


        Toast.makeText(requireContext(), data.getName(), Toast.LENGTH_SHORT).show();
        buttonMore.setOnClickListener(v -> {
            if (description.getMaxLines() == maxLines) {
                description.setMaxLines(Integer.MAX_VALUE);
                buttonMore.setText(R.string.read_less);
            } else {
                description.setMaxLines(maxLines);
                buttonMore.setText(R.string.event_read_more);
            }
        });
    }
}