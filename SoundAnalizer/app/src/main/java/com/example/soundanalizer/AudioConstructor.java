package com.example.soundanalizer;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import android.util.Log;
import android.widget.Toast;


import org.jtransforms.fft.DoubleFFT_1D;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class AudioConstructor extends Fragment {
    private final int freq = 48000;
    private final int channels = 1;
    private final int bitsPerSample = 16;
    private final int secondsForRecording = 10;
    private final int secondsforActive = 1;
    // protected int bufferSize = ((50*freq*2)/8); <-działająca wersja

    public int bufferSizeToRecord = (freq * channels * (bitsPerSample / 8) * secondsForRecording);
    public int bufferSizeToActive = (freq * channels * (bitsPerSample / 8) * secondsforActive);
    public int oneSecondBufferSize = (freq * channels * (bitsPerSample / 8));


    private AudioRecord audioRecord = null;
    public static boolean endSaving;


    private int channelOut = AudioFormat.CHANNEL_OUT_STEREO;
    private int channelIN = AudioFormat.CHANNEL_IN_STEREO;
    private int encoding = AudioFormat.ENCODING_PCM_16BIT; //change from float to bit 16
    public int minBuffer = AudioRecord.getMinBufferSize(freq, channelIN, encoding);

    MainActivity mainObject = new MainActivity();
    protected AudioTrack audioTrack = null;

    private Thread Rthread = null;
    private Thread FFTthread = null;
    private Thread FFT2thread = null;
    private final Semaphore avaiable = new Semaphore(0);
    public int numBytesRead;



    protected void recordSample() {

            Rthread = new Thread(new Runnable() {
                public void run() {
                    short[] buffer = new short[bufferSizeToRecord]; // change from float -> short

                    if (ContextCompat.checkSelfPermission(MyApplication.getInstance(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, freq, channelIN, encoding, bufferSizeToRecord); //AudioSource.MIC->UNPROCESSED

                        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
                        if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                            audioRecord.startRecording();


                            Log.i("BUFFER SIZE", String.valueOf(bufferSizeToRecord));
                            while (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                                try {
                                    Thread.currentThread().sleep(100);
                                    Log.d("Sleep", "Thread Sleep");

                                } catch (InterruptedException e) {
                                    Log.e("Error", "Thread Sleep not working");
                                    e.printStackTrace();
                                }
                            }
                            numBytesRead = 0;

                            try {

                                numBytesRead = audioRecord.read(buffer, 0, bufferSizeToRecord, AudioRecord.READ_BLOCKING);


                                if (numBytesRead > 0) {
                                    MainActivity.DataCopied(Arrays.copyOf(buffer, numBytesRead), numBytesRead);
                                }

                            } catch (Throwable t) {
                                Log.e("Error", "Read failed");
                                t.printStackTrace();
                            }


                        } else {
                            Log.e("Error", "Record state failed");
                        }
                    }
                }
            });
            Rthread.start();

    }
    protected void activeSoundDetector() {

        Rthread = new Thread(new Runnable() {
            public void run() {
                short[] buffer = new short[bufferSizeToActive]; // change from float -> short

                if (ContextCompat.checkSelfPermission(MyApplication.getInstance(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, freq, channelIN, encoding, bufferSizeToActive); //AudioSource.MIC->UNPROCESSED

                    Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
                    if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                        audioRecord.startRecording();


                        Log.i("BUFFER SIZE", String.valueOf(bufferSizeToActive));
                        while (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                            try {
                                Thread.currentThread().sleep(100);
                                Log.d("Sleep", "Thread Sleep");

                            } catch (InterruptedException e) {
                                Log.e("Error", "Thread Sleep not working");
                                e.printStackTrace();
                            }
                        }
                        numBytesRead = 0;

                        try {
                            while(MainActivity.recording) {
                                numBytesRead = audioRecord.read(buffer, 0, bufferSizeToActive, AudioRecord.READ_BLOCKING);

                                if (numBytesRead > 0) {
                                    double rms = calculateRMS(buffer,numBytesRead);
                                    if(rms>500) {
                                        Log.d("RMS","I heard - start calculate");
                                        fftHPS(Arrays.copyOf(buffer, numBytesRead));
                                        avaiable.acquire();

                                    }
                                    else {
                                        Log.d("RMS","Too Quiet");
                                        mainObject.DataCopied(00.00);
                                        if(mainObject.numTypeOutput) mainObject.updateTextView();
                                        else mainObject.updateTextFreqView();
                                    }
                                    MainActivity.DataCopied(Arrays.copyOf(buffer, numBytesRead), numBytesRead);
                                }

                            }
                        } catch (Throwable t) {
                            Log.e("Error", "Read failed");
                            t.printStackTrace();
                        }


                    } else {
                        Log.e("Error", "Record state failed");
                    }
                }
            }
        });
        Rthread.start();

    }

    private double calculateRMS(short[] buffer, int numBytesRead) {
        double sum = 0;
        for (int i = 0; i < numBytesRead; i++) {
            sum += buffer[i] * buffer[i];
        }
        double rms = Math.sqrt(sum / numBytesRead);
        return rms;
    }

    protected void playSample(short[] data) {
        Rthread = new Thread(new Runnable() {
            public void run() {

                audioTrack = new AudioTrack.Builder()
                        .setAudioAttributes(new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build())
                        .setAudioFormat(new AudioFormat.Builder()
                                .setEncoding(encoding)
                                .setSampleRate(freq)
                                .setChannelMask(channelOut)
                                .build())
                        .setBufferSizeInBytes(data.length * 2)
                        .build();
                audioTrack.setPlaybackRate(freq);
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);




                Log.i("AudioTracker", "Audio Playing started");
                if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {

                    audioTrack.play();
                    while (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
                        try {
                            Thread.sleep(100);
                            Log.d("Sleep", "Thread Sleep");
                        } catch (InterruptedException e) {
                            Log.e("Error", "Thread Sleep not working");
                            e.printStackTrace();
                        }
                    }
                    audioTrack.setNotificationMarkerPosition(data.length/2);
                    audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                        @Override
                        public void onMarkerReached(AudioTrack track) {
                            Log.d("Playing Status", "Stop Playing");
                            stopPlaying();
                            mainObject.changeTextButton();
                        }

                        @Override
                        public void onPeriodicNotification(AudioTrack track) {

                        }
                    });

                    int countTime =0;
                    int checker = 0;
                    try {
                        checker = audioTrack.write(data, 0, data.length, AudioTrack.WRITE_BLOCKING);

                    } catch (Throwable t) {
                        Log.i("State", "Playing Stop");
                    }

//                    while(countTime<checker) countTime++;

//                    if(countTime == checker)
//                    {
//
//                    }

                } else {
                    Log.e("Error", "Track state not initialized");

                }
            }
        });
        Rthread.start();
    }
//    @SuppressLint({"MissingPermission", "RestrictedApi"})
//    protected void onSoundActivation() {
//        Rthread = new Thread(new Runnable() {
//            public void run() {
//                Looper.prepare();
//                Handler handler = new Handler();
//
//
//                short[] buffer = new short[oneSecondBufferSize]; // change from float -> short
//                endSaving = false;
//                audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, freq, channelIN, encoding, oneSecondBufferSize); //AudioSource.MIC->UNPROCESSED
//
//                Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
//                if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
//                    audioRecord.startRecording();
//
//
//                    Log.i(LOG_TAG, "Audio Recording started");
//                    Log.i("BUFFER SIZE", String.valueOf(oneSecondBufferSize));
//                    while (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
//                        try {
//                            Thread.currentThread().sleep(100);
//                            Log.d("Sleep", "Thread Sleep");
//
//                        } catch (InterruptedException e) {
//                            Log.e("Error", "Thread Sleep not working");
//                            e.printStackTrace();
//                        }
//                    }
//                    numBytesRead = 0;
//
//                    try {
//
//                            numBytesRead = audioRecord.read(buffer, 0, oneSecondBufferSize, AudioRecord.READ_BLOCKING);
//
//
//
//
//
//                    } catch (Throwable t) {
//                        Log.e("Error", "Read failed");
//                        t.printStackTrace();
//                    }
//                    if (numBytesRead > 0) {
//                        MainActivity.DataCopied(Arrays.copyOf(buffer, numBytesRead), numBytesRead);
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(MyApplication.getInstance(),"Udało się nagrać Tyle nagrania "+numBytesRead,Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//
//                    }
//                    endSaving = true;
//                   // Log.e("Bool end Saving",String.valueOf(endSaving));
//                    stopRecording();
//                } else {
//                    Log.e("Error", "Record state failed");
//                }
//                Looper.loop();
//            }
//        });
//        Rthread.start();
//
//
//    }
    protected void saveData(short[] audioData, File file) {

        Rthread = new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                Handler handler = new Handler();
                try {
                FileOutputStream outputStream = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                Log.i("Path",file.getPath());
                        for (int i = 0; i < audioData.length; i++) {

                                outputStreamWriter.write( audioData[i]+"\n");

                        }

                    outputStreamWriter.close();
                    outputStream.close();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MyApplication.getInstance(),"Zapisane w "+file.getName(),Toast.LENGTH_SHORT).show();
                        }
                    });


                } catch (IOException e) {
                    Log.e("Error","Some problem");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MyApplication.getInstance(),"Nie udalo sie zapisac do pliku",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                Looper.loop();
            }
        });
        Rthread.start();
    }
    protected void saveData(double[] audioData, File file) {

        Rthread = new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                Handler handler = new Handler();
                try {
                FileOutputStream outputStream = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                Log.i("Path",file.getPath());
                        for (int i = 0; i < audioData.length; i++) {

                                outputStreamWriter.write( audioData[i]+"\n");

                        }

                    outputStreamWriter.close();
                    outputStream.close();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MyApplication.getInstance(),"Zapisane w "+file.getName(),Toast.LENGTH_SHORT).show();
                        }
                    });


                } catch (IOException e) {
                    Log.e("Error","Some problem");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MyApplication.getInstance(),"Nie udalo sie zapisac do pliku",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                Looper.loop();
            }
        });
        Rthread.start();
    }

    protected void stopPlaying() {

        audioTrack.stop();
        while(audioTrack.getPlayState()==AudioTrack.PLAYSTATE_PLAYING);
        audioTrack.release();
        audioTrack = null;

    }

    protected void stopRecording() {
        audioRecord.stop();
        while (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING);
        audioRecord.release();
        audioRecord = null;

    }
//    protected void FFT(short[] data) {
//        FFTthread = new Thread(new Runnable() {
//            public void run() {
//                Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
//              //   Log.i("Check", String.valueOf(data.length==buffor));
//                // double freqArray[][] = new double[mag.length][2];
//                int mNumberOfFFTPoints = data.length;
//                double[] output = new double[mNumberOfFFTPoints];
//                DoubleFFT_1D fft = new DoubleFFT_1D(mNumberOfFFTPoints);
//
//
//                double [] windowedBuffer = new double[mNumberOfFFTPoints];
//                double[]  doubleFftData= new double[2*mNumberOfFFTPoints];
//                double[] mag = new double[mNumberOfFFTPoints/2];
//                double [] outputFft = new double[mNumberOfFFTPoints];
//                double a0=1, a1= 1.93, a2= 1.29, a3= 0.388, a4 = 0.032;
//
//                double czestotliwosc;
//
//                for(int i = 0; i< mNumberOfFFTPoints; i++){
//                   //Hann Window
//                    output[i] = (double)data[i]*0.5*(1-Math.cos(2*Math.PI*i/(mNumberOfFFTPoints-1)));
//
//
//
//                    // Flat Top window
//                    /* output[i]= (double)data[i]*a0-a1*Math.cos((2*Math.PI*i)/ (window.length-1))
//                            +a2*Math.cos((4*Math.PI*i)/ (window.length-1))
//                            +a3*Math.cos((6*Math.PI*i)/ (window.length-1))
//                            +a4*Math.cos((8*Math.PI*i)/ (window.length-1));*/
//                }
//
//                fft.realForward(output);
//                double maxAmp = -1;
//                int maxIndex = 0;
//
//                for (int i = 0; i < mag.length; i++) {
//                    double re = output[2*i];
//                    double im = output[2*i+1];
//                    outputFft[i] = re*re+im*im;
//
//
//                    mag[i] = Math.sqrt(Math.pow(re,2)+ Math.pow(im,2));
//                    if (mag[i] > maxAmp) {
//                        maxAmp = mag[i];
//                        maxIndex = i;
//
//                    }
//                }
//
//
//
//
//
//
//                 czestotliwosc = ((double)freq*(double)maxIndex/(double)(mNumberOfFFTPoints));
//                // czestotliwosc = (double)freq*(double)maxIndex;
//                Log.d("Freq z FFT",String.valueOf(czestotliwosc));
//
//
//
//
//                czestotliwosc = czestotliwosc*100;
//                czestotliwosc = Math.round(czestotliwosc);
//                czestotliwosc = czestotliwosc/100;
//
//                mainObject.FreqArrayCopy(mag);
//                mainObject.DataCopied(czestotliwosc);
//                if(mainObject.numTypeOutput) mainObject.updateTextView();
//                else mainObject.updateTextFreqView();
//
//
//            }
//            });
//        FFTthread.start();
//
//    }

    protected void fftHPS(short[] data) {
        FFTthread = new Thread(new Runnable() {
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
                int mNumberOfFFTPoints = data.length;
                DoubleFFT_1D fft = new DoubleFFT_1D(mNumberOfFFTPoints);
                double[] output = new double[2*mNumberOfFFTPoints];
                double[] mag = new double[mNumberOfFFTPoints/2];
                double czestotliwosc, czestotliwoscAfterHPS;
                //1.Windowing Hann
                for(int i = 0; i< mNumberOfFFTPoints; i++){

                    output[i] = (double)data[i]*0.5*(1-Math.cos(2*Math.PI*i/(mNumberOfFFTPoints-1)));
                }
                for(int i = mNumberOfFFTPoints; i< output.length; i++){

                    output[i] = 0;
                }

                //2.FFT
                fft.complexForward(output);


                double maxAmp = -1;
                int maxIndex = 0;

                for (int i = 0; i < mag.length; i++) {
                    double re = output[2*i];
                    double im = output[2*i+1];
                    mag[i] = Math.sqrt(Math.pow(re,2)+ Math.pow(im,2));
                    if (mag[i] > maxAmp) {
                        maxAmp = mag[i];
                        maxIndex = i;

                    }
                }
                //3.Decymacja -> downsampling

                double [] d2 = new double[mag.length];
                double [] d3 = new double[mag.length];
                double [] d4 = new double[mag.length];
                double [] d5 = new double[mag.length];
                double [] d6 = new double[mag.length];
                double [] result = new double[mag.length];

                for(int i=0; i<mag.length; i++){
                    if((2*i)<mag.length) d2[i]=mag[2*i];
                    else d2[i] = 0;
                    if((3*i)<mag.length) d3[i]=mag[3*i];
                        else d3[i]=0;
                    if((4*i)<mag.length) d4[i]=mag[4*i];
                    else d4[i]=0;
                    if((5*i)<mag.length) d5[i]=mag[5*i];
                    else d5[i]=0;
                    if((6*i)<mag.length) d6[i]=mag[6*i];
                    else d6[i]=0;
                }

                for(int i =0; i< result.length;i++)
                {
                    result[i]=mag[i]*d2[i]*d3[i]*d4[i]*d5[i]*d6[i];
                }
                double maxAmpAfterHPS=-1;
                int maxIndexAfterHPS=0;
                for(int i =0; i<result.length;i++)
                {
                    if (result[i] > maxAmpAfterHPS) {
                        maxAmpAfterHPS = result[i];
                        maxIndexAfterHPS = i;
                    }
                }









                czestotliwosc = ((double)freq*(double)maxIndex/(double)(mNumberOfFFTPoints));
                Log.d("Freq beforeHps",String.valueOf(czestotliwosc));
                czestotliwoscAfterHPS = ((double)freq*(double)maxIndexAfterHPS/(double)(mNumberOfFFTPoints));
                Log.d("Freq After Hps",String.valueOf(czestotliwoscAfterHPS));


                czestotliwoscAfterHPS *=100.0;
                czestotliwoscAfterHPS = Math.round(czestotliwoscAfterHPS);
                czestotliwoscAfterHPS =czestotliwoscAfterHPS/100.0;

                mainObject.FreqArrayCopy(result);
                mainObject.DataCopied(czestotliwoscAfterHPS);
                if(mainObject.numTypeOutput) mainObject.updateTextView();
                else mainObject.updateTextFreqView();

               if(avaiable.hasQueuedThreads()) avaiable.release();





            }
        });
        FFTthread.start();
    }

}
