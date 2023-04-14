package com.together.traveler;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.together.traveler.context.AppContext;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppContext.init(this);
        setContentView(R.layout.activity_add_event);
    }
}