package com.together.traveler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.together.traveler.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<String> categories;
    private final CategoryAdapter.OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(String item);
    }
    public CategoryAdapter(List<String> categoryList,  CategoryAdapter.OnItemClickListener listener) {
        this.categories = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.mCategoryName.setText(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView mCategoryName;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            mCategoryName = itemView.findViewById(R.id.categoryTvName);
        }
    }
}