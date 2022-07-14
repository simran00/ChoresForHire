package com.example.choresforhire.post;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.choresforhire.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    public static final String TAG = "PostsAdapter";

    private Context context;
    private List<Post> posts;
    private SelectListener selectListener;

    public PostsAdapter(Context context, List<Post> posts, SelectListener selectListener) {
        this.context = context;
        this.posts = posts;
        this.selectListener = selectListener;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPay;
        private TextView tvTitle;
        private TextView tvPoster;
        private TextView tvDistance;
        private TextView tvDescription;
        private ChipGroup chipGroupHome;
        private ConstraintLayout itemPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPay = itemView.findViewById(R.id.tvPay);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvPoster = itemView.findViewById(R.id.tvPoster);
            itemPost = itemView.findViewById(R.id.itemPost);
            chipGroupHome = itemView.findViewById(R.id.chipGroupHome);
        }

        public void bind(Post post) {
            tvTitle.setText(post.getTitle());
            tvPay.setText("$" + post.getPay());
            tvDescription.setText(post.getDescription());

            ParseGeoPoint currUserLoc = (ParseGeoPoint) ParseUser.getCurrentUser().get("location");
            ParseGeoPoint postLocation = post.getLocation();

            if (currUserLoc.equals(new ParseGeoPoint(0,0))) {
                tvDistance.setText("-- mi");
            } else {
                double distance = currUserLoc.distanceInMilesTo(postLocation);
                tvDistance.setText(String.valueOf(Math.round (distance * 100.0) / 100.0) + " mi");
            }

            tvPoster.setText(post.getUser().getUsername());


            itemPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectListener != null) {
                        selectListener.onItemClicked(post);
                    }
                }
            });

            chipGroupHome.removeAllViews();
            if (post.isAgeRestricted()) {
                Chip ageRestricted = new Chip(chipGroupHome.getContext());
                ageRestricted.setText("18+");
                chipGroupHome.addView(ageRestricted);
            }

            if (post.isOneTime()) {
                Chip oneTime = new Chip(chipGroupHome.getContext());
                oneTime.setText("One-time");
                chipGroupHome.addView(oneTime);
            }

            if (post.isRecurring()) {
                Chip recurring = new Chip(chipGroupHome.getContext());
                recurring.setText("Recurring");
                chipGroupHome.addView(recurring);
            }

        }
    }


    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    public void add(Post post) {
        posts.add(post);
        notifyDataSetChanged();
    }
}
