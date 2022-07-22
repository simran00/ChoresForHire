package com.example.choresforhire.group;

import static com.example.choresforhire.home.HomeFragment.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.choresforhire.R;
import com.example.choresforhire.home.MainActivity;
import com.example.choresforhire.post.PostDetails;
import com.example.choresforhire.profile.OtherProfile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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
        private Button mBtnJoin;
        private ImageView mGroupPic;
        private TextView mGroupName;
        private TextView mGroupDescription;

        public GroupViewHolder(@NonNull View view) {
            super(view);
            mGroupName = view.findViewById(R.id.tvGroupName);
            mGroupPic = view.findViewById(R.id.tvGroupImage);
            mGroupDescription = view.findViewById(R.id.tvGroupDescription);
            mBtnJoin = view.findViewById(R.id.btnGroupJoin);
        }

        public void bind(Group group) throws JSONException {
            mGroupName.setText(group.getName());
            mGroupDescription.setText(group.getDescription());
            ParseUser currUser = ParseUser.getCurrentUser();

            ParseFile groupPic = (ParseFile) group.get("groupPic");

            if (groupPic != null) {
                Glide.with(mContext).load(groupPic.getUrl()).into(mGroupPic);
            } else {
                int drawableIdentifier = (mContext).getResources().getIdentifier("broom", "drawable", (mContext).getPackageName());
                Glide.with(mContext).load(drawableIdentifier).into(mGroupPic);
            }

            ParseRelation<Group> currGroups = currUser.getRelation("groupsJoined");
            ParseQuery<Group> query = currGroups.getQuery();
            query.whereEqualTo("objectId", group.getObjectId());
            query.addDescendingOrder("createdAt");
            query.findInBackground(new FindCallback<Group>() {
                @Override
                public void done(List<Group> objects, ParseException e) {
                    if (!objects.isEmpty()) {
                        mBtnJoin.setText("Enter");
                        mBtnJoin.setBackgroundColor(Color.rgb(79, 121, 66));
                        mBtnJoin.setOnClickListener(new View.OnClickListener() {
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
                        mBtnJoin.setText("Join");
                        mBtnJoin.setBackgroundColor(Color.rgb(41, 65, 78));
                        mBtnJoin.setOnClickListener(new View.OnClickListener() {
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
