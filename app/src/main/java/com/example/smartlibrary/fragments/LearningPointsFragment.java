package com.example.smartlibrary.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.adapters.LearningPointAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.smartlibrary.databinding.FragmentLearningPointsBinding;
import com.example.smartlibrary.models.LearningPoint;
import com.example.smartlibrary.utils.SessionManager;

import java.util.List;

public class LearningPointsFragment extends Fragment {

    private FragmentLearningPointsBinding binding;
    private DatabaseReference mDatabase;
    private SessionManager sessionManager;
    private LearningPointAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLearningPointsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference("learningPoints");
        sessionManager = new SessionManager(requireContext());

        adapter = new LearningPointAdapter();
        binding.rvLearningPoints.setAdapter(adapter);

        loadLearningPoints();
    }

    private void loadLearningPoints() {
        String userId = sessionManager.getUserSession() != null ? sessionManager.getUserSession().getUserId() : "user_101";
        mDatabase.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;
                List<LearningPoint> list = new java.util.ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    LearningPoint lp = data.getValue(LearningPoint.class);
                    if (lp != null) {
                        list.add(lp);
                    }
                }
                
                if (list.isEmpty()) {
                    binding.layoutEmpty.emptyStateContainer.setVisibility(View.VISIBLE);
                    binding.layoutEmpty.tvEmptyTitle.setText("No Learning Points Saved");
                    binding.layoutEmpty.tvEmptyDescription.setText("Add key takeaways from your borrowed books to view them here.");
                    binding.rvLearningPoints.setVisibility(View.GONE);
                } else {
                    binding.layoutEmpty.emptyStateContainer.setVisibility(View.GONE);
                    binding.rvLearningPoints.setVisibility(View.VISIBLE);
                    adapter.setLearningPoints(list);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLearningPoints();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
