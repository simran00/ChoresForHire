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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(currentUser, title, pay, description);
            }
        });
    }

    private void savePost(ParseUser currentUser, String title, Integer pay, String description) {
        Post post = new Post();
        post.setTitle(title);
        post.setPay(pay);
        post.setDescription(description);
        post.setUser(currentUser);
        post.setLocation(currentUser.getParseGeoPoint("location"));

        // get chips selected
        List<Integer> ids = chipGroup.getCheckedChipIds();
        for (Integer id : ids) {
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
                }
                Log.i(TAG, "Post was successful!");
                composeTitle.setText("");
                composePay.setText("");
                composeDescription.setText("");

                Intent i = new Intent(getContext(), CheckAnimation.class);
                startActivity(i);
            }
        });
    }
}