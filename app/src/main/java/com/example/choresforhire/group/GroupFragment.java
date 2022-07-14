package com.example.choresforhire.group;

import static com.example.choresforhire.home.MainActivity.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.choresforhire.R;
import com.example.choresforhire.post.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {
    private List<GroupPost> mAllPosts;
    private GroupPostAdapter mGroupPostAdapter;
    private RecyclerView mPostsView;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.onCreate(savedInstanceState);

        mPostsView = view.findViewById(R.id.rvGroupPosts);

        mAllPosts = new ArrayList<>();
        mGroupPostAdapter = new GroupPostAdapter(getContext(), mAllPosts);

        mPostsView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPostsView.setAdapter(mGroupPostAdapter);

        queryPosts();
    }

    private void queryPosts() {
        ParseQuery<GroupPost> query = ParseQuery.getQuery(GroupPost.class);
        query.include(GroupPost.KEY_GROUP);
//        query.whereEqualTo("group", currGroup);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<GroupPost>() {
            @Override
            public void done(List<GroupPost> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                mAllPosts.addAll(posts);
                mGroupPostAdapter.notifyDataSetChanged();
            }
        });
    }
}