package com.example.stroik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Activity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        ImageView view = (ImageView) findViewById(R.id.imageView1);
        TextView txt = (TextView) findViewById(R.id.textView3);
        Button btng = (Button) findViewById(R.id.btn1);
        Button btnb = (Button) findViewById(R.id.btn2);

            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(view,
                    PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.2f));
            scaleDown.setDuration(1000);
            scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
            //
            scaleDown.start();
            btng.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    scaleDown.cancel();
                    txt.setText("Udało się");
                    view.setBackgroundResource(R.drawable.circle);
                    view.setImageResource(R.drawable.check);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    },1000);

                }


            });
            btnb.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    scaleDown.cancel();
                    txt.setText("Podkręć strune");
                    view.setBackgroundResource(R.drawable.circle);
                    view.setImageResource(R.drawable.x);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    },3000);
                }


            });



    }
}