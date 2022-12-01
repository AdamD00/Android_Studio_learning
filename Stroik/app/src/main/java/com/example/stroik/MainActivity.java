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
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;


public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{
   private GestureDetector mDetector;
   private static final String TAG = "Swipe Position";
   private float x1,x2,y1,y2;
   private int i = 0;
   private static int MIN_DISTANCE = 150;
   private int[] tabImg ={
           R.drawable.violin,
           R.drawable.cello,
           R.drawable.guitar
   };
   private String[] tabString={
           "Skrzypce",
           "Wiolonczela",
           "Gitara"
   };
   private ImageView view;
   private TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         view = findViewById(R.id.imageView);
         txtView = findViewById(R.id.textView3);
        this.mDetector = new GestureDetector(MainActivity.this,this);
        view.setImageResource(tabImg[i]);
        txtView.setText(tabString[i]);

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
                        if(i==2) i=-1;
                        i++;

                    }
                    else
                    {
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
        return false;
    }
}