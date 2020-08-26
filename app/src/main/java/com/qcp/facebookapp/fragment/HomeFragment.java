package com.qcp.facebookapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qcp.facebookapp.R;
import com.qcp.facebookapp.activity.LoginActivity;
import com.qcp.facebookapp.adapter.HomeAdapter;
import com.qcp.facebookapp.client.APIClient;
import com.qcp.facebookapp.interfaces.RequestAPI;
import com.qcp.facebookapp.listener.OnItemClickedListener;
import com.qcp.facebookapp.model.FriendList;
import com.qcp.facebookapp.model.Profile;
import com.qcp.facebookapp.model.Status;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment implements OnItemClickedListener {
    private static HomeFragment INSTANCE;
    public static final String FONT_PATH = "fonts/Nabila.ttf";
    private TextView tvCreatePost;
    private List<Profile> friends;
    private FriendList friendList;
    private String profileName;
    private RecyclerView rcHome;
    private HomeAdapter homeAdapter;
    private List<Status> allStatuses;
    private List<Status> statuses;

    public static HomeFragment getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new HomeFragment();
        }
        return INSTANCE;
    }

    public HomeFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvCreatePost = view.findViewById(R.id.tv_create_post);
        setTypeface();
        rcHome = view.findViewById(R.id.rc_home);
        statuses = new ArrayList<Status>();
        profileName = getProfileName();
        getStatuses();
        return view;
    }

    private String getProfileName() {
        SharedPreferences prefs = getContext().getSharedPreferences(LoginActivity.PROFILE_NAME, Context.MODE_PRIVATE);
        String profileName = prefs.getString("profileName", "No name defined");
        Log.d("qcpTag", "getProfile" + profileName + "check");
        return profileName;
    }

    private void getFriendList(String profileName) {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<FriendList> call = requestApi.getFriendList(profileName);
        call.enqueue(new Callback<FriendList>() {
            @Override
            public void onResponse(Call<FriendList> call, Response<FriendList> response) {
                friendList = response.body();
                friends = friendList.getFriend();
                Log.d("qcpTag", "Go to get friendlist" + friends.size());
                for (Status s : allStatuses) {
                    for (Profile p : friends) {
                        if (s.getProfile().getProfileName().equals(p.getProfileName())) {
                            statuses.add(s);
                        }
                    }
                    Log.d("qcpTag", "Statuses size" + statuses.size());
                }
                homeAdapter = new HomeAdapter(HomeFragment.this, statuses, getContext());
                rcHome.setLayoutManager(new LinearLayoutManager(getContext()));
                rcHome.setAdapter(homeAdapter);
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

    private void getStatuses() {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<List<Status>> call = requestApi.getAllStatuses();
        call.enqueue(new Callback<List<Status>>() {
            @Override
            public void onResponse(Call<List<Status>> call, Response<List<Status>> response) {
                allStatuses = response.body();
                getFriendList(profileName);
                Log.d("qcpTag", "Go to get all statuses" + allStatuses.size());
                // Log.d("qcpTag", statuses.get(3).getProfile().toString() + "");
            }

            @Override
            public void onFailure(Call<List<Status>> call, Throwable t) {
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

    }

    private void setTypeface() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), FONT_PATH);
        tvCreatePost.setTypeface(typeface);
    }
}
