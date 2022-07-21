package com.example.choresforhire.group;

import static com.example.choresforhire.home.HomeFragment.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.choresforhire.R;
import com.example.choresforhire.home.MainActivity;
import com.example.choresforhire.post.PostDetails;
import com.example.choresforhire.profile.OtherProfile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private Context mContext;
    private List<Group> mGroups;

    public GroupAdapter(Context context, List<Group> groups) {
        this.mContext = context;
        this.mGroups = groups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_card_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = mGroups.get(position);
        try {
            holder.bind(group);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        private Button btnJoin;
        private TextView groupName;
        private TextView groupDescription;

        public GroupViewHolder(@NonNull View view) {
            super(view);
            groupName = view.findViewById(R.id.tvGroupName);
            groupDescription = view.findViewById(R.id.tvGroupDescription);
            btnJoin = view.findViewById(R.id.btnGroupJoin);
        }

        public void bind(Group group) throws JSONException {
            groupName.setText(group.getName());
            groupDescription.setText(group.getDescription());
            ParseUser currUser = ParseUser.getCurrentUser();
            ParseRelation<Group> currGroups = currUser.getRelation("groupsJoined");
            ParseQuery<Group> query = currGroups.getQuery();
            query.whereEqualTo("objectId", group.getObjectId());
            query.findInBackground(new FindCallback<Group>() {
                @Override
                public void done(List<Group> objects, ParseException e) {
                    if (!objects.isEmpty()) {
                        btnJoin.setText("Enter");
                        btnJoin.setBackgroundColor(Color.rgb(79, 121, 66));
                        btnJoin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle args = new Bundle();
                                args.putParcelable("group", group);
                                MainActivity activity = (MainActivity) v.getContext();
                                GroupFragment fragment = new GroupFragment();
                                fragment.setArguments(args);
                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
                            }
                        });
                    } else {
                        btnJoin.setText("Join");
                        btnJoin.setBackgroundColor(Color.rgb(41, 65, 78));
                        btnJoin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                currGroups.add(group);
                                currUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Log.i(TAG, "Group added to user");
                                        Bundle args = new Bundle();
                                        args.putParcelable("group", group);
                                        MainActivity activity = (MainActivity) v.getContext();
                                        GroupFragment fragment = new GroupFragment();
                                        fragment.setArguments(args);
                                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
                                    }
                                });

                            }
                        });
                    }
                }
            });
        }
    }

    public void clear() {
        mGroups.clear();
        notifyDataSetChanged();
    }

    public void add(Group group) {
        mGroups.add(group);
        notifyDataSetChanged();
    }

    public void addAll(List<Group> list) {
        mGroups.addAll(list);
        notifyDataSetChanged();
    }
}
