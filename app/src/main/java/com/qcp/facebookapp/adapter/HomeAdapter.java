package com.qcp.facebookapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.qcp.facebookapp.R;
import com.qcp.facebookapp.listener.OnItemClickedListener;
import com.qcp.facebookapp.model.Status;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private OnItemClickedListener onItemClickedListener;
    private List<Status> statuses;
    private Context context;

    public HomeAdapter(OnItemClickedListener onItemClickedListener, List<Status> statuses, Context context) {
        this.onItemClickedListener = onItemClickedListener;
        this.statuses = statuses;
        this.context = context;
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
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatusProfileName, tvStatusTimeCreated, tvStatusContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatusProfileName = itemView.findViewById(R.id.tv_status_profile_name);
            tvStatusTimeCreated = itemView.findViewById(R.id.tv_status_time_created);
            tvStatusContent = itemView.findViewById(R.id.tv_status_content);
        }
    }
}
