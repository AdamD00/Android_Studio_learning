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
import org.jtransforms.fft.DoubleFFT_1D;
import org.jtransforms.fft.FloatFFT_1D;

import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    private int encoding = AudioFormat.ENCODING_PCM_FLOAT;

    MainActivity mainObject = new MainActivity();
    protected AudioTrack audioTrack = null;

    private Thread Rthread = null;

    public int numBytesRead;



    protected void Start(int choice, float[] data, int numBytes) {


        Rthread = new Thread(new Runnable() {
            public void run() {


                switch (choice) {
                    case 1:
                        recordSample();

                        break;
                    case 2:
                        playSample(data);
                        break;
                    case 3:
                        FFT(data,numBytes);
                        break;

                }

            }

    });
        Rthread.start();
}







    @SuppressLint({"MissingPermission", "RestrictedApi"})
    protected void recordSample() {

            float[] buffer = new float[bufferSize];
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
                 numBytesRead = 0;



                    try {

                        numBytesRead = audioRecord.read(buffer, 0, bufferSize, AudioRecord.READ_BLOCKING);


                        if (numBytesRead > 0) {
                            MainActivity.DataCopied(Arrays.copyOfRange(buffer, 0,numBytesRead),numBytesRead);

                        }
                } catch(Throwable t){
                    Log.e("Error", "Read failed");
                    t.printStackTrace();
                }





            } else {
                Log.e("Error", "Record state failed");
            }


    }




    protected void playSample(float[] data){

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
                        while(true) {
                           int check= audioTrack.write(data, 0, data.length, AudioTrack.WRITE_NON_BLOCKING);
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
        while(audioTrack.getPlayState()==AudioTrack.PLAYSTATE_PLAYING);
        audioTrack.release();

    }
    protected void stopRecording()
    {
        audioRecord.stop();
        while (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING);
        audioRecord.release();

    }
    protected void FFT(float[] data,int buffor)
    {

        float[] complexarray = Arrays.copyOf(data,data.length*2);

        FloatFFT_1D fft = new FloatFFT_1D(buffor);
        fft.complexForward(data);

        double[] mag = new double[buffor/2];
        double czestotliwosc;
        for(int i = 0; i<buffor/2; i++){
            double re = data[2*i];
            double im = data[2*i+1];
            mag[i] = Math.sqrt(re*re+im*im);
        }
        int maxIndex=0;
        if(data != null) {
            double maxAmp=0;

            for (int j = 0; j < mag.length; j++) {
                if(j==0){
                    maxAmp = mag[j];
                    maxIndex = j;
                }else if(mag[j]>maxAmp)
                {
                    maxAmp = mag[j];
                    maxIndex = j;
                }
            }
        }
        else{
            Log.e("Error", "Data null");
        }
        czestotliwosc =  (double) maxIndex * (double)freq/(double)(buffor);
        czestotliwosc = Math.round(czestotliwosc*100)/100;

        mainObject.DataCopied(czestotliwosc);
        mainObject.updateTextView();


    }
}
