package com.example.choresforhire.post;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.choresforhire.home.MainActivity;
import com.example.choresforhire.R;
import com.example.choresforhire.profile.OtherProfile;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class PostDetails extends AppCompatActivity {
    public static final String TAG = "PostsDetails";

    private Post post;
    private Button btnAccept;
    private Button btnCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_details);

        TextView detailTitle;
        TextView detailPay;
        TextView author;
        TextView detailDescription;
        CardView mOtherProfile;
        btnAccept = findViewById(R.id.btnAccept);
        btnCancel = findViewById(R.id.btnCancel);

        post = null;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                post = (Post) extras.get("post");
            }
        } else {
            post = (Post) savedInstanceState.getSerializable("post");
        }

        detailTitle = findViewById(R.id.detailTitle);
        detailPay = findViewById(R.id.detailPay);
        detailDescription = findViewById(R.id.detailDescription);
        mOtherProfile = findViewById(R.id.otherProfile);
        author = findViewById(R.id.tvPostDetAut);

        detailTitle.setText(post.getTitle());
        detailPay.setText("$" + post.getPay());
        detailDescription.setText(post.getDescription());
        author.setText(post.getUser().getUsername());

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currUser = ParseUser.getCurrentUser();
                post.setAccepted(currUser);
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(PostDetails.this, "Success!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(PostDetails.this, MainActivity.class);
                        startActivity(i);
                    }
                });

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
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
                i.putExtra("user", post.getUser());
                startActivity(i);
            }
        });
    }
}
