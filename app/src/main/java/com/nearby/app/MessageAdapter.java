package com.nearby.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private ArrayList<MessageObject> mMessageArray;
    private Context context;

    public MessageAdapter(ArrayList<MessageObject> messageObjects) {
        this.mMessageArray = messageObjects;
    }

    @Override
    public int getItemViewType(int position) {
        MessageObject messageObject = mMessageArray.get(position);
        if (messageObject.getFromUser()){
            return 0;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        context = parent.getContext();
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_send, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_receive, parent, false);
        }

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageObject messageObject = mMessageArray.get(position);

        holder.displayNameTV.setText(messageObject.getDisplayName());

        if (Objects.equals(messageObject.getMessageContent(), MessageObject.MESSAGE_CONTENT_TEXT)) {
            holder.messageContentTV.setVisibility(View.VISIBLE);
            holder.messageContentTV.setText(messageObject.getMessageBody());
            holder.messageContentIV.setVisibility(View.GONE);
        } else if (Objects.equals(messageObject.getMessageContent(), MessageObject.MESSAGE_CONTENT_IMAGE)){
            holder.messageContentTV.setVisibility(View.GONE);
            holder.messageContentIV.setVisibility(View.VISIBLE);
            byte[] imageString = Base64.decode(messageObject.getMessageBody(), Base64.NO_WRAP);
            Bitmap bm = BitmapFactory.decodeByteArray(imageString, 0, imageString.length);
            holder.messageContentIV.setImageBitmap(bm);
        }
        if (messageObject.getUserAvatarUrl() != null || !messageObject.getUserAvatarUrl().isEmpty()) {
            Glide.with(context)
                    .load(messageObject.getUserAvatarUrl())
                    .into(holder.avatarIV);
        }

        if (position > 0){
            String prevUsername = mMessageArray.get(position - 1).getDisplayName();
            if (Objects.equals(prevUsername, messageObject.getDisplayName())){
                holder.avatarIV.setVisibility(View.GONE);
                holder.displayNameTV.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mMessageArray != null) {
            return mMessageArray.size();
        } else return 0;
    }
}
