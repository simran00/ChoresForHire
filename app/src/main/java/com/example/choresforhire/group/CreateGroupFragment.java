package com.example.choresforhire.group;

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

import com.example.choresforhire.R;
import com.example.choresforhire.post.CheckAnimation;
import com.parse.ParseException;
import com.parse.SaveCallback;


public class CreateGroupFragment extends Fragment {
    public static final String TAG = "CreateGroupFragment";

    private Button mBtnCreate;
    private EditText mGroupTitle;
    private EditText mGroupDescription;

    public CreateGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGroupTitle = view.findViewById(R.id.composeTitleGroup);
        mGroupDescription = view.findViewById(R.id.composeDescriptionGroup);
        mBtnCreate = view.findViewById(R.id.btnCreateGroup);

        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mGroupTitle.getText().toString();
                String description = mGroupDescription.getText().toString();

                if (title.isEmpty()) {
                    Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveGroup(title, description);
            }
        });
    }

    private void saveGroup(String title, String description) {
        Group group = new Group();
        group.setName(title);
        group.setDescription(description);

        group.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while creating", e);
                    Toast.makeText(getContext(), "Error while creating group!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Group was successfully created!");
                mGroupTitle.setText("");
                mGroupDescription.setText("");

                Intent i = new Intent(getContext(), CheckAnimation.class);
                startActivity(i);
            }
        });


    }

}