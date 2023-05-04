package com.together.traveler.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.together.traveler.R;
import com.together.traveler.model.Event;

import java.util.List;


public class EventCardsAdapter extends RecyclerView.Adapter<EventCardsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Event item);
    }

    private List<Event> events;
    private final OnItemClickListener listener;
    private Context context;
    public EventCardsAdapter(List<Event> events, OnItemClickListener listener) {
        this.events = events;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View cardFragment = inflater.inflate(R.layout.fragment_event_card, parent, false);

        return new ViewHolder(cardFragment);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(events.get(position), listener);
        Event card = events.get(position);

        TextView tvName = holder.tvName;
        TextView tvLocation = holder.tvLocation;
        TextView tvDate = holder.tvDate;
        TextView tvTime = holder.tvTime;
        ImageView eventImage = holder.ivImage;
        ImageView userAvatar = holder.ivUserImage;
        TextView tvUserUsername = holder.tvUserUsername;

        tvName.setText(card.getTitle());
        tvLocation.setText(card.getLocation());
        tvDate.setText(String.format("%s -", card.getStartDate()));
        tvTime.setText(card.getEndDate());
        tvUserUsername.setText(card.getUser().getUsername());
        String imageUrl = String.format("https://drive.google.com/uc?export=wiew&id=%s", card.getImgId());
        String userImageUrl = String.format("https://drive.google.com/uc?export=wiew&id=%s", card.getUser().getAvatar());
        Glide.with(context).load(imageUrl).into(eventImage);
        Glide.with(context).load(userImageUrl).into(userAvatar);

        View sharedView = holder.ivImage;
        String transitionName = "event_image_transition";
        ViewCompat.setTransitionName(sharedView, transitionName);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvLocation;
        public TextView tvDate;
        public TextView tvTime;
        public ImageView ivImage;
        public ImageView ivUserImage;
        public TextView tvUserUsername;
        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.eventCardTvName);
            tvLocation = itemView.findViewById(R.id.eventCardTvLocation);
            tvDate =  itemView.findViewById(R.id.eventCardTvDate);
            tvTime =  itemView.findViewById(R.id.eventCardTvTime);
            ivImage = itemView.findViewById(R.id.eventCardIvImage);
            ivUserImage = itemView.findViewById(R.id.eventCardIvUserImage);
            tvUserUsername = itemView.findViewById(R.id.eventCardTvUsername);
        }

        public void bind(final Event item, OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}