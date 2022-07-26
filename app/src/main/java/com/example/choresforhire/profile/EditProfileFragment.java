package com.example.choresforhire.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.choresforhire.R;
import com.example.choresforhire.home.MainActivity;
import com.example.choresforhire.login.LoginActivity;
import com.example.choresforhire.login.SignupActivity;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditProfileFragment extends Fragment {
    public static final String TAG = "EditProfileFragment";
    private TextView mProfileName;
    private TextView mProfilePass;
    private Button mModify;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ParseUser currUser = ParseUser.getCurrentUser();
        mProfileName = view.findViewById(R.id.editUsername);
        mProfilePass = view.findViewById(R.id.editPassword);

        mModify = view.findViewById(R.id.btnModify);
        mModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mProfileName.getText().toString();
                String password = mProfilePass.getText().toString();

                if (!name.isEmpty()) {
                    currUser.setUsername(name);
                }

                if (!password.isEmpty()) {
                    currUser.setPassword(password);
                }

                if (!name.isEmpty() || !password.isEmpty()) {
                    currUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(getContext(), "Modified profile!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getContext(), MainActivity.class);
                            startActivity(i);
                            getActivity().finish();
                        }
                    });
                }
            }
        });

    }
}