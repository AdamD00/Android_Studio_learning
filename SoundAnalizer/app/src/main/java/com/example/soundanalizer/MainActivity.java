package com.example.soundanalizer;

import static android.app.PendingIntent.getActivity;
import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Process;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;


import androidx.fragment.app.Fragment;

import java.util.Arrays;



import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;



import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    public static TextView textView;
    public static boolean recording = false;

    private static short[] audioData;
    private static int numBytes;

    private static final String TAG = "PERMISSION_TAG";
    private Thread WThread = null;


    @RequiresPermission(value = "android.permission.RECORD_AUDIO")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnR = (Button) findViewById(R.id.btnRecord);
        Button btnP = (Button) findViewById(R.id.btnPla);
        Button btnD = (Button) findViewById(R.id.btnData);
        textView = findViewById(R.id.textView);

        btnP.setVisibility(View.GONE);
        btnD.setVisibility(View.GONE);
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
                btnD.setVisibility(View.VISIBLE);

            }

        });
        btnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audiomanager.stopPlaying();
                btnD.setVisibility(View.GONE);
                WThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0; i<audioData.length;i++)
                        {


                            try {
                                Thread.sleep(500);
                                Log.d("Data",String.valueOf(audioData[i]));

                            } catch (InterruptedException e) {
                                Log.e("Error", "Thread Sleep not working");
                                e.printStackTrace();
                            }
                        }
                    }
                });
               // WThread.start();

            }
        });
    }

    static void DataCopied(short[] data, int numBytesRead) {

        audioData = data;
        numBytes = numBytesRead;
    }
}







