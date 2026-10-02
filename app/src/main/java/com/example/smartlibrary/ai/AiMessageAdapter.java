package com.example.smartlibrary.ai;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlibrary.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AiMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 1;
    private static final int TYPE_AI = 2;
    private static final int TYPE_SYSTEM = 3;

    private List<AiMessage> messages = new ArrayList<>();
    private TextToSpeech textToSpeech;
    private Context context;

    public AiMessageAdapter(Context context) {
        this.context = context;
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
            }
        });
    }

    public void setMessages(List<AiMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        String role = messages.get(position).getRole();
        if ("USER".equals(role)) {
            return TYPE_USER;
        } else if ("ASSISTANT".equals(role)) {
            return TYPE_AI;
        } else {
            return TYPE_SYSTEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(v);
        } else {
            // Both AI and SYSTEM will use the AI layout for now
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ai_message, parent, false);
            return new AiMessageViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AiMessage msg = messages.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).textMessage.setText(msg.getContent());
        } else if (holder instanceof AiMessageViewHolder) {
            AiMessageViewHolder aiHolder = (AiMessageViewHolder) holder;
            aiHolder.textMessage.setText(msg.getContent());

            aiHolder.btnCopy.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("AI Response", msg.getContent());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            });

            aiHolder.btnShare.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, msg.getContent());
                context.startActivity(Intent.createChooser(shareIntent, "Share via"));
            });

            aiHolder.btnSpeak.setOnClickListener(v -> {
                if (textToSpeech != null) {
                    textToSpeech.speak(msg.getContent(), TextToSpeech.QUEUE_FLUSH, null, null);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_user_message);
        }
    }

    static class AiMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        ImageButton btnCopy, btnShare, btnSpeak;
        AiMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_ai_message);
            btnCopy = itemView.findViewById(R.id.btn_copy);
            btnShare = itemView.findViewById(R.id.btn_share);
            btnSpeak = itemView.findViewById(R.id.btn_speak);
        }
    }
}
