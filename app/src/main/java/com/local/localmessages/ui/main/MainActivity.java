package com.local.localmessages.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.local.localmessages.Config;
import com.local.localmessages.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView userName= findViewById(R.id.userName);
        userName.setText(Config.currentUser.getDisplayName());
    }
}