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
    private final List<Card> cards;

    public CardsAdapter(List<Card> cards) {
        this.cards = cards;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View cardFragment = inflater.inflate(R.layout.fragment_card, parent, false);

        return new ViewHolder(cardFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

            tvName = itemView.findViewById(R.id.tvCardEventName);
            tvLocation = itemView.findViewById(R.id.tvCardLocation);
            tvDate =  itemView.findViewById(R.id.tvCardDate);
            tvTime =  itemView.findViewById(R.id.tvCardTime);

        }
    }
}