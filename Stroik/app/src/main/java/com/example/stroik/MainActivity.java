package com.example.stroik;



import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import android.os.Bundle;


public class MainActivity extends AppCompatActivity {
   private GestureDetector mDetector;
   private static final String TAG = "Swipe Position";
   private float xDown, xDefault, distanceX;
   private int i = 0;
   private static int MIN_DISTANCE = 100;
   private static int MAX_DISTANCE = 700;
   private int[] tabImg ={
           R.drawable.violin,
           R.drawable.cello,
   };

   private String[] tabString={
           "Skrzypce",
           "Wiolonczela"
   };
   private ImageButton btn;
   private ImageView view, instrument;
   private TextView txtView;
   private RelativeLayout linLay;

    public MainActivity() {
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         txtView = findViewById(R.id.textView3);
         linLay = findViewById(R.id.layout);
       // view.setImageResource(tabImg[i]);
        txtView.setText(tabString[i]);
        btn = findViewById(R.id.imageButton1);

        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN:
                        xDown = event.getX();
                       xDefault = btn.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float moveX;
                        moveX = event.getX();
                         distanceX=moveX-xDown;
                        float transformation = btn.getX()+distanceX;
                        if((Math.abs(xDefault-transformation)>MIN_DISTANCE) && (Math.abs(xDefault-transformation)<MAX_DISTANCE)) btn.setX(btn.getX()+distanceX);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(distanceX>0)
                        {
                            if(i==1) i=-1;
                            i++;

                        }
                        else if(distanceX<0)
                        {
                            if(i==0) i=2;
                            i--;
                        }
                        else {
                            openActivity2();

                        }
                        distanceX = 0;
                        btn.setImageResource(tabImg[i]);
                        txtView.setText(tabString[i]);
                       btn.setX(xDefault);
                        break;
                }
                return true;
            }
        });


    }
    public void openActivity2()
    {
        Intent intent = new Intent(this,Activity2.class);
        startActivity(intent);
    }



/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1=event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2= event.getX();


                float valueX = x2-x1;


                if(Math.abs(valueX)>MIN_DISTANCE)
                {

                    if(x2>x1)
                    {
                    float x = view.getTranslationX();
                        if(i==2) i=-1;
                        i++;
                        AnimatorSet w = new AnimatorSet();
                        AnimatorSet s = new AnimatorSet();
                        s.playSequentially(ObjectAnimator.ofFloat(view,"translationX",1000));
                        s.setDuration(800);
                        s.start();
                        view.setTranslationX(-1000);

                        w.playSequentially(ObjectAnimator.ofFloat(view,"translationX",50));
                        w.setDuration(800);
                        w.start();



                    }
                    else
                    {
                        view.animate()
                                .translationX(0)
                                .setDuration(800)
                                .start();
                        if(i==0) i=3;
                        i--;
                    }
                    view.setImageResource(tabImg[i]);
                    txtView.setText(tabString[i]);
                }



        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;*/
    }