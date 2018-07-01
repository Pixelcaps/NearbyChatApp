package com.nearby.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private ArrayList<UserObject> mUserArray;
    private Context context;

    public UserAdapter(ArrayList<UserObject> userObjects) {
        this.mUserArray = userObjects;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_active_users, parent, false);
        context = parent.getContext();
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final UserObject userObject = mUserArray.get(position);
        if (userObject.getUserAvatarUrl() != null || !userObject.getUserAvatarUrl().isEmpty()) {
            Glide.with(context)
                    .load(userObject.getUserAvatarUrl())
                    .into(holder.userPic);
        }
    }

    @Override
    public int getItemCount() {
        if (mUserArray != null) {
            return mUserArray.size();
        } else {
            return 0;
        }
    }
}
