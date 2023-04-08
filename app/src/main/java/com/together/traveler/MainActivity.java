package com.together.traveler;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.together.traveler.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {
    NavHostFragment navHostFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        BottomNavigationView bottomNav = findViewById(R.id.nvMain);
        NavController navController;
        FloatingActionButton addButton = findViewById(R.id.floatingActionButton2);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNav, navController);
        }
        addButton.setOnClickListener(v -> {
            Intent switchActivityIntent = new Intent(this, AddActivity.class);
            startActivity(switchActivityIntent);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("asd", "onKeyDown: " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            scrollUp();
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            scrollDown();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void scrollDown() {
        HomeFragment homeFragment = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
        if (homeFragment!=null)
            homeFragment.scrollDown();
    }

    private void scrollUp() {
        HomeFragment homeFragment = (HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
        if (homeFragment!=null)
            homeFragment.scrollUp();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

}