package com.qcp.facebookapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qcp.facebookapp.R;
import com.qcp.facebookapp.adapter.CommentAdapter;
import com.qcp.facebookapp.adapter.HomeAdapter;
import com.qcp.facebookapp.client.APIClient;
import com.qcp.facebookapp.interfaces.RequestAPI;
import com.qcp.facebookapp.model.Comment;
import com.qcp.facebookapp.model.FriendList;
import com.qcp.facebookapp.model.Profile;
import com.qcp.facebookapp.model.Status;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentActivity extends AppCompatActivity implements Serializable {
    private RecyclerView rcComments;
    private List<Comment> comments;
    private CommentAdapter commentAdapter;
    private Button btnComment;
    private EditText edtComment;
    private Status status;
    private TextView tvStatusProfileName, tvStatusCreated, tvStatusContent;
    private String profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        findViewById();
        profileName = getProfileName();
        setStatus();
        comments = new ArrayList<>();
        clickBtnComment();
        getComments();
    }

    private void clickBtnComment() {
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtComment.getText().toString().isEmpty()) {
                    showAlertDialog("Please fill in your comment!");
                } else {
                    Log.d("qcpLog", "Posting comment here");
                    postComment();
                }
            }
        });
    }

    private void postComment() {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call callProfile = requestApi.getProfile(profileName);
        callProfile.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Profile profile = (Profile) response.body();
                Comment comment = new Comment();
                comment.setComment(edtComment.getText().toString());
                comment.setProfile(profile);
                comment.setStatus(status);
                Call callPostComment = requestApi.addComment(comment);
                callPostComment.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("qcpLog", "Posting comment successfully");
                        edtComment.setText(null);
                        comments.add(comment);
                        commentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        showAlertDialog("CommentActivity.postComment(): can't get data.");
                    }
                });
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                showAlertDialog("CommentActivity.postComment(): can't get data.");
            }
        });
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(CommentActivity.this)
                .setTitle("Warning")
                .setMessage(message)
                .setCancelable(true)
                .show();
    }

    private void findViewById() {
        rcComments = findViewById(R.id.rc_comments);
        btnComment = findViewById(R.id.btn_status_comment);
        edtComment = findViewById(R.id.edt_comment);
        tvStatusContent = findViewById(R.id.tv_status_content);
        tvStatusProfileName = findViewById(R.id.tv_status_profile_name);
        tvStatusCreated = findViewById(R.id.tv_status_time_created);
    }

    public void setStatus() {
        Intent intent = getIntent();
        status = (Status) intent.getExtras().getSerializable("Status");
        tvStatusProfileName.setText(status.getProfile().getFirstName() + " " + status.getProfile().getLastName());
        tvStatusCreated.setText(status.getCreated());
        tvStatusContent.setText(status.getStatus());
    }

    private String getProfileName() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(LoginActivity.PROFILE_NAME, Context.MODE_PRIVATE);
        String profileName = prefs.getString("profileName", "No name defined");
        Log.d("qcpTag", "getProfile" + profileName + "check");
        return profileName;
    }

    private void getComments() {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<List<Comment>> call = requestApi.getAllComments();
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                for (Comment c : response.body()) {
                    if (c.getStatus().getId() == status.getId()) {
                        comments.add(c);
                    }
                }
                commentAdapter = new CommentAdapter(comments, getApplicationContext());
                commentAdapter.notifyDataSetChanged();
                rcComments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rcComments.setAdapter(commentAdapter);

            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                showAlertDialog("CommentActivity.getComment(): can't get data.");
            }
        });
    }
}