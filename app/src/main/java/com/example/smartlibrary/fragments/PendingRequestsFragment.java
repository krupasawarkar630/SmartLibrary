package com.example.smartlibrary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartlibrary.activities.QrCodeActivity;
import com.example.smartlibrary.adapters.PendingRequestAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.smartlibrary.databinding.FragmentPendingRequestsBinding;
import com.example.smartlibrary.models.BookRequest;
import com.example.smartlibrary.utils.SessionManager;

import java.util.List;

public class PendingRequestsFragment extends Fragment {

    private FragmentPendingRequestsBinding binding;
    private DatabaseReference mDatabase;
    private SessionManager sessionManager;
    private PendingRequestAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPendingRequestsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference("bookRequests");
        sessionManager = new SessionManager(requireContext());

        adapter = new PendingRequestAdapter(request -> {
            Intent intent = new Intent(requireContext(), QrCodeActivity.class);
            intent.putExtra(QrCodeActivity.EXTRA_REQUEST_ID, request.getRequestId());
            startActivity(intent);
        });
        binding.rvPendingRequests.setAdapter(adapter);

        loadRequests();
    }

    private void loadRequests() {
        String userId = sessionManager.getUserSession() != null ? sessionManager.getUserSession().getUserId() : "user_101";
        
        mDatabase.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;
                List<BookRequest> list = new java.util.ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    BookRequest req = data.getValue(BookRequest.class);
                    if (req != null) {
                        list.add(req);
                    }
                }
                
                if (list.isEmpty()) {
                    binding.layoutEmpty.emptyStateContainer.setVisibility(View.VISIBLE);
                    binding.layoutEmpty.tvEmptyTitle.setText("No Requests Found");
                    binding.layoutEmpty.tvEmptyDescription.setText("You have no active or pending book requests.");
                    binding.rvPendingRequests.setVisibility(View.GONE);
                } else {
                    binding.layoutEmpty.emptyStateContainer.setVisibility(View.GONE);
                    binding.rvPendingRequests.setVisibility(View.VISIBLE);
                    adapter.setRequests(list);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
