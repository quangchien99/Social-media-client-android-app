package com.qcp.facebookapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment implements OnItemClickedListener {
    private static HomeFragment INSTANCE;
    public static final String FONT_PATH = "fonts/Nabila.ttf";
    private View view;
    private TextView tvCreatePost;
    private List<Profile> friends;
    private FriendList friendList;
    private String profileName;
    private RecyclerView rcHome;
    private HomeAdapter homeAdapter;
    private List<Status> allStatuses;
    private List<Status> statuses;
    private EditText edtStatusContent;
    private Button btnPost;
    private Profile profile;

    public static HomeFragment getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new HomeFragment();
        }
        return INSTANCE;
    }

    public HomeFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (homeAdapter != null) {
            homeAdapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        tvCreatePost = view.findViewById(R.id.tv_create_post);
        findViewById();
        setTypeface();
        statuses = new ArrayList<Status>();
        profileName = getProfileName();
        getStatuses();
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postStatus();
            }
        });
        return view;
    }

    private void postStatus() {
        if (edtStatusContent.getText().toString().isEmpty()) {
            showAlert("HomeFragment.postStatus(): Can't get data.");
        } else {
            postStatusToServer();
        }
    }

    private void postStatusToServer() {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<Profile> call = requestApi.getProfile(profileName);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                profile = response.body();
                Status status = new Status();
                status.setProfile(profile);
                status.setStatus(edtStatusContent.getText().toString());
                Call<ResponseBody> callPostStatus = requestApi.postStatus(status);
                callPostStatus.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 201) {
                            Toast.makeText(getContext(), "Status posted successfully!", Toast.LENGTH_SHORT).show();
                            edtStatusContent.setText(null);
                            statuses.add(0, status);
                            homeAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        showAlert("HomeFragment.postStatusToServer(): Can't get data.");
                    }
                });
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                showAlert("HomeFragment.postStatusToServer(): Can't get data.");
            }
        });
    }


    private void findViewById() {
        rcHome = view.findViewById(R.id.rc_home);
        edtStatusContent = view.findViewById(R.id.edt_post_status_content);
        btnPost = view.findViewById(R.id.btn_post_status);
    }

    private String getProfileName() {
        SharedPreferences prefs = getContext().getSharedPreferences(LoginActivity.PROFILE_NAME, Context.MODE_PRIVATE);
        String profileName = prefs.getString("profileName", "No name defined");
        return profileName;
    }

    private void getFriendList(String profileName) {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<FriendList> call = requestApi.getFriendList(profileName);
        call.enqueue(new Callback<FriendList>() {
            @Override
            public void onResponse(Call<FriendList> call, Response<FriendList> response) {
                if (response.isSuccessful()) {
                    friendList = response.body();
                    friends = friendList.getFriend();
                    for (Status s : allStatuses) {
                        for (Profile p : friends) {
                            if (s.getProfile().getProfileName().equals(p.getProfileName())) {
                                statuses.add(s);
                            }
                        }
                        if(s.getProfile().getProfileName().equals(profileName)){
                            statuses.add(s);
                        }
                    }
                    Collections.reverse(statuses);
                    homeAdapter = new HomeAdapter(HomeFragment.this, statuses, getContext());
                    rcHome.setLayoutManager(new LinearLayoutManager(getContext()));
                    rcHome.setAdapter(homeAdapter);
                }
            }

            @Override
            public void onFailure(Call<FriendList> call, Throwable t) {
                showAlert("HomeFragment.getFriendList(): Can't get data.");
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
            }

            @Override
            public void onFailure(Call<List<Status>> call, Throwable t) {
                showAlert("HomeFragment.getStatus(): Can't get data.");
            }
        });
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("Warning")
                .setMessage("Can't get user data")
                .setCancelable(true)
                .show();
    }

    @Override
    public void onItemClick(int position) {

    }

    private void setTypeface() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), FONT_PATH);
        tvCreatePost.setTypeface(typeface);
    }
}
