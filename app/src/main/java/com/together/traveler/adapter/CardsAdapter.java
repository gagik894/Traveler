package com.together.traveler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.together.traveler.R;
import com.together.traveler.model.Card;

import java.util.List;


public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Card item);
    }
    private final List<Card> cards;
    private final OnItemClickListener listener;

    public CardsAdapter(List<Card> cards, OnItemClickListener listener) {
        this.cards = cards;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View cardFragment = inflater.inflate(R.layout.fragment_event_card, parent, false);

        return new ViewHolder(cardFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(cards.get(position), listener);
        Card card = cards.get(position);

        TextView tvName = holder.tvName;
        TextView tvLocation = holder.tvLocation;
        TextView tvDate = holder.tvDate;
        TextView tvTime = holder.tvTime;

        tvName.setText(card.getName());
        tvLocation.setText((CharSequence) card.getLocation());
        tvDate.setText(String.valueOf(card.getDate()));
        tvTime.setText(String.valueOf(card.getTime()));

    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvLocation;
        public TextView tvDate;
        public TextView tvTime;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.cardTvEventName);
            tvLocation = itemView.findViewById(R.id.cardTvLocation);
            tvDate =  itemView.findViewById(R.id.cardTvDate);
            tvTime =  itemView.findViewById(R.id.cardTvTime);

        }

        public void bind(final Card item, OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}