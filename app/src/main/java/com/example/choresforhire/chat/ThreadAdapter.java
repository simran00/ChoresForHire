package com.example.choresforhire.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.choresforhire.R;
import com.example.choresforhire.post.Post;
import com.example.choresforhire.post.PostsAdapter;
import com.example.choresforhire.post.SelectListener;
import com.parse.ParseUser;

import java.util.List;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {
    private final Context context;
    private final List<ParseUser> users;
    private SelectListenerChat selectListenerChat;

    public ThreadAdapter(Context context, List<ParseUser> users, SelectListenerChat selectListenerChat) {
        this.users = users;
        this.context = context;
        this.selectListenerChat = selectListenerChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_thread, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout mChatThread;
        private TextView mChatUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mChatUser = itemView.findViewById(R.id.thread_author);
            mChatThread = itemView.findViewById(R.id.thread_item);
        }

        public void bind(ParseUser user) {
            mChatUser.setText(user.getUsername());
            mChatThread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectListenerChat != null) {
                        selectListenerChat.onItemClicked(user);
                    }
                }
            });
        }
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ParseUser> list) {
        users.addAll(list);
        notifyDataSetChanged();
    }

    public void add(ParseUser user) {
        users.add(user);
        notifyDataSetChanged();
    }
}
