package com.example.choresforhire.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.choresforhire.login.LoginActivity;
import com.example.choresforhire.R;
import com.example.choresforhire.chores.MyChoresFragment;
import com.example.choresforhire.chores.ToDoChoresFragment;
import com.example.choresforhire.post.PostDetails;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {
    public static final String TAG = "HomeFragment";

    private Button mBtnLogout;
    private Button mBtnMyChores;
    private ImageButton mBtnEdit;
    private Button mBtnTodoChores;
    private ImageView mProfilePic;
    private TextView mProfileName;
    private TextView mProfileEmail;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ParseUser currUser = ParseUser.getCurrentUser();

        mProfileName = view.findViewById(R.id.profileName);
        mProfileEmail = view.findViewById(R.id.profileEmail);
        mProfilePic = view.findViewById(R.id.ivProfilePicPost);

        mProfileName.setText(currUser.getUsername());
        mProfileEmail.setText(currUser.getEmail());

        ParseFile profilePic = (ParseFile) currUser.get("profilePic");

        if (profilePic != null) {
            Glide.with(getContext()).load(profilePic.getUrl()).into(mProfilePic);
        } else {
            int drawableIdentifier = (getContext()).getResources().getIdentifier("blank_profile", "drawable", (getContext()).getPackageName());
            Glide.with(getContext()).load(drawableIdentifier).into(mProfilePic);
        }

        mBtnEdit = view.findViewById(R.id.ibEdit);
        mBtnLogout = view.findViewById(R.id.btnLogout);
        mBtnMyChores = view.findViewById(R.id.btnMyChores);
        mBtnTodoChores = view.findViewById(R.id.btnToDo);

        mBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new EditProfileFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();

                if (currentUser == null) {
                    Intent i = new Intent(getContext(), LoginActivity.class);
                    startActivity(i);
                }

            }
        });

        mBtnMyChores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MyChoresFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mBtnTodoChores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ToDoChoresFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
}