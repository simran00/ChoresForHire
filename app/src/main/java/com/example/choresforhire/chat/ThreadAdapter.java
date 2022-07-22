package com.example.choresforhire.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.choresforhire.R;
import com.example.choresforhire.post.Post;
import com.example.choresforhire.post.PostsAdapter;
import com.example.choresforhire.post.SelectListener;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {
    private final Context mContext;
    private final List<ParseUser> mUsers;
    private SelectListenerChat mSelectListenerChat;

    public ThreadAdapter(Context context, List<ParseUser> users, SelectListenerChat selectListenerChat) {
        this.mUsers = users;
        this.mContext = context;
        this.mSelectListenerChat = selectListenerChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_thread, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser user = mUsers.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mChatUser;
        private ImageView mProfilePic;
        private ConstraintLayout mChatThread;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mChatUser = itemView.findViewById(R.id.thread_author);
            mChatThread = itemView.findViewById(R.id.thread_item);
            mProfilePic = itemView.findViewById(R.id.ivProfilePicPost);
        }

        public void bind(ParseUser user) {
            mChatUser.setText(user.getUsername());

            ParseFile profilePic = (ParseFile) user.get("profilePic");

            if (profilePic != null) {
                Glide.with(mContext).load(profilePic.getUrl()).into(mProfilePic);
            } else {
                int drawableIdentifier = (mContext).getResources().getIdentifier("blank_profile", "drawable", (mContext).getPackageName());
                Glide.with(mContext).load(drawableIdentifier).into(mProfilePic);
            }

            mChatThread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectListenerChat != null) {
                        mSelectListenerChat.onItemClicked(user);
                    }
                }
            });
        }
    }

    public void clear() {
        mUsers.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ParseUser> list) {
        mUsers.addAll(list);
        notifyDataSetChanged();
    }

    public void add(ParseUser user) {
        mUsers.add(user);
        notifyDataSetChanged();
    }
}
