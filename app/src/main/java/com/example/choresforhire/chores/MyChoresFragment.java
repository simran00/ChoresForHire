package com.example.choresforhire.chores;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.choresforhire.post.Post;
import com.example.choresforhire.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.boltsinternal.Task;

import java.util.ArrayList;
import java.util.List;

public class MyChoresFragment extends Fragment {
    public static final String TAG = "MyChoresFragment";

    private final String MY_CHORES_LABEL = "myChores";
    private List<Post> allPosts;
    private MyChoresAdapter mChoresAdapter;
    private RecyclerView mMyPosts;

    public MyChoresFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_chores, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.onCreate(savedInstanceState);

        mMyPosts = view.findViewById(R.id.rvMyPosts);

        allPosts = new ArrayList<>();
        mChoresAdapter = new MyChoresAdapter(getContext(), allPosts);

        // set the layout manager on the recycler view
        mMyPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        // set the adapter on the recycler view
        mMyPosts.setAdapter(mChoresAdapter);
        // query posts
        queryPosts();

        ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private final ColorDrawable background = new ColorDrawable(0xFFFF6666);

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                mMyPosts.post(new Runnable() {
                    public void run() {
                        mChoresAdapter.showMenu(viewHolder.getBindingAdapterPosition());
                    }
                });
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;

                if (dX > 0) {
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                } else if (dX < 0) {
                    background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mMyPosts);

        mMyPosts.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                mMyPosts.post(new Runnable() {
                    public void run() {
                        mChoresAdapter.closeMenu();
                    }
                });
            }
        });

        getParentFragmentManager().setFragmentResultListener("postDelete", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                Post deletePost = (Post) bundle.get("post");
                ParseQuery<Post> query = ParseQuery.getQuery("Post");
                query.getInBackground(deletePost.getObjectId(), new GetCallback<Post>() {
                    @Override
                    public void done(Post object, ParseException e) {
                        try {
                            object.delete();
                            object.saveInBackground();
                            mChoresAdapter.closeMenu();
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                allPosts.remove(deletePost);
                mChoresAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        getParentFragmentManager().setFragmentResultListener("closeMenu", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                mChoresAdapter.closeMenu();
            }
        });
    }

    private void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // include current user in feed
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");


        // diffutil
       //  query from cache
        query.fromPin(MY_CHORES_LABEL).findInBackground().continueWithTask((task) -> {
            // Update UI with results from Local Datastore
            ParseException error = (ParseException) task.getError();
            if (error == null){
                List<Post> posts = task.getResult();
                allPosts.clear();
                allPosts.addAll(posts);
                mChoresAdapter.notifyDataSetChanged();
            }
            // Now query the network:
            return query.fromNetwork().findInBackground();
        }, ContextCompat.getMainExecutor(this.requireContext())).continueWithTask(task -> {
            // Update UI with results from Network
            ParseException error = (ParseException) task.getError();
            if(error == null){
                List<Post> posts = task.getResult();
                // Release any objects previously pinned for this query.
                Post.unpinAllInBackground(MY_CHORES_LABEL, posts, new DeleteCallback() {
                    public void done(ParseException e) {
                        if (e != null) {
                            // There was some error.
                            return;
                        }

                        // Add the latest results for this query to the cache.
                        Post.pinAllInBackground(MY_CHORES_LABEL, posts);
                        allPosts.clear();
                        allPosts.addAll(posts);
                        mChoresAdapter.notifyDataSetChanged();
                    }
                });
            }
            return task;
        }, ContextCompat.getMainExecutor(this.requireContext()));

//        query.fromLocalDatastore().findInBackground()
//                .continueWithTask((task) -> {
//                    ParseException error = (ParseException) task.getError();
//                    if (error != null || task.getResult().size() == 0) {
//                        return query.fromNetwork().findInBackground();
//                    }
//                    Log.d("Cache", "" + task.getResult().size());
//                    allPosts.clear();
//                    allPosts.addAll(task.getResult());
//                    mChoresAdapter.notifyDataSetChanged();
//                    return task;
//                }, Task.UI_THREAD_EXECUTOR)
//                .continueWithTask((task) -> {
//                    // Update UI with results ...
//                    Log.d("Network", "" + task.getResult().size());
//                    ParseException error = (ParseException) task.getError();
//                    if (error == null) {
//                        Post.unpinAllInBackground(MY_CHORES_LABEL, task.getResult(), new DeleteCallback() {
//                        public void done(ParseException e) {
//                            if (e != null) {
//                                // There was some error.
//                                return;
//                            }
//
//                            // Add the latest results for this query to the cache.
//                            Post.pinAllInBackground(task.getResult());
//                            allPosts.clear();
//                            allPosts.addAll(task.getResult());
//                            mChoresAdapter.notifyDataSetChanged();
//                        }
//                    });
//                    }
//                    return task;
//                }, Task.UI_THREAD_EXECUTOR);



//
//        query.fromLocalDatastore().findInBackground().continueWithTask((task) -> {
//            ParseException error = (ParseException) task.getError();
//            if (error instanceof ParseException && ((ParseException) error).getCode() == ParseException.CACHE_MISS) {
//                // No results from cache. Let's query the network.
//                error = (ParseException) task.getError();
//                if (error == null){
//                    Log.e(TAG, "Query from network");
//                    List<Post> posts = task.getResult();
//                    Post.unpinAllInBackground(MY_CHORES_LABEL, posts, new DeleteCallback() {
//                        public void done(ParseException e) {
//                            if (e != null) {
//                                // There was some error.
//                                return;
//                            }
//
//                            // Add the latest results for this query to the cache.
//                            Post.pinAllInBackground(posts);
//                            allPosts.addAll(posts);
//                            mChoresAdapter.notifyDataSetChanged();
//                        }
//                    });
//                }
//                return query.fromNetwork().findInBackground();
//            }
//            return task;
//        }).continueWithTask((task) -> {
//            return task;
//        }, ContextCompat.getMainExecutor(this.requireContext()));


    }

}