package com.together.traveler;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
{
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bottomNavigationView = findViewById(R.id.nvMain);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Log.i("LKJ", "onNavigationItemSelected: ");
            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            transaction.setReorderingAllowed(true);
            try {
                int itemId = item.getItemId();
                if (itemId == R.id.action_home) {
                    transaction.replace(R.id.viewFragment, HomeFragment.class, null);
                } else if (itemId == R.id.action_search) {
                    transaction.replace(R.id.viewFragment, EventFragment.class, null);
                } else if (itemId == R.id.action_add) {
                    transaction.replace(R.id.viewFragment, BlankFragment.class, null);
                }else if (itemId == R.id.action_map) {
                    transaction.replace(R.id.viewFragment, MapFragment.class, null);
                }else if (itemId == R.id.action_user) {
                    transaction.replace(R.id.viewFragment, UserSelfFragment.class, null);
                }

                transaction.commit();
            } catch (Exception e) {
                Log.e("LKJ", "onCreate: ", e);
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

}