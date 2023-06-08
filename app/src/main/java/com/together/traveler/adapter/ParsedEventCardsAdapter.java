package com.together.traveler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.together.traveler.R;
import com.together.traveler.model.Event;
import com.together.traveler.model.ParsedEvent;

import java.util.List;


public class ParsedEventCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_EVENT = 0;
    private static final int VIEW_TYPE_PARSED = 1;
    private static final int VIEW_TYPE_DIVIDER = 2;

    private List<Event> events;
    private List<ParsedEvent> parsedEvents;
    private OnItemClickListener onItemClickListener;
    private OnParsedItemClickListener onParsedItemClickListener;
    private Context context;

    public ParsedEventCardsAdapter(List<Event> events, List<ParsedEvent> parsedEvents, OnItemClickListener itemClickListener, OnParsedItemClickListener parsedItemClickListener) {
        this.events = events;
        this.parsedEvents = parsedEvents;
        this.onItemClickListener = itemClickListener;
        this.onParsedItemClickListener = parsedItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < events.size()) {
            return VIEW_TYPE_EVENT;
        } else if (position == events.size()) {
            return VIEW_TYPE_DIVIDER;
        } else {
            return VIEW_TYPE_PARSED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_TYPE_EVENT) {
            // Inflate event item layout
            View cardItemView = inflater.inflate(R.layout.fragment_event_card, parent, false);
            return new EventViewHolder(cardItemView);
        } else if (viewType == VIEW_TYPE_PARSED) {
            // Inflate parsed event item layout
            View parsedItemView = inflater.inflate(R.layout.fragment_event_card, parent, false);
            return new ParsedEventViewHolder(parsedItemView);
        } else if (viewType == VIEW_TYPE_DIVIDER) {
            // Inflate divider item layout
            View dividerItemView = inflater.inflate(R.layout.card_home_divider, parent, false);
            return new DividerViewHolder(dividerItemView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EventViewHolder) {
            EventViewHolder eventViewHolder = (EventViewHolder) holder;
            eventViewHolder.populate(events.get(position), context);
            eventViewHolder.bind(events.get(position), onItemClickListener);
        } else if (holder instanceof ParsedEventViewHolder) {
            int parsedPosition = position - events.size() - 1; // Subtract 1 for the divider
            ParsedEventViewHolder parsedEventViewHolder = (ParsedEventViewHolder) holder;
            parsedEventViewHolder.populate(parsedEvents.get(parsedPosition), context);
            parsedEventViewHolder.bind(parsedEvents.get(parsedPosition), onParsedItemClickListener);
        }
    }

    @Override
    public int getItemCount() {
        // Add 1 for the divider
        return events.size() + parsedEvents.size() + 1;
    }


    private static class DividerViewHolder extends RecyclerView.ViewHolder {
        public DividerViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvLocation;
        public TextView tvDate;
        public TextView tvTime;
        public ImageView ivImage;
        public ImageView ivUserImage;
        public TextView tvUserUsername;

        public EventViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.eventCardTvName);
            tvLocation = itemView.findViewById(R.id.eventCardTvLocation);
            tvDate = itemView.findViewById(R.id.eventCardTvDate);
            tvTime = itemView.findViewById(R.id.eventCardTvTime);
            ivImage = itemView.findViewById(R.id.eventCardIvImage);
            ivUserImage = itemView.findViewById(R.id.eventCardIvUserImage);
            tvUserUsername = itemView.findViewById(R.id.eventCardTvUsername);
        }

        public void populate(Event event, Context ctx) {
            tvName.setText(event.getTitle());
            tvLocation.setText(event.getLocation());
            tvDate.setText(String.format("%s -", event.getStartDate()));
            tvTime.setText(event.getEndDate());
            tvUserUsername.setText(event.getUser().getUsername());
            String imageUrl = String.format("https://drive.google.com/uc?export=wiew&id=%s", event.getImgId());
            String userImageUrl = String.format("https://drive.google.com/uc?export=wiew&id=%s", event.getUser().getAvatar());
            Glide.with(ctx).load(imageUrl).into(ivImage);
            Glide.with(ctx).load(userImageUrl).into(ivUserImage);
        }
        public void bind(final Event item, ParsedEventCardsAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

    private static class ParsedEventViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvLocation;
        public TextView tvDate;
        public TextView tvTime;
        public ImageView ivImage;
        public ImageView ivUserImage;
        public TextView tvUserUsername;

        public ParsedEventViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.eventCardTvName);
            tvLocation = itemView.findViewById(R.id.eventCardTvLocation);
            tvDate = itemView.findViewById(R.id.eventCardTvDate);
            tvTime = itemView.findViewById(R.id.eventCardTvTime);
            ivImage = itemView.findViewById(R.id.eventCardIvImage);
            ivUserImage = itemView.findViewById(R.id.eventCardIvUserImage);
            tvUserUsername = itemView.findViewById(R.id.eventCardTvUsername);
        }

        public void populate(ParsedEvent event, Context ctx) {
            tvName.setText(event.getTitle());
            tvLocation.setText(event.getLocation());
            tvDate.setText(String.format("%s -", event.getStartDate()));
            tvTime.setText(event.getEndDate());
            tvUserUsername.setText(event.getUsername());
            String imageUrl = event.getImg_url();
            String userImageUrl = event.getUserAvatar();
            Glide.with(ctx).load(imageUrl).into(ivImage);
            Glide.with(ctx).load(userImageUrl).into(ivUserImage);
        }
        public void bind(final ParsedEvent item, ParsedEventCardsAdapter.OnParsedItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Event item);
    }

    public interface OnParsedItemClickListener {
        void onItemClick(ParsedEvent item);
    }
}

