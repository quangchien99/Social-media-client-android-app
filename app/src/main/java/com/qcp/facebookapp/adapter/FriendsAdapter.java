package com.qcp.facebookapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qcp.facebookapp.R;
import com.qcp.facebookapp.listener.OnItemClickedListener;
import com.qcp.facebookapp.model.Profile;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private OnItemClickedListener onItemClickedListener;
    private List<Profile> friends;
    private Context context;

    public FriendsAdapter(OnItemClickedListener onItemClickedListener, List<Profile> friends, Context context) {
        this.onItemClickedListener = onItemClickedListener;
        this.friends = friends;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profile profile = friends.get(position);
        holder.tvFriendName.setText(profile.getFirstName() + " " + profile.getLastName());
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFriendName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFriendName = itemView.findViewById(R.id.tv_friend_name);
        }
    }
}
