package com.nearby.app;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private ArrayList<UserObject> mUserArray;
    public UserAdapter(ArrayList<UserObject> userObjects) {
        this.mUserArray = userObjects;
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_active_users, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final UserObject userObject = mUserArray.get(position);

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
