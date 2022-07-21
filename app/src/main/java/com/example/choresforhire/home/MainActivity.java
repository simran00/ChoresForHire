package com.example.choresforhire.home;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.choresforhire.R;
import com.example.choresforhire.chat.ChatsFragment;
import com.example.choresforhire.group.AllGroupsFragment;
import com.example.choresforhire.post.ComposeFragment;
import com.example.choresforhire.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.ic_add:
                        fragment = new ComposeFragment();
                        break;
                    case R.id.ic_group:
                        fragment = new AllGroupsFragment();
                        break;
                    case R.id.ic_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.ic_profile:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.ic_chat:
                        fragment = new ChatsFragment();
                        break;
                    default:
                        fragment = new HomeFragment();
                        break;

                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.ic_home);
    }
}