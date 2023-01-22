package com.example.colors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView mInfoTextView;
    private ConstraintLayout mConstraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConstraintLayout = findViewById(R.id.constraintLayout);
        mInfoTextView = findViewById(R.id.textView);

    }

    public void onClickRed(View view) {
        mInfoTextView.setText(R.string.red);
        mConstraintLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.red));

    }

    public void onClickGreen(View view) {
        mInfoTextView.setText(R.string.green);
        mConstraintLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.green));
    }

    public void onClickBlue(View view) {
        mInfoTextView.setText(R.string.blue);
        mConstraintLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.blue));
    }
}