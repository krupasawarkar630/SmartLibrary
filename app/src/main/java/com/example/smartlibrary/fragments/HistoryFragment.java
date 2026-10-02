package com.example.smartlibrary.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.adapters.HistoryAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.smartlibrary.databinding.FragmentHistoryBinding;
import com.example.smartlibrary.models.IssuedBook;
import com.example.smartlibrary.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private DatabaseReference mDatabase;
    private SessionManager sessionManager;
    private HistoryAdapter adapter;
    private List<IssuedBook> fullHistoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference("issuedBooks");
        sessionManager = new SessionManager(requireContext());

        adapter = new HistoryAdapter();
        binding.rvHistory.setAdapter(adapter);

        binding.tabLayoutHistory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterHistory(tab.getPosition());
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadHistory();
    }

    private void loadHistory() {
        String userId = sessionManager.getUserSession() != null ? sessionManager.getUserSession().getUserId() : "user_101";
        mDatabase.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;
                fullHistoryList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    IssuedBook ib = data.getValue(IssuedBook.class);
                    if (ib != null) {
                        fullHistoryList.add(ib);
                    }
                }
                filterHistory(binding.tabLayoutHistory.getSelectedTabPosition());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void filterHistory(int tabPosition) {
        List<IssuedBook> filtered = new ArrayList<>();
        for (IssuedBook b : fullHistoryList) {
            if (tabPosition == 0) { // All
                filtered.add(b);
            } else if (tabPosition == 1 && "RETURNED".equalsIgnoreCase(b.getStatus())) { // Returned
                filtered.add(b);
            } else if (tabPosition == 2 && "OVERDUE".equalsIgnoreCase(b.getStatus())) { // Overdue
                filtered.add(b);
            }
        }

        if (filtered.isEmpty()) {
            binding.layoutEmpty.emptyStateContainer.setVisibility(View.VISIBLE);
            binding.layoutEmpty.tvEmptyTitle.setText("No History Found");
            binding.layoutEmpty.tvEmptyDescription.setText("No records match the selected tab filter.");
            binding.rvHistory.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.emptyStateContainer.setVisibility(View.GONE);
            binding.rvHistory.setVisibility(View.VISIBLE);
            adapter.setHistoryList(filtered);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
