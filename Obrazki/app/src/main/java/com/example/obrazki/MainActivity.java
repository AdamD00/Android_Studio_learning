package com.example.obrazki;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView firstTextView = findViewById(R.id.textView2);
        TextView secondTextView = findViewById(R.id.textView4);

        ImageView TopView = findViewById(R.id.imageView);
        TopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String [] phrases = {"Spoko loko",
                        "Loki są spoko",
                        "Dasz radę"};

                shuffleArray(phrases);
                firstTextView.setText(phrases[0]);
                secondTextView.setText(phrases[1]);
            }
        });
    }
    void shuffleArray(String[]ar){
        Random rnd = new Random();
        for(int i = ar.length -1;i>0;i--){
            int index = rnd.nextInt(i+1);
            String a = ar[index];
            ar[index]=ar[i];
            ar[i]=a;
        }
    }
}