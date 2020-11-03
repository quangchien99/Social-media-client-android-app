package com.qcp.facebookapp.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qcp.facebookapp.R;
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

public class SearchActivity extends AppCompatActivity implements OnItemClickedListener {
    private RecyclerView rcSearchResult;
    private TextView tvNumberResult;
    private List<Profile> searchResult;
    private List<Profile> allProfiles;
    private SearchResultAdapter searchResultAdapter;
    private String profileName, searchName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        rcSearchResult = findViewById(R.id.rc_friends);
        tvNumberResult = findViewById(R.id.tv_number_result);
        searchResult = new ArrayList<Profile>();
        profileName = getProfileName();
        searchName = getSearchName();
        show(profileName);
    }

    private String getSearchName() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Const.SEARCH_NAME, Context.MODE_PRIVATE);
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
                            rcSearchResult = findViewById(R.id.rc_search_result);
                            searchResultAdapter = new SearchResultAdapter(SearchActivity.this, searchResult, getApplicationContext());
                            rcSearchResult.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            rcSearchResult.setAdapter(searchResultAdapter);
                        }
                    }
                    tvNumberResult.setText(searchResult.size() + "");
                }
            }

            @Override
            public void onFailure(Call<List<Profile>> call, Throwable t) {
                showAlertDialog("SearchActivity.show():Can't get data");
            }
        });
    }

    private String getProfileName() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(LoginActivity.PROFILE_NAME, MODE_PRIVATE);
        String profileName = prefs.getString("profileName", "No name defined");
        Log.d("qcpTag", "Search - getProfile" + profileName + "check");
        return profileName;
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(getApplicationContext())
                .setTitle("Warning")
                .setMessage(message)
                .setCancelable(true)
                .show();
        Log.d("qcpTag", message + " ");
    }


    @Override
    public void onItemClick(int position) {
        Log.d("qcpTag", "Check item clicked");
        Toast.makeText(getApplicationContext(), "Search - Item clicked" + position, Toast.LENGTH_SHORT).show();
    }
}
