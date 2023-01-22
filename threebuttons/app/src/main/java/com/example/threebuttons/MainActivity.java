package com.example.threebuttons;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private TextView mInfTextView;
    private Button mElefantCounterButton;
    private Button mElefantCounterButton2;
    private int mCount = 0;
    private int mCount2 = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInfTextView = findViewById(R.id.textView);
        mElefantCounterButton = findViewById(R.id.b2);
        mElefantCounterButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                mInfTextView.setText("I can see " + ++mCount + " elephants");
            }
        });
        mElefantCounterButton2 = findViewById(R.id.b3);
        mElefantCounterButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInfTextView.setText("I can see " + ++mCount2 + " another elephants");
            }
        });

    }
    public void onClick(View view) {
        mInfTextView.setText("Hello Elephant");
    }
}