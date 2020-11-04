package com.qcp.facebookapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.qcp.facebookapp.R;
import com.qcp.facebookapp.activity.CommentActivity;
import com.qcp.facebookapp.activity.LoginActivity;
import com.qcp.facebookapp.client.APIClient;
import com.qcp.facebookapp.interfaces.RequestAPI;
import com.qcp.facebookapp.listener.OnItemClickedListener;
import com.qcp.facebookapp.model.Comment;
import com.qcp.facebookapp.model.Like;
import com.qcp.facebookapp.model.Profile;
import com.qcp.facebookapp.model.Status;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private String profileName;
    private OnItemClickedListener onItemClickedListener;
    private List<Status> statuses;
    private Context context;
    public HomeAdapter( OnItemClickedListener onItemClickedListener, List<Status> statuses, Context context) {
        this.onItemClickedListener = onItemClickedListener;
        this.statuses = statuses;
        this.context = context;
        profileName = getProfileName();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Status status = statuses.get(position);
        holder.tvStatusProfileName.setText(status.getProfile().getFirstName() + " " + status.getProfile().getLastName());
        holder.tvStatusTimeCreated.setText(status.getCreated());
        holder.tvStatusContent.setText(status.getStatus());
        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickBtnLike(holder, status);
            }
        });
        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("qcpLog", "Clicked btnComment number: " + position + " Stastus" + status.getId());
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("Status", status);
                context.startActivity(intent);
            }
        });
        setNoLikes(holder, status);
    }

    private void clickBtnLike(ViewHolder holder, Status status) {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<List<Like>> call = requestApi.getAllLikes();
        call.enqueue(new Callback<List<Like>>() {
            @Override
            public void onResponse(Call<List<Like>> call, Response<List<Like>> response) {
                int likeCount = 0;
                List<Like> likes = new ArrayList<>();
                if (response.isSuccessful()) {
                    for (Like like : response.body()) {
                        if (like.getStatus().getId() == status.getId()) {
                            likes.add(like);
                            likeCount = likes.size();
                        }
                    }
                    if (likes.size() == 0) {
                        holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_red, 0, 0, 0);
                        likeCount++;
                        addLike(profileName, status);
                    } else {
                        ArrayList<String> profileNameLiked = new ArrayList<>();
                        for (Like like : likes) {
                            profileNameLiked.add(like.getProfile().getProfileName());
                        }
                        if (profileNameLiked.contains(profileName)) {
                            holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
                            deleteLike(likes.get(profileNameLiked.indexOf(profileName)).getId());
                            likeCount--;
                        }
                        else {
                            holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_red, 0, 0, 0);
                            addLike(profileName, status);
                            likeCount++;
                        }
                    }
                }
                holder.tvNoLikes.setText(context.getString(R.string.number_of_likes, likeCount));
            }

            @Override
            public void onFailure(Call<List<Like>> call, Throwable t) {
                showAlertDialog("HomeAdapter.clickBtnLike(): Can't get data.");
            }
        });
    }

    public void addLike(String profileName, Status status) {
        Like like = new Like();
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<Profile> callProfile = requestApi.getProfile(profileName);
        callProfile.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Profile profile = new Profile();
                profile = response.body();
                like.setProfile(profile);
                like.setStatus(status);
                Call<ResponseBody> callLike = requestApi.addLike(like);
                callLike.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d("qcpLog", "Add like successfully");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        showAlertDialog("HomeAdapter.addLike(): Can't get data.");
                    }
                });
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.d("qcpLog", "add like unsuccessfull1");
                showAlertDialog("HomeAdapter.addLike(): Can't get data.");
            }
        });


    }

    public void deleteLike(long id) {
        Log.d("qcLog", "Delete like");
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<ResponseBody> call = requestApi.deleteLike(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("qcpLog", "Delete like successfully");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("qcpLog", "Delete like unsuccessfully");
                showAlertDialog("HomeAdapter.deleteLike(): Can't connect to server.");
            }
        });
    }

    private String getProfileName() {
        SharedPreferences prefs = context.getSharedPreferences(LoginActivity.PROFILE_NAME, Context.MODE_PRIVATE);
        String profileName = prefs.getString("profileName", "No name defined");
        return profileName;
    }

    public void setNoLikes(@NonNull ViewHolder holder, Status status) {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<List<Like>> call = requestApi.getAllLikes();
        call.enqueue(new Callback<List<Like>>() {
            @Override
            public void onResponse(Call<List<Like>> call, Response<List<Like>> response) {
                int likeCount = 0;
                if (response.isSuccessful()) {
                    for (Like like : response.body()) {
                        if (like.getStatus().getId() == status.getId()) {
                            likeCount++;
                            if (like.getProfile().getProfileName().equalsIgnoreCase(profileName)) {
                                //
                                holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_red, 0, 0, 0);
                            } else {
                                holder.btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
                            }
                        }

                    }

                    holder.tvNoLikes.setText(context.getString(R.string.number_of_likes, likeCount));
                    setNoComments(holder, status);
                }
            }

            @Override
            public void onFailure(Call<List<Like>> call, Throwable t) {
                showAlertDialog("HomeAdapter.setNoLikes(): Can't get data.");
            }
        });

    }

    public void setNoComments(@NonNull ViewHolder holder, Status status) {
        Retrofit retrofit = APIClient.getClient();
        RequestAPI requestApi = retrofit.create(RequestAPI.class);
        Call<List<Comment>> call = requestApi.getAllComments();
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                int commentCount = 0;
                if (response.isSuccessful()) {
                    for (Comment c : response.body()) {
                        if (c.getStatus().getId() == status.getId()) {
                            commentCount++;
                        }
                    }
                    holder.tvNoComments.setText(context.getString(R.string.number_of_comments, commentCount));
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                showAlertDialog("HomeAdapter.setNoComments(): Can't get data.");
            }
        });
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(context)
                .setTitle("Warning")
                .setMessage(message)
                .setCancelable(true)
                .show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatusProfileName, tvStatusTimeCreated, tvStatusContent, tvNoLikes, tvNoComments;
        Button btnLike, btnComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatusProfileName = itemView.findViewById(R.id.tv_status_profile_name);
            tvStatusTimeCreated = itemView.findViewById(R.id.tv_status_time_created);
            tvStatusContent = itemView.findViewById(R.id.tv_status_content);
            tvNoLikes = itemView.findViewById(R.id.tv_number_of_likes);
            tvNoComments = itemView.findViewById(R.id.tv_number_of_comments);
            btnLike = itemView.findViewById(R.id.btn_status_like);
            btnComment = itemView.findViewById(R.id.btn_status_comment);

        }
    }
}
