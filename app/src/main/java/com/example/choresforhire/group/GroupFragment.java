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
import android.widget.Button;
import android.widget.EditText;

import com.example.choresforhire.R;
import com.example.choresforhire.post.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {
    private Group currGroup;
    private Button mBtnPost;
    private Button mBtnLeave;
    private RecyclerView mPostsView;
    private EditText mComposeMessage;
    private List<GroupPost> mAllPosts;
    private GroupPostAdapter mGroupPostAdapter;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currGroup = getArguments().getParcelable("group");
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.onCreate(savedInstanceState);

        mBtnPost = view.findViewById(R.id.btnPostGroup);
        mBtnLeave = view.findViewById(R.id.btnLeaveGroup);
        mComposeMessage = view.findViewById(R.id.etComposeGroupPost);

        mPostsView = view.findViewById(R.id.rvGroupPosts);

        mAllPosts = new ArrayList<>();
        mGroupPostAdapter = new GroupPostAdapter(getContext(), mAllPosts);

        mPostsView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPostsView.setAdapter(mGroupPostAdapter);

        queryPosts();

        mBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupPost post = new GroupPost();
                post.setUser(ParseUser.getCurrentUser());
                post.setGroup(currGroup);
                post.setDescription(mComposeMessage.getText().toString());
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.i(TAG, "Created post in group!");
                        queryPosts();
                    }
                });
            }
        });

        mBtnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseRelation<Group> currGroups = ParseUser.getCurrentUser().getRelation("groupsJoined");
                currGroups.remove(currGroup);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.i(TAG, "Removed user");
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, new AllGroupsFragment()).commit();
                    }
                });
            }
        });
    }

    private void queryPosts() {
        ParseQuery<GroupPost> query = ParseQuery.getQuery(GroupPost.class);
        query.include(GroupPost.KEY_GROUP);
        query.include(GroupPost.KEY_USER);
        query.whereEqualTo("group", currGroup);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<GroupPost>() {
            @Override
            public void done(List<GroupPost> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                mAllPosts.clear();
                mAllPosts.addAll(posts);
                mGroupPostAdapter.notifyDataSetChanged();
            }
        });
    }
}