package com.example.soundanalizer;



import android.Manifest;
import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.app.Application;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;



public class MainActivity extends AppCompatActivity {
    private static short[] audioDataToPlay;
    private static double[] audioDataToFFT;
    private static double[] FreqArray;
    private static double[] FreqArray2;
    private static int numBytes;

    private static final String TAG = "PERMISSION_TAG";
    private int RECORD_PERMISSION_CODE = 1;
    private Thread WThread = null;
    private Thread Counter = null;
    private String filename="";
    private String filepath = "FileToCalculate";

    public static TextView textView;
    public static TextView soundTxtView;
    public static  Button btnP;
    public static boolean recording = false;
    public static boolean playing = false;
    public static boolean numTypeOutput = false;

    public static double updateDoubleText;
    public static int updateIntText;

    public int Timer;
    @RequiresPermission(value = "android.permission.RECORD_AUDIO")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnR = (Button) findViewById(R.id.btnRecord);
         btnP = (Button) findViewById(R.id.btnPla);
        Button btnActive = (Button) findViewById(R.id.btnActive);
        Button btnRuntime = (Button) findViewById(R.id.btnRuntime);
        Switch simpleSwitch = (Switch) findViewById(R.id.switch1);
        textView = findViewById(R.id.titleTxtView);
        soundTxtView = findViewById(R.id.numTextView);
        LinearLayout showButtons = (LinearLayout) findViewById(R.id.groupLayout);

        showButtons.setVisibility(View.GONE);


        if(!isExternalStorageAvailable())
        {
            btnRuntime.setEnabled(false);
            btnRuntime.setVisibility(View.GONE);
        }
        AudioConstructor audiomanager = new AudioConstructor();
        simpleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(simpleSwitch.isChecked())
                {
                    numTypeOutput = false;
                }
                else {
                    numTypeOutput = true;
                }
            }
        });
        btnR.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        if (recording) {
                                            recording = false;
                                            audiomanager.stopRecording();
                                            btnR.setText("Record");
                                            audiomanager.fftHPS(audioDataToPlay);

                                           showButtons.setVisibility(View.VISIBLE);
                                            Log.d("Ilosc nagrany probek",String.valueOf(numBytes));
                                        } else {
                                            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED) {
                                                Timer = 3;
                                                new CountDownTimer(3000, 1000) {

                                                    @Override
                                                    public void onTick(long millisUntilFinished) {
                                                        soundTxtView.setText(String.valueOf(Timer));
                                                        Timer--;
                                                    }

                                                    @Override
                                                    public void onFinish() {
                                                        soundTxtView.setText("Recording");
                                                        recording = true;
                                                        btnR.setText("STOP");
                                                        // textView.setText("Recording");
                                                        audiomanager.recordSample();
                                                       // audiomanager.activeSoundDetector();
                                                    }
                                                }.start();
                                            }
                                            else {
                                                requestMicroPermission();
                                            }
                                        }
                                    }
                                });

        btnP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playing)
                {
                    audiomanager.stopPlaying();
                    soundTxtView.setText("-----");
                    btnP.setText("Play");
                    playing = false;
                }
                    else
                {
                    audiomanager.playSample(audioDataToPlay);
                    soundTxtView.setText("Playing");
                   btnP.setText("Stop");
                   playing = true;
                }


            }

        });
        btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (recording) {
                    recording = false;
                    audiomanager.stopRecording();
                    btnActive.setText("Active Freq Detect");
                    soundTxtView.setText("-----");

                } else {
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED) {

                                soundTxtView.setText("Recording");
                                recording = true;
                                btnActive.setText("STOP");
                                 audiomanager.activeSoundDetector();
                    }
                    else {
                        requestMicroPermission();
                    }
                }

            }
        });

        btnRuntime.setOnClickListener(new View.OnClickListener() {
            final CharSequence[] items = {"FFT","Frequency"};
            boolean fileChoose;
            @Override
            public void onClick(View v) {
                AlertDialog.Builder choose = new AlertDialog.Builder(MainActivity.this);
                choose.setTitle("What you wanna save?")
                        .setItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(which == 0)
                                        {
                                            fileChoose=false;
                                        }
                                        else if(which == 1)
                                        {
                                            fileChoose=true;
                                        }

                                    }
                                });


                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Nazwa pliku");
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filename=input.getText().toString()+".txt";
                        File file= new File(getExternalFilesDir(filepath),filename);
                        if(fileChoose) audiomanager.saveData(FreqArray, file);
                        else audiomanager.saveData(audioDataToPlay, file);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                choose.show();
            }
        });
    }

    private void requestMicroPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_PERMISSION_CODE);
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_PERMISSION_CODE);
        }
    }

    private boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if(extStorageState.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        else return false;
    }

    static void DataCopied(double[] data) {

        audioDataToFFT = data;

    }
    static void DataCopied(short[] data, int numBytesRead) {

        audioDataToPlay = data;
        numBytes = numBytesRead;
    }
    static void FreqArrayCopy(double [] Array) {

        FreqArray = Array;
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
                String sound="";
                boolean check=false;
                double Value = updateDoubleText;
                if(Value>=81 && Value<=83){
                    sound = "E2";
                }
                else if(Value>=109 && Value<=111)  sound = "A2";
                else if(Value>=146 && Value<=148)  sound = "D3";
                else if(Value>=195 && Value<=197)  sound = "G3";
                else if(Value>=246 && Value<=248)  sound = "B3";
                else if(Value>=329 && Value<=331)  sound = "E4";
                else {
                    soundTxtView.setText("OoR");
                    check= true;
                }
                if(!check) soundTxtView.setText(String.valueOf(sound));



                }
            });

        }
    public  void updateTextFreqView(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                double Value = updateDoubleText;
              // if(Value>=64 && Value<=1000)
                soundTxtView.setText(String.valueOf(Value));
                //else
                 //  soundTxtView.setText(String.valueOf("OoR"));


                }
            });
        }
    public void changeTextButton(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                soundTxtView.setText("-----");
                btnP.setText("Play");
                playing = false;

                }
            });
        }

    }










