package com.example.smartlibrary.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlibrary.R;
import com.example.smartlibrary.models.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    public interface OnTransactionActionListener {
        void onReturnClick(Transaction transaction);
        void onQRPassClick(Transaction transaction);
    }

    private List<Transaction> transactions;
    private final OnTransactionActionListener listener;

    public TransactionAdapter(List<Transaction> transactions, OnTransactionActionListener listener) {
        this.transactions = transactions;
        this.listener = listener;
    }

    public void updateList(List<Transaction> newTransactions) {
        this.transactions = newTransactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction t = transactions.get(position);
        holder.bind(t, listener);
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvStatus, tvIssueDate, tvReturnDate;
        Button btnReturn, btnViewQRPass;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTransTitle);
            tvAuthor = itemView.findViewById(R.id.tvTransAuthor);
            tvStatus = itemView.findViewById(R.id.tvTransStatus);
            tvIssueDate = itemView.findViewById(R.id.tvIssueDate);
            tvReturnDate = itemView.findViewById(R.id.tvReturnDate);
            btnReturn = itemView.findViewById(R.id.btnReturnBook);
            btnViewQRPass = itemView.findViewById(R.id.btnViewQRPass);
        }

        public void bind(final Transaction t, final OnTransactionActionListener listener) {
            tvTitle.setText(t.getBookTitle());
            tvAuthor.setText("By " + t.getBookAuthor());
            tvIssueDate.setText(t.getIssueDate());
            tvReturnDate.setText(t.getReturnDate());

            if ("borrowed".equalsIgnoreCase(t.getStatus())) {
                tvStatus.setText("BORROWED");
                tvStatus.setBackgroundColor(Color.parseColor("#0EA5E9")); // Blue accent
                btnReturn.setVisibility(View.VISIBLE);
                btnViewQRPass.setVisibility(View.VISIBLE);
            } else if ("returned".equalsIgnoreCase(t.getStatus())) {
                tvStatus.setText("RETURNED");
                tvStatus.setBackgroundColor(Color.parseColor("#10B981")); // Green success
                btnReturn.setVisibility(View.GONE);
                btnViewQRPass.setVisibility(View.GONE);
            } else {
                tvStatus.setText("OVERDUE");
                tvStatus.setBackgroundColor(Color.parseColor("#EF4444")); // Red warning
                btnReturn.setVisibility(View.VISIBLE);
                btnViewQRPass.setVisibility(View.VISIBLE);
            }

            btnReturn.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReturnClick(t);
                }
            });

            btnViewQRPass.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQRPassClick(t);
                }
            });
        }
    }
}
