package com.together.traveler.ui.event;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.together.traveler.R;
import com.together.traveler.databinding.FragmentEventBinding;
import com.together.traveler.ui.cards.EventCard;
import com.together.traveler.ui.cards.UserCard;

import java.util.Objects;

public class EventFragment extends Fragment {
    private FragmentEventBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventViewModel eventViewModel =
                new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final ImageView image = binding.eventIvImage;
        final TextView name = binding.ticketTvName;
        final TextView location = binding.eventTvLocation;
        final TextView date = binding.eventTvDate;
        final TextView time = binding.eventTvTime;
        final TextView description = binding.eventTvDescription;
        final TextView moreButton = binding.eventTvMore;
        final Button bottomButton = binding.eventBtnBottom;
        final ImageButton backButton = binding.eventIBtnBack;
        final ImageButton saveButton = binding.eventIBtnSave;
        final FragmentContainerView userCard = binding.eventUser;
        final FragmentContainerView mapCard = binding.eventMap;

        int maxLines = description.getMaxLines();

        if (getArguments() != null) {
            Log.d("asd", "onCreateView: " + getArguments());
            eventViewModel.setData(getArguments().getParcelable("cardData"));
            eventViewModel.setUserId(getArguments().getString("userId"));
        }

        eventViewModel.getData().observe(getViewLifecycleOwner(), data -> {
            String imageUrl = String.format("https://drive.google.com/uc?export=wiew&id=%s", data.getImgId());
            Log.i("asd", "onCreateView: " + imageUrl);
            Glide.with(requireContext()).load(imageUrl).into(image);
            name.setText(data.getTitle());
            location.setText(data.getLocation());
            date.setText(String.format("From %s, %s", data.getStartDate(), data.getStartTime()));
            time.setText(String.format("To %s, %s", data.getEndDate(), data.getEndTime()));
            description.setText(data.getDescription());

            if (data.isEnrolled()){
                bottomButton.setText(R.string.event_button_ticket);
            }else if(data.isUserOwned()){
                bottomButton.setText(R.string.event_button_check_tickets);
            }else{
                bottomButton.setText(R.string.event_button_enroll);
            }

            if(data.isSaved()){
                saveButton.setImageResource(R.drawable.favorite);
            }else{
                saveButton.setImageResource(R.drawable.favorite_border);
            }

            ViewTreeObserver vto = description.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int lineCount = description.getLineCount();
                    if (description.getMaxLines()==lineCount){
                        moreButton.setVisibility(View.VISIBLE);
                    }else{
                        moreButton.setVisibility(View.GONE);
                    }
                    ViewTreeObserver vto = description.getViewTreeObserver();
                    vto.removeOnGlobalLayoutListener(this);
                }
            });
        });

        saveButton.setOnClickListener(v -> eventViewModel.save());
        bottomButton.setOnClickListener(v -> {
            if (v instanceof Button) {
                Button button = (Button) v;
                String buttonText = button.getText().toString();
                if (buttonText.equals(getString(R.string.event_button_ticket))) {
                    TicketDialog dialog = new TicketDialog(requireContext(), eventViewModel.getData().getValue(), eventViewModel.getUserId());
                    dialog.show();
                }else if(buttonText.equals(getString(R.string.event_button_check_tickets))){
                    if (eventViewModel.getData().getValue() == null)
                        return;
                    Bundle bundle = new Bundle();
                    bundle.putString("_id", eventViewModel.getData().getValue().get_id());
                    NavHostFragment.findNavController(this).navigate(R.id.action_eventFragment_to_scanFragment, bundle);
                }else{
                    eventViewModel.enroll();
                }
            }
        });
        backButton.setOnClickListener(v-> NavHostFragment.findNavController(this).navigateUp());
        moreButton.setOnClickListener(v -> {
            if (description.getMaxLines() == maxLines) {
                description.setMaxLines(Integer.MAX_VALUE);
                moreButton.setText(R.string.event_read_less);
            } else {
                description.setMaxLines(maxLines);
                moreButton.setText(R.string.event_read_more);
            }
        });
        userCard.setOnClickListener(v-> {
            Bundle bundle = new Bundle();
            bundle.putString("userId", Objects.requireNonNull(eventViewModel.getData().getValue()).getUser().get_id());
            NavHostFragment.findNavController(this).navigate(R.id.action_eventFragment_to_userFragment, bundle);
        });

        mapCard.setOnClickListener(v-> {
            Toast.makeText(requireContext(), "map clicked", Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putString("location", Objects.requireNonNull(eventViewModel.getData().getValue()).getLocation());
            NavHostFragment.findNavController(this).navigate(R.id.action_eventFragment_to_mapFragment, bundle);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
