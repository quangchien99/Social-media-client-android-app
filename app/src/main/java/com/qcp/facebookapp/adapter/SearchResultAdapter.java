package com.qcp.facebookapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.qcp.facebookapp.client.APIClient;
import com.qcp.facebookapp.constant.Const;
import com.qcp.facebookapp.fragment.FriendsFragment;
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

import static android.content.Context.MODE_PRIVATE;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.Viewholder> {
    private OnItemClickedListener onItemClickedListener;
    private List<Profile> searchResult;
    private Context context;
    private FriendList friendList;
    private List<Profile> friends;
    private String profileName;

    public SearchResultAdapter(OnItemClickedListener onItemClickedListener, List<Profile> searchResult, Context context) {
        this.onItemClickedListener = onItemClickedListener;
        this.searchResult = searchResult;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        profileName = getProfileName();
        Profile profile = searchResult.get(position);
        setActionForButton(holder, profile, position);
    }

    public void setActionForButton(Viewholder holder, Profile profile, int position) {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<FriendList> call = requestApi.getFriendList(profileName);
        call.enqueue(new Callback<FriendList>() {
            @Override
            public void onResponse(Call<FriendList> call, Response<FriendList> response) {
                friendList = response.body();
                friends = friendList.getFriend();
                holder.tvProfileResultName.setText(profile.getFirstName() + " " + profile.getLastName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickedListener.onItemClick(position);
                    }
                });
                for (Profile profile1 : friends) {
                    if (profile.getProfileName().equals(profile1.getProfileName())) {
                        holder.btnOption.setText("UnFriend");
                        holder.btnOption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unfriend, 0, 0, 0);
                    }
                    if (!profile.getProfileName().equals(profile1.getProfileName())) {
                        holder.btnOption.setText("Add friend");
                        holder.btnOption.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_friend, 0, 0, 0);
                    }
                }
                holder.btnOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.btnOption.getText().toString().equalsIgnoreCase("UnFriend")) {
                            deleteFriend(position);
                        } else {
                            addFriend(profile);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<FriendList> call, Throwable t) {
                showAlertDialog("SearchResultAdapter.setActionForButton: Can get user data");
            }
        });
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(context)
                .setTitle("Waring")
                .setMessage(message)
                .setCancelable(true)
                .show();
    }

    private void deleteFriend(int position) {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<ResponseBody> call2 = requestApi.deleteFriend(profileName, friends.get(position).getProfileName());
        call2.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(context, "" + searchResult.get(position).getFirstName() + " " + searchResult.get(position).getLastName() + " is not your friend any more! ", Toast.LENGTH_SHORT).show();
                friends.remove(searchResult.get(position));
                SearchResultAdapter.this.notifyDataSetChanged();
                FriendsFragment.getINSTANCE().getFriendList(profileName);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showAlertDialog("SearchResultAdapter.deleteFriend: Can get user data");
            }
        });
    }

    private void addFriend(Profile profile) {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<Profile> callGetProfile = requestApi.getProfile(profileName);
        callGetProfile.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                friends.add(profile);
                FriendList friendList = new FriendList(response.body(), friends);
                Call<ResponseBody> call3 = requestApi.addFriend(friendList);
                call3.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        SearchResultAdapter.this.notifyDataSetChanged();
                        FriendsFragment.getINSTANCE().getFriendList(profileName);
                        Toast.makeText(context, "Add " + profile.getProfileName() + " Successfully!" + response.code(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        showAlertDialog("SearchResultAdapter.addFriend: Can get user data");
                    }
                });

            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                showAlertDialog("SearchResultAdapter.addFriend2: Can get user data");
            }
        });
    }

    private String getProfileName() {
        SharedPreferences prefs = context.getSharedPreferences(Const.PROFILE_NAME, MODE_PRIVATE);
        String profileName = prefs.getString("profileName", "No name defined");
        return profileName;
    }

    @Override
    public int getItemCount() {
        return searchResult.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView tvProfileResultName;
        Button btnOption;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            tvProfileResultName = itemView.findViewById(R.id.tv_search_result_name);
            btnOption = itemView.findViewById(R.id.btn_search_result_option);
        }
    }
}
