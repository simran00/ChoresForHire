package com.example.choresforhire.chores;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.choresforhire.R;
import com.example.choresforhire.chat.ChatsFragment;
import com.example.choresforhire.post.ComposeFragment;
import com.example.choresforhire.home.HomeFragment;
import com.example.choresforhire.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ChoresActivity extends AppCompatActivity {
    public static final String TAG = "ChoresActivity";
    public final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chores);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.ic_add:
                        fragment = new ComposeFragment();
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
    }
}
