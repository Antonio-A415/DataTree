package com.datatree.infraestructure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datatree.R;
import com.datatree.infraestructure.dataclass.DataMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<DataMessage> messageList;

    public ChatAdapter(List<DataMessage> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        DataMessage message = messageList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView userMessage;
        private TextView botMessage;

        public ChatViewHolder(View itemView) {
            super(itemView);
            userMessage = itemView.findViewById(R.id.userMessage);
            botMessage = itemView.findViewById(R.id.botMessage);
        }
        // esra es la funcion que muestra los mensajes para los usuarios y el chat bot.
        public void bind(DataMessage message) {
            if (message.isUser()) {
                userMessage.setText(message.getText());
                userMessage.setVisibility(View.VISIBLE);
                botMessage.setVisibility(View.GONE);
            } else {
                botMessage.setText(message.getText());
                botMessage.setVisibility(View.VISIBLE);
                userMessage.setVisibility(View.GONE);
            }
        }
    }
}
