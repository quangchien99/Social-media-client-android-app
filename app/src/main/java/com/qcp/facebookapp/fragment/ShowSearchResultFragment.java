package com.qcp.facebookapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.qcp.facebookapp.adapter.SearchResultAdapter;
import com.qcp.facebookapp.client.APIClient;
import com.qcp.facebookapp.constant.Const;
import com.qcp.facebookapp.interfaces.RequestAPI;
import com.qcp.facebookapp.listener.OnItemClickedListener;
import com.qcp.facebookapp.model.Profile;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;

public class ShowSearchResultFragment extends Fragment implements OnItemClickedListener {
    private static ShowSearchResultFragment INSTANCE;
    private RecyclerView rcSearchResult;
    private TextView tvNumberResult;
    private List<Profile> searchResult;
    private List<Profile> allProfiles;
    private SearchResultAdapter searchResultAdapter;
    private String profileName, searchName;

    public ShowSearchResultFragment() {
    }

    public static ShowSearchResultFragment getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ShowSearchResultFragment();
        }
        return INSTANCE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search, container, false);
        rcSearchResult = view.findViewById(R.id.rc_friends);
        tvNumberResult = view.findViewById(R.id.tv_number_result);
        searchResult = new ArrayList<Profile>();
        profileName = getProfileName();
        searchName = getSearchName();
        show(profileName);
        return view;
    }

    private String getSearchName() {
        SharedPreferences prefs = getContext().getSharedPreferences(Const.SEARCH_NAME, Context.MODE_PRIVATE);
        searchName = prefs.getString(Const.SEARCH_NAME, "No name defined");
        return searchName;
    }

    private void show(String profileName) {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<List<Profile>> callAllProfiles = requestApi.getAllProfiles();
        callAllProfiles.enqueue(new Callback<List<Profile>>() {
            @Override
            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                allProfiles = response.body();
                String[] words = searchName.split("\\s");
                for (Profile profile : allProfiles) {
                    for (String w : words) {
                        if (w.equals(profile.getFirstName()) || w.equals(profile.getLastName())) {
                            searchResult.add(profile);
                            searchResultAdapter = new SearchResultAdapter(ShowSearchResultFragment.this, searchResult, getContext());
                            rcSearchResult.setLayoutManager(new LinearLayoutManager(getContext()));
                            rcSearchResult.setAdapter(searchResultAdapter);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                showAlertDialog("Can not get data from server");
            }
        });
    }

    private String getProfileName() {
        SharedPreferences prefs = getContext().getSharedPreferences(LoginActivity.PROFILE_NAME, MODE_PRIVATE);
        String profileName = prefs.getString("profileName", "No name defined");
        Log.d("qcpTag", "Search - getProfile" + profileName + "check");
        return profileName;
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("Waring")
                .setMessage(message)
                .setCancelable(true)
                .show();
        Log.d("qcpTag", message + " ");
    }

    @Override
    public void onItemClick(int position) {
        Log.d("qcpTag", "Check item clicked");
        Toast.makeText(getContext(), "Search - Item clicked" + position, Toast.LENGTH_SHORT).show();
    }
}
