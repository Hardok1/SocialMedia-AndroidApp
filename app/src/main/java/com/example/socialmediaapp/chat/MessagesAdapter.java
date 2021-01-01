package com.example.socialmediaapp.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.chat.model.MessageModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    List<MessageModel> messageModels;
    Context context;

    public MessagesAdapter(List<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.message_row, parent, false);
        return new MessagesAdapter.MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {
        holder.senderName.setText(messageModels.get(position).getName());
        holder.messageContent.setText(messageModels.get(position).getContent());
        holder.createdAt.setText(messageModels.get(position).getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    class MessagesViewHolder extends RecyclerView.ViewHolder {

        TextView senderName;
        TextView messageContent;
        TextView createdAt;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderNameTextView);
            messageContent = itemView.findViewById(R.id.messageContentTextView);
            createdAt = itemView.findViewById(R.id.createdAt);
        }
    }
}
