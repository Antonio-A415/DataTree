package com.datatree.infraestructure.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datatree.R;
import com.datatree.infraestructure.dataclass.DataMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        // User
        private LinearLayout userLayout;
        private ImageView userAvatar, userImage;
        private TextView userMessage, userTime;

        // Bot
        private LinearLayout botLayout;
        private ImageView botAvatar, botImage;
        private TextView botMessage, botTime;

        public ChatViewHolder(View itemView) {
            super(itemView);

            userLayout = itemView.findViewById(R.id.userLayout);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            userImage = itemView.findViewById(R.id.userImage);
            userMessage = itemView.findViewById(R.id.userMessage);
            userTime = itemView.findViewById(R.id.userTime);

            botLayout = itemView.findViewById(R.id.botLayout);
            botAvatar = itemView.findViewById(R.id.botAvatar);
            botImage = itemView.findViewById(R.id.botImage);
            botMessage = itemView.findViewById(R.id.botMessage);
            botTime = itemView.findViewById(R.id.botTime);
        }

        public void bind(DataMessage message) {
            // Formatear la hora
            String time = new SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(new Date(message.getTimestamp()));

            if (message.isUser()) {
                userLayout.setVisibility(View.VISIBLE);
                botLayout.setVisibility(View.GONE);

                // Avatar
                Glide.with(itemView.getContext())
                        .load(message.getAvatarUrl())
                        .placeholder(R.drawable.bunny_24px)
                        .into(userAvatar);

                // Texto
                if (message.getText() != null && !message.getText().isEmpty()) {
                    userMessage.setText(message.getText());
                    userMessage.setVisibility(View.VISIBLE);
                } else {
                    userMessage.setVisibility(View.GONE);
                }

                // Imagen adjunta
                if (message.getMessageImageUrl() != null) {
                    userImage.setVisibility(View.VISIBLE);
                    Glide.with(itemView.getContext())
                            .load(message.getMessageImageUrl())
                            .into(userImage);
                } else {
                    userImage.setVisibility(View.GONE);
                }

                userTime.setText(time);

            } else {
                botLayout.setVisibility(View.VISIBLE);
                userLayout.setVisibility(View.GONE);

                // Avatar
                Glide.with(itemView.getContext())
                        .load(message.getAvatarUrl())
                        .placeholder(R.drawable.bot_24px)
                        .into(botAvatar);

                // Texto
                if (message.getText() != null && !message.getText().isEmpty()) {
                    botMessage.setText(message.getText());
                    botMessage.setVisibility(View.VISIBLE);
                } else {
                    botMessage.setVisibility(View.GONE);
                }

                // Imagen adjunta
                if (message.getMessageImageUrl() != null) {
                    botImage.setVisibility(View.VISIBLE);
                    Glide.with(itemView.getContext())
                            .load(message.getMessageImageUrl())
                            .into(botImage);
                } else {
                    botImage.setVisibility(View.GONE);
                }

                botTime.setText(time);
            }
        }
    }
}
