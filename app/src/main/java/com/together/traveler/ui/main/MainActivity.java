package com.together.traveler.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.together.traveler.context.AppContext;
import com.together.traveler.model.Event;
import com.together.traveler.ui.add.AddActivity;
import com.together.traveler.R;
import com.together.traveler.ui.event.EventFragment;
import com.together.traveler.ui.main.bottomSheet.AddBottomSheet;
import com.together.traveler.ui.main.home.HomeFragment;
import com.together.traveler.ui.main.user.UserFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private NavHostFragment navHostFragment;
    private List<Integer> menuItems = new ArrayList<>(Arrays.asList(R.id.homeFragment, R.id.universalTicketFragment, R.id.mapFragment, R.id.userFragment));
    private BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.init(this);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        bottomNav = findViewById(R.id.nvMain);
        NavController navController;
        FloatingActionButton addButton = findViewById(R.id.floatingActionButton2);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNav, navController);
        }
        addButton.setOnClickListener(v -> {
            AddBottomSheet bottomSheet = new AddBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "bottomSheet");
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("asd", "onKeyDown: " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
             scrollUp();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
             scrollDown();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            switchMenu("right");
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            switchMenu("left");
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_SPACE) {
            Intent switchActivityIntent = new Intent(this, AddActivity.class);
            startActivity(switchActivityIntent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void scrollDown() {
        Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
        if (fragment instanceof HomeFragment) {
            HomeFragment homeFragment = (HomeFragment) fragment;
            homeFragment.scrollDown();
        }else if(fragment instanceof UserFragment){
            UserFragment userFragment = (UserFragment) fragment;
            userFragment.scrollDown();
        } else if (fragment instanceof EventFragment) {
            EventFragment eventFragment = (EventFragment) fragment;
            eventFragment.scrollDown();
        }
    }

    private void scrollUp() {
        Fragment fragment =  navHostFragment.getChildFragmentManager().getFragments().get(0);
        if (fragment instanceof HomeFragment) {
            HomeFragment homeFragment = (HomeFragment) fragment;
            homeFragment.scrollUp();
        }else if(fragment instanceof UserFragment){
            UserFragment userFragment = (UserFragment) fragment;
            userFragment.scrollUp();
        } else if (fragment instanceof EventFragment) {
            EventFragment eventFragment = (EventFragment) fragment;
            eventFragment.scrollUp();
        }
    }

    private void switchMenu(String side){
        Fragment fragment =  navHostFragment.getChildFragmentManager().getFragments().get(0);
        if (fragment instanceof EventFragment) {
            EventFragment eventFragment = (EventFragment) fragment;
            eventFragment.backPress();
            return;
        }
        int currentId = menuItems.indexOf(bottomNav.getSelectedItemId());
        if(Objects.equals(side, "left")) {
            bottomNav.setSelectedItemId(menuItems.get((currentId+3)%4));
        }else{
            bottomNav.setSelectedItemId(menuItems.get((currentId+1)%4));
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//    }

}