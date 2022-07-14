package com.example.choresforhire.chores;

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

import com.example.choresforhire.post.Post;
import com.example.choresforhire.post.PostDetails;
import com.example.choresforhire.post.PostsAdapter;
import com.example.choresforhire.R;
import com.example.choresforhire.post.SelectListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ToDoChoresFragment extends Fragment implements SelectListener {
    public static final String TAG = "ToDoChoresFragment";

    private List<Post> mAllTodoPosts;
    private List<Post> mAllRecPosts;
    private ChoresTodoAdapter mToDoAdapter;
    private PostsAdapter mRecAdapter;
    private RecyclerView mTodoPosts;
    private RecyclerView mRecPosts;

    public ToDoChoresFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_do_chores, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.onCreate(savedInstanceState);

        mTodoPosts = view.findViewById(R.id.rvTodoPosts);
        mAllTodoPosts = new ArrayList<>();
        mToDoAdapter = new ChoresTodoAdapter(getContext(), mAllTodoPosts);
        mTodoPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        mTodoPosts.setAdapter(mToDoAdapter);
        queryTodoPosts();


        mRecPosts = view.findViewById(R.id.rvRecommended);
        mAllRecPosts = new ArrayList<>();
        mRecAdapter = new PostsAdapter(getContext(), mAllRecPosts, this);
        mRecPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecPosts.setAdapter(mRecAdapter);
        queryRecPosts();
    }

    private void queryTodoPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_ACCEPTED);
        query.include(Post.KEY_USER);
        // get accepted chores
        query.whereEqualTo("accepted", ParseUser.getCurrentUser());
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // save received posts to list and notify adapter of new data
                mAllTodoPosts.addAll(posts);
                mToDoAdapter.notifyDataSetChanged();
            }
        });
    }

    private void queryRecPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereNotEqualTo("user", ParseUser.getCurrentUser());
        query.whereNotEqualTo("accepted", ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.whereWithinMiles("location", ParseUser.getCurrentUser().getParseGeoPoint("location"), 100);
        query.setLimit(3);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                mAllRecPosts.addAll(posts);
                mRecAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClicked(Post post) {
        Intent i = new Intent(getContext(), PostDetails.class);
        i.putExtra("post", post);
        startActivity(i);
    }
}