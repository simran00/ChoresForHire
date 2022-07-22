package com.example.choresforhire.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.choresforhire.R;
import com.example.choresforhire.chores.ChoresTodoAdapter;
import com.example.choresforhire.chores.MyChoresAdapter;
import com.example.choresforhire.home.MainActivity;
import com.example.choresforhire.post.Post;
import com.example.choresforhire.post.PostDetails;
import com.example.choresforhire.post.PostsAdapter;
import com.example.choresforhire.post.SelectListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class OtherProfile extends AppCompatActivity implements SelectListener {

    public static final String TAG = "OtherProfile";

    private ImageButton mCancel;
    private ParseUser mOtherUser;
    private List<Post> mAllPosts;
    private PostsAdapter mChoresAdapter;
    private RecyclerView mTheirChoresView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_profile);

        TextView mProfileName;
        ImageView mProfilePic;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mOtherUser = (ParseUser) extras.get("user");
            }
        } else {
            mOtherUser = (ParseUser) savedInstanceState.getSerializable("user");
        }

        mAllPosts = new ArrayList<>();
        mChoresAdapter = new PostsAdapter(OtherProfile.this, mAllPosts, this);

        mProfileName = findViewById(R.id.profileNameOther);
        mProfilePic = findViewById(R.id.ivProfilePicPost);
        mTheirChoresView = findViewById(R.id.rvTheirChores);
        mCancel = findViewById(R.id.btnProfileExit);

        mProfileName.setText(mOtherUser.getUsername());

        ParseFile profilePic = (ParseFile) mOtherUser.get("profilePic");

        if (profilePic != null) {
            Glide.with(OtherProfile.this).load(profilePic.getUrl()).into(mProfilePic);
        } else {
            int drawableIdentifier = (OtherProfile.this).getResources().getIdentifier("blank_profile", "drawable", (OtherProfile.this).getPackageName());
            Glide.with(OtherProfile.this).load(drawableIdentifier).into(mProfilePic);
        }

        mTheirChoresView.setLayoutManager(new LinearLayoutManager(OtherProfile.this));
        mTheirChoresView.setAdapter(mChoresAdapter);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OtherProfile.this, MainActivity.class);
                startActivity(i);
            }
        });

        queryPosts();

    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo("user", mOtherUser);
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                mAllPosts.addAll(posts);
                mChoresAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onItemClicked(Post post) {
        Intent i = new Intent(OtherProfile.this, PostDetails.class);
        i.putExtra("post", post);
        startActivity(i);
    }
}
