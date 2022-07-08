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
import android.widget.TextView;

import com.example.choresforhire.login.LoginActivity;
import com.example.choresforhire.R;
import com.example.choresforhire.chores.MyChoresFragment;
import com.example.choresforhire.chores.ToDoChoresFragment;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {
    public static final String TAG = "HomeFragment";

    private Button btnLogout;
    private Button btnMyChores;
    private Button btnTodoChores;
    private TextView profileName;
    private TextView profileEmail;

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

        profileName = view.findViewById(R.id.profileName);
        profileEmail = view.findViewById(R.id.profileEmail);

        //profileName.setText((CharSequence) currUser.get("name"));
        profileName.setText(currUser.getUsername());
        profileEmail.setText(currUser.getEmail());

        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        btnMyChores = (Button) view.findViewById(R.id.btnMyChores);
        btnTodoChores = (Button) view.findViewById(R.id.btnToDo);

        btnLogout.setOnClickListener(new View.OnClickListener() {
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

        btnMyChores.setOnClickListener(new View.OnClickListener() {
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

        btnTodoChores.setOnClickListener(new View.OnClickListener() {
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