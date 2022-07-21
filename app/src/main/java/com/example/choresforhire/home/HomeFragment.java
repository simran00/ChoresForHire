package com.example.choresforhire.home;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import com.google.android.material.textfield.TextInputLayout;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements SelectListener {
    public static final String TAG = "HomeFragment";
    private final String MY_HOME_LABEL = "myHome";

    private SearchView svSearch;
    private RadioGroup mSorting;
    private List<Post> mAllPosts;
    private RecyclerView rvPosts;
    private PostsAdapter mPostsAdapter;
    private TextInputLayout mTagMenu;
    private FloatingActionButton btnMap;
    private AutoCompleteTextView mTagOptions;
    private SwipeRefreshLayout swipeContainer;

    private boolean statusRadioBtn;

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

        statusRadioBtn = true;

        rvPosts = view.findViewById(R.id.rvPosts);
        svSearch = view.findViewById(R.id.svSearch);
        mSorting = view.findViewById(R.id.sortRadioGroup);

        mTagOptions = view.findViewById(R.id.autoCompleteTag);
        mTagMenu = view.findViewById(R.id.tagMenu);

        ArrayList<String> mOptions = new ArrayList<>();
        mOptions.add("18+");
        mOptions.add("One-time");
        mOptions.add("Recurring");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.sort_list_item, mOptions);
        mTagOptions.setAdapter(arrayAdapter);

        mTagOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    queryPostsByTime(statusRadioBtn, "18+");
                }
                if (position == 1) {
                    queryPostsByTime(statusRadioBtn, "One-time");
                }
                if (position == 2) {
                    queryPostsByTime(statusRadioBtn, "Recurring");
                }
            }
        });


        mSorting.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.recentRadio:
                        statusRadioBtn = true;
                        break;
                    case R.id.distRadio:
                        statusRadioBtn = false;
                        break;
                }
                queryPostsByTime(statusRadioBtn, "");
            }
        });


        swipeContainer = view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPostsByTime(statusRadioBtn, "");
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // initialize the array that will hold posts and create a PostsAdapter
        mAllPosts = new ArrayList<>();
        mPostsAdapter = new PostsAdapter(getContext(), mAllPosts, this);

        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        // set the adapter on the recycler view
        rvPosts.setAdapter(mPostsAdapter);
        // query posts
        queryPostsByTime(statusRadioBtn, "");

        btnMap = view.findViewById(R.id.btnMapReturn);

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
                queryList.whereFullText("title", query);
                queryList.whereNotEqualTo("user", ParseUser.getCurrentUser());

                queryList.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> posts, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with getting posts", e);
                            return;
                        }
                        mAllPosts.clear();

                        // check if post has already been accepted
                        for (int i = 0; i < posts.size(); i++) {
                            if (posts.get(i).getAccepted() == null) {
                                mAllPosts.add(posts.get(i));
                            }
                        }

                        mPostsAdapter.notifyDataSetChanged();
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

    private void queryPostsByTime(boolean statusRadioBtn, String tag) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // exclude current user in feed
        query.whereNotEqualTo("user", ParseUser.getCurrentUser());

        if (statusRadioBtn) {
            // order posts by time created
            query.addDescendingOrder("createdAt");
            switch (tag) {
                case "18+":
                    query.whereEqualTo(Post.KEY_AGE, true);
                    break;
                case "One-time":
                    query.whereEqualTo(Post.KEY_ONE_TIME, true);
                    break;
                case "Recurring":
                    query.whereEqualTo(Post.KEY_RECURRING, true);
                    break;
            }
        } else {
            // order posts by distance
            query.whereWithinMiles("location", ParseUser.getCurrentUser().getParseGeoPoint("location"), 100);
            switch (tag) {
                case "18+":
                    query.whereEqualTo(Post.KEY_AGE, true);
                    break;
                case "One-time":
                    query.whereEqualTo(Post.KEY_ONE_TIME, true);
                    break;
                case "Recurring":
                    query.whereEqualTo(Post.KEY_RECURRING, true);
                    break;
            }
        }

        //  query from cache
        query.fromPin(MY_HOME_LABEL).findInBackground().continueWithTask((task) -> {
            // Update UI with results from Local Datastore
            ParseException error = (ParseException) task.getError();
            if (error == null){
                List<Post> posts = task.getResult();
                mAllPosts.clear();
                for (int i = 0; i < posts.size(); i++) {
                    if (posts.get(i).getAccepted() == null) {
                        mAllPosts.add(posts.get(i));
                    }
                }
                mPostsAdapter.notifyDataSetChanged();
            }
            // Now query the network:
            return query.fromNetwork().findInBackground();
        }, ContextCompat.getMainExecutor(this.requireContext())).continueWithTask(task -> {
            // Update UI with results from Network
            ParseException error = (ParseException) task.getError();
            if(error == null){
                List<Post> posts = task.getResult();
                // Release any objects previously pinned for this query.
                Post.unpinAllInBackground(MY_HOME_LABEL, posts, new DeleteCallback() {
                    public void done(ParseException e) {
                        if (e != null) {
                            // There was some error.
                            return;
                        }

                        // Add the latest results for this query to the cache.
                        Post.pinAllInBackground(MY_HOME_LABEL, posts);
                        mAllPosts.clear();
                        // check if post has already been accepted
                        for (int i = 0; i < posts.size(); i++) {
                            if (posts.get(i).getAccepted() == null) {
                                mAllPosts.add(posts.get(i));
                            }
                        }
                        mPostsAdapter.notifyDataSetChanged();
                    }
                });
            }
            return task;
        }, ContextCompat.getMainExecutor(this.requireContext()));
    }

    @Override
    public void onItemClicked(Post post) {
        Intent i = new Intent(getContext(), PostDetails.class);
        i.putExtra("post", post);
        startActivity(i);
    }
}