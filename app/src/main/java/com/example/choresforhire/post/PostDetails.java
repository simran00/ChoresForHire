package com.example.choresforhire.post;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.choresforhire.home.MainActivity;
import com.example.choresforhire.R;
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
        TextView detailDescription;
        btnAccept = findViewById(R.id.btnAccept);
        btnCancel = findViewById(R.id.btnCancel);

        post = null;
        String title = "";
        String pay = "";
        String description = "";

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                post = (Post) extras.get("post");
                title = extras.getString("title");
                pay = extras.getString("pay");
                description = extras.getString("description");
            }
        } else {
            post = (Post) savedInstanceState.getSerializable("post");
            title = (String) savedInstanceState.getSerializable("title");
            pay = (String) savedInstanceState.getSerializable("pay");
            description = (String) savedInstanceState.getSerializable("description");
        }

        detailTitle = (TextView) findViewById(R.id.detailTitle);
        detailPay = (TextView) findViewById(R.id.detailPay);
        detailDescription = (TextView) findViewById(R.id.detailDescription);

        detailTitle.setText(title);
        detailPay.setText("$" + pay);
        detailDescription.setText(description);

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
    }
}