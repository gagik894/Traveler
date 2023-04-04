package com.together.traveler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.together.traveler.ui.add.event.AddEvent;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, AddEvent.newInstance())
                    .commitNow();
        }
    }
}