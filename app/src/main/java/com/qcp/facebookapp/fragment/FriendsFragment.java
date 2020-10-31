package com.qcp.facebookapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qcp.facebookapp.R;
import com.qcp.facebookapp.activity.LoginActivity;
import com.qcp.facebookapp.adapter.FriendsAdapter;
import com.qcp.facebookapp.client.APIClient;
import com.qcp.facebookapp.interfaces.RequestAPI;
import com.qcp.facebookapp.listener.OnItemClickedListener;
import com.qcp.facebookapp.model.FriendList;
import com.qcp.facebookapp.model.Profile;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendsFragment extends Fragment implements OnItemClickedListener {
    private static FriendsFragment INSTANCE;
    private RecyclerView rcFriends;
    private List<Profile> friends;
    private FriendList friendList;
    private FriendsAdapter friendsAdapter;
    private String profileName;

    public static FriendsFragment getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new FriendsFragment();
        }
        return INSTANCE;
    }

    public FriendsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        rcFriends = view.findViewById(R.id.rc_friends);
        friends = new ArrayList<Profile>();
        profileName = getProfileName();
        getFriendList(profileName);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
//    @Override
//    public void onResume() {
//        getActivity().registerReceiver(mReceiverLocation, new IntentFilter("friendReceiver"));
//        super.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        getActivity().unregisterReceiver(mReceiverLocation);
//        super.onPause();
//    }
//
//    private BroadcastReceiver mReceiverLocation = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d("qcpp","Received broadcast");
//            getFriendList(profileName);
//        }
//    };

    public void getFriendList(String profileName) {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<FriendList> call = requestApi.getFriendList(profileName);
        call.enqueue(new Callback<FriendList>() {
            @Override
            public void onResponse(Call<FriendList> call, Response<FriendList> response) {
                if (response.isSuccessful()){
                friendList = response.body();
                friends = friendList.getFriend();
                friendsAdapter = new FriendsAdapter(FriendsFragment.this, friends, getContext());
                friendsAdapter.notifyDataSetChanged();
                rcFriends.setLayoutManager(new LinearLayoutManager(getContext()));
                rcFriends.setAdapter(friendsAdapter);
                Log.d("qcpTag", "Go to get all profiles");}
            }

            @Override
            public void onFailure(Call<FriendList> call, Throwable t) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Waring")
                        .setMessage("Can't get user data")
                        .setCancelable(true)
                        .show();
                Log.d("qcpTag", t.getMessage() + "");
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getContext(), "Item clicked" + position, Toast.LENGTH_SHORT).show();
    }

    private String getProfileName() {
        SharedPreferences prefs = getContext().getSharedPreferences(LoginActivity.PROFILE_NAME, Context.MODE_PRIVATE);
        String profileName = prefs.getString("profileName", "No name defined");
        Log.d("qcpTag", "getProfile" + profileName + "check");
        return profileName;
    }
}
