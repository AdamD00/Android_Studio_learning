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

public class AudioConstructor extends Fragment {
    private final int freq = 44100;
    private final int channels = 2;
    private final int bitsPerSample = 16;
    private final int seconds = 5;
   // protected int bufferSize = ((50*freq*2)/8); <-działająca wersja
    protected int bufferSize = (int)(freq* channels*(bitsPerSample/8)*seconds);

    private AudioRecord audioRecord = null;
    private int channelOut = AudioFormat.CHANNEL_OUT_STEREO;
    private int channelIN = AudioFormat.CHANNEL_IN_STEREO;
    private int encoding = AudioFormat.ENCODING_PCM_16BIT;


    protected AudioTrack audioTrack = null;

    private Thread Rthread = null;





    protected void Start(int choice, short[] data, int numBytes) {


        Rthread = new Thread(new Runnable() {
            public void run() {


                switch (choice) {
                    case 1:
                        recordSample();

                        break;
                    case 2:
                        playSample(data,numBytes);
                        break;

                }

            }

    });
        Rthread.start();
}







    @SuppressLint({"MissingPermission", "RestrictedApi"})
    protected void recordSample() {

            short[] buffer = new short[bufferSize]; // check bufferSize w tablicy vs w audioRecord
            boolean whileCheck = false;
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, freq, channelIN, encoding, bufferSize*2);
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                audioRecord.startRecording();


                Log.i(LOG_TAG, "Audio Recording started");
                Log.i("BUFFER SIZE", String.valueOf(bufferSize));
                while (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                    whileCheck = true;
                    try {
                        Thread.currentThread().sleep(100);
                        Log.d("Sleep", "Thread Sleep");

                    } catch (InterruptedException e) {
                        Log.e("Error", "Thread Sleep not working");
                        e.printStackTrace();
                    }
                }
                if(whileCheck)  Log.d("While Loop", "1 while - initialized");
                else Log.d("While Loop", "1 while - NOT initialized");
                int numBytesRead = 0;

               // while (true) {
                    try {
                        numBytesRead = audioRecord.read(buffer, 0, bufferSize);
                 //   if(!MainActivity.recording)
                   // {
                        audioRecord.stop();

                   // }
                } catch(Throwable t){
                    Log.e("Error", "Read failed");
                    t.printStackTrace();
                }
           // }
                if (numBytesRead > 0) {
                    MainActivity.DataCopied(Arrays.copyOfRange(buffer, 0,numBytesRead),numBytesRead);
                }

                audioRecord.release();

            } else {
                Log.e("Error", "Record state failed");
            }


    }




    protected void playSample(short[] data, int numBytesToWrite){

        audioTrack = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(freq)
                        .setChannelMask(channelOut)
                        .build())
                .setBufferSizeInBytes(bufferSize*2)
                .build();
        audioTrack.setPlaybackRate(freq);
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
        Log.i("AudioTracker", "Audio Playing started");
        if(audioTrack.getState()==AudioTrack.STATE_INITIALIZED) {

        audioTrack.play();
        while(audioTrack.getPlayState()!=AudioTrack.PLAYSTATE_PLAYING)
        {
            try{
                Thread.sleep(100);
                Log.d("Sleep", "Thread Sleep");
            }catch (InterruptedException e)
            {
                Log.e("Error", "Thread Sleep not working");
                e.printStackTrace();
            }
        }
                    try {
                        while(true) { // Sprawdz PlayBackPosition
                           int check= audioTrack.write(data, 0, data.length);
                            if(check == AudioTrack.ERROR_INVALID_OPERATION) break;
                        }
                    } catch (Throwable t) {
                        Log.e("Error", "Write failed");
                        t.printStackTrace();


                }


        }else {
            Log.e("Error", "Track state not initialized");
        }

    }
    protected void stopPlaying()
    {
        audioTrack.stop();
        Rthread.interrupt();
        audioTrack.release();

    }
    protected void stopRecording()
    {
        audioRecord.stop();
       //+ Rthread.interrupt();
        audioRecord.release();

    }
}
