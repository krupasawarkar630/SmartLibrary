package com.example.smartlibrary.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlibrary.R;
import com.example.smartlibrary.databinding.ItemHistoryBinding;
import com.example.smartlibrary.models.IssuedBook;
import com.example.smartlibrary.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<IssuedBook> list = new ArrayList<>();

    public void setHistoryList(List<IssuedBook> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryBinding binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        IssuedBook item = list.get(position);
        holder.binding.tvHistBookTitle.setText(item.getBookTitle());
        holder.binding.tvHistBookAuthor.setText("by " + item.getBookAuthor());
        holder.binding.tvHistIssueDate.setText("Issued: " + DateUtils.formatDate(item.getIssueDate()));

        String status = item.getStatus() != null ? item.getStatus().toUpperCase() : "RETURNED";
        holder.binding.tvHistStatus.setText(status);

        if ("RETURNED".equalsIgnoreCase(status)) {
            holder.binding.tvHistStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.success));
            holder.binding.tvHistReturnDate.setText("Returned: " + DateUtils.formatDate(item.getReturnDate()));
            holder.binding.tvHistReturnDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.success));
        } else if ("OVERDUE".equalsIgnoreCase(status)) {
            holder.binding.tvHistStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.danger));
            holder.binding.tvHistReturnDate.setText("Overdue!");
            holder.binding.tvHistReturnDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.danger));
        } else {
            holder.binding.tvHistStatus.setBackgroundResource(R.drawable.bg_chip_selected);
            holder.binding.tvHistReturnDate.setText("Due: " + DateUtils.formatDate(item.getDueDate()));
            holder.binding.tvHistReturnDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.accent));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        final ItemHistoryBinding binding;

        HistoryViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
