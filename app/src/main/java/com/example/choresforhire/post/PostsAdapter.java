package com.example.choresforhire.post;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.choresforhire.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    public static final String TAG = "PostsAdapter";

    private Context mContext;
    private List<Post> mPosts;
    private SelectListener mSelectListener;

    public PostsAdapter(Context context, List<Post> posts, SelectListener selectListener) {
        this.mContext = context;
        this.mPosts = posts;
        this.mSelectListener = selectListener;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mPay;
        private TextView mTitle;
        private TextView mPoster;
        private TextView mDistance;
        private TextView mDescription;
        private ImageView mProfilePic;
        private ChipGroup mChipGroupHome;
        private ConstraintLayout mItemPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tvTitle);
            mPay = itemView.findViewById(R.id.tvPay);
            mDescription = itemView.findViewById(R.id.tvDescription);
            mDistance = itemView.findViewById(R.id.tvDistance);
            mPoster = itemView.findViewById(R.id.tvPoster);
            mProfilePic = itemView.findViewById(R.id.ivProfilePicPost);
            mItemPost = itemView.findViewById(R.id.itemPost);
            mChipGroupHome = itemView.findViewById(R.id.chipGroupHome);
        }

        public void bind(Post post) {
            mTitle.setText(post.getTitle());
            mPay.setText("$" + post.getPay());
            mDescription.setText(post.getDescription());

            ParseGeoPoint currUserLoc = (ParseGeoPoint) ParseUser.getCurrentUser().get("location");
            ParseGeoPoint postLocation = post.getLocation();

            if (currUserLoc.equals(new ParseGeoPoint(0,0))) {
                mDistance.setText("-- mi");
            } else {
                double distance = currUserLoc.distanceInMilesTo(postLocation);
                mDistance.setText(String.valueOf(Math.round (distance * 100.0) / 100.0) + " mi");
            }

            mPoster.setText(post.getUser().getUsername());

            ParseFile profilePic = (ParseFile) post.getUser().get("profilePic");

            if (profilePic != null) {
                Glide.with(mContext).load(profilePic.getUrl()).into(mProfilePic);
            } else {
                int drawableIdentifier = mContext.getResources().getIdentifier("blank_profile", "drawable", mContext.getPackageName());
                Glide.with(mContext).load(drawableIdentifier).into(mProfilePic);
            }


            mItemPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectListener != null) {
                        mSelectListener.onItemClicked(post);
                    }
                }
            });

            mChipGroupHome.removeAllViews();
            if (post.isAgeRestricted()) {
                Chip ageRestricted = new Chip(mChipGroupHome.getContext());
                ageRestricted.setText("18+");
                mChipGroupHome.addView(ageRestricted);
            }

            if (post.isOneTime()) {
                Chip oneTime = new Chip(mChipGroupHome.getContext());
                oneTime.setText("One-time");
                mChipGroupHome.addView(oneTime);
            }

            if (post.isRecurring()) {
                Chip recurring = new Chip(mChipGroupHome.getContext());
                recurring.setText("Recurring");
                mChipGroupHome.addView(recurring);
            }

        }
    }


    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }

    public void add(Post post) {
        mPosts.add(post);
        notifyDataSetChanged();
    }
}
