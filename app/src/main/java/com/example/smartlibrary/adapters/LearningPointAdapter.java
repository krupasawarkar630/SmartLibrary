package com.example.smartlibrary.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlibrary.databinding.ItemLearningPointBinding;
import com.example.smartlibrary.models.LearningPoint;
import com.example.smartlibrary.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class LearningPointAdapter extends RecyclerView.Adapter<LearningPointAdapter.LpViewHolder> {

    private List<LearningPoint> list = new ArrayList<>();

    public void setLearningPoints(List<LearningPoint> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLearningPointBinding binding = ItemLearningPointBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LpViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LpViewHolder holder, int position) {
        LearningPoint item = list.get(position);
        holder.binding.tvLpBookTitle.setText(item.getBookTitle());
        holder.binding.tvLpInsight.setText(item.getInsightText());
        holder.binding.ratingBarLp.setRating(item.getRating());
        holder.binding.tvLpDate.setText("Saved: " + DateUtils.formatDate(item.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class LpViewHolder extends RecyclerView.ViewHolder {
        final ItemLearningPointBinding binding;

        LpViewHolder(ItemLearningPointBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
