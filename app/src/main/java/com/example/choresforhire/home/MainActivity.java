package com.example.choresforhire.home;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.choresforhire.R;
import com.example.choresforhire.chat.ChatsFragment;
import com.example.choresforhire.post.ComposeFragment;
import com.example.choresforhire.home.HomeFragment;
import com.example.choresforhire.profile.ProfileFragment;
import com.example.choresforhire.home.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

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
                    case R.id.ic_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.ic_search:
                        fragment = new SearchFragment();
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