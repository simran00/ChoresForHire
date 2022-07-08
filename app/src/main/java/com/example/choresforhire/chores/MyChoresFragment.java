package com.example.choresforhire.chores;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.choresforhire.post.PostsAdapter;
import com.example.choresforhire.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MyChoresFragment extends Fragment {
    public static final String TAG = "MyChoresFragment";

    private List<Post> allPosts;
    private MyChoresAdapter mChoresadapter;
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
        mChoresadapter = new MyChoresAdapter(getContext(), allPosts);

        // set the layout manager on the recycler view
        mMyPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        // set the adapter on the recycler view
        mMyPosts.setAdapter(mChoresadapter);
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
                        mChoresadapter.showMenu(viewHolder.getBindingAdapterPosition());
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
                        mChoresadapter.closeMenu();
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
                            mChoresadapter.closeMenu();
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                allPosts.remove(deletePost);
                mChoresadapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        getParentFragmentManager().setFragmentResultListener("closeMenu", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                mChoresadapter.closeMenu();
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
                mChoresadapter.notifyDataSetChanged();
            }
        });
    }

}