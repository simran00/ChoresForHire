package com.example.choresforhire.chores;

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

import com.example.choresforhire.post.Post;
import com.example.choresforhire.post.PostDetails;
import com.example.choresforhire.post.PostsAdapter;
import com.example.choresforhire.R;
import com.example.choresforhire.post.SelectListener;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ToDoChoresFragment extends Fragment implements SelectListener {
    public static final String TAG = "ToDoChoresFragment";
    private static final String REC_POSTS_LABEL = "recPosts";
    private static final String TODO_CHORES_LABEL = "todoChores";

    private RecyclerView mRecPosts;
    private RecyclerView mTodoPosts;
    private List<Post> mAllRecPosts;
    private List<Post> mAllTodoPosts;
    private PostsAdapter mRecAdapter;
    private ChoresTodoAdapter mToDoAdapter;

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

        mRecPosts = view.findViewById(R.id.rvRecommended);
        mAllRecPosts = new ArrayList<>();
        mRecAdapter = new PostsAdapter(getContext(), mAllRecPosts, this);
        mRecPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecPosts.setAdapter(mRecAdapter);

        queryTodoPosts();

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

        cacheToDo(query);
    }

    private void cacheToDo(ParseQuery<Post> query) {
        //  query from cache
        query.fromPin(TODO_CHORES_LABEL).findInBackground().continueWithTask((task) -> {
            // Update UI with results from Local Datastore
            ParseException error = (ParseException) task.getError();
            if (error == null){
                List<Post> posts = task.getResult();

                // query recommended based on tasks already accepted
                queryRecPosts(posts);


                mAllTodoPosts.clear();
                mAllTodoPosts.addAll(posts);
                mToDoAdapter.notifyDataSetChanged();
            }
            // Now query the network:
            return query.fromNetwork().findInBackground();
        }, ContextCompat.getMainExecutor(this.requireContext())).continueWithTask(task -> {
            // Update UI with results from Network
            ParseException error = (ParseException) task.getError();
            if(error == null){
                List<Post> posts = task.getResult();
                // Release any objects previously pinned for this query.
                Post.unpinAllInBackground(TODO_CHORES_LABEL, posts, new DeleteCallback() {
                    public void done(ParseException e) {
                        if (e != null) {
                            // There was some error.
                            return;
                        }

                        // Add the latest results for this query to the cache.
                        Post.pinAllInBackground(TODO_CHORES_LABEL, posts);
                        mAllTodoPosts.clear();
                        mAllTodoPosts.addAll(posts);
                        mToDoAdapter.notifyDataSetChanged();
                    }
                });
            }
            return task;
        }, ContextCompat.getMainExecutor(this.requireContext()));
    }

    private void queryRecPosts(List<Post> toDoPosts) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereNotEqualTo("user", ParseUser.getCurrentUser());
        query.whereNotEqualTo("accepted", ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.setLimit(3);

        if (toDoPosts.isEmpty()) {
            query.whereWithinMiles("location", ParseUser.getCurrentUser().getParseGeoPoint("location"), 100);
        } else {
            Random rand = new Random();
            int randIndex = rand.nextInt(toDoPosts.size());
            if (toDoPosts.get(randIndex).isAgeRestricted()) {
                query.whereEqualTo("overEighteen", true);
            } else if (toDoPosts.get(randIndex).isOneTime()) {
                query.whereEqualTo("oneTime", true);
            } else if (toDoPosts.get(randIndex).isRecurring()) {
                query.whereEqualTo("recurring", true);
            }
        }
        cacheRec(query);

    }

    private void cacheRec(ParseQuery<Post> query) {
        //  query from cache
        query.fromPin(REC_POSTS_LABEL).findInBackground().continueWithTask((task) -> {
            // Update UI with results from Local Datastore
            ParseException error = (ParseException) task.getError();
            if (error == null){
                List<Post> posts = task.getResult();
                mAllRecPosts.clear();
                for (int i = 0; i < posts.size(); i++) {
                    if (posts.get(i).getAccepted() == null) {
                        mAllRecPosts.add(posts.get(i));
                    }
                }
                mRecAdapter.notifyDataSetChanged();
            }
            // Now query the network:
            return query.fromNetwork().findInBackground();
        }, ContextCompat.getMainExecutor(this.requireContext())).continueWithTask(task -> {
            // Update UI with results from Network
            ParseException error = (ParseException) task.getError();
            if(error == null){
                List<Post> posts = task.getResult();
                // Release any objects previously pinned for this query.
                Post.unpinAllInBackground(REC_POSTS_LABEL, posts, new DeleteCallback() {
                    public void done(ParseException e) {
                        if (e != null) {
                            // There was some error.
                            return;
                        }

                        // Add the latest results for this query to the cache.
                        Post.pinAllInBackground(REC_POSTS_LABEL, posts);
                        mAllRecPosts.clear();
                        // check if post has already been accepted
                        for (int i = 0; i < posts.size(); i++) {
                            if (posts.get(i).getAccepted() == null) {
                                mAllRecPosts.add(posts.get(i));
                            }
                        }
                        mRecAdapter.notifyDataSetChanged();
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