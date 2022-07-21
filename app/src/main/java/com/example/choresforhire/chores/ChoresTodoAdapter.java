package com.example.choresforhire.chores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.choresforhire.R;
import com.example.choresforhire.post.Post;
import com.example.choresforhire.post.PostDetails;
import com.example.choresforhire.post.PostsAdapter;
import com.example.choresforhire.post.SelectListener;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ChoresTodoAdapter extends RecyclerView.Adapter<ChoresTodoAdapter.ToDoViewHolder>{
    private Context mContext;
    private List<Post> mPosts;

    public ChoresTodoAdapter(Context context, List<Post> posts) {
        this.mContext = context;
        this.mPosts = posts;
    }

    @NonNull
    @Override
    public ChoresTodoAdapter.ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chores_todo_item, parent, false);
        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoresTodoAdapter.ToDoViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ToDoViewHolder extends RecyclerView.ViewHolder {
        private TextView mTodoPay;
        private TextView mTodoTitle;
        private TextView mTodoPoster;
        private TextView mTodoDistance;
        private CheckBox mTodoCheckBox;
        private TextView mTodoDescription;

        public ToDoViewHolder(@NonNull View itemView) {
            super(itemView);
            mTodoPay = itemView.findViewById(R.id.todoPay);
            mTodoTitle = itemView.findViewById(R.id.todoTitle);
            mTodoPoster = itemView.findViewById(R.id.todoPoster);
            mTodoDistance = itemView.findViewById(R.id.todoDistance);
            mTodoCheckBox = itemView.findViewById(R.id.todoCheckBox);
            mTodoDescription = itemView.findViewById(R.id.todoDescription);
        }

        public void bind(Post post) {
            mTodoTitle.setText(post.getTitle());
            mTodoPay.setText("$" + post.getPay());
            mTodoDescription.setText(post.getDescription());

            ParseGeoPoint currUserLoc = (ParseGeoPoint) ParseUser.getCurrentUser().get("location");
            ParseGeoPoint postLocation = post.getLocation();

            if (currUserLoc.equals(new ParseGeoPoint(0,0))) {
                mTodoDistance.setText("-- km");
            } else {
                double distance = currUserLoc.distanceInKilometersTo(postLocation);
                mTodoDistance.setText(String.valueOf(Math.round (distance * 100.0) / 100.0) + " km");
            }

            mTodoPoster.setText(post.getUser().getUsername());

            if (post.isCompleted()) {
                mTodoCheckBox.setChecked(true);
            }

            mTodoCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {
                        post.setCompleted(true);
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(v.getContext(), "Finished!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        pushNotification(post);
                    } else {
                        post.setCompleted(false);
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(v.getContext(), "Undo", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    private void pushNotification(Post post) {
        JSONObject data = new JSONObject();
        // Put data in the JSON object
        try {
            data.put("alert", post.getAccepted().getUsername() + " completed your chore!");
            data.put("title", "Completed: " + post.getTitle());
        } catch ( JSONException error) {
            // should not happen
            throw new IllegalArgumentException("unexpected parsing error", error);
        }

        // Configure the push
        ParsePush push = new ParsePush();
        // push to post author
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereEqualTo("user", post.getUser());
        push.setQuery(query);
        push.setData(data);
        push.sendInBackground();
    }

    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    public void add(Post post) {
        mPosts.add(post);
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }
}
