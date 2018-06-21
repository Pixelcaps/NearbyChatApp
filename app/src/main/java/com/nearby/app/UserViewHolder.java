package com.nearby.app;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class UserViewHolder extends RecyclerView.ViewHolder {
    public ImageView userPic;
    public TextView toolTipTarget;

    public UserViewHolder(View itemView) {
        super(itemView);
        this.userPic = (ImageView) itemView.findViewById(R.id.avatar);
        this.toolTipTarget = (TextView) itemView.findViewById(R.id.tooltip_anchor);
    }
}
