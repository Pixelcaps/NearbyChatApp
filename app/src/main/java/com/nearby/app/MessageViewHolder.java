package com.nearby.app;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView displayNameTV;
    public TextView messageContentTV;
    public ImageView avatarIV;
    public ImageView messageContentIV;

    public MessageViewHolder(View itemView) {
        super(itemView);
        this.displayNameTV = (TextView) itemView.findViewById(R.id.display_name);
        this.messageContentTV = (TextView) itemView.findViewById(R.id.message_content);
        this.avatarIV = (ImageView) itemView.findViewById(R.id.avatar);
        this.messageContentIV = (ImageView) itemView.findViewById(R.id.message_content_image);
    }
}
