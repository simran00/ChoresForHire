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
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ChoresTodoAdapter extends RecyclerView.Adapter<ChoresTodoAdapter.ToDoViewHolder>{
    private Context context;
    private List<Post> posts;

    public ChoresTodoAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ChoresTodoAdapter.ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chores_todo_item, parent, false);
        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoresTodoAdapter.ToDoViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ToDoViewHolder extends RecyclerView.ViewHolder {
        private TextView todoPay;
        private TextView todoTitle;
        private TextView todoPoster;
        private TextView todoDistance;
        private CheckBox todoCheckBox;
        private TextView todoDescription;

        public ToDoViewHolder(@NonNull View itemView) {
            super(itemView);
            todoPay = itemView.findViewById(R.id.todoPay);
            todoTitle = itemView.findViewById(R.id.todoTitle);
            todoPoster = itemView.findViewById(R.id.todoPoster);
            todoDistance = itemView.findViewById(R.id.todoDistance);
            todoCheckBox = itemView.findViewById(R.id.todoCheckBox);
            todoDescription = itemView.findViewById(R.id.todoDescription);
        }

        public void bind(Post post) {
            todoTitle.setText(post.getTitle());
            todoPay.setText("$" + post.getPay());
            todoDescription.setText(post.getDescription());

            ParseGeoPoint currUserLoc = (ParseGeoPoint) ParseUser.getCurrentUser().get("location");
            ParseGeoPoint postLocation = post.getLocation();

            if (currUserLoc.equals(new ParseGeoPoint(0,0))) {
                todoDistance.setText("-- km");
            } else {
                double distance = currUserLoc.distanceInKilometersTo(postLocation);
                todoDistance.setText(String.valueOf(Math.round (distance * 100.0) / 100.0) + " km");
            }

            todoPoster.setText(post.getUser().getUsername());

            if (post.isCompleted()) {
                todoCheckBox.setChecked(true);
            }

            todoCheckBox.setOnClickListener(new View.OnClickListener() {
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

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void add(Post post) {
        posts.add(post);
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
}
