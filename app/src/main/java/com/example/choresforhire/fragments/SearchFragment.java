package com.example.choresforhire.fragments;

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
import android.widget.SearchView;

import com.example.choresforhire.Post;
import com.example.choresforhire.PostDetails;
import com.example.choresforhire.PostsAdapter;
import com.example.choresforhire.R;
import com.example.choresforhire.SelectListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SelectListener {
    public static final String TAG = "SearchFragment";
    private RecyclerView rvSearch;
    protected PostsAdapter adapter;
    protected List<Post> allPosts;
    private SearchView svSearch;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.onCreate(savedInstanceState);

        rvSearch = view.findViewById(R.id.rvSearch);

        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts, this);

        // set the layout manager on the recycler view
        rvSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        // set the adapter on the recycler view
        rvSearch.setAdapter(adapter);

        svSearch = view.findViewById(R.id.svSearch);

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ParseQuery<Post> queryList = ParseQuery.getQuery(Post.class);
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
                        allPosts.addAll(posts);
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