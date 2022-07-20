package com.example.choresforhire.post;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.choresforhire.home.MainActivity;
import com.example.choresforhire.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class ComposeFragment extends Fragment {
    public static final String TAG = "ComposeFragment";

    private Button btnSubmit;
    private EditText composePay;
    private ChipGroup chipGroup;
    private EditText composeTitle;
    private EditText composeDescription;

    public ComposeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        composeTitle = view.findViewById(R.id.composeTitle);
        composePay = view.findViewById(R.id.composePay);
        composeDescription = view.findViewById(R.id.composeDescription);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        chipGroup = view.findViewById(R.id.chipGroupCompose);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = composeTitle.getText().toString();
                int pay = -1;
                pay = Integer.parseInt(composePay.getText().toString());
                String description = composeDescription.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pay < 0) {
                    Toast.makeText(getContext(), "Pay cannot be empty or less than $0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Integer> chipsChecked = chipGroup.getCheckedChipIds();

                if (chipsChecked.isEmpty()) {
                    Toast.makeText(getContext(), "Choose a tag", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(currentUser, title, pay, description, chipsChecked);

            }
        });
    }

    private void savePost(ParseUser currentUser, String title, Integer pay, String description, List<Integer> chipsChecked) {
        Post post = new Post();
        post.setTitle(title);
        post.setPay(pay);
        post.setDescription(description);
        post.setUser(currentUser);
        post.setLocation(currentUser.getParseGeoPoint("location"));

        // set chips selected
        for (Integer id : chipsChecked) {
            Chip chip = chipGroup.findViewById(id);
            switch (chip.getText().toString()) {
                case "18+":
                    post.setAgeRestriction(true);
                    break;
                case "One-time":
                    post.setOneTime(true);
                    break;
                case "Recurring":
                    post.setRecurring(true);
                    break;
            }
        }

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Post was successful!");
                composeTitle.setText("");
                composePay.setText("");
                composeDescription.setText("");

                Intent i = new Intent(getContext(), CheckAnimation.class);
                startActivity(i);
                getActivity().finish();

                JSONObject data = new JSONObject();
                // Put data in the JSON object
                try {
                    data.put("alert", post.getTitle());
                    data.put("title", "New Chore");
                } catch ( JSONException error) {
                    // should not happen
                    throw new IllegalArgumentException("unexpected parsing error", error);
                }

                // Configure the push
                ParsePush push = new ParsePush();
                // push to all users except current user
                ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
                query.whereNotEqualTo("user", ParseUser.getCurrentUser());
                push.setQuery(query);
                push.setData(data);
                push.sendInBackground();
            }
        });
    }
}