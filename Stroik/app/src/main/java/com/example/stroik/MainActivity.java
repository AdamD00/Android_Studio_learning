package com.example.stroik;



import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;

import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import android.os.Bundle;


public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{
   private GestureDetector mDetector;
   private static final String TAG = "Swipe Position";
   private float x1,x2,y1,y2;
   private static int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View view = findViewById(R.id.imageView);
        this.mDetector = new GestureDetector(MainActivity.this,this);

        FlingAnimation fling = new FlingAnimation(view, DynamicAnimation.SCROLL_X);
        fling.setStartVelocity(2000)
                .setMinValue(0)
                .setMaxValue(15)
                .setFriction(1.1f)
                .start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);

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
                        Toast.makeText(this,"Right is swiped",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Right Swipe");
                    }
                    else
                    {
                        Toast.makeText(this,"Left is swiped",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Left Swipe");
                    }
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
        return false;
    }
}