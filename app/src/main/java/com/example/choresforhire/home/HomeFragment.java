package com.example.choresforhire.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.choresforhire.map.MapsActivity;
import com.example.choresforhire.post.Post;
import com.example.choresforhire.post.PostDetails;
import com.example.choresforhire.post.PostsAdapter;
import com.example.choresforhire.R;
import com.example.choresforhire.post.SelectListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements SelectListener {
    public static final String TAG = "HomeFragment";

    private List<Post> allPosts;
    private SearchView svSearch;
    private PostsAdapter adapter;
    private RecyclerView rvPosts;
    private FloatingActionButton btnMap;
    private SwipeRefreshLayout swipeContainer;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ParseUser.getCurrentUser().get("location") == null) {
            Log.e(TAG, "INVALID LOCATION");
            ParseUser.getCurrentUser().put("location", new ParseGeoPoint(0,0));
        }

        rvPosts = view.findViewById(R.id.rvPosts);
        svSearch = view.findViewById(R.id.svSearch);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                queryPosts();
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // initialize the array that will hold posts and create a PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts, this);

        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // query posts
        queryPosts();

        btnMap = (FloatingActionButton) view.findViewById(R.id.btnMapReturn);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MapsActivity.class);
                startActivity(i);

            }
        });

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ParseQuery<Post> queryList = ParseQuery.getQuery(Post.class);
                queryList.include(Post.KEY_USER);
                queryList.whereStartsWith("title", query);
                queryList.whereNotEqualTo("user", ParseUser.getCurrentUser());
                queryList.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> posts, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with getting posts", e);
                            return;
                        }
                        allPosts.clear();

                        // check if post has already been accepted
                        for (int i = 0; i < posts.size(); i++) {
                            if (posts.get(i).getAccepted() == null) {
                                allPosts.add(posts.get(i));
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // exclude current user in feed
        query.whereNotEqualTo("user", ParseUser.getCurrentUser());
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

                allPosts.clear();

                // check if post has already been accepted
                for (int i = 0; i < posts.size(); i++) {
                    if (posts.get(i).getAccepted() == null) {
                        allPosts.add(posts.get(i));
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClicked(Post post) {
        Intent i = new Intent(getContext(), PostDetails.class);
        i.putExtra("post", post);
        i.putExtra("title", (CharSequence) post.get("title"));
        i.putExtra("pay", String.valueOf(post.get("pay")));
        i.putExtra("description", post.getDescription());
        startActivity(i);
    }
}