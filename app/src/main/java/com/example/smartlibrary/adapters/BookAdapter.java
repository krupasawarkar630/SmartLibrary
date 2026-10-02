package com.example.smartlibrary.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartlibrary.R;
import com.example.smartlibrary.databinding.ItemBookBinding;
import com.example.smartlibrary.models.Book;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    private List<Book> books = new ArrayList<>();
    private final OnBookClickListener listener;

    public BookAdapter(OnBookClickListener listener) {
        this.listener = listener;
    }

    public void setBooks(List<Book> newBooks) {
        this.books = newBooks != null ? newBooks : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookBinding binding = ItemBookBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        
        if (parent.getLayoutManager() instanceof androidx.recyclerview.widget.GridLayoutManager) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.getRoot().getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.setMargins(16, 16, 16, 40);
            binding.getRoot().setLayoutParams(params);
        }
        
        return new BookViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.binding.tvBookTitle.setText(book.getTitle());
        holder.binding.tvBookAuthor.setText("by " + book.getAuthor());
        holder.binding.tvBookCategory.setText(book.getCategory());
        holder.binding.tvBookRating.setText(String.format(java.util.Locale.getDefault(), "%.1f", book.getRating()));

        if (book.getAvailableCopies() > 0) {
            holder.binding.tvBookAvailability.setText(book.getAvailableCopies() + " Available");
            holder.binding.tvBookAvailability.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.success));
        } else {
            holder.binding.tvBookAvailability.setText("Currently unavailable");
            holder.binding.tvBookAvailability.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.danger));
        }

        if (book.getEffectiveCoverImage() != null && !book.getEffectiveCoverImage().trim().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(book.getEffectiveCoverImage())
                    .placeholder(R.drawable.ic_book)
                    .error(R.drawable.ic_book)
                    .into(holder.binding.imgBookCover);
        } else {
            holder.binding.imgBookCover.setImageResource(R.drawable.ic_book);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onBookClick(book);
        });

        holder.binding.btnViewDetails.setOnClickListener(v -> {
            if (listener != null) listener.onBookClick(book);
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        final ItemBookBinding binding;

        BookViewHolder(ItemBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
