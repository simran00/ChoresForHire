package com.example.choresforhire.chores;

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
import com.example.choresforhire.post.PostsAdapter;
import com.example.choresforhire.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ToDoChoresFragment extends Fragment {
    public static final String TAG = "ToDoChoresFragment";

    private List<Post> allPosts;
    private ChoresTodoAdapter adapter;
    private RecyclerView rvMyPosts;

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

        rvMyPosts = view.findViewById(R.id.rvMyPosts);

        allPosts = new ArrayList<>();
        adapter = new ChoresTodoAdapter(getContext(), allPosts);

        // set the layout manager on the recycler view
        rvMyPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        // set the adapter on the recycler view
        rvMyPosts.setAdapter(adapter);
        // query posts
        queryPosts();
    }

    private void queryPosts() {
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
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}