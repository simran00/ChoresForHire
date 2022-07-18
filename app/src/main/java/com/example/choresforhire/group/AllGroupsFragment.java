package com.example.choresforhire.group;

import static com.example.choresforhire.home.MainActivity.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.choresforhire.R;
import com.example.choresforhire.chores.ChoresTodoAdapter;
import com.example.choresforhire.home.MainActivity;
import com.example.choresforhire.post.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class AllGroupsFragment extends Fragment {
    private List<Group> mAllGroups;
    private RecyclerView mGroupsView;
    private GroupAdapter mGroupAdapter;

    private FloatingActionButton btnCreateGroup;

    public AllGroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState);

        mGroupsView = view.findViewById(R.id.rvAllGroups);

        mAllGroups = new ArrayList<>();
        mGroupAdapter = new GroupAdapter(getContext(), mAllGroups);

        // set the layout manager on the recycler view
        mGroupsView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // set the adapter on the recycler view
        mGroupsView.setAdapter(mGroupAdapter);
        // query posts
        queryGroups();


        btnCreateGroup = view.findViewById(R.id.fab_create_group);

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) v.getContext();
                CreateGroupFragment fragment = new CreateGroupFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });
    }

    private void queryGroups() {
        ParseQuery<Group> query = ParseQuery.getQuery(Group.class);
        query.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(List<Group> groups, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting chats", e);
                    return;
                }
                mAllGroups.addAll(groups);
                mGroupAdapter.notifyDataSetChanged();
            }
        });
    }
}