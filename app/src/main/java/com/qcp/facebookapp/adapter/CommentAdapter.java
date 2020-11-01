package com.qcp.facebookapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qcp.facebookapp.R;
import com.qcp.facebookapp.model.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment> comments;
    private Context context;

    public CommentAdapter(List<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.tvProfileName.setText(comment.getProfile().getFirstName() + " " + comment.getProfile().getLastName());
        holder.tvTimeCreated.setText(comment.getCreated());
        holder.tvCommentContent.setText(comment.getComment());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProfileName;
        private TextView tvTimeCreated;
        private TextView tvCommentContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProfileName = itemView.findViewById(R.id.tv_comment_profile_name);
            tvTimeCreated = itemView.findViewById(R.id.tv_comment_time_created);
            tvCommentContent = itemView.findViewById(R.id.tv_comment_content);
        }
    }
}
