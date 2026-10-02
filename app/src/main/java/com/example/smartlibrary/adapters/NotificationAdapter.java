package com.example.smartlibrary.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlibrary.databinding.ItemNotificationBinding;
import com.example.smartlibrary.models.NotificationItem;
import com.example.smartlibrary.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

    private List<NotificationItem> list = new ArrayList<>();

    public void setNotifications(List<NotificationItem> newList) {
        this.list = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NotifViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        NotificationItem item = list.get(position);
        holder.binding.tvNotifTitle.setText(item.getTitle());
        holder.binding.tvNotifMessage.setText(item.getMessage());
        holder.binding.tvNotifDate.setText(DateUtils.formatDateTime(item.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class NotifViewHolder extends RecyclerView.ViewHolder {
        final ItemNotificationBinding binding;

        NotifViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
