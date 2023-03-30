package com.example.soundanalizer;



import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Timer;


public class MainActivity extends AppCompatActivity {


    public static TextView textView;
    public static TextView numTxtView;
    public static boolean recording = false;
    public static double updateDoubleText;
    public static int updateIntText;
    private Timer timer = new Timer();
    private static float[] audioData;
    private static int numBytes;

    private static final String TAG = "PERMISSION_TAG";
    private Thread WThread = null;
    private Thread Counter = null;



    @RequiresPermission(value = "android.permission.RECORD_AUDIO")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnR = (Button) findViewById(R.id.btnRecord);
        Button btnP = (Button) findViewById(R.id.btnPla);
        Button btnD = (Button) findViewById(R.id.btnData);
        Button btnS = (Button) findViewById(R.id.btnStopPlaying);
        textView = findViewById(R.id.titleTxtView);
        numTxtView = findViewById(R.id.numTextView);


        btnP.setVisibility(View.GONE);
        btnD.setVisibility(View.GONE);
        btnS.setVisibility(View.GONE);
        int permission = checkSelfPermission("android.permission.RECORD_AUDIO");
        if (permission != 0) {
            btnR.setActivated(false);
            btnR.setText("Dont click");
            btnP.setActivated(false);
            btnP.setText("Dont click");


            Log.i("PERMISSION", "DENIED");
        } else {
            Log.i("PERMISSION", "GRANTED");


        }


        AudioConstructor audiomanager = new AudioConstructor();
        btnR.setOnClickListener(new View.OnClickListener() {
                                    @SuppressLint("MissingPermission")
                                    @Override
                                    public void onClick(View v) {
                                        if (recording) {
                                            recording = false;
                                            audiomanager.stopRecording();
                                            btnR.setText("Record");
                                            btnP.setVisibility(View.VISIBLE);
                                            btnD.setVisibility(View.VISIBLE);
                                            textView.setText("Record");

                                        } else {
                                            recording = true;
                                            btnR.setText("STOP");
                                            textView.setText("Recording");
                                            audiomanager.Start(1, null,numBytes);


                                        }
                                    }
                                });

        btnP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                audiomanager.Start(2, audioData,numBytes);
                textView.setText("Playing");
                btnS.setVisibility(View.VISIBLE);

            }

        });
        btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audiomanager.stopPlaying();
                btnS.setVisibility(View.GONE);

            }
        });
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                audiomanager.Start(3,audioData,numBytes);


                    }
        });
    }

    static void DataCopied(float[] data, int numBytesRead) {

        audioData = data;
        numBytes = numBytesRead;
    }
    static void DataCopied(double Values ) {

        updateDoubleText = Values;

    }
    static void DataCopied(int Values ) {

        updateIntText = Values;

    }
    public  void updateTextView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                    numTxtView.setText(String.valueOf(updateIntText)+"Hz");

                }
            });

        }

    }










