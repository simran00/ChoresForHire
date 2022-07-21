package com.example.choresforhire.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.choresforhire.R;
import com.parse.ParseUser;

import java.util.List;

public class GroupPostAdapter extends RecyclerView.Adapter<GroupPostAdapter.PostViewHolder>{
    private Context mContext;
    private List<GroupPost> mPosts;

    public GroupPostAdapter(Context context, List<GroupPost> posts) {
        this.mContext = context;
        this.mPosts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        GroupPost post = mPosts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView mAuthor;
        private TextView mGroupName;
        private TextView mPostDescription;


        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mAuthor = itemView.findViewById(R.id.tvPostAuthor);
            mGroupName = itemView.findViewById(R.id.tvGroupNamePost);
            mPostDescription = itemView.findViewById(R.id.tvGroupPostDes);
        }

        public void bind(GroupPost post) {
            mAuthor.setText(post.getUser().getUsername());
            mGroupName.setText(post.getGroup().getName());
            mPostDescription.setText(post.getDescription());
        }
    }
}
