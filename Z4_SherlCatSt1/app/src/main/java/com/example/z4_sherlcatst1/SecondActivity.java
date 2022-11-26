package com.example.z4_sherlcatst1;

import android.app.Activity;
import android.widget.TextView;
import android.os.Bundle;

public class SecondActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        String user = "John";
        String gift = "How are you?";

        user = getIntent().getExtras().getString("username");
        gift = getIntent().getExtras().getString("gift");

        TextView infoTextView = findViewById(R.id.textView);
        infoTextView.setText((user+", send "+ gift));
    }
}