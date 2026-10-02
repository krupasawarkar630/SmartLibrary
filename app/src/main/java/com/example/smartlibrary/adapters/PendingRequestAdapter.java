package com.example.smartlibrary.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlibrary.R;
import com.example.smartlibrary.databinding.ItemPendingRequestBinding;
import com.example.smartlibrary.models.BookRequest;
import com.example.smartlibrary.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestAdapter.RequestViewHolder> {

    public interface OnRequestActionListener {
        void onShowQrClick(BookRequest request);
    }

    private List<BookRequest> requests = new ArrayList<>();
    private final OnRequestActionListener listener;

    public PendingRequestAdapter(OnRequestActionListener listener) {
        this.listener = listener;
    }

    public void setRequests(List<BookRequest> newRequests) {
        this.requests = newRequests != null ? newRequests : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPendingRequestBinding binding = ItemPendingRequestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        BookRequest req = requests.get(position);
        holder.binding.tvReqBookTitle.setText(req.getBookTitle());
        holder.binding.tvReqBookAuthor.setText("by " + req.getBookAuthor());
        holder.binding.tvReqDate.setText("Requested on: " + DateUtils.formatDate(req.getRequestedAt()));

        String status = req.getStatus() != null ? req.getStatus().toUpperCase() : "PENDING";
        holder.binding.tvReqStatus.setText(status);

        if ("APPROVED".equalsIgnoreCase(status)) {
            holder.binding.tvReqStatus.setBackgroundResource(R.drawable.bg_chip_selected);
            holder.binding.btnShowQr.setVisibility(View.VISIBLE);
            holder.binding.btnShowQr.setOnClickListener(v -> {
                if (listener != null) listener.onShowQrClick(req);
            });
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            holder.binding.tvReqStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.danger));
            holder.binding.btnShowQr.setVisibility(View.GONE);
        } else {
            holder.binding.tvReqStatus.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.warning));
            holder.binding.btnShowQr.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        final ItemPendingRequestBinding binding;

        RequestViewHolder(ItemPendingRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
