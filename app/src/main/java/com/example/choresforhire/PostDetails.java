package com.example.choresforhire;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class PostDetails extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_details);

        TextView detailTitle;
        TextView detailPay;
        TextView detailDescription;

        String title = "";
        String pay = "";
        String description = "";

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                title = extras.getString("title");
                pay = extras.getString("pay");
                description = extras.getString("description");
            }
        } else {
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
    }
}
