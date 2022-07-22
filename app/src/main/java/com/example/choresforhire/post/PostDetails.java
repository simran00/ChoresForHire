package com.example.choresforhire.post;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.choresforhire.home.MainActivity;
import com.example.choresforhire.R;
import com.example.choresforhire.profile.OtherProfile;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class PostDetails extends AppCompatActivity {
    public static final String TAG = "PostsDetails";

    private Post mPost;
    private Button mBtnAccept;
    private Button mBtnCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_details);

        TextView mAuthor;
        TextView mDetailPay;
        TextView mDetailTitle;
        ImageView mProfilePic;
        CardView mOtherProfile;
        TextView mDetailDescription;

        mBtnAccept = findViewById(R.id.btnAccept);
        mBtnCancel = findViewById(R.id.btnCancel);

        mPost = null;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mPost = (Post) extras.get("post");
            }
        } else {
            mPost = (Post) savedInstanceState.getSerializable("post");
        }

        mDetailTitle = findViewById(R.id.detailTitle);
        mDetailPay = findViewById(R.id.detailPay);
        mDetailDescription = findViewById(R.id.detailDescription);
        mOtherProfile = findViewById(R.id.otherProfile);
        mAuthor = findViewById(R.id.tvPostDetAut);
        mProfilePic = findViewById(R.id.ivProfilePicPost);

        mDetailTitle.setText(mPost.getTitle());
        mDetailPay.setText("$" + mPost.getPay());
        mDetailDescription.setText(mPost.getDescription());
        mAuthor.setText(mPost.getUser().getUsername());

        ParseFile profilePic = (ParseFile) mPost.getUser().get("profilePic");

        if (profilePic != null) {
            Glide.with(PostDetails.this).load(profilePic.getUrl()).into(mProfilePic);
        } else {
            int drawableIdentifier = (PostDetails.this).getResources().getIdentifier("blank_profile", "drawable", (PostDetails.this).getPackageName());
            Glide.with(PostDetails.this).load(drawableIdentifier).into(mProfilePic);
        }

        mBtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currUser = ParseUser.getCurrentUser();
                mPost.setAccepted(currUser);
                mPost.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(PostDetails.this, "Success!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(PostDetails.this, MainActivity.class);
                        startActivity(i);
                    }
                });

                pushNotification();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PostDetails.this, MainActivity.class);
                startActivity(i);
            }
        });

        mOtherProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PostDetails.this, OtherProfile.class);
                i.putExtra("user", mPost.getUser());
                startActivity(i);
            }
        });
    }

    private void pushNotification() {
        JSONObject data = new JSONObject();
        // Put data in the JSON object
        try {
            data.put("alert", mPost.getAccepted().getUsername() + " accepted your chore!");
            data.put("title", "Accepted: " + mPost.getTitle());
        } catch ( JSONException error) {
            // should not happen
            throw new IllegalArgumentException("unexpected parsing error", error);
        }

        // Configure the push
        ParsePush push = new ParsePush();
        // push to post author
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereEqualTo("user", mPost.getUser());
        push.setQuery(query);
        push.setData(data);
        push.sendInBackground();
    }
}
