package com.example.smartlibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.smartlibrary.ai.AiMessageAdapter;
import com.example.smartlibrary.ai.AiViewModel;
import com.example.smartlibrary.databinding.ActivityAiAssistantBinding;

import java.util.ArrayList;

public class AiAssistantActivity extends AppCompatActivity {

    private ActivityAiAssistantBinding binding;
    private AiViewModel aiViewModel;
    private AiMessageAdapter adapter;
    private static final int SPEECH_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiAssistantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        aiViewModel = new ViewModelProvider(this).get(AiViewModel.class);
        
        adapter = new AiMessageAdapter(this);
        binding.recyclerAiMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerAiMessages.setAdapter(adapter);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSend.setOnClickListener(v -> {
            String text = binding.editAiInput.getText().toString();
            if (!text.trim().isEmpty()) {
                aiViewModel.sendMessage(text);
                binding.editAiInput.setText("");
            }
        });

        binding.btnVoice.setOnClickListener(v -> startVoiceInput());

        aiViewModel.getMessages().observe(this, messages -> {
            adapter.setMessages(new ArrayList<>(messages));
            if (messages.size() > 0) {
                binding.recyclerAiMessages.smoothScrollToPosition(messages.size() - 1);
            }
        });

        aiViewModel.getIsLoading().observe(this, isLoading -> {
            binding.layoutLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        // Handle context from intent (e.g. from book details)
        String contextData = getIntent().getStringExtra("BOOK_CONTEXT");
        if (contextData != null && aiViewModel.getMessages().getValue().isEmpty()) {
            aiViewModel.addContextMessage("Book Context: " + contextData);
            aiViewModel.addContextMessage("I'm ready to answer questions about this book.");
        } else if (aiViewModel.getMessages().getValue().isEmpty()) {
            aiViewModel.addContextMessage("Hi! I'm your AI Library Assistant. Ask me anything about your library.");
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                binding.editAiInput.setText(results.get(0));
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (adapter != null) {
            adapter.shutdown();
        }
        super.onDestroy();
    }
}
