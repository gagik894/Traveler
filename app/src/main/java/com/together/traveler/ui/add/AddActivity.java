package com.together.traveler.ui.add;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.together.traveler.R;
import com.together.traveler.context.AppContext;
import com.together.traveler.ui.add.event.AddEvent;
import com.together.traveler.ui.add.place.AddPlace;
import com.together.traveler.ui.main.MainActivity;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.init(this);
        setContentView(R.layout.activity_add);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        new AlertDialog.Builder(this)
                .setTitle("What you want to add?")
                .setItems(new CharSequence[]{"Event", "Place"}, (dialog, which) -> {
                    if (which == 0) {
                        AddEvent addEventFragment = new AddEvent();
                        fragmentTransaction.replace(R.id.addActivityFragment, addEventFragment);
                    } else {
                        AddPlace addPlaceFragment = new AddPlace();
                        fragmentTransaction.replace(R.id.addActivityFragment, addPlaceFragment);
                    }
                    fragmentTransaction.addToBackStack(null);
                    // Commit the transaction after setting up the fragment.
                    fragmentTransaction.commit();
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        // create an Intent to start the MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}