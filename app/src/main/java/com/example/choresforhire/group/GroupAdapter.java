package com.example.choresforhire.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.choresforhire.R;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONException;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private Context context;
    private List<Group> groups;

    public GroupAdapter(Context context, List<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_card_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groups.get(position);
        try {
            holder.bind(group);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        private TextView groupName;
        private TextView groupDescription;
        private Button btnJoin;

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

            btnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currGroups.add(group);
                    currUser.saveInBackground();
//                    currUser.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            Log.i(TAG, "Group added to user");
//                        }
//                    });
////
//                    if (group.getMembers() == null) {
//                        group.setMembers(new JSONArray());
//                    }
//                    // add to array of members already in group
//                    group.setMembers(group.getMembers().put(currUser));
//                    group.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            Toast.makeText(v.getContext(), "Joined!", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }
            });
        }
    }

    public void clear() {
        groups.clear();
        notifyDataSetChanged();
    }

    public void add(Group group) {
        groups.add(group);
        notifyDataSetChanged();
    }

    public void addAll(List<Group> list) {
        groups.addAll(list);
        notifyDataSetChanged();
    }
}
