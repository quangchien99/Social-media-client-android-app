package com.qcp.facebookapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.qcp.facebookapp.R;
import com.qcp.facebookapp.activity.ChangePasswordActivity;
import com.qcp.facebookapp.activity.LoginActivity;
import com.qcp.facebookapp.client.APIClient;
import com.qcp.facebookapp.interfaces.RequestAPI;
import com.qcp.facebookapp.listener.OnItemClickedListener;
import com.qcp.facebookapp.model.FriendList;
import com.qcp.facebookapp.model.Profile;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickedListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private String profileName;
        private String friendName;
        private FriendList friendList;
        TextView tvFriendName;
        Button btnUnfriend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFriendName = itemView.findViewById(R.id.tv_friend_name);
            btnUnfriend = itemView.findViewById(R.id.btn_unfriend);
            btnUnfriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Clicked " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    profileName = getProfileName();
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Friend")
                            .setMessage("Are you sure to remove " + friends.get(getAdapterPosition()).getFirstName() + " " + friends.get(getAdapterPosition()).getLastName() + " from your friendlist?")
                            .setCancelable(false)
                            .setPositiveButton("Of Course", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteFriend(getAdapterPosition());
                                }
                            })
                            .setNegativeButton("Hell Nooo", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(context, "Noo", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
            });
        }

        private String getProfileName() {
            SharedPreferences prefs = context.getSharedPreferences(LoginActivity.PROFILE_NAME, Context.MODE_PRIVATE);
            String profileName = prefs.getString("profileName", "No name defined");
            Log.d("qcpTag", "getProfile" + profileName + "check");
            return profileName;
        }

        private void deleteFriend(int number) {
            Retrofit retrofit = APIClient.getClient();
            RequestAPI requestApi = retrofit.create(RequestAPI.class);
            Call<FriendList> call = requestApi.getFriendList(profileName);
            call.enqueue(new Callback<FriendList>() {
                @Override
                public void onResponse(Call<FriendList> call, Response<FriendList> response) {
                    friendList = response.body();
                    friends = friendList.getFriend();
                    friendName = friends.get(number).getProfileName();
                    Call<ResponseBody> call2 = requestApi.deleteFriend(profileName, friendName);
                    call2.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Toast.makeText(context, "Done code " + response.code(), Toast.LENGTH_SHORT).show();
                            friends.remove(getAdapterPosition());
                            FriendsAdapter.this.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            showAlertDialog("FriendAdapter.deleteFriend(): Can connect to server.");
                        }
                    });
                }

                @Override
                public void onFailure(Call<FriendList> call, Throwable t) {
                    showAlertDialog("FriendAdapter.deleteFriend(): Can connect to server.");
                }
            });
        }
        private void showAlertDialog(String message) {
            new AlertDialog.Builder(context)
                    .setTitle("Warning")
                    .setMessage(message)
                    .setCancelable(true)
                    .show();
        }
    }
}
