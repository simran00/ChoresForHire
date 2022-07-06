package com.example.choresforhire.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment implements SelectListenerChat {
    public static final String TAG = "ChatsFragment";

    private List<ParseUser> allChats;
    private ThreadAdapter adapter;
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

        allChats = new ArrayList<>();
        adapter = new ThreadAdapter(getContext(), allChats, this);

        mChats.setLayoutManager(new LinearLayoutManager(getContext()));

        mChats.setAdapter(adapter);

        queryChatUsers();

    }

    private void queryChatUsers() {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting chats", e);
                    return;
                }
                allChats.addAll(users);
                adapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    public void onItemClicked(ParseUser user) {
        Intent i = new Intent(getContext(), ChatActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }
}