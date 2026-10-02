package com.example.smartlibrary.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlibrary.R;
import com.example.smartlibrary.databinding.ItemIssuedBookBinding;
import com.example.smartlibrary.models.IssuedBook;
import com.example.smartlibrary.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class IssuedBookAdapter extends RecyclerView.Adapter<IssuedBookAdapter.IssuedViewHolder> {

    public interface OnIssuedBookClickListener {
        void onIssuedBookClick(IssuedBook issuedBook);
        void onAddLearningPoint(IssuedBook issuedBook);
        void onReturnBook(IssuedBook issuedBook);
    }

    private List<IssuedBook> list = new ArrayList<>();
    private final OnIssuedBookClickListener listener;

    public IssuedBookAdapter(OnIssuedBookClickListener listener) {
        this.listener = listener;
    }

    public void setIssuedBooks(List<IssuedBook> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IssuedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemIssuedBookBinding binding = ItemIssuedBookBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new IssuedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IssuedViewHolder holder, int position) {
        IssuedBook item = list.get(position);
        holder.binding.tvIssuedBookTitle.setText(item.getBookTitle());
        holder.binding.tvIssuedBookAuthor.setText("by " + item.getBookAuthor());
        holder.binding.tvIssueDate.setText("Issued: " + DateUtils.formatDate(item.getIssueDate()));

        long daysLeft = DateUtils.getDaysRemaining(item.getDueDate());
        if (daysLeft > 0) {
            holder.binding.tvDueDate.setText("Due: " + DateUtils.formatDate(item.getDueDate()) + " (" + daysLeft + " days left)");
            holder.binding.tvDueDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.accent));
            holder.binding.tvIssuedStatus.setText("ISSUED");
            holder.binding.tvIssuedStatus.setBackgroundResource(R.drawable.bg_chip_selected);
        } else {
            double fine = DateUtils.calculateFine(item.getDueDate());
            holder.binding.tvDueDate.setText("OVERDUE! Fine: $" + String.format(java.util.Locale.getDefault(), "%.2f", fine));
            holder.binding.tvDueDate.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.danger));
            holder.binding.tvIssuedStatus.setText("OVERDUE");
            holder.binding.tvIssuedStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.danger));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onIssuedBookClick(item);
        });

        holder.binding.btnAddInsight.setOnClickListener(v -> {
            if (listener != null) listener.onAddLearningPoint(item);
        });

        holder.binding.btnReturnBook.setOnClickListener(v -> {
            if (listener != null) listener.onReturnBook(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class IssuedViewHolder extends RecyclerView.ViewHolder {
        final ItemIssuedBookBinding binding;

        IssuedViewHolder(ItemIssuedBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
