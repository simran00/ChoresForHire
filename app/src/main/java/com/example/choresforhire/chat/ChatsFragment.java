package com.example.choresforhire.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.choresforhire.chat.ChatActivity;
import com.example.choresforhire.R;
import com.example.choresforhire.post.Post;
import com.example.choresforhire.post.PostsAdapter;
import com.example.choresforhire.post.SelectListener;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment implements SelectListenerChat {
    public static final String TAG = "ChatsFragment";
    private static final String CHAT_LABEL = "myChats";

    private List<ParseUser> mAllChats;
    private ThreadAdapter mThreadAdapter;
    private RecyclerView mChats;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.onCreate(savedInstanceState);

        mChats = view.findViewById(R.id.rvChatThreads);

        mAllChats = new ArrayList<>();
        mThreadAdapter = new ThreadAdapter(getContext(), mAllChats, this);

        mChats.setLayoutManager(new LinearLayoutManager(getContext()));

        mChats.setAdapter(mThreadAdapter);

        queryChatUsers();

    }

    private void queryChatUsers() {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);

        //  query from cache
        query.fromPin(CHAT_LABEL).findInBackground().continueWithTask((task) -> {
            // Update UI with results from Local Datastore
            ParseException error = (ParseException) task.getError();
            if (error == null){
                List<ParseUser> users = task.getResult();
                mAllChats.clear();
                mAllChats.addAll(users);
                mThreadAdapter.notifyDataSetChanged();
            }
            // Now query the network:
            return query.fromNetwork().findInBackground();
        }, ContextCompat.getMainExecutor(this.requireContext())).continueWithTask(task -> {
            // Update UI with results from Network
            ParseException error = (ParseException) task.getError();
            if(error == null){
                List<ParseUser> users = task.getResult();
                // Release any objects previously pinned for this query.
                Post.unpinAllInBackground(CHAT_LABEL, users, new DeleteCallback() {
                    public void done(ParseException e) {
                        if (e != null) {
                            // There was some error.
                            return;
                        }

                        // Add the latest results for this query to the cache.
                        Post.pinAllInBackground(CHAT_LABEL, users);
                        mAllChats.clear();
                        mAllChats.addAll(users);
                        mThreadAdapter.notifyDataSetChanged();
                    }
                });
            }
            return task;
        }, ContextCompat.getMainExecutor(this.requireContext()));

    }

    @Override
    public void onItemClicked(ParseUser user) {
        Intent i = new Intent(getContext(), ChatActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }
}